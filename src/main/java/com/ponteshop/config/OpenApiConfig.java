package com.ponteshop.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(
    info = @Info(
        title = "PonteShop API",
        version = "v1",
        description = "API do e-commerce PRDIigital / PonteShop"
    )
)
public class OpenApiConfig {
}

