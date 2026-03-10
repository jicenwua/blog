package com.xcz.gateway.filter;

import com.xcz.common.core.utils.ServletUtils;
import lombok.Getter;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * 黑名单过滤器
 */
@Component
@RefreshScope
public class BlackListUrlFilter extends AbstractGatewayFilterFactory<BlackListUrlFilter.Config> {
    public BlackListUrlFilter() {
        super(Config.class);
    }

    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {
            //获取请求路径
            String url = exchange.getRequest().getURI().getPath();
            //判断是否在黑名单中
            if (config.matchBlacklist(url)) {
                return ServletUtils.webFluxResponseWriter(exchange.getResponse(), "请求地址不允许访问");
            }

            return chain.filter(exchange);
        };
    }
    /**
     * 过滤器配置类
     */
    public static class Config {
        @Getter
        private List<String> blacklistUrl;

        // 存储编译后的正则表达式对象，用于提高匹配性能
        private List<Pattern> blacklistUrlPattern = new ArrayList<>();

        /**
         * 判断是否在黑名单中
         *
         * @param url 请求路径
         * @return 是否在黑名单中
         */
        public boolean matchBlacklist(String url) {
            return !blacklistUrlPattern.isEmpty() && blacklistUrlPattern.stream().anyMatch(p -> p.matcher(url).find());
        }

        public void setBlacklistUrl(List<String> blacklistUrl) {
            this.blacklistUrl = blacklistUrl;
            this.blacklistUrlPattern.clear();
            this.blacklistUrl.forEach(url -> {
                this.blacklistUrlPattern.add(Pattern.compile(url.replaceAll("\\*\\*", "(.*?)"), Pattern.CASE_INSENSITIVE));
            });
        }
    }
}
