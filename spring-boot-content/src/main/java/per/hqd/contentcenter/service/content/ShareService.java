package per.hqd.contentcenter.service.content;

import com.alibaba.fastjson.JSON;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.support.RocketMQHeaders;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.messaging.Source;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import per.hqd.contentcenter.dao.content.ShareMapper;
import per.hqd.contentcenter.dao.rocketmqTransactionLogMapper.RocketmqTransactionLogMapper;
import per.hqd.contentcenter.domain.dto.content.ShareAuditDTO;
import per.hqd.contentcenter.domain.dto.content.ShareDTO;
import per.hqd.contentcenter.domain.dto.messaging.UserAddBonusMsgDTO;
import per.hqd.contentcenter.domain.dto.user.UserDTO;
import per.hqd.contentcenter.domain.entity.content.Share;
import per.hqd.contentcenter.domain.entity.rocketmqTransactionLogMapper.RocketmqTransactionLog;
import per.hqd.contentcenter.domain.enums.AuditStatusEnum;
import per.hqd.contentcenter.feignClient.UserCenterFeignClient;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class ShareService {

    private final ShareMapper shareMapper;

    private final UserCenterFeignClient userCenterFeignClient;

    private final RocketmqTransactionLogMapper rocketmqTransactionLogMapper;

    private final Source source;

    public ShareDTO findById(Integer id) {
        Share share = this.shareMapper.selectByPrimaryKey(id);
        Integer userId = share.getUserId();
        UserDTO userDTO = this.userCenterFeignClient.findById(userId);
        // 消息装配 使用spring的对象装配工具
        ShareDTO shareDTO = new ShareDTO();
        BeanUtils.copyProperties(share, shareDTO);
        shareDTO.setWxNickName(userDTO.getWxNickname());
        return shareDTO;
    }

    /**
     * @param id       share表id
     * @param auditDTO 要修改的审核状态和原因包装体
     * @return
     */
    public Share auditById(Integer id, ShareAuditDTO auditDTO) {
        Share share = this.shareMapper.selectByPrimaryKey(id);
        if (share == null) {
            throw new IllegalArgumentException("参数非法！该分享不存在");
        }
        //只有未审核状态才能审核
        if (!Objects.equals(share.getAuditStatus(), "NOT_YET")) {
            throw new IllegalArgumentException("该分享已经审核");
        }

        if (AuditStatusEnum.PASS.equals(auditDTO.getAuditStatusEnum())) {
            // 如果通过审核，把消息发送到消息队列，让用户中心消费，并且加积分。发给add-bonus
            // 发送半消息
            String transactionId = UUID.randomUUID().toString();
            /* 用rocketMQTemplate发送消息
            this.rocketMQTemplate.sendMessageInTransaction(
                    "tx-add-bonus-group",
                    "add-bonus",
                    MessageBuilder
                            // 放入消息对象
                            .withPayload(
                                    UserAddBonusMsgDTO.builder()
                                            .userId(share.getUserId())
                                            .bonus(50)
                                            .build()
                            )
                            //
                            .setHeader(RocketMQHeaders.TRANSACTION_ID, transactionId)
                            .setHeader("shareId", id)
                            .build(),
                    // arg额外参数, 埋点用来做修改审核
                    auditDTO
            );*/
            // 用SpringCloudStream发送消息
            source.output().send(
                    MessageBuilder
                            .withPayload(
                                    UserAddBonusMsgDTO.builder()
                                            .userId(share.getUserId())
                                            .bonus(50)
                                            .build()
                            )
                            .setHeader(RocketMQHeaders.TRANSACTION_ID, transactionId)
                            .setHeader("shareId", id)
                            // 有个坑，由于传入是String类型，强转会失败
                            // .setHeader("dto", auditDTO)
                            .setHeader("dto", JSON.toJSONString(auditDTO))// 把java对象转成json字符串
                            .build()
            );
        } else {
            // 拒绝时
            auditByIdInDB(id, auditDTO);
        }
        return share;
    }

    /**
     * 修改审核状态
     *
     * @param id       share.id
     * @param auditDTO 修改内容
     */
    @Transactional(rollbackFor = Exception.class)
    public void auditByIdInDB(Integer id, ShareAuditDTO auditDTO) {
        Share share = Share.builder()
                .id(id)
                .auditStatus(auditDTO.getAuditStatusEnum().toString())
                .reason(auditDTO.getReason())
                .build();
        // updateByPrimaryKey会全字段更新包括null字段，而updateByPrimaryKeySelective只更新share中非空的字段
        this.shareMapper.updateByPrimaryKeySelective(share);
        // TODO 把share写进缓存
    }

    @Transactional(rollbackFor = Exception.class)
    public void auditByIdWithRocketMqLog(Integer id, ShareAuditDTO auditDTO, String transactionId) {
        auditByIdInDB(id, auditDTO);
        // 如果修改失败的，事务回滚走不到下面的代码，rocketmqlog表中就不会有记录
        this.rocketmqTransactionLogMapper.insertSelective(
                RocketmqTransactionLog.builder()
                        .transactionId(transactionId)
                        .log("审核分享。。。")
                        .build()
        );
    }


    public static void main(String[] args) {
        //测试使用restTemplate去调用user服务的api
        RestTemplate restTemplate = new RestTemplate();
        //用get方法请求，并返回一个对象
        String forObject = restTemplate.getForObject(
                "http://localhost:8080/users/{id}",
                String.class,
                1
        );
        System.out.println(forObject);
        //如果想获取状态码
        ResponseEntity<String> forEntity = restTemplate.getForEntity(
                "http://localhost:8080/users/{id}",
                String.class,
                1
        );
        System.out.println(forEntity.getBody());
        System.out.println(forEntity.getStatusCode());
    }

    public PageInfo<Share> q(String title, Integer pageNum, Integer pageSize) {
        // 会切入下面那条不分页的sql， 自动拼接limit
        PageHelper.startPage(pageNum, pageSize);
        // 不分页的sql
        List<Share> shares = this.shareMapper.selectByParam(title);
        return new PageInfo<Share>(shares);
    }
}
