package com.grupocordillera.datosorg;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class DatosOrgApplication {
    public static void main(String[] args) {
        SpringApplication.run(DatosOrgApplication.class, args);
    }
}
