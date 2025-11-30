package com.ecoswap.ecoswap.dto;

import lombok.Data;
import lombok.NonNull;

@Data
public class ItemRegistroDTO {
    @NonNull private String titulo;
    @NonNull private String descripcion;
    // 💡 CORRECCIÓN: Cambiar 'int' (primitivo) por 'Integer' (wrapper)
    @NonNull private Integer puntosAGanar; 
    @NonNull private String categoria;
    private String imagenPrincipal; 
}