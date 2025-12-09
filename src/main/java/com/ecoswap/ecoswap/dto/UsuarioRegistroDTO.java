package com.ecoswap.ecoswap.dto;
import lombok.Data;
import lombok.NonNull;
@Data
public class UsuarioRegistroDTO {
    @NonNull private String nombre;
    @NonNull private String mail;
    @NonNull private String contrasena;
    private String imagenPerfil;
    public Object DTO; 
}