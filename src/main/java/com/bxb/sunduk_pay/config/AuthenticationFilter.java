package com.bxb.sunduk_pay.config;
import com.bxb.sunduk_pay.exception.InvalidUserException;
import jakarta.servlet.*;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;


import java.io.IOException;
import java.util.Arrays;
import java.util.List;

@Component
public class AuthenticationFilter implements Filter {
    private final SundukSecurityProperties securityProperties;


    private static final Logger logger= LoggerFactory.getLogger(AuthenticationFilter.class);

    public AuthenticationFilter(SundukSecurityProperties securityProperties) {
        this.securityProperties = securityProperties;
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {

        HttpServletRequest httpServletRequest=(HttpServletRequest) request;
        HttpServletResponse httpServletResponse=(HttpServletResponse) response;

        String path = httpServletRequest.getRequestURI();

        if (securityProperties.getExcludePaths() != null &&
                securityProperties.getExcludePaths().stream().anyMatch(path::startsWith)) {
            chain.doFilter(request, response);
            return;
        }


        HttpSession session = httpServletRequest.getSession(false);

        if (session == null || session.getAttribute("SPRING_SECURITY_CONTEXT") == null) {
            logger.error("Session is invalid");
           throw new InvalidUserException("Session is invalid");

        }

        setSessionId(httpServletRequest, httpServletResponse);

        chain.doFilter(request, response);
    }





    private static void setSessionId(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) {
        Cookie[] cookies = httpServletRequest.getCookies();
        if (cookies == null) {
            logger.error("Cookies are invalid");
            throw new InvalidUserException("Session is invalid");
        }

        String sessionId = Arrays.stream(cookies)
                .filter(cookie -> "JSESSIONID".equals(cookie.getName()))
                .map(Cookie::getValue)
                .findFirst()
                .orElse(null);
        if (sessionId == null || sessionId.isBlank()) {

            logger.error("Session are invalid");
            throw new InvalidUserException("Session is invalid");
        }

        Cookie newCookie = new Cookie("JSESSIONID", sessionId);
        httpServletResponse.addCookie(newCookie);
    }
}



