package com.ecoswap.ecoswap.dto;
import lombok.Data;
import jakarta.validation.constraints.PositiveOrZero;
@Data
public class ItemUpdateDTO {
    private String titulo;
    private String descripcion;
    private String categoria;
    @PositiveOrZero(message = "Los puntos a ganar deben ser cero o positivos")
    private Integer puntosAGanar; 
    private String eliminarImagenActual; 
}