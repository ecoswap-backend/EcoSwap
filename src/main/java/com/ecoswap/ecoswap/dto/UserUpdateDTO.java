package com.ecoswap.ecoswap.dto;
import lombok.Data;
import jakarta.validation.constraints.Email;
@Data
public class UserUpdateDTO {
    private String nombre;
    @Email(message = "Debe ser un formato de correo válido")
    private String mail;
    private String contrasenaActual;
    private String nuevaContrasena;
    private String eliminarImagenActual;
}