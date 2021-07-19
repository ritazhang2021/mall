package com.rita.mall.gateway.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;


import org.springframework.web.cors.reactive.CorsConfigurationSource;
import org.springframework.web.cors.reactive.CorsWebFilter;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;


/**
 * @Author: Rita
 * @Date:6/18/2021 4:05 PM
 * Spring Cloud配置跨域访问的五种方案
 * 解决方案一：在Controller上添加@CrossOrigin注解
 * 这种方式适合只有一两个rest接口需要跨域或者没有网关的情况下，这种处理方式就非常简单，适合在原来基代码基础上修改，影响比较小
 * 解决方案二：增加WebMvcConfigurer全局配置
 * 如果有大量的rest接口的时候，显然第一种方案已经不适合了，工作量大，也容易出错，那就通过全局配置的方式，允许SpringBoot端所有的rest接口都支持跨域访问，这个时候就需要考虑安全性的问题。
解决方案三：结合Filter使用
这种方案的使用场景跟第二种方案类似，只不过换成使用Filter的方式实现。
在spring boot的主类中，增加一个CorsFilter

解决方案四，在Gateway端增加CorsFilter拦截器
这种方案跟方案三有些类似，只不过是放到了Gateway端，对于有多个微服务模块的情况下，就大大减少了SpringBoot模块端的代码量，让各个模块更集中精力做业务逻辑实现。这个方案只需要在Gateway里添加Filter代码类即可。
解决方案五，修改Gateway配置文件
在仔细阅读过Gateway的文档你就会发现，原来CorsFilter早已经在Gateway里了，不需要自己写代码实现，而且更灵活，修改配置文件即可，结合配置中心使用，可以实现动态修改。

 */
@Configuration
public class CorsConfig {
    @Bean
    public CorsWebFilter corsWebFilter(){
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
//        CorsConfigurationSource corsConfigurationSource = new CorsConfigurationSource() {
//            @Override
//            public CorsConfiguration getCorsConfiguration(javax.servlet.http.HttpServletRequest httpServletRequest) {
//                return null;
//            }
//        }
        CorsConfiguration corsConfiguration = new CorsConfiguration();
        //配置跨域
        //任意头信息
        corsConfiguration.addAllowedHeader("*");
        //get,post...都可以
        corsConfiguration.addAllowedMethod("*");
        corsConfiguration.addAllowedOrigin("*");
        //允许使用cookie，但是使用cookie是addAllowedOrigin必须是具体的地址，不能是*
        corsConfiguration.setAllowCredentials(true);
        source.registerCorsConfiguration("/**", corsConfiguration);
        /*注意引入的包是否正确
        * 要用响应式编程的包，reactive*/
        return new CorsWebFilter((CorsConfigurationSource) source);

    }
}

