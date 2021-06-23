package per.hqd.usercenter.service.user;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import per.hqd.usercenter.dao.user.UserMapper;
import per.hqd.usercenter.domain.entity.user.User;

@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class UserService {

    private final UserMapper userMapper;

    public User findById(Integer id){
        // select * from user where id=#{id}
        return this.userMapper.selectByPrimaryKey(id);
    }
}
