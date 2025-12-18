package com.ecoswap.ecoswap.dto;
import lombok.Data;
import lombok.NonNull;

@Data
public class ItemRegistroDTO {

    @NonNull private String titulo;
    @NonNull private String categoria;

    private String descripcion;
    private Integer puntosAGanar;
    private String talla;
    private String estado;
    private String ubicacion;

}