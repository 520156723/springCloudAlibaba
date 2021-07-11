package per.hqd.contentcenter.sentineltest;

import com.alibaba.csp.sentinel.adapter.servlet.callback.UrlBlockHandler;
import com.alibaba.csp.sentinel.slots.block.BlockException;
import com.alibaba.csp.sentinel.slots.block.authority.AuthorityException;
import com.alibaba.csp.sentinel.slots.block.degrade.DegradeException;
import com.alibaba.csp.sentinel.slots.block.flow.FlowException;
import com.alibaba.csp.sentinel.slots.block.flow.param.ParamFlowException;
import com.alibaba.csp.sentinel.slots.system.SystemBlockException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 处理被sentinel流控降级等异常
 * 实现UrlBlockHandler
 * */
@Component
public class MyUrlBlockHandler implements UrlBlockHandler {
    @Override
    public void blocked(HttpServletRequest request, HttpServletResponse response, BlockException ex) throws IOException {
        ErrorMsg msg = null;
        if(ex instanceof FlowException){
            msg = ErrorMsg.builder()
                    .status(500)
                    .msg("被限流了")
                    .build();
        } else if (ex instanceof DegradeException) {
            msg = ErrorMsg.builder()
                    .status(500)
                    .msg("被降级了")
                    .build();
        }else if (ex instanceof AuthorityException) {
            msg = ErrorMsg.builder()
                    .status(500)
                    .msg("授权规则不通过")
                    .build();
        }else if (ex instanceof ParamFlowException) {
            msg = ErrorMsg.builder()
                    .status(500)
                    .msg("热点参数限流")
                    .build();
        }else if (ex instanceof SystemBlockException) {
            msg = ErrorMsg.builder()
                    .status(500)
                    .msg("系统规则不满足")
                    .build();
        }
        // 填充response
        response.setStatus(500);
        response.setCharacterEncoding("utf-8");
        response.setHeader("Content-Type", "application/json;character=utf-8");//响应是json
        response.setContentType("application/json;character=utf-8");
        // spring mvc的java转json jackson
        new ObjectMapper()
                .writeValue(
                        response.getWriter(),
                        msg
                );

    }
}

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
class ErrorMsg{
    private Integer status;
    private String msg;
}