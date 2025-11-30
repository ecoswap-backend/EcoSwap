package com.ecoswap.ecoswap.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class ItemDTO {
    private Long id;
    private String titulo;
    private String descripcion;
    private LocalDateTime fechaCreacion;
    private String estado; 
    private int puntosAGanar;
    private String categoria;
    private String imagenPrincipal;
    
    private String duenoNombre; 
    private String duenoImagenPerfil;
}