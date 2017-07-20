package com.hjzgg.stateless.shiroWeb.config;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@EnableWebMvc
@EnableSwagger2
@ComponentScan("com.hjzgg.stateless.shiroWeb.controller")
@Configuration
public class Swagger2Config extends WebMvcConfigurationSupport {

	@Bean
	public Docket docket() {
		return new Docket(DocumentationType.SWAGGER_2)
				.apiInfo(apiInfo())
				.groupName("admin")
				.select()
//				.apis(RequestHandlerSelectors.withClassAnnotation(Api.class))
//				.apis(RequestHandlerSelectors.withMethodAnnotation(ApiOperation.class))
				.paths(PathSelectors.any())
				.build();
	}

//	@Bean
//	public Docket xxx() {
//		return new Docket(DocumentationType.SWAGGER_2)
//				.apiInfo(apiInfo())
//				.groupName("xxx")
//				.select()
//				.apis(RequestHandlerSelectors.withClassAnnotation(Api.class))
//				.apis(RequestHandlerSelectors.withMethodAnnotation(ApiOperation.class))
//				.paths(PathSelectors.ant("/xxx/**"))
//				.build();
//	}

	private ApiInfo apiInfo() {
		return new ApiInfoBuilder()
				.title("shiro 无状态组件")
				.contact(new Contact("胡峻峥", "", "2570230521@qq.com"))
				.version("1.0")
				.build();
	}
}
