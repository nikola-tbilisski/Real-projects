package com.kvantino.book.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.servers.Server;

@OpenAPIDefinition(
        info = @Info(
                contact = @Contact(
                        name = "Kvantino",
                        email = "kvantino@gmail.com",
                        url = "https://github.com/nikola-tbilisski"
                ),
                description = "Spring security OpenApi documentation",
                title = "OpenApi specification - Kvantino",
                version = "1.0",
                license = @License(
                        name = "MIT License Copyright (c) 2023 Nikola",
                        url = "https://github.com/nikola-tbilisski"
                ),
                termsOfService = "https://github.com/nikola-tbilisski/Real-projects/blob/master/Project3-WeatherSensor/LICENSE.txt"
        ),
        servers = {
                @Server(
                        description = "Local ENV",
                        url = "http://localhost:8088/api/v1"
                ),
                @Server(
                        description = "Prod ENV",
                        url = "https://kvantino.com/book/api.v1"
                )
        },
        security = {
                @SecurityRequirement(
                        name = "bearerAuth"
                )
        }
)
@SecurityScheme(
        name = "bearerAuth",
        description = "JWT auth description",
        scheme = "bearer",
        type = SecuritySchemeType.HTTP,
        bearerFormat = "JWT",
        in = SecuritySchemeIn.HEADER
)
public class OpenApiConfig {
}
