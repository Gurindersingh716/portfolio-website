package dev.gurindersingh.portfolio.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI portfolioOpenApi() {
        return new OpenAPI().info(new Info()
                .title("gurindersingh.dev API")
                .version("1.0.0")
                .description("""
                        Backend for gurindersingh.dev. Serves project records and accepts
                        contact form submissions.

                        Public and read-only apart from the contact endpoint, which is
                        rate limited to 3 submissions per IP per hour.
                        """)
                .contact(new Contact()
                        .name("Gurinder Singh")
                        .email("contact@gurindersingh.dev")
                        .url("https://gurindersingh.dev"))
                .license(new License().name("MIT")));
    }
}
