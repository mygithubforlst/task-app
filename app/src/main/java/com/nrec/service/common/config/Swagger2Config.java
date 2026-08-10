package com.nrec.service.common.config;

import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.nrec.base.version.service.VersionService;
import com.nrec.service.common.properties.Swagger2Properties;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.RequestHandler;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * 生成swagger2 API文档
 *
 * @author chenjia
 * @date 2018-5-17
 */
@Configuration
@EnableSwagger2
public class Swagger2Config {

    @Resource
    private Swagger2Properties swagger;

    @Resource
    private VersionService versionService;

    @Value("${spring.application.name:app}")
    private String appName;

    @Bean
    public Docket createRestApi() {
        String[] packages = swagger.getBasePackage().split(swagger.getSplit());
        List<Predicate<RequestHandler>> predicateList = new ArrayList<>();
        for (String pg : packages) {
            predicateList.add(RequestHandlerSelectors.basePackage(pg));
        }
        return new Docket(DocumentationType.SWAGGER_2)
                .apiInfo(apiInfo())
                .select()
                .apis(Predicates.or(predicateList))
                .paths(PathSelectors.any())
                .build();
    }

    private ApiInfo apiInfo() {
        return new ApiInfoBuilder()
                .title(appName+" RESTful API")
                 .contact(new Contact(swagger.getAuthor(), "", swagger.getEmail()))
                .description(swagger.getDescription())
                .termsOfServiceUrl("")
                .version(versionService.gerVersionFromGitProperties())
                .build();
    }
}


