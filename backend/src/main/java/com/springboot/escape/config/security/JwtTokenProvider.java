package com.springboot.escape.config.security;

import com.springboot.escape.domain.user.constant.RoleEnum;
import com.springboot.escape.domain.auth.exception.AuthErrorCode;
import com.springboot.escape.exception.ErrorCode;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Component
@RequiredArgsConstructor
public class JwtTokenProvider {

    private final Logger LOGGER = LoggerFactory.getLogger(JwtTokenProvider.class);
    private final UserDetailsService userDetailsService;
    private final RedisTemplate<String, String> redisTemplate;

    @Value("${jwt.secret}")
    private String secretKey;
    private SecretKey key;
    @Value("${jwt.accessTokenExpiration}")
    private long accessTokenExpiration;
    @Value("${jwt.refreshTokenExpiration}")
    private long refreshTokenExpiration;

    final static public String ACCESS_TOKEN_NAME = "accessToken";
    final static public String REFRESH_TOKEN_NAME = "refreshToken";

    @PostConstruct
    protected void init() {
        LOGGER.info("[init] JwtTokenProvider 내 secretKey 초기화 시작");
        if (secretKey == null || secretKey.isBlank()) {
            throw new IllegalStateException("secretKey가 설정되지 않았습니다!");
        }
        key = Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8)); // DESC : HMAC SHA 키 생성
        LOGGER.info("[init] JwtTokenProvider 내 secretKey 초기화 완료");
    }

    public String createAccessToken(String email, RoleEnum role) {
        LOGGER.info("[createToken] 토큰 생성 시작");
        Date now = new Date();
        Date expiredDate = new Date(now.getTime() + accessTokenExpiration);

        ClaimsBuilder claims = Jwts.claims().subject(email);
        claims.add("role", role.name());

        String token = Jwts.builder()
                .claims(claims.build())
                .issuedAt(now)
                .expiration(expiredDate)
                .signWith(key)
                .compact();

        LOGGER.info("[createToken] 토큰 생성 완료");
        return token;
    }

    public String createRefreshToken(String email) {
        Date now = new Date();
        Date expiredDate = new Date(now.getTime() + refreshTokenExpiration);
        ClaimsBuilder claims = Jwts.claims().subject(email);

        String refreshToken = Jwts.builder()
                .claims(claims.build())
                .issuedAt(now)
                .expiration(expiredDate)
                .signWith(key)
                .compact();
        redisTemplate.opsForValue().set(
                "refresh:"+email,
                refreshToken,
                refreshTokenExpiration,
                TimeUnit.MILLISECONDS
        );
        return refreshToken;
    }

    public Authentication getAuthentication(String accessToken) {
        LOGGER.info("[getAuthentication] 토큰 인증 정보 조회 시작");
        String email = this.getUserEmailFromAccessToken(accessToken);
        String role = this.getUserEmailFromRole(accessToken);
        GrantedAuthority authority = new SimpleGrantedAuthority(role);
        LOGGER.info("[getAuthentication] 토큰 인증 정보 조회 완료, email : {}, role : {}",
                email, role);
        return new UsernamePasswordAuthenticationToken(email, null, List.of(authority));
    }

    public String getUserEmailFromAccessToken(String accessToken) {
        return this.getUserEmailFromToken(accessToken, AuthErrorCode.INVALID_ACCESS_TOKEN);
    }
    public String getUserEmailFromRefreshToken(String refreshToken) {
        return this.getUserEmailFromToken(refreshToken, AuthErrorCode.INVALID_REFRESH_TOKEN);
    }

    private String getUserEmailFromToken(String token, ErrorCode errorCode) {
        LOGGER.info("[getUsername] 토큰 기반 회원 구별 정보 추출");
        try {
            String info = Jwts.parser().verifyWith(key).build().parseSignedClaims(token).getPayload().getSubject();
            LOGGER.info("[getUsername] 토큰 기반 회원 구별 정보 추출 완료, info : {}", info);
            return info;
        } catch (JwtException e) {
            LOGGER.info("[getUsername] 토큰 기반 회원 구별 정보 추출 실패, error : {}", e.getMessage());
            throw errorCode.defaultException(e);
        }
    }

    private String getUserEmailFromRole(String token) {
        LOGGER.info("[getUsername] 토큰 기반 회원 역할 추출");
        try {
            String role = Jwts.parser().verifyWith(key).build().parseSignedClaims(token).getPayload().get("role", String.class);
            LOGGER.info("[getUsername] 토큰 기반 회원 역할 추출 완료, info : {}", role);
            return role;
        } catch (JwtException e) {
            LOGGER.info("[getUsername] 토큰 기반 회원 역할 추출 실패, error : {}", e.getMessage());
            throw AuthErrorCode.INVALID_ACCESS_TOKEN.defaultException(e);
        }
    }
    public String resolveAccessToken(HttpServletRequest request) {
        LOGGER.info("[resolveAccessToken] HTTP 헤더에서 Token 값 추출");
        return request.getHeader("X-AUTH-TOKEN");
    }

    public boolean isValidToken(String token) {
        LOGGER.info("[isValidToken] 토큰 유효 체크 시작");
        try {
            Jws<Claims> claims = Jwts.parser().verifyWith(key).build().parseSignedClaims(token);
            return !claims.getPayload().getExpiration().before(new Date());
        } catch (Exception e) {
            LOGGER.info("[isValidToken] 토큰 유효 체크 예외 발생 : ");
            return false;
        }
    }
    public boolean isTokenBlackList(String token) {
        if (redisTemplate.opsForValue().get("blacklist:"+token) != null) {
            throw AuthErrorCode.BLACKLISTED_ACCESS_TOKEN.defaultException();
        }
        return false;
    }

    public long getExpiration(String token) {
        Jws<Claims> claims = Jwts.parser().verifyWith(key).build().parseSignedClaims(token);
        Date expiration = claims.getPayload().getExpiration();
        long now = new Date().getTime();
        return Math.max(expiration.getTime() - now, 1);
    }
}