package com.imooc.mall;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * 拦截器的配置，拦截器是基于url的
 */
@Configuration
public class InterceptorConfig implements WebMvcConfigurer {

	@Override
	public void addInterceptors(InterceptorRegistry registry) {
		//添加UserLoginInterceptor()拦截器，默认对所有路径都拦截，除了"/user/login", "/user/register"
		registry.addInterceptor(new UserLoginInterceptor())
				.addPathPatterns("/**")
				.excludePathPatterns("/user/login", "/user/register");
	}
}
