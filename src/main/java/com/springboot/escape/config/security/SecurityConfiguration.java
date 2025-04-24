package com.springboot.escape.config.security;

import com.springboot.escape.data.entity.RoleEnum;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.RequestMatcher;

@Configuration
public class SecurityConfiguration {

    private final Logger LOGGER = LoggerFactory.getLogger(CustomAccessDeniedHandler.class);
    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final RequestMatcherHolder requestMatcherHolder;

    public SecurityConfiguration(JwtAuthenticationFilter jwtAuthenticationFilter, RequestMatcherHolder requestMatcherHolder) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
        this.requestMatcherHolder = requestMatcherHolder;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity httpSecurity) throws Exception {

        RequestMatcher matcher = requestMatcherHolder.getRequestMatchersByMinRole(null);
        LOGGER.info("permitAll matcher: {}", matcher);
        httpSecurity
                .csrf((csrfConfig)->
                        csrfConfig.disable())
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .httpBasic(httpbc -> httpbc
                        .disable())
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(requestMatcherHolder.getRequestMatchersByMinRole(null)).permitAll()
                        .requestMatchers(requestMatcherHolder.getRequestMatchersByMinRole(RoleEnum.ADMIN)).hasAuthority(RoleEnum.ADMIN.getAuthority())
                        .requestMatchers(requestMatcherHolder.getRequestMatchersByMinRole(RoleEnum.USER)).hasAnyAuthority(RoleEnum.ADMIN.getAuthority(), RoleEnum.USER.getAuthority())
                        .anyRequest().hasAuthority(RoleEnum.ADMIN.name()))
                .exceptionHandling(ex -> {
                    ex.accessDeniedHandler(new CustomAccessDeniedHandler());
                    ex.authenticationEntryPoint(new CustomAuthenticationEntryPoint());})
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
        return httpSecurity.build();
    }
}
