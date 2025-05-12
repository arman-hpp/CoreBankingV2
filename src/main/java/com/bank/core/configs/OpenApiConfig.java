package com.bank.core.configs;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.responses.ApiResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {
    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .components(new Components()
                        .addResponses("400", new ApiResponse().description("Invalid request"))
                        .addResponses("500", new ApiResponse().description("Internal server error")))
                .info(new Info()
                        .title("CoreBanking API")
                        .version("v1.0"));
    }
}