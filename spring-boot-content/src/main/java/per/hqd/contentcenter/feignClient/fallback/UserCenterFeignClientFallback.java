package per.hqd.contentcenter.feignClient.fallback;

import org.springframework.stereotype.Component;
import per.hqd.contentcenter.domain.dto.user.UserAddBonusDTO;
import per.hqd.contentcenter.domain.dto.user.UserDTO;
import per.hqd.contentcenter.feignClient.UserCenterFeignClient;

@Component
public class UserCenterFeignClientFallback implements UserCenterFeignClient {
    @Override
    public UserDTO findById(Integer id) {//限流时会进入这个方法
        UserDTO userDTO = new UserDTO();
        userDTO.setWxNickname("宁被限流了奥");
        return userDTO;
    }

    @Override
    public UserDTO addBonus(UserAddBonusDTO userAddBonusDTO) {
        return null;
    }
}
