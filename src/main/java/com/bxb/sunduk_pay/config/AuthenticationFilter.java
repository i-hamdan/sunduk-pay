package com.bxb.sunduk_pay.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;

@Component
public class AuthenticationFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {


        String path = request.getRequestURI();

        if (
                path.startsWith("/api/sunduk-service/oauth2/authorization") ||
                        path.startsWith("/api/sunduk-service/login/oauth2/code") ||
                        path.startsWith("/api/sunduk-service/custom-logout") ||
                        path.startsWith("/api/sunduk-service/custom-login")) {
            filterChain.doFilter(request, response);
            return;
        }

        HttpSession session = request.getSession(false);

        if (session == null || session.getAttribute("SPRING_SECURITY_CONTEXT") == null) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("Your session has been expired or is no longer valid!\n Please login again.");
            return;
        }

        Cookie[] cookies = request.getCookies();
        if (cookies == null) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("Cookies are missing!");
            return;
        }

        String sessionId = Arrays.stream(cookies)
                .filter(cookie -> "JSESSIONID".equals(cookie.getName()))
                .map(Cookie::getValue)
                .findFirst()
                .orElse(null);
        if (sessionId == null || sessionId.isBlank()) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("Session Id is expired or invalid!");
            return;
        }


        filterChain.doFilter(request, response);
    }


}



