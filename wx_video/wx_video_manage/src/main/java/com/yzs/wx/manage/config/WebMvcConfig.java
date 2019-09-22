package com.yzs.wx.manage.config;

import com.yzs.wx.manage.interceptor.LoginInterceptor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

@Configuration
public class WebMvcConfig extends WebMvcConfigurerAdapter {

    @Value("${bgm.filepath.prefix}")
    private String prefix;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        ResourceHandlerRegistration handler = registry.addResourceHandler("/**");
        handler.addResourceLocations("file:"+prefix);
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(myInterceptor()).addPathPatterns("/*/**")
                .excludePathPatterns("/static/**","/users/login.action");
        super.addInterceptors(registry);
    }

    @Bean
    public LoginInterceptor myInterceptor(){
        return new LoginInterceptor();
    }
}
