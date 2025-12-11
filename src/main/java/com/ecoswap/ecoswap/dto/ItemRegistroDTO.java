package com.ecoswap.ecoswap.dto;
import lombok.Data;
import lombok.NonNull;

@Data
public class ItemRegistroDTO {
    // Campos obligatorios (Non-Null)
    @NonNull private String titulo;
    @NonNull private Integer puntosAGanar; 
    @NonNull private String categoria;
    
    // Campo opcional (Permite null o cadena vacía)
    private String descripcion; // <-- CORREGIDO: SE ELIMINA @NonNull
    
    // Este campo no debe estar en el DTO en un @PostMapping con MultipartFile
    // El ItemController maneja la imagen por separado. Lo mantengo aquí si 
    // lo usas internamente, pero generalmente se omite en el DTO de registro.
    private String imagenPrincipal; 
}