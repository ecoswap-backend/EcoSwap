package com.ecoswap.ecoswap.dto;
import lombok.Data;
import lombok.NonNull;
@Data
public class UsuarioLoginDTO {
    @NonNull private String mail;
    @NonNull private String contrasena;
}