package per.hqd.contentcenter.sentineltest;

import com.alibaba.csp.sentinel.adapter.servlet.callback.RequestOriginParser;
import org.apache.commons.lang.StringUtils;

import javax.servlet.http.HttpServletRequest;

//@Component
public class MyRequestOriginParser implements RequestOriginParser {
    @Override
    public String parseOrigin(HttpServletRequest request) {
        // 从请求中获得名为origin的参数并返回，如果获取不到就抛异常
        String origin = request.getParameter("origin");
        if (StringUtils.isBlank(origin)) {
            throw new IllegalArgumentException("origin must be specified");
        }
        return origin;
    }
}
