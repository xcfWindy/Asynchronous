package com.example.gateway.filter;

import com.example.gateway.constants.MdcConstant;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.time.Instant;
import java.util.UUID;

@Component
@Slf4j
public class MDCGatewayFilter implements GlobalFilter, Ordered {

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();

        // 获取或生成追踪ID
        String traceId = getOrCreateTraceId(request);
        String spanId = generateSpanId();

        // 设置MDC上下文
        MDC.put(MdcConstant.TRACE_ID.getKey(), traceId);
        MDC.put(MdcConstant.SPAN_ID.getKey(), spanId);
        MDC.put(MdcConstant.REQUEST_URI.getKey(), request.getURI().getPath());

        log.info("开始处理请求: {} {}", request.getMethod(), request.getURI());

        ServerWebExchange decoratedExchange = decorateExchange(exchange, traceId, spanId);

        Instant start = Instant.now();
        return chain.filter(decoratedExchange)
                .doFinally(signalType -> {
                    Instant end = Instant.now();
                    long duration = Duration.between(start, end).toMillis();
                    log.info("请求处理完成，耗时: {} ms", duration);
                    MDC.clear();
                })
                .doOnError(throwable -> {
                    log.error("请求处理出错", throwable);
                    MDC.clear();
                });
    }

    private String getOrCreateTraceId(ServerHttpRequest request) {
        String traceId = request.getHeaders().getFirst(MdcConstant.X_B3_TRACEID.getKey());
        if (traceId == null || traceId.isEmpty()) {
            return UUID.randomUUID().toString().replace("-", "").substring(0, 16);
        }
        return traceId;
    }

    private String generateSpanId() {
        return UUID.randomUUID().toString().replace("-", "").substring(0, 16);
    }

    private ServerWebExchange decorateExchange(ServerWebExchange exchange, String traceId, String spanId) {
        ServerHttpRequest modifiedRequest = exchange.getRequest().mutate()
                .header(MdcConstant.X_B3_TRACEID.getKey(), traceId)
                .header(MdcConstant.X_B3_SPANID.getKey(), spanId)
                .build();

        return exchange.mutate().request(modifiedRequest).build();
    }

    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE + 1;
    }
}
/**
 * ## 分布式系统 `traceId` 整体流转流程
 *
 * ### 1. `traceId` 生成阶段
 * - **入口服务**：当请求首次进入系统时，由网关或第一个服务生成唯一的 `traceId`
 * - **前端生成**：也可由客户端（浏览器/移动应用）预先生成并传递
 * - **格式规范**：通常为16位或32位十六进制字符串

 * ### 2. 请求头传播机制
 * - **HTTP头部传递**：通过标准头部如 `x-b3-traceid` 在服务间传递
 * - **网关处理**：`gateway` 服务检查并确保 `traceId` 存在，不存在则生成
 * - **透明传输**：各中间件和服务保持 `traceId` 不变地向下游传递
 *
 * ### 3. 服务间调用传播
 * - **同步调用**：REST API、gRPC等调用时自动携带 `traceId` 头部
 * - **异步消息**：通过消息队列（如 Kafka、RabbitMQ）传递时需显式处理
 * - **数据库操作**：在日志记录中包含 `traceId` 便于问题追踪
 *
 * ### 4. 追踪数据收集
 * - **Span生成**：每个服务节点生成自己的 `spanId` 并关联到 `traceId`
 * - **数据上报**：通过 `zipkin-reporter-brave` 等组件上报到追踪系统
 * - **链路构建**：追踪系统根据 `traceId` 聚合所有相关 `span` 构建完整调用链
 *
 * ### 5. 数据存储与展示
 * - **持久化存储**：追踪数据存储在 Zipkin、Jaeger 等系统中
 * - **可视化展示**：通过追踪系统 UI 展示完整的请求调用链路
 * - **查询分析**：支持按 `traceId` 查询特定请求的完整执行轨迹
 */