package com.rita.mall.auth.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class AuthWebConfig implements WebMvcConfigurer {

    /**
     * 发送一个请求直接跳转到一个页面并且不传值，我们原先是在controller里创建一个跳转页面的空方法。
     * 现在我们使用SpringMVC viewController: 将请求html页面映射过来;不需要写空方法
     *
     * */
    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        /**
         *     @GetMapping("/login.html")
         *     public String loginPage(){
         *         return "login";
         *     }
         */
        registry.addViewController("/login.html").setViewName("login");
        registry.addViewController("/reg.html").setViewName("reg");
    }
}
