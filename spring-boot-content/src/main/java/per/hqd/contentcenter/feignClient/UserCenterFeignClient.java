package per.hqd.contentcenter.feignClient;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import per.hqd.contentcenter.domain.dto.user.UserDTO;

/**
 * 请求user-center服务的实例都通过这个类的方法去调用
 */
@FeignClient(name = "user-center")
public interface UserCenterFeignClient {

    /**
     * 该方法被调用时会构造出url：http://user-center/users/{id}
     * @param id
     * @return
     */
    @GetMapping("/users/{id}")
    UserDTO findById(@PathVariable Integer id);
}
