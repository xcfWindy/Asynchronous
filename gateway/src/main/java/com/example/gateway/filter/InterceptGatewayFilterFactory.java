package com.example.gateway.filter;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.Arrays;
import java.util.List;

/**
 * 应用到单个路由或者一个分组的路由上（需要在配置文件中配置）
 * 名称必须是xxxGatewayFilterFactory形式
 */
@Component
@Slf4j
public class InterceptGatewayFilterFactory extends AbstractGatewayFilterFactory<InterceptGatewayFilterFactory.Config> {

    //构造函数，加载Config
    public InterceptGatewayFilterFactory() {
        //固定写法
        super(InterceptGatewayFilterFactory.Config.class);
        log.info("Loaded GatewayFilterFactory [Authorize]");
    }

    //读取配置文件中的参数 赋值到 配置类中
    @Override
    public List<String> shortcutFieldOrder() {
        //Config.enabled
        return Arrays.asList("message");
    }

    @Override
    public GatewayFilter apply(InterceptGatewayFilterFactory.Config config) {
        return (exchange, chain) -> {

            ServerHttpRequest serverHttpRequest = exchange.getRequest();
            ServerHttpResponse serverHttpResponse = exchange.getResponse();
            HttpMethod method = serverHttpRequest.getMethod();
            String contentType = serverHttpRequest.getHeaders().getFirst(HttpHeaders.CONTENT_TYPE);

            if (!(method == HttpMethod.POST || method == HttpMethod.GET)
                    || (contentType == null || !contentType.contains(MediaType.APPLICATION_JSON_VALUE))) {
                log.warn("请求方法或参数格式错误,uri={};method={};Content-Type={}", serverHttpRequest.getURI(), method.name(), contentType);
                //使用 DataBuffer 包装字节数据，这是 Spring WebFlux 响应式编程的标准数据载体
                DataBuffer dataBuffer = serverHttpResponse.bufferFactory().wrap(config.message.getBytes());
                // 将内容类型字符串转换为数据缓冲区，并写入HTTP响应
                serverHttpResponse.setStatusCode(HttpStatus.BAD_REQUEST);
                return serverHttpResponse.writeWith(Mono.just(dataBuffer));

            }

            return chain.filter(exchange);
        };
    }

    /**
     * 支持从配置文件中读取参数并自动装配到配置类中
     */
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Config {
        // 控制是否开启认证
        private String message;
    }
}