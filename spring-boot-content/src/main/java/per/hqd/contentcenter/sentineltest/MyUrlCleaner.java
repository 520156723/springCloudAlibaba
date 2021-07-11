package per.hqd.contentcenter.sentineltest;

import com.alibaba.csp.sentinel.adapter.servlet.callback.UrlCleaner;
import org.apache.commons.lang.math.NumberUtils;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Component
public class MyUrlCleaner implements UrlCleaner {
    @Override
    public String clean(String originUrl) {//转化url
        // 让share/1 和 share/2 的返回值相同 -> 都作为share/{number}
        String[] split = originUrl.split("/");
        return Arrays.stream(split)
                .map(param -> NumberUtils.isNumber(param) ? "{number}" : param)
                .reduce((a, b) -> a + "/" + b)//把每个字符串用/拼接起来
                .orElse("");
    }
}
