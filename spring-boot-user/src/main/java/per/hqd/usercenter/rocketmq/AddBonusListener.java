package per.hqd.usercenter.rocketmq;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import per.hqd.usercenter.dao.bonusEventLogMapper.BonusEventLogMapper;
import per.hqd.usercenter.dao.user.UserMapper;
import per.hqd.usercenter.domain.dto.messaging.UserAddBonusMsgDTO;
import per.hqd.usercenter.domain.entity.bonusEventLogMapper.BonusEventLog;
import per.hqd.usercenter.domain.entity.user.User;

import java.util.Date;

@Service
@RocketMQMessageListener(consumerGroup = "consumer-group", topic = "add-bonus")//表明这是一个rocketmq的listener
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@Slf4j
public class AddBonusListener implements RocketMQListener<UserAddBonusMsgDTO> {

    private final UserMapper userMapper;

    private final BonusEventLogMapper bonusEventLogMapper;
    /**
     * 当收到消息做什么
     * @param userAddBonusMsgDTO 收到的消息
     */
    @Override
    public void onMessage(UserAddBonusMsgDTO userAddBonusMsgDTO) {
        //加积分(先查后写)
        Integer userId = userAddBonusMsgDTO.getUserId();
        Integer bonus = userAddBonusMsgDTO.getBonus();
        User user = this.userMapper.selectByPrimaryKey(userId);
        user.setBonus(user.getBonus() + bonus);
        this.userMapper.updateByPrimaryKey(user);
        //记录日志到bonus_event_log表里
        this.bonusEventLogMapper.insert(
                BonusEventLog.builder()
                        .userId(userId)
                        .value(bonus)
                        .event("CONTRIBUTE")//事件是投稿
                        .createTime(new Date())
                        .description("投稿加积分")
                        .build()
        );
        log.info("积分添加完毕。。。");
    }
}
