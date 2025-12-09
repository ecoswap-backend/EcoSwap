package com.ecoswap.ecoswap.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import java.nio.file.Path;
import java.nio.file.Paths;

@Configuration
public class MvcConfig implements WebMvcConfigurer {

    // Define la ubicación del directorio de archivos subidos
    private static final Path UPLOADS_ROOT = Paths.get("uploads").toAbsolutePath().normalize();

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // Mapea la URL /uploads/** a la ubicación física de los archivos subidos.
        // Esto permite que el navegador acceda a las imágenes
        // usando una ruta como: http://localhost:8080/uploads/nombre_del_archivo.jpg
        registry.addResourceHandler("/uploads/**")
                .addResourceLocations("file:" + UPLOADS_ROOT.toString() + "/");
    }
}
