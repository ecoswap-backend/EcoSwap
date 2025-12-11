package com.ecoswap.ecoswap.model;

import com.fasterxml.jackson.annotation.JsonIgnore; // Importación necesaria
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.util.List;
// Asegúrate de que todas las importaciones de JPA están ahí (jakarta.persistence.*)


@Entity
@Table(name = "users")
@Data 
@NoArgsConstructor 
@AllArgsConstructor 
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String nombre;

    @Column(nullable = false, unique = true, length = 150)
    private String mail;

    @Column(nullable = false)
    private String contrasena; 

    @Column(name = "imagen_perfil")
    private String imagenPerfil; 

    @Column(nullable = false)
    private int puntos = 0;

    // Relación para mostrar los artículos creados por el usuario
    // CRUCIAL: Añadimos @JsonIgnore para evitar la excepción 500 (LazyInitializationException/Bucle)
    @JsonIgnore
    @OneToMany(mappedBy = "dueno", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Item> articulosCreados;
}