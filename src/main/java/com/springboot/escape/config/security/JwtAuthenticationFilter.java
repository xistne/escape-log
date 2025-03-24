package com.springboot.escape.config.security;


import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final Logger LOGGER = LoggerFactory.getLogger(JwtAuthenticationFilter.class);
    private final JwtTokenProvider jwtTokenProvider;

    public JwtAuthenticationFilter(JwtTokenProvider jwtTokenProvider) {
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        try {
            String accessToken = jwtTokenProvider.resolveAccessToken(request);
            LOGGER.info("[doFilterInternal] token 값 추출 완료. token : {}", accessToken);
            if (accessToken == null) throw new JwtException("access token is null");
            LOGGER.info("[doFilterInternal] token 값 유효성 체크 시작");
            if (jwtTokenProvider.isValidToken(accessToken) && !jwtTokenProvider.isTokenBlackList(accessToken)) {
                Authentication authentication = jwtTokenProvider.getAuthentication(accessToken);
                SecurityContextHolder.getContext().setAuthentication(authentication);
                LOGGER.info("[doFilterInternal] token 값 유효성 체크 완료");
            }
        } catch (JwtException | UsernameNotFoundException ex) {
            LOGGER.info("Failed to authorize/authenticate with JWT due to " + ex.getMessage());
        }
        filterChain.doFilter(request, response);
    }
}