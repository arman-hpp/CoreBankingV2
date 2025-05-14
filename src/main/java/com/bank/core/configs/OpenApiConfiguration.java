package com.bank.core.configs;

import com.bank.core.enums.ApiCommonResponses;
import com.bank.core.exceptions.ApiErrorResponse;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.media.Content;
import io.swagger.v3.oas.models.media.MediaType;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.responses.ApiResponse;
import io.swagger.v3.oas.models.responses.ApiResponses;
import org.springdoc.core.customizers.OpenApiCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.LocalDateTime;

/**
 * Configures OpenAPI (Swagger) documentation for the CoreBanking API.
 */
@Configuration
public class OpenApiConfiguration {
    /**
     * Defines the OpenAPI metadata, including title, version, and contact information.
     */
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

    /**
     * Customizes OpenAPI to add global HTTP response codes for all API operations.
     */
    @Bean
    public OpenApiCustomizer globalApiResponseCustomizer() {
        return openApi -> openApi.getPaths().forEach((_, pathItem) ->
                pathItem.readOperations().forEach(this::addGlobalApiResponses));
    }

    private void addGlobalApiResponses(Operation operation) {
        var apiResponses = operation.getResponses();
        if (apiResponses == null) {
            apiResponses = new ApiResponses();
            operation.setResponses(apiResponses);
        }

        for (var response : ApiCommonResponses.values()) {
            var apiResponse = new ApiResponse().description(response.getDescription());
            var errorSchema = new Schema<>().$schema("ApiErrorResponse");
            var errorMediaType = new MediaType().schema(errorSchema)
                    .example(new ApiErrorResponse(response.getError(), response.getDescription(), response.getCode(), LocalDateTime.now().toString()));
            apiResponse.setContent(new Content().addMediaType("application/json", errorMediaType));
            apiResponses.addApiResponse(response.getCode().toString(), apiResponse);
        }
    }
}