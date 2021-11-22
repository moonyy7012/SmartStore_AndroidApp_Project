package com.ssafy.smartstore;

import com.ssafy.smartstore.model.dao.OrderDao;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@EnableCaching
@EnableScheduling
@SpringBootApplication
@EnableSwagger2
@MapperScan(basePackageClasses = OrderDao.class)
public class SmartstoreBackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(SmartstoreBackendApplication.class, args);
    }

    @Bean
    public Docket postsApi() {
        final ApiInfo apiInfo = new ApiInfoBuilder()
                .title("SmartStore Rest API Guide")
                .description("<h3>SmartStore 관통 프로젝트용 API 가이드 문서</h3>")
                .contact(new Contact("Minjeong Kim", "https://velog.io/@tenykim1109", "lona573@gmail.com"))
                .license("MIT License")
                .version("1.0")
                .build();

        Docket docket = new Docket(DocumentationType.SWAGGER_2)
                .groupName("com.ssafy.smartstore")
                .apiInfo(apiInfo)
                .select()
                .apis(RequestHandlerSelectors.basePackage("com.ssafy.smartstore.controller.rest"))
                .build();

        return docket;
    }

}
