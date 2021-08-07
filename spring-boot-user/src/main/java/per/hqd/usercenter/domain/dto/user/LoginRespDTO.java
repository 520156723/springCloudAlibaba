package per.hqd.usercenter.domain.dto.user;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 *  登录响应
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class LoginRespDTO {

    /**
     * token信息
     */
    private JwtTokenRespDTO token;

    /**
     * 用户信息
     */
    private UserRespDTO user;

}
