package com.ecoswap.ecoswap.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor; 
import lombok.Data;
import lombok.NoArgsConstructor; 

@Data
@NoArgsConstructor // Asegura el constructor sin argumentos para Jackson (Mapeo JSON)
@AllArgsConstructor // Provee un constructor con todos los argumentos (Buena práctica)
public class UsuarioRegistroDTO {

    // 1. Asegurar que el nombre no sea nulo ni vacío
    @NotBlank(message = "El nombre es obligatorio") 
    private String nombre;

    // 2. Asegurar que sea un correo válido y no vacío
    @Email(message = "Formato de correo inválido")
    @NotBlank(message = "El correo es obligatorio")
    private String mail;

    // 3. Asegurar una longitud mínima para la contraseña
    // La longitud mínima es 6, lo cual acepta "password123"
    @Size(min = 6, message = "La contraseña debe tener al menos 6 caracteres")
    @NotBlank(message = "La contraseña es obligatoria")
    private String contrasena;
    
    private String imagenPerfil;
    
    // El campo 'public Object DTO' se eliminó o se omitió por ser innecesario
}