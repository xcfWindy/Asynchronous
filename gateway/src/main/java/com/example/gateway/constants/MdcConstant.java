package com.example.gateway.constants;

public enum MdcConstant {

    TRACE_ID("traceId", "追踪ID"),
    SPAN_ID("spanId", "跨度ID"),
    REQUEST_URI("uri", "请求URI"),
    REQUEST_METHOD("method", "请求方法"),
    USER_ID("userId", "用户ID"),
    SESSION_ID("sessionId", "会话ID"),
    REQUEST_START_TIME("requestStartTime", "请求开始时间"),
    /** Zipkin追踪ID */
    X_B3_TRACEID("x-b3-traceid", "Zipkin追踪ID"),
    /** Zipkin跨度ID */
    X_B3_SPANID("x-b3-spanid", "Zipkin跨度ID"),
    /** Zipkin父跨度ID */
    X_B3_PARENT_SPAN_ID("x-b3-parentspanid", "Zipkin父跨度ID"),
    /** 采样标志 */
    X_B3_SAMPLED("x-b3-sampled", "采样标志"),
    /** 请求ID */
    X_REQUEST_ID("x-request-id", "请求ID"),
    /** 用户令牌 */
    AUTHORIZATION("Authorization", "用户令牌");

    private final String key;
    private final String description;

    MdcConstant(String key, String description) {
        this.key = key;
        this.description = description;
    }

    public String getKey() {
        return key;
    }

    public String getDescription() {
        return description;
    }
}
