package ru.noleg.prreviewerservice.configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfiguration {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI().info(new Info()
                .title("PR Reviewer Assignment Service")
                .description("My implementation of a service for assigning PR")
                .version("1.0")
                .contact(new Contact()
                        .name("Oleg Nazarov")
                        .email("noleg867@gmail.com")));
    }
}