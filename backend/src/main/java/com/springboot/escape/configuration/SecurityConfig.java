package com.springboot.escape.configuration;

import com.springboot.escape.configuration.security.CustomAccessDeniedHandler;
import com.springboot.escape.configuration.security.CustomAuthenticationEntryPoint;
import com.springboot.escape.configuration.security.JwtAuthenticationFilter;
import com.springboot.escape.configuration.security.RequestMatcherHolder;
import com.springboot.escape.domain.user.constant.RoleEnum;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.web.cors.CorsConfigurationSource;

@Configuration
public class SecurityConfig {

    private final Logger LOGGER = LoggerFactory.getLogger(CustomAccessDeniedHandler.class);
    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final RequestMatcherHolder requestMatcherHolder;
    private final CorsConfigurationSource corsConfigurationSource;

    public SecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter, RequestMatcherHolder requestMatcherHolder, CorsConfigurationSource corsConfigurationSource) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
        this.requestMatcherHolder = requestMatcherHolder;
        this.corsConfigurationSource = corsConfigurationSource;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity httpSecurity) throws Exception {

        RequestMatcher matcher = requestMatcherHolder.getRequestMatchersByMinRole(null);
        LOGGER.info("permitAll matcher: {}", matcher);
        httpSecurity
                .csrf((csrfConfig)->
                        csrfConfig.disable())
                .cors(cors -> cors.configurationSource(corsConfigurationSource))
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .httpBasic(httpbc -> httpbc
                        .disable())
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(requestMatcherHolder.getRequestMatchersByMinRole(null)).permitAll()
                        .requestMatchers(requestMatcherHolder.getRequestMatchersByMinRole(RoleEnum.ADMIN)).hasAuthority(RoleEnum.ADMIN.name())
                        .requestMatchers(requestMatcherHolder.getRequestMatchersByMinRole(RoleEnum.USER)).hasAnyAuthority(RoleEnum.ADMIN.name(), RoleEnum.USER.name())
                        .anyRequest().hasAuthority(RoleEnum.ADMIN.name()))
                .exceptionHandling(ex -> {
                    ex.accessDeniedHandler(new CustomAccessDeniedHandler());
                    ex.authenticationEntryPoint(new CustomAuthenticationEntryPoint());})
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
        return httpSecurity.build();
    }
}
