package com.tdea.backend.config;

import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Configura el acceso local del frontend a la API REST.
 *
 * <p>Los orígenes se pueden cambiar mediante la propiedad
 * {@code app.cors.allowed-origins} o la variable de entorno
 * {@code CORS_ALLOWED_ORIGINS}. Esta configuración está pensada para el
 * prototipo y no sustituye una política de seguridad para producción.</p>
 */
@Configuration
public class CorsConfig implements WebMvcConfigurer {

    private final List<String> allowedOrigins;

    /**
     * Crea la configuración CORS con los orígenes permitidos.
     *
     * @param origins lista separada por comas de orígenes confiables
     */
    public CorsConfig(@Value("${app.cors.allowed-origins:http://localhost:5173}") String origins) {
        this.allowedOrigins = Arrays.stream(origins.split(","))
                .map(String::trim)
                .filter(origin -> !origin.isBlank())
                .toList();
    }

    /**
     * Registra la política CORS para las rutas de la API.
     *
     * @param registry registro de configuración MVC
     */
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/**")
                .allowedOrigins(allowedOrigins.toArray(String[]::new))
                .allowedMethods("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS")
                .allowedHeaders("*");
    }
}
