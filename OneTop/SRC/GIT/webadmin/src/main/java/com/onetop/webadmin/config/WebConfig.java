package com.onetop.webadmin.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;


@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addInterceptors(InterceptorRegistry registry) {

//        List<String> pathPatternsList = new ArrayList<String>();
//        pathPatternsList.add("/member_detail");
//        pathPatternsList.add("/bank2_mod");
//        pathPatternsList.add("/admin_mod");
//        pathPatternsList.add("/mon*");
//        pathPatternsList.add("/sys*");
//        pathPatternsList.add("/mem*");
//        pathPatternsList.add("/bank*");
//        pathPatternsList.add("/board*");
//        pathPatternsList.add("/auth*");
//
//        registry.addInterceptor(new AccessPermissionInterceptor()).addPathPatterns(pathPatternsList);

        registry.addInterceptor(new AccessPermissionInterceptor())
                .addPathPatterns("/*")
                .excludePathPatterns("/test/**/")
                .excludePathPatterns("/login"); //로그인 쪽은 예외처리
    }
}
