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
import java.util.List;

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

        HttpServletRequest httpReq = (HttpServletRequest) request;
        HttpServletResponse httpRes = (HttpServletResponse) response;

        final String path = httpReq.getRequestURI();
        final String method = httpReq.getMethod();
        log.info("Incoming request: {} {}", method, path);

        // 1. Skip filter for public endpoints
        List<String> excluded = securityProperties.getExcludePaths();
        if (excluded != null && excluded.stream().anyMatch(path::startsWith)) {
            log.debug("Skipping filter for public endpoint: {}", path);
            chain.doFilter(request, response);
            return;
        }

        // 2. Validate session
        HttpSession session = httpReq.getSession(false);
        if (session == null) {
            log.warn("No HTTP session found for path: {}", path);
            throw new InvalidUserException("Session expired or invalid");
        }
        if (session.getAttribute("SPRING_SECURITY_CONTEXT") == null) {
            log.warn("SPRING_SECURITY_CONTEXT not found in session for path: {}", path);
            throw new InvalidUserException("Session expired or invalid");
        }

        // 3. Validate cookies
        Cookie[] cookies = httpReq.getCookies();
        if (cookies == null || cookies.length == 0) {
            log.warn("Cookies are missing in the request for path: {}", path);
            throw new InvalidUserException("Cookies missing!");
        }

        String sessionId = Arrays.stream(cookies)
                .filter(cookie -> "JSESSIONID".equals(cookie.getName()))
                .map(Cookie::getValue)
                .findFirst()
                .orElse(null);

        if (sessionId == null || sessionId.isBlank()) {
            log.warn("JSESSIONID cookie not found or blank for path: {}", path);
            throw new InvalidUserException("Session ID invalid!");
        }

        // Optionally: you could also compare sessionId with session.getId() for extra safety:
        // if (!sessionId.equals(session.getId())) { ... throw ... }

        log.debug("Request passed AuthenticationFilter. User session validated.");
        chain.doFilter(request, response);
    }

    @Override
    public void init(FilterConfig filterConfig) {
        log.info("AuthenticationFilter initialised");
    }

    @Override
    public void destroy() {
        log.info("AuthenticationFilter destroyed");
    }
}
