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
import springfox.documentation.service.ApiKey;
import springfox.documentation.service.AuthorizationScope;
import springfox.documentation.service.Contact;
import springfox.documentation.service.SecurityReference;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Arrays;
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
                .build()
                .securitySchemes(Arrays.asList(apiKey()))
                .securityContexts(Arrays.asList(securityContext()));
    }

    /**
     * Swagger UI 的全局认证方案：以 Authorization 请求头传递 Bearer Token。
     * 仅影响 UI 的「Authorize」按钮与默认鉴权展示，不改变后端 SecurityConfig 的真实拦截逻辑。
     */
    private ApiKey apiKey() {
        return new ApiKey("JWT", "Authorization", "header");
    }

    private springfox.documentation.spi.service.contexts.SecurityContext securityContext() {
        return springfox.documentation.spi.service.contexts.SecurityContext.builder()
                .securityReferences(defaultAuth())
                .forPaths(PathSelectors.any())
                .build();
    }

    private List<SecurityReference> defaultAuth() {
        AuthorizationScope authorizationScope = new AuthorizationScope("global", "accessEverything");
        AuthorizationScope[] authorizationScopes = new AuthorizationScope[1];
        authorizationScopes[0] = authorizationScope;
        return Arrays.asList(new SecurityReference("JWT", authorizationScopes));
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


