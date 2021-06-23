package per.hqd.usercenter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import per.hqd.usercenter.dao.user.UserMapper;
import per.hqd.usercenter.domain.entity.user.User;

import java.util.Date;

@RestController
public class TestController {

    @Autowired(required = false)
    UserMapper userMapper;

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
}
