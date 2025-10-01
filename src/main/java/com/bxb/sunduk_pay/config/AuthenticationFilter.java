package com.bxb.sunduk_pay.config;

import jakarta.servlet.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

import java.io.IOException;

/**
 * Filter for authenticating requests by validating session and cookies.
 */
@Log4j2
//@Component
@RequiredArgsConstructor
public class AuthenticationFilter implements Filter {
//  /** Security properties containing excluded
//   *  paths and other configs. **/
//    private final SundukSecurityProperties securityProperties;

        /**
         * Performs authentication checks on incoming requests.
         *
         * @param request  incoming servlet request
         * @param response servlet response
         * @param chain    filter chain
         * @throws IOException      if an input or output error occurs
         * @throws ServletException if a servlet error occurs
         */
    @Override
    public void doFilter(final ServletRequest request,
                         final ServletResponse response,
                         final FilterChain chain)
            throws IOException, ServletException {

//HttpServletRequest httpServletRequest = (HttpServletRequest) request;
//HttpServletResponse httpServletResponse = (HttpServletResponse) response;
//
//        String path = httpServletRequest.getRequestURI();
//        log.info("Incoming request: {} {}",
//                httpServletRequest.getMethod(), path);
//
//        // Skip filter for public endpoints from properties
//        if (securityProperties.getExcludePaths() != null
//                && securityProperties
//                        .getExcludePaths()
//                        .stream()
//                        .anyMatch(path::startsWith)) {
//            log.debug("Skipping filter for public endpoint: {}", path);
//            chain.doFilter(request, response);
//            return;
//        }
//
//        // Validate session
//        HttpSession session = httpServletRequest.getSession(false);
//        if (session == null || session.getAttribute(
//                "SPRING_SECURITY_CONTEXT") == null) {
//            log.error("Session is null or invalid for path: {}", path);
//            throw new InvalidUserException("Session expired or invalid");
//        }
//
//        // Validate cookies (no rewriting)
//        Cookie[] cookies = httpServletRequest.getCookies();
//        if (cookies == null) {
//            log.error("Cookies are missing in the request.");
//            throw new InvalidUserException("Cookies missing!");
//        }
//
//        String sessionId = Arrays.stream(cookies)
//                .filter(cookie -> "JSESSIONID".equals(cookie.getName()))
//                .map(Cookie::getValue)
//                .findFirst()
//                .orElse(null);
//
//        if (sessionId == null || sessionId.isBlank()) {
//            log.error("JSESSIONID not found or is blank.");
//            throw new InvalidUserException("Session ID invalid!");
//        }
//
//        log.info("Request passed filter and is authorized.");
        chain.doFilter(request, response);
    }
        /**
         * Initializes the filter.
         *
         * @param filterConfig filter configuration
         */
        @Override
        public void init(final FilterConfig filterConfig) {
            log.info("AuthenticationFilter initialised");
        }

        /**
         * Destroys the filter.
         */
        @Override
        public void destroy() {
            log.info("AuthenticationFilter destroyed");
        }

    }
