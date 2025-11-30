package com.ecoswap.ecoswap.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Entity
@Table(name = "users")
@Data // Genera getters, setters, toString, equals, hashCode
@NoArgsConstructor // Genera constructor sin argumentos
@AllArgsConstructor // Genera constructor con todos los argumentos
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String nombre;

    @Column(nullable = false, unique = true, length = 150)
    private String mail;

    @Column(nullable = false)
    private String contrasena; // Contraseña hasheada

    @Column(name = "imagen_perfil")
    private String imagenPerfil; // URL al servicio de almacenamiento

    @Column(nullable = false)
    private int puntos = 0;
}