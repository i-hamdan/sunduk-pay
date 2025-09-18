package com.bxb.sunduk_pay.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.session.HttpSessionEventPublisher;
import org.springframework.session.web.http.CookieSerializer;
import org.springframework.session.web.http.DefaultCookieSerializer;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

/**
 * Spring Security configuration for the application.
 */
@Configuration
@EnableWebSecurity
public  class SecurityConfig {

    /**
     * Custom authentication filter injected into the Spring Security chain.
     */
    private final AuthenticationFilter authenticationFilter;

    /**
     * Instantiates SecurityConfig with the provided authentication filter.
     *
     * @param authenticationFilter custom authentication filter
     */
    public SecurityConfig(final AuthenticationFilter authenticationFilter) {
        this.authenticationFilter = authenticationFilter;
    }

    /**
     * Defines the Spring Security filter chain.
     *
     * @param http the {@link HttpSecurity} to modify
     * @return configured {@link SecurityFilterChain}
     * @throws Exception if an error occurs during configuration
     */
    @Bean
    public SecurityFilterChain securityFilterChain(final HttpSecurity http)
            throws Exception {

        http
                // enable CORS with your bean below
                .cors(Customizer.withDefaults())
                // disable CSRF for APIs
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> auth
                        // allow pre-flight CORS requests
                        .requestMatchers(HttpMethod.OPTIONS, "/**")
                        .permitAll()
                        // protect login endpoint
                        .requestMatchers("/custom-login")
                        .authenticated()
                        // all other endpoints open (adjust as needed)
                        .anyRequest()
                        .permitAll())
                // OAuth2 login
                .oauth2Login(oauth ->
                        oauth.defaultSuccessUrl("/custom-login", true))
                .sessionManagement(session ->
                        session.maximumSessions(1)
                                .maxSessionsPreventsLogin(true))
                .logout(logout ->
                        logout.logoutUrl("/logout")
                                .invalidateHttpSession(true)
                                .deleteCookies("JSESSIONID"))
                // add your custom filter before Spring Security's authentication filter
                .addFilterBefore(authenticationFilter,
                        UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    /**
     * Publishes session lifecycle events so Spring Security’s concurrent
     * session control works.
     *
     * @return {@link HttpSessionEventPublisher} bean
     */
    @Bean
    public HttpSessionEventPublisher httpSessionEventPublisher() {
        return new HttpSessionEventPublisher();
    }

    /**
     * Configure allowed origins/headers/methods for cross-origin requests.
     *
     * @return {@link CorsConfigurationSource} with allowed settings
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration cors = new CorsConfiguration();
        cors.setAllowedOrigins(List.of(
                "http://localhost:5174",
                "https://f6be298fe7d5.ngrok-free.app",
                "http://localhost:5173"
        ));
        cors.setAllowedMethods(
                List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        cors.setAllowedHeaders(List.of("*"));
        cors.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source =
                new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", cors);
        return source;
    }

    /**
     * Customize session cookie behaviour.
     *
     * @return {@link CookieSerializer} with configured settings
     */
    @Bean
    public CookieSerializer cookieSerializer() {
        DefaultCookieSerializer serializer =
                new DefaultCookieSerializer();
        serializer.setCookieName("JSESSIONID");
        serializer.setSameSite("None");         // needed for cross-site cookies
        serializer.setUseSecureCookie(true);    // true if you’re using HTTPS
        return serializer;
    }

    /**
     * Provides a RestTemplate bean for making REST API calls.
     *
     * @return new {@link RestTemplate} instance
     */
    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}
