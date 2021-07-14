package per.hqd.contentcenter.service.content;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import per.hqd.contentcenter.dao.content.ShareMapper;
import per.hqd.contentcenter.domain.dto.content.ShareAuditDTO;
import per.hqd.contentcenter.domain.dto.content.ShareDTO;
import per.hqd.contentcenter.domain.dto.messaging.UserAddBonusMsgDTO;
import per.hqd.contentcenter.domain.dto.user.UserDTO;
import per.hqd.contentcenter.domain.entity.content.Share;
import per.hqd.contentcenter.feignClient.UserCenterFeignClient;

import java.util.Objects;

@Slf4j
@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class ShareService {

    private final ShareMapper shareMapper;

    private final UserCenterFeignClient userCenterFeignClient;

    private final RocketMQTemplate rocketMQTemplate;

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

    public Share auditById(Integer id, ShareAuditDTO auditDTO) {
        Share share = this.shareMapper.selectByPrimaryKey(id);
        if (share == null) {
            throw new IllegalArgumentException("参数非法！该分享不存在");
        }
        //只有未审核状态才能审核
        if (!Objects.equals(share.getAuditStatus(), "NOT_YET")) {
            throw new IllegalArgumentException("该分享已经审核");
        }
        //修改审核状态
        share.setAuditStatus(auditDTO.getAuditStatusEnum().toString());
        this.shareMapper.updateByPrimaryKey(share);
        if ("PASS".equals(auditDTO.getAuditStatusEnum().toString())) {
            UserAddBonusMsgDTO msg = UserAddBonusMsgDTO.builder()
                    .userId(share.getUserId())
                    .bonus(50)
                    .build();
            //如果通过审核，把消息发送到消息队列，让用户中心消费，并且加积分。发给add-bonus，第二个参数是消息体
            rocketMQTemplate.convertAndSend("add-bonus", msg);
            log.info("发送消息{}给主题add-bonus", msg.toString());
        }
        return share;
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
}
