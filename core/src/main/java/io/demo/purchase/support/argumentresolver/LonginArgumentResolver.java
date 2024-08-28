package io.demo.purchase.support.argumentresolver;

import io.demo.purchase.core.PermissionIssueException;
import io.demo.purchase.support.exception.CoreDomainErrorType;
import io.demo.purchase.core.domain.user.JwtProvider;
import io.demo.purchase.core.domain.user.User;
import io.demo.purchase.core.domain.user.UserReader;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import java.util.Arrays;

@Slf4j
@Component
public class LonginArgumentResolver implements HandlerMethodArgumentResolver {

    private final JwtProvider jwtProvider;
    private final UserReader userReader;

    @Autowired
    public LonginArgumentResolver(final JwtProvider jwtProvider, UserReader userReader) {
        this.jwtProvider = jwtProvider;
        this.userReader = userReader;
    }

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        log.info("argument support 들어왔어요");
        return parameter.hasParameterAnnotation(AuthorizedUser.class)
                && User.class.isAssignableFrom(parameter.getParameterType());
    }

    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer, NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {
        log.info("argument resolver 들어왔어요");

        HttpServletRequest request = (HttpServletRequest) webRequest.getNativeRequest();

        // cookie 확인하기
        Cookie[] cookies = request.getCookies();
        String accessToken = Arrays.stream(cookies)
                .filter((c) -> "accessToken".equals(c.getName()))
                .findFirst()
                .map(Cookie::getValue)
                .orElseThrow(() -> new PermissionIssueException(CoreDomainErrorType.UNAUTHORIZED, "쿠키를 찾지 못했습니다"));


        // 쿠키 -> 유저 검색 -> User 반환 받기
        Long userId = jwtProvider.verifyToken(accessToken);
        User user = userReader.findById(userId);

        return user;
    }
}
