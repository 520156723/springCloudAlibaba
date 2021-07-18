package per.hqd.usercenter.rocketmq;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.cloud.stream.messaging.Sink;
import org.springframework.stereotype.Service;
import per.hqd.usercenter.domain.dto.messaging.UserAddBonusMsgDTO;
import per.hqd.usercenter.service.user.UserService;

@Service
@Slf4j
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class AddBonusStreamConsumer {

    private final UserService userService;

    /**
     * 当收到消息做什么
     * @param message 收到的消息
     */
    @StreamListener(Sink.INPUT)
    public void receive(UserAddBonusMsgDTO message) {
        message.setEvent("CONTRIBUTE");
        message.setDescription("投稿加积分..");
        this.userService.addBonus(message);
    }
}
