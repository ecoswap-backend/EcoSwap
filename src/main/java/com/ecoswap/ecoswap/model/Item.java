package com.ecoswap.ecoswap.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "items")
@Data 
@NoArgsConstructor
@AllArgsConstructor
public class Item { 

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User dueno; 

    @Column(nullable = false, length = 150)
    private String titulo;

    @Lob 
    private String descripcion;

    @Column(name = "fecha_creacion", nullable = false, updatable = false)
    private LocalDateTime fechaCreacion = LocalDateTime.now(); 

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EstadoItem estado = EstadoItem.DISPONIBLE; 

    @Column(name = "puntos_a_ganar", nullable = false)
    private int puntosAGanar;

    @Column(nullable = false, length = 50)
    private String categoria;
    
    @Column(name = "imagen_principal")
    private String imagenPrincipal; 

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reservado_por_id")
    private User reservadoPor; 
}