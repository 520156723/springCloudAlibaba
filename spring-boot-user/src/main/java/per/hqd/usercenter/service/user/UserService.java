package per.hqd.usercenter.service.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import per.hqd.usercenter.dao.bonusEventLogMapper.BonusEventLogMapper;
import per.hqd.usercenter.dao.user.UserMapper;
import per.hqd.usercenter.domain.dto.messaging.UserAddBonusMsgDTO;
import per.hqd.usercenter.domain.dto.user.UserLoginDTO;
import per.hqd.usercenter.domain.entity.bonusEventLogMapper.BonusEventLog;
import per.hqd.usercenter.domain.entity.user.User;

import java.util.Date;

@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@Slf4j
public class UserService {

    private final UserMapper userMapper;

    private final BonusEventLogMapper bonusEventLogMapper;

    public User findById(Integer id) {
        log.info("我被请求了");
        // select * from user where id=#{id}
        return this.userMapper.selectByPrimaryKey(id);
    }

    @Transactional(rollbackFor = Exception.class)
    public void addBonus(UserAddBonusMsgDTO msgDTO) {
        //加积分(先查后写)
        Integer userId = msgDTO.getUserId();
        Integer bonus = msgDTO.getBonus();
        User user = this.userMapper.selectByPrimaryKey(userId);
        user.setBonus(user.getBonus() + bonus);
        this.userMapper.updateByPrimaryKeySelective(user);
        //记录日志到bonus_event_log表里
        this.bonusEventLogMapper.insert(
                BonusEventLog.builder()
                        .userId(userId)
                        .value(bonus)
                        .event(msgDTO.getEvent())//事件是投稿
                        .createTime(new Date())
                        .description(msgDTO.getDescription())
                        .build()
        );
        log.info("积分添加完毕。。。");
    }

    public User login(UserLoginDTO userLoginDTO, String openId) {
        User user = this.userMapper.selectOne(
                User.builder()
                        .wxId(openId)
                        .build()
        );
        if (user == null) {
            User userToSave = User.builder()
                    .wxId(openId)
                    .wxNickname(userLoginDTO.getWxNickname())
                    .roles("user") // 通过这种方式注册的都是普通用户
                    .avatarUrl(userLoginDTO.getAvatarUrl())
                    .createTime(new Date())
                    .updateTime(new Date())
                    .bonus(300)
                    .build();
            this.userMapper.insertSelective(userToSave);
            return userToSave;
        }
        return user;
    }
}
