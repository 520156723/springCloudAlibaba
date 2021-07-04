package per.hqd.contentcenter.service.content;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import per.hqd.contentcenter.dao.content.ShareMapper;
import per.hqd.contentcenter.domain.dto.content.ShareDTO;
import per.hqd.contentcenter.domain.dto.user.UserDTO;
import per.hqd.contentcenter.domain.entity.content.Share;
import per.hqd.contentcenter.feignClient.UserCenterFeignClient;

@Slf4j
@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class ShareService {

    private final ShareMapper shareMapper;

    private final UserCenterFeignClient userCenterFeignClient;

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
