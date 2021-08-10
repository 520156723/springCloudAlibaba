package per.hqd.contentcenter.controller.content;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import per.hqd.contentcenter.auth.CheckLogin;
import per.hqd.contentcenter.domain.dto.content.ShareDTO;
import per.hqd.contentcenter.service.content.ShareService;
import per.hqd.contentcenter.util.JwtOperator;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/share")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class ShareController {

    private final ShareService shareService;

    private final JwtOperator jwtOperator;

    @CheckLogin
    @GetMapping("/{id}")
    public ShareDTO findById(@PathVariable Integer id, @RequestHeader("X-Token") String token){
        return this.shareService.findById(id, token);
    }

    /**
     * 假的登录验证
     * @return
     */
    @GetMapping("/gen-token")
    public String genToken(){
        Map<String, Object> userInfo = new HashMap<>(3);
        userInfo.put("id", 1);
        userInfo.put("wxNickname", "hqd");
        userInfo.put("role", "user");
        return this.jwtOperator.generateToken(userInfo);
    }
}
