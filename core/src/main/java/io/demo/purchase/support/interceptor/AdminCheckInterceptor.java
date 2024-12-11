package io.demo.purchase.support.interceptor;

import io.demo.purchase.core.PermissionIssueException;
import io.demo.purchase.support.exception.CoreDomainErrorType;
import io.demo.purchase.core.domain.user.JwtProvider;
import io.demo.purchase.core.domain.user.User;
import io.demo.purchase.core.domain.user.UserReader;
import io.demo.purchase.support.RoleType;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.Arrays;
import java.util.Optional;

@Component
public class  AdminCheckInterceptor implements HandlerInterceptor {
    private final JwtProvider jwtProvider;
    private final UserReader userReader;

    @Autowired
    public AdminCheckInterceptor(JwtProvider jwtProvider, UserReader userReader) {
        this.jwtProvider = jwtProvider;
        this.userReader = userReader;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        String accessToken = Optional.ofNullable(request.getCookies())
                .flatMap(cookies ->
                        Arrays.stream(cookies).filter((c) -> "accessToken".equals(c.getName())).findFirst())
                .map(Cookie::getValue)
                .orElseThrow(() -> new PermissionIssueException(CoreDomainErrorType.UNAUTHORIZED, "쿠키를 찾지 못했습니다"));

        // 쿠키 -> 유저 검색 -> User 반환 받기
        Long userId = jwtProvider.verifyToken(accessToken);
        User user = userReader.findById(userId);

        if (user.getRole() != RoleType.ADMIN)
            throw new PermissionIssueException(CoreDomainErrorType.UNAUTHORIZED, "일반 사용자는 접근이 제한됩니다");

        return true;
    }
}
