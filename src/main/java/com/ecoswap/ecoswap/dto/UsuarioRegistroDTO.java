package com.ecoswap.ecoswap.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor; 
import lombok.Data;
import lombok.NoArgsConstructor; 

@Data
@NoArgsConstructor 
@AllArgsConstructor 
public class UsuarioRegistroDTO {

    @NotBlank(message = "El nombre es obligatorio") 
    private String nombre;

    @Email(message = "Formato de correo inválido")
    @NotBlank(message = "El correo es obligatorio")
    private String mail;

    @Size(min = 6, message = "La contraseña debe tener al menos 6 caracteres")
    @NotBlank(message = "La contraseña es obligatoria")
    private String contrasena;
    
    private String imagenPerfil;

}