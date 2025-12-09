package com.example.future.filter;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@Slf4j
public class MDCFilter implements Filter {
    
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        // 初始化操作（如果需要）
    }
    
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) 
            throws IOException, ServletException {
        
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        
        try {
            // 从请求头中获取追踪信息
            String traceId = httpRequest.getHeader("x-b3-traceid");
            String spanId = httpRequest.getHeader("x-b3-spanid");
            
            // 设置MDC上下文
            if (traceId != null && !traceId.isEmpty()) {
                MDC.put("traceId", traceId);
            }
            if (spanId != null && !spanId.isEmpty()) {
                MDC.put("spanId", spanId);
            }
            
            // 继续执行过滤器链
            chain.doFilter(request, response);
        } finally {
            // 清理MDC上下文
            MDC.clear();
        }
    }
    
    @Override
    public void destroy() {
        // 销毁操作（如果需要）
    }
}
