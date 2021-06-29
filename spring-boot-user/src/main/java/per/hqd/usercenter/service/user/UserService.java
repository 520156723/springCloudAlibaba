package per.hqd.usercenter.service.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import per.hqd.usercenter.dao.user.UserMapper;
import per.hqd.usercenter.domain.entity.user.User;

@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@Slf4j
public class UserService {

    private final UserMapper userMapper;

    public User findById(Integer id){
        log.info("我被请求了");
        // select * from user where id=#{id}
        return this.userMapper.selectByPrimaryKey(id);
    }
}
