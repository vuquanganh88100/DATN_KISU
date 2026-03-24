package com.elearning.elearning_support.configurations.openAPI;

import java.util.List;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;

@Configuration
@ComponentScan(basePackages = "com.elearning.elearning_support.configurations")
public class OpenAPIConfig {

    public static final String LOCAL_HOST_IP = "http://127.0.0.1:8088/e-learning";

    public static final String LOCAL_HOST = "localhost:8088/e-learning";

    public static final String DEFAULT_PATH = "/e-learning/swagger-ui.html";

    private static final List<Server> lstServer =  List.of(new Server().url(LOCAL_HOST_IP).description("Local IP"),
                                                           new Server().url(LOCAL_HOST).description("Local Host"));

    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI().servers(lstServer)
            .addSecurityItem(new SecurityRequirement().addList("Bearer Authentication"))
            .components(new Components().addSecuritySchemes("Bearer Authentication", createAPIKeyScheme()))
            .info(new Info()
                .contact(new Contact()
                    .email("chiendao1808@gmail.com")
                    .name("Chien Dao - Hanoi University of Science and Technology"))
                .version("1.0.0"));
    }

    /**
     * Create security scheme
     */
    private SecurityScheme createAPIKeyScheme() {
        return new SecurityScheme().type(SecurityScheme.Type.HTTP)
            .bearerFormat("JWT")
            .scheme("bearer");
    }
}
