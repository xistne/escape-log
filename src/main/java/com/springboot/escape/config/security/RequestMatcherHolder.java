package com.springboot.escape.config.security;

import com.springboot.escape.data.entity.RoleEnum;
import jakarta.annotation.Nullable;
import org.springframework.http.HttpMethod;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.OrRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

import static org.springframework.http.HttpMethod.GET;
import static org.springframework.http.HttpMethod.POST;

@Component
public class RequestMatcherHolder {
    private static final List<RequestInfo> REQUEST_INFO_LIST = List.of(
            // auth
            new RequestInfo(POST, "/auth/sign-in", null),
            new RequestInfo(POST, "/users/sign-up", null),
            new RequestInfo(POST, "/auth/reissue", null),
            new RequestInfo(GET, "/auth/exception", null),

            // swagger
            new RequestInfo(GET, "/v3/api-docs/**", null),
            new RequestInfo(GET, "/swagger-ui.html", null),
            new RequestInfo(GET, "/swagger-ui/**", null),

            // temp
            new RequestInfo(GET, "/temp/user", RoleEnum.USER),
            new RequestInfo(GET, "/temp/admin", RoleEnum.ADMIN)
    );
    private final ConcurrentHashMap<String, RequestMatcher> reqMatcherCacheMap = new ConcurrentHashMap<>();

    public RequestMatcher getRequestMatchersByMinRole(@Nullable RoleEnum minRole) {
        String key = getKeyByRole(minRole);
        if (!reqMatcherCacheMap.containsKey(key)) {
            RequestMatcher requestMatcherByMinRole = new OrRequestMatcher(REQUEST_INFO_LIST.stream()
                    .filter(requestInfo -> Objects.equals(requestInfo.minRole(), minRole))
                    .map(requestInfo -> new AntPathRequestMatcher(requestInfo.pattern(), requestInfo.httpMethod().name()))
                    .toArray(AntPathRequestMatcher[]::new));
            reqMatcherCacheMap.put(key, requestMatcherByMinRole);
        }
        return reqMatcherCacheMap.get(key);
    }

    private String getKeyByRole(@Nullable RoleEnum minRole) {
        if (minRole == null) {
            return "VISITOR";
        }
        return minRole.name();
    }

    private record RequestInfo(HttpMethod httpMethod, String pattern, RoleEnum minRole) {
    }
}
