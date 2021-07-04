package per.hqd.usercenter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import per.hqd.usercenter.dao.user.UserMapper;
import per.hqd.usercenter.domain.entity.user.User;

import java.util.Date;
import java.util.List;

@RestController
public class TestController {

    @Autowired(required = false)
    private UserMapper userMapper;

    @Autowired
    private DiscoveryClient discoveryClient;

    @GetMapping("/test")
    public User test(){
        User user = new User();
        user.setAvatarUrl("xxx");
        user.setBonus(100);
        user.setCreateTime(new Date());
        user.setUpdateTime(new Date());
        userMapper.insertSelective(user);
        return user;
    }

    @GetMapping("/allServices")
    public List<String> getAllServices(){
        return this.discoveryClient.getServices();
    }

    @GetMapping("/q")
    public User query(User user){
        return user;
    }

    @PostMapping("/p")
    public User post(@RequestBody User user){
        return user;
    }

}
