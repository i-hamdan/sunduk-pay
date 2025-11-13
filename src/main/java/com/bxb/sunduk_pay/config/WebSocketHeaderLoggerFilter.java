package com.bxb.sunduk_pay.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Enumeration;
import java.util.stream.Collectors;
import java.util.Collections;

@Component
public class WebSocketHeaderLoggerFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        // Only log the handshake request (not every request)
        if (request.getRequestURI().contains("/api/sunduk-service/ws")) {
            logger.info("==== WebSocket Handshake Headers ====");
            Enumeration<String> headerNames = request.getHeaderNames();
            if (headerNames != null) {
                for (String header : Collections.list(headerNames)) {
                    logger.info(header + ": " + request.getHeader(header));
                }
            }
            logger.info("=====================================");
        }

        filterChain.doFilter(request, response);
    }
}
