package com.bxb.sunduk_pay.config;

import jakarta.servlet.*;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Component;


import java.io.IOException;
import java.util.Arrays;

@Component
public class AuthenticationFilter implements Filter {
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {

        HttpServletRequest httpServletRequest=(HttpServletRequest) request;
        HttpServletResponse httpServletResponse=(HttpServletResponse) response;

        String path = httpServletRequest.getRequestURI();

        if (
                path.startsWith("/api/sunduk-service/oauth2/authorization") ||
                        path.startsWith("/api/sunduk-service/login/oauth2/code") ||
                        path.startsWith("/api/sunduk-service/custom-logout") ||
                        path.startsWith("/api/sunduk-service/custom-login")) {
            chain.doFilter(request, response);
            return;
        }

        HttpSession session = httpServletRequest.getSession(false);

        if (session == null || session.getAttribute("SPRING_SECURITY_CONTEXT") == null) {
            httpServletResponse.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("Your session has been expired or is no longer valid!\n Please login again.");
            return;
        }

        Cookie[] cookies = httpServletRequest.getCookies();
        if (cookies == null) {
            httpServletResponse.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("Cookies are missing!");
            return;
        }

        String sessionId = Arrays.stream(cookies)
                .filter(cookie -> "JSESSIONID".equals(cookie.getName()))
                .map(Cookie::getValue)
                .findFirst()
                .orElse(null);
        if (sessionId == null || sessionId.isBlank()) {

            httpServletResponse.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("Session Id is expired or invalid!");
            return;
        }

        Cookie newCookie = new Cookie("JSESSIONID", sessionId);
        httpServletResponse.addCookie(newCookie);

        chain.doFilter(request, response);
    }
}



