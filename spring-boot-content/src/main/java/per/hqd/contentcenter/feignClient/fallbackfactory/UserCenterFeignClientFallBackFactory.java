package per.hqd.contentcenter.feignClient.fallbackfactory;

import feign.hystrix.FallbackFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import per.hqd.contentcenter.domain.dto.user.UserAddBonusDTO;
import per.hqd.contentcenter.domain.dto.user.UserDTO;
import per.hqd.contentcenter.feignClient.UserCenterFeignClient;

@Component
@Slf4j
public class UserCenterFeignClientFallBackFactory implements FallbackFactory<UserCenterFeignClient> {
    @Override
    public UserCenterFeignClient create(Throwable throwable) {
        return new UserCenterFeignClient() {
            @Override
            public UserDTO findById(Integer id) {
                UserDTO userDTO = new UserDTO();
                userDTO.setWxNickname("宁被限流了捏");
                log.warn("远程调用被限流/降级了", throwable);
                return userDTO;
            }

            @Override
            public UserDTO addBonus(UserAddBonusDTO userAddBonusDTO) {
                log.warn("远程调用被限流/降级了", throwable);
                return null;
            }
        };
    }
}
