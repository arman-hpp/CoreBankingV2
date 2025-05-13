package com.bank.core.configs;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.responses.ApiResponse;
import io.swagger.v3.oas.models.responses.ApiResponses;
import org.springdoc.core.customizers.OpenApiCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {
    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("CoreBanking API")
                        .version("v1.0")
                        .description("RESTful API with Java and Spring Boot 3")
                        .termsOfService("https://swagger.io/terms/")
                        .license(new License()
                                .name("Apache 2.0")
                                .url("https://www.apache.org/licenses/LICENSE-2.0.html"))
                        .contact(new Contact()
                                .name("Arman Hasanpour")
                                .email("arman.hassanpoor2000@gmail.com")
                                .url("https://github.com/arman-hpp")));
    }

    @Bean
    public OpenApiCustomizer globalApiResponseCustomizer() {
        return openApi -> {
            openApi.getPaths().forEach((path, pathItem) -> {
                pathItem.readOperations().forEach(operation -> {
                    var apiResponses = operation.getResponses();
                    if (apiResponses == null) {
                        apiResponses = new ApiResponses();
                        operation.setResponses(apiResponses);
                    }
                    apiResponses.addApiResponse("400", new ApiResponse().description("درخواست نامعتبر"));
                    apiResponses.addApiResponse("500", new ApiResponse().description("خطای سرور"));
                });
            });
        };
    }
}