package per.hqd.contentcenter.feignClient;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import per.hqd.contentcenter.domain.dto.user.UserDTO;
import per.hqd.contentcenter.feignClient.fallbackfactory.UserCenterFeignClientFallBackFactory;

/**
 * 请求user-center服务的实例都通过这个类的方法去调用
 */
//@FeignClient(name = "user-center", configuration = UserCenterFeignConfiguration.class)
//@FeignClient(name = "user-center", fallback = UserCenterFeignClientFallback.class)//被限流时不再抛异常，而是fallback处理
@FeignClient(name = "user-center",
        fallbackFactory = UserCenterFeignClientFallBackFactory.class //用这个就不能用fallback，这个可以捕捉到异常
)
public interface UserCenterFeignClient {

    /**
     * 该方法被调用时会构造出url：http://user-center/users/{id}
     * @param id
     * @return
     */
    @GetMapping("/users/{id}")
    UserDTO findById(@PathVariable Integer id);
}
