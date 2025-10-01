package com.bxb.sunduk_pay.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.session.web.http.CookieSerializer;
import org.springframework.web.client.RestTemplate;

/**
 * Spring Security configuration for the application.
 */

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {
//    /** Custom authentication
//     *  filter for processing authentication logic. */
//    private final AuthenticationFilter filter;
//
    /**
     * Defines the Spring Security filter chain.
     *
     * @param http the {@link HttpSecurity} to modify
     * @return configured {@link SecurityFilterChain}
     * @throws Exception if an error occurs during configuration
     */


    @Bean
    public SecurityFilterChain filterChain(
            final HttpSecurity http) throws Exception {
        http.cors(Customizer.withDefaults())
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                        .anyRequest().permitAll()
                );
//                .authorizeHttpRequests(auth -> auth
//                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
//                        .requestMatchers("/custom-login").authenticated()
//                        .anyRequest().permitAll())
//                .oauth2Login(auth -> auth.defaultSuccessUrl("/custom-login"))
                //.oauth2Login(Customizer.withDefaults())
//                .sessionManagement(session -> session
//                        .maximumSessions(1)
//                        .maxSessionsPreventsLogin(true))
//                .logout(logout -> logout
//                        .logoutUrl("/logout")
//                        .invalidateHttpSession(true));
//                .addFilterBefore(filter,
//                        UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

//    /**
//     * Publishes session lifecycle events so Spring Security’s concurrent
//     * session control works.
//     *
//     * @return {@link HttpSessionEventPublisher} bean
//     */
//
//    @Bean
//    public HttpSessionEventPublisher httpSessionEventPublisher() {
//        return new HttpSessionEventPublisher();
//    }
//
//    /**
//     * Configure allowed origins/headers/methods for cross-origin requests.
//     *
//     * @return {@link CorsConfigurationSource} with allowed settings
//     */


//    @Bean
//    /* we will be not needing this method once moved to domain**/
//    public CorsConfigurationSource corsConfigurationSource() {
//        CorsConfiguration corsConfiguration = new CorsConfiguration();
//        corsConfiguration.setAllowedOrigins(List.of(
//                "http://localhost:5174",
//                "https://f6be298fe7d5.ngrok-free.app",
//                "http://localhost:5173"
//
//        ));
//        corsConfiguration.setAllowedMethods(
//                List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
//        corsConfiguration.setAllowCredentials(true);
//        corsConfiguration.setAllowedHeaders(List.of("*"));
//        UrlBasedCorsConfigurationSource source
//                = new UrlBasedCorsConfigurationSource();
//        source.registerCorsConfiguration(
//                "/**", corsConfiguration);
//        return source;
//    }

    /**
     * Customize session cookie behaviour.
     *
     * @return {@link CookieSerializer} with configured settings
     */

//    @Bean
//    public CookieSerializer cookieSerializer() {
//        DefaultCookieSerializer serializer = new DefaultCookieSerializer();
//        serializer.setSameSite("None");
//        serializer.setUseSecureCookie(true); // if using https
//        serializer.setCookieName("JSESSIONID");
//        return serializer;
//    }

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
