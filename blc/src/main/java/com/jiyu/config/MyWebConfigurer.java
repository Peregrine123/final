package com.jiyu.config;

import com.jiyu.interceptor.LoginInterceptor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.file.Path;
import java.nio.file.Paths;

@SpringBootConfiguration
public class MyWebConfigurer implements WebMvcConfigurer {

    @Value("${blc.upload.dir:${user.dir}/uploads}")
    private String uploadDir;

    @Bean
    public LoginInterceptor getLoginIntercepter() {
        return new LoginInterceptor();
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry){
        registry.addInterceptor(getLoginIntercepter())
                // Only protect backend APIs. Frontend SPA routes/static assets must remain accessible
                // so the login/register pages can render before authentication.
                .addPathPatterns("/api/**")
                .excludePathPatterns("/api/login")
                .excludePathPatterns("/api/register")
                .excludePathPatterns("/api/logout")
                .excludePathPatterns("/api/covers")//传图片
                .excludePathPatterns("/api/file/**")//静态文件（封面/头像等）
                .excludePathPatterns("/api/register/check")//注册验证
                .excludePathPatterns("/api/login/check");
        //先通过 Configurer 判断是否需要拦截，如果需要，才会触发拦截器 LoginInterceptor
    }

    //所有请求都允许跨域
    @Override
    public void addCorsMappings(CorsRegistry registry) {

        registry.addMapping("/api/**")
                .allowCredentials(true)
                // local dev: support both localhost and 127.0.0.1
                .allowedOrigins("http://localhost:8080", "http://127.0.0.1:8080")
                .allowedMethods("POST", "GET", "PUT", "OPTIONS", "DELETE")
                .allowedHeaders("*");
    }

    //虚拟路径
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        Path dir = Paths.get(uploadDir).toAbsolutePath().normalize();
        String location = dir.toUri().toString();
        if (!location.endsWith("/")) {
            location = location + "/";
        }
        registry.addResourceHandler("/api/file/**").addResourceLocations(location);
    }


}
