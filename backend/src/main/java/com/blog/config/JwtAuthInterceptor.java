package com.blog.config;

import com.blog.common.AuthContext;
import com.blog.common.Result;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.Arrays;
import java.util.List;

@Component
public class JwtAuthInterceptor implements HandlerInterceptor {

    @Autowired
    private JwtUtil jwtUtil;

    private final ObjectMapper objectMapper = new ObjectMapper();

    // 公开路径列表
    private static final List<String> PUBLIC_PATHS = Arrays.asList(
            "/api/auth/register",
            "/api/auth/login",
            "/api/articles",
            "/api/categories",
            "/api/tags",
            "/api/comments",
            "/api/stats",
            "/api/collections",
            "/api/about"
    );

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // OPTIONS 请求直接通过（预检请求）
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            return true;
        }

        String path = request.getRequestURI();

        // 检查是否是公开路径
        if (isPublicPath(path)) {
            return true;
        }

        String token = request.getHeader("Authorization");
        if (!StringUtils.hasText(token) || !token.startsWith("Bearer ")) {
            sendUnauthorized(response, "Missing or invalid Authorization header");
            return false;
        }

        token = token.substring(7);
        if (!jwtUtil.validateToken(token)) {
            sendUnauthorized(response, "Invalid or expired token");
            return false;
        }

        Long userId = jwtUtil.getUserIdFromToken(token);
        AuthContext.setUserId(userId);
        return true;
    }

    private boolean isPublicPath(String path) {
        // 检查是否匹配任何公开路径
        for (String publicPath : PUBLIC_PATHS) {
            if (path.equals(publicPath) || path.startsWith(publicPath + "/") || path.matches(publicPath.replace("{id}", "\\d+").replace("{name}", "[^/]+"))) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        AuthContext.clear();
    }

    private void sendUnauthorized(HttpServletResponse response, String message) throws Exception {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().write(objectMapper.writeValueAsString(Result.error(401, message)));
    }
}
