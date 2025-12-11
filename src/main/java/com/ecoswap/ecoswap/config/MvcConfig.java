package com.ecoswap.ecoswap.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry; 
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import java.nio.file.Path;
import java.nio.file.Paths;

@Configuration
public class MvcConfig implements WebMvcConfigurer {

    // Define la ubicación del directorio de archivos subidos. 
    // Debe coincidir con la variable 'storageLocation' en ItemService.java.
    // Usamos 'file:///' para asegurar que Spring sepa que es una URL de archivo local.
    private static final String UPLOADS_PATH = Paths.get("uploads").toAbsolutePath().normalize().toString();

    /**
     * Configuración de CORS para permitir solicitudes desde el frontend (http://localhost:5173).
     */
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**") // Aplica esta política a TODAS las rutas de la API
                .allowedOrigins("http://localhost:5173") // Origen de tu frontend
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS") // Métodos HTTP permitidos
                .allowedHeaders("*") // Permite todas las cabeceras (incluyendo Content-Type, Authorization, etc.)
                .allowCredentials(true); // Necesario para enviar cookies o headers de autenticación (JWT)
    }

    /**
     * Mapea la carpeta local 'uploads' a una URL pública (/uploads/**).
     * Esto permite que el navegador muestre las imágenes guardadas.
     */
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // Mapea la URL /uploads/** a la ubicación física de los archivos subidos.
        // La sintaxis 'file:' + path + '/' es requerida por Spring para rutas de archivos.
        registry.addResourceHandler("/uploads/**")
                .addResourceLocations("file:" + UPLOADS_PATH + "/");
    }
}