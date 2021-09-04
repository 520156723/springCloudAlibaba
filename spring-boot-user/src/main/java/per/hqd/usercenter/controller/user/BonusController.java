package per.hqd.usercenter.controller.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import per.hqd.usercenter.domain.dto.messaging.UserAddBonusMsgDTO;
import per.hqd.usercenter.domain.dto.user.UserAddBonusDTO;
import per.hqd.usercenter.domain.entity.user.User;
import per.hqd.usercenter.service.user.UserService;

@Slf4j
@RestController
@RequestMapping("/users")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class BonusController {

    private final UserService userService;

    @PutMapping("/add-bonus")
    public User addBonus(@RequestBody UserAddBonusDTO userAddBonusDTO) {
        Integer userId = userAddBonusDTO.getUserId();
        this.userService.addBonus(
                UserAddBonusMsgDTO.builder()
                        .userId(userId)
                        .bonus(userAddBonusDTO.getBonus())
                        .description("积分兑换")
                        .event("BUY")
                        .build());
        return this.userService.findById(userId);
    }
}
