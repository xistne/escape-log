package com.springboot.escape.config.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
public class SecurityConfiguration {
    private final JwtTokenProvider jwtTokenProvider;

    public SecurityConfiguration(JwtTokenProvider jwtTokenProvider) {
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity httpSecurity) throws Exception {
        httpSecurity
                .csrf((csrfConfig)->
                        csrfConfig.disable())
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .httpBasic(httpbc -> httpbc
                        .disable())
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/**exception**").permitAll()
                        .requestMatchers(
                                "/v3/api-docs/**",  // Swagger 3.x API 문서 엔드포인트
                                "/swagger-ui/**",   // Swagger UI 기본 경로
                                "/swagger-resources/**",
                                "/webjars/**",
                                "/sign-api/exception"
                        ).permitAll()
                        .anyRequest().hasRole("ADMIN"))
                .exceptionHandling(ex -> {
                    ex.accessDeniedHandler(new CustomAccessDeniedHandler());
                    ex.authenticationEntryPoint(new CustomAuthenticationEntryPoint());})
                .addFilterBefore(new JwtAuthenticationFilter(jwtTokenProvider), UsernamePasswordAuthenticationFilter.class);
        return httpSecurity.build();
    }

    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        return (web) -> web.ignoring().requestMatchers("/v2/api-docs",
                "/swagger-resources/**", "/swagger-ui.html", "/webjars/**",
                "/swagger/**");
    }

}
