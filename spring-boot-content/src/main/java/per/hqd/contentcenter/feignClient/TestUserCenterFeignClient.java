package per.hqd.contentcenter.feignClient;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.cloud.openfeign.SpringQueryMap;
import org.springframework.web.bind.annotation.*;
import per.hqd.contentcenter.domain.dto.user.UserDTO;

@FeignClient(name = "user-center")
public interface TestUserCenterFeignClient {

    @GetMapping("/q")
    UserDTO query(@SpringQueryMap UserDTO userDTO);//不能完全跟springmvc请求一样，需要加@SpringQueryMap做兼容

    //@RequestMapping(value = "/p", method = RequestMethod.POST)
    @PostMapping("/p")
    UserDTO post(@RequestBody UserDTO userDTO);
}
