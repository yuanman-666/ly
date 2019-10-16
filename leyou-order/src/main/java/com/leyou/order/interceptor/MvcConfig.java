package com.leyou.order.interceptor;

import com.leyou.order.config.JwtProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@EnableConfigurationProperties(JwtProperties.class)
public class MvcConfig implements WebMvcConfigurer {
    @Autowired
    private JwtProperties jwtProperties;

    @Bean
    public LoginInterceptor loginInterceptor( ){
        return new LoginInterceptor(jwtProperties);
    }


    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        //注释掉放行，不拦截
        registry.addInterceptor(loginInterceptor()).addPathPatterns("/**");
    }
}
