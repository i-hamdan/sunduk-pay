package com.bxb.sunduk_pay.config;

import com.bxb.sunduk_pay.exception.InvalidUserException;
import jakarta.servlet.*;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Arrays;

@Log4j2
@Component
public class AuthenticationFilter implements Filter {
    private final SundukSecurityProperties securityProperties;

    public AuthenticationFilter(SundukSecurityProperties securityProperties) {
        this.securityProperties = securityProperties;
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest httpServletRequest = (HttpServletRequest) request;
        HttpServletResponse httpServletResponse = (HttpServletResponse) response;

        String path = httpServletRequest.getRequestURI();
        log.info("Incoming request: {} {}", httpServletRequest.getMethod(), path);

        // Skip filter for public endpoints from properties
        if (securityProperties.getExcludePaths() != null &&
                securityProperties.getExcludePaths().stream().anyMatch(path::startsWith)) {
            log.debug("Skipping filter for public endpoint: {}", path);
            chain.doFilter(request, response);
            return;
        }

        // Validate session
        HttpSession session = httpServletRequest.getSession(false);
        if (session == null || session.getAttribute("SPRING_SECURITY_CONTEXT") == null) {
            log.error("Session is null or invalid for path: {}", path);
            throw new InvalidUserException("Session expired or invalid");
        }

        // Validate cookies (no rewriting)
        Cookie[] cookies = httpServletRequest.getCookies();
        if (cookies == null) {
            log.error("Cookies are missing in the request.");
            throw new InvalidUserException("Cookies missing!");
        }

        String sessionId = Arrays.stream(cookies)
                .filter(cookie -> "JSESSIONID".equals(cookie.getName()))
                .map(Cookie::getValue)
                .findFirst()
                .orElse(null);

        if (sessionId == null || sessionId.isBlank()) {
            log.error("JSESSIONID not found or is blank.");
            throw new InvalidUserException("Session ID invalid!");
        }

        log.info("Request passed filter and is authorized.");
        chain.doFilter(request, response);
    }
}