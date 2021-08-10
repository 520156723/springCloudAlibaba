package per.hqd.contentcenter.auth;

import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import per.hqd.contentcenter.util.JwtOperator;

import javax.servlet.http.HttpServletRequest;

@Slf4j
@Aspect
@Component
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class CheckLoginAspect {

    private final JwtOperator jwtOperator;

    @Around("@annotation(per.hqd.contentcenter.auth.CheckLogin)")
    public Object checkLogin(ProceedingJoinPoint pjp){
        try {
            // 从header获取token
            ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            HttpServletRequest request = requestAttributes.getRequest();
            // 这里假设前端传过来的token参数名为X-token
            String token = request.getHeader("X-token");
            // 校验是否合法和过期
            Boolean isValid = jwtOperator.validateToken(token);
            if (!isValid) {
                throw new SecurityException("Token不合法！");
            }
            // 执行方法
            Object res = pjp.proceed();
            // 校验成功就把用户信息设置到request的attribute里
            Claims claims = jwtOperator.getClaimsFromToken(token);
            request.setAttribute("id", claims.get("id"));
            request.setAttribute("wxNickname", claims.get("wxNickname"));
            request.setAttribute("role", claims.get("role"));
            return pjp.proceed();
        } catch (Throwable throwable) {
            throw new SecurityException("Token不合法！");
        }
    }
}
