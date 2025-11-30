package com.ecoswap.ecoswap.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "items")
@Data // Genera getters, setters, toString, equals, hashCode (Lombok)
@NoArgsConstructor
@AllArgsConstructor
public class Item { 

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Dueño de la prenda
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User dueno; 

    @Column(nullable = false, length = 150)
    private String titulo;

    @Lob // Para textos largos
    private String descripcion;

    @Column(name = "fecha_creacion", nullable = false, updatable = false)
    private LocalDateTime fechaCreacion = LocalDateTime.now(); 

    // Mapea el Enum a su nombre String en la base de datos
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EstadoItem estado = EstadoItem.DISPONIBLE; // ¡Aquí debe funcionar!

    @Column(name = "puntos_a_ganar", nullable = false)
    private int puntosAGanar;

    @Column(nullable = false, length = 50)
    private String categoria;

    @Column(name = "imagen_principal")
    private String imagenPrincipal; // URL (String)

    // Usuario que ha reservado el artículo
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reservado_por_id")
    private User reservadoPor; 
}