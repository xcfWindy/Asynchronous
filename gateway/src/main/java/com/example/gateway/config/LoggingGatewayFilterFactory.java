package com.example.gateway.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
public class LoggingGatewayFilterFactory extends AbstractGatewayFilterFactory<LoggingGatewayFilterFactory.Config> {

    private static final Logger log = LoggerFactory.getLogger(LoggingGatewayFilterFactory.class);

    public LoggingGatewayFilterFactory() {
        super(Config.class);
    }

    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {
            ServerHttpRequest request = exchange.getRequest();

            if (config.isPreLogger()) {
                log.info("请求前日志: {} {}", request.getMethod(), request.getURI());
            }

            return chain.filter(exchange).then(
                    Mono.fromRunnable(() -> {
                        if (config.isPostLogger()) {
                            log.info("响应后日志: {} {}", request.getMethod(), request.getURI());
                        }
                    })
            );
        };
    }

    public static class Config {
        private boolean preLogger = true;
        private boolean postLogger = true;

        // getters and setters
        public boolean isPreLogger() { return preLogger; }
        public void setPreLogger(boolean preLogger) { this.preLogger = preLogger; }
        public boolean isPostLogger() { return postLogger; }
        public void setPostLogger(boolean postLogger) { this.postLogger = postLogger; }
    }
}
