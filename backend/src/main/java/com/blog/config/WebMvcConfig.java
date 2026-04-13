package com.blog.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Autowired
    private JwtAuthInterceptor jwtAuthInterceptor;

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOriginPatterns("*")
                .allowedMethods("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .allowCredentials(true);
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(jwtAuthInterceptor)
                .addPathPatterns("/api/**")
                .excludePathPatterns(
                        // 公开认证
                        "/api/auth/register",
                        "/api/auth/login",
                        // 公开文章
                        "/api/articles",
                        "/api/articles/hot",
                        "/api/articles/{id}",
                        // 分类/标签/评论（公开）
                        "/api/categories",
                        "/api/tags",
                        "/api/comments/article/{articleId}",
                        // 公开统计
                        "/api/stats",
                        // 公开合集
                        "/api/collections",
                        "/api/collections/{id}"
                );
    }
}
