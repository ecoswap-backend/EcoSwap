package com.ecoswap.ecoswap;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class EcoswapApplication {

    public static void main(String[] args) {
        
        try {
            Dotenv dotenv = Dotenv.load();
            dotenv.entries().forEach(entry -> 
                System.setProperty(entry.getKey(), entry.getValue())
            );
        } catch (io.github.cdimascio.dotenv.DotenvException e) {
            
            System.err.println("ADVERTENCIA: No se encontró el archivo .env. Asegúrate de que existe en la raíz del proyecto.");
        }

        SpringApplication.run(EcoswapApplication.class, args);
    }

}