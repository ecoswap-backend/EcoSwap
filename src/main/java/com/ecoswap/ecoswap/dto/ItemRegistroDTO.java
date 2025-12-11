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
    private String descripcion; 
    
    // ¡CORRECCIÓN CRÍTICA!
    // El campo 'imagenPrincipal' YA NO está aquí. Es manejado por 
    // el controlador directamente como MultipartFile.
}