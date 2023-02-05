package com.w6w.corns.jwt;

import com.w6w.corns.service.jwt.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Component
@RequiredArgsConstructor
public class JwtInterceptor implements HandlerInterceptor {

    private final JwtService jwtService;
    private final AuthorizationExtractor authorizationExtractor;
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        String url = request.getRequestURI();
        if(url.contains("swagger") || url.contains("api-docs") || url.contains("webjars"))
            return true;

        String token = authorizationExtractor.extract(request, "Bearer");
        System.out.println("token = " + token);
        if(token.isEmpty() || token == null || !jwtService.checkToken(token)) {
            response.setStatus(401);
            System.out.println("interceptor!");
            return false;
        }
        return true;
    }
}