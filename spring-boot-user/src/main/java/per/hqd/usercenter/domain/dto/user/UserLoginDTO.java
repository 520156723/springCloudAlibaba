package per.hqd.usercenter.domain.dto.user;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 用户登录信息
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserLoginDTO {

    /**
     * 头像地址
     */
    private String avatarUrl;

    /**
     * code
     */
    private String code;

    /**
     * 微信昵称
     */
    private String wxNickname;

}
