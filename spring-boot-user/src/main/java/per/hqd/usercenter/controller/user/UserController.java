package per.hqd.usercenter.controller.user;

import cn.binarywang.wx.miniapp.api.WxMaService;
import cn.binarywang.wx.miniapp.bean.WxMaJscode2SessionResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.common.error.WxErrorException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import per.hqd.usercenter.domain.dto.user.JwtTokenRespDTO;
import per.hqd.usercenter.domain.dto.user.LoginRespDTO;
import per.hqd.usercenter.domain.dto.user.UserLoginDTO;
import per.hqd.usercenter.domain.dto.user.UserRespDTO;
import per.hqd.usercenter.domain.entity.user.User;
import per.hqd.usercenter.service.user.UserService;
import per.hqd.usercenter.util.JwtOperator;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/users")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class UserController {

    private final UserService userService;

    private final WxMaService wxMaService;

    private final JwtOperator jwtOperator;

    @GetMapping("/{id}")
    public User findById(@PathVariable Integer id) {
        return this.userService.findById(id);
    }

    @PostMapping("login")
    public LoginRespDTO login(@RequestBody UserLoginDTO userLoginDTO) throws WxErrorException {
        // 微信小程序服务端校验是否已经成功登录 未登录成功会抛异常
        WxMaJscode2SessionResult result = this.wxMaService
                .getUserService()
                .getSessionInfo(userLoginDTO.getCode());
        // openId是用户在微信这边的唯一标识 即微信Id
        String openId = result.getOpenid();
        // 看用户是否注册 没注册就插入注册 注册了就颁发token
        User user = this.userService.login(userLoginDTO, openId);
        // 颁发token
        Map<String, Object> userInfo = new HashMap<>(3);
        userInfo.put("id", user.getId());
        userInfo.put("wxNickname", user.getWxNickname());
        userInfo.put("role", user.getRoles());
        String token = this.jwtOperator.generateToken(userInfo);
        Date expirationTime = this.jwtOperator.getExpirationTime();
        log.info("用户{}登录成功，生成的token={}，有效期至{}",
                userLoginDTO.getWxNickname(),
                token,
                expirationTime);
        // 构建响应
        return LoginRespDTO.builder()
                .token(JwtTokenRespDTO.builder()
                        .expirationTime(expirationTime.getTime())
                        .token(token)
                        .build())
                .user(UserRespDTO.builder()
                        .avatarUrl(user.getAvatarUrl())
                        .bonus(user.getBonus())
                        .id(user.getId())
                        .wxNickname(user.getWxNickname())
                        .build())
                .build();
    }
}
