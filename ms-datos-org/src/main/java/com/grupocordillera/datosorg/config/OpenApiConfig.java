package com.grupocordillera.datosorg.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI datosOrgOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("ms-datos-org API")
                        .description("Microservicio de datos organizacionales (empleados y departamentos) - Grupo Cordillera")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("Grupo Cordillera")
                                .email("contacto@cordillera.cl"))
                        .license(new License()
                                .name("Apache 2.0")
                                .url("https://www.apache.org/licenses/LICENSE-2.0")));
    }
}
