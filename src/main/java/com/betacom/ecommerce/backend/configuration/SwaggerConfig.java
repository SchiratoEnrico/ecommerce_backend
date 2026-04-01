package com.betacom.ecommerce.backend.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;

@Configuration
public class SwaggerConfig {
	
	@Bean
    OpenAPI customizeOpenAPI() {
        final String securitySchemeName = "bearerAuth";
        
        return new OpenAPI()
            // 1. Applica la sicurezza a tutti gli endpoint in modo globale su Swagger
            .addSecurityItem(new SecurityRequirement().addList(securitySchemeName))
            // 2. Definisce come è fatto il token (Bearer JWT)
            .components(new Components()
                .addSecuritySchemes(securitySchemeName, new SecurityScheme()
                    .name(securitySchemeName)
                    .type(SecurityScheme.Type.HTTP)
                    .scheme("bearer")
                    .bearerFormat("JWT")));
    }
}
