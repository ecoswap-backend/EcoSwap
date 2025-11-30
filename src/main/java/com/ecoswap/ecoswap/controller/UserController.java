package com.ecoswap.ecoswap.controller;

import com.ecoswap.ecoswap.model.User;
import com.ecoswap.ecoswap.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController 
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    // 🚩 Endpoint para ver el perfil de CUALQUIER usuario (público)
    // Requisito: Mostrar el perfil de usuarios registrados.
    // Ruta: GET /api/users/{id}
    @GetMapping("/{id}")
    public ResponseEntity<?> getPublicProfile(@PathVariable Long id) {
        try {
            User user = userService.obtenerUsuarioPorId(id);
            
            // 💡 NOTA: En un caso real, usarías un DTO para NO exponer la contraseña.
            // Para mantener la simplicidad inicial, devolvemos la Entidad.
            return ResponseEntity.ok(user);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }

    // 🚩 Endpoint para obtener el perfil del usuario LOGUEADO
    // Requisito: Gestionar información del perfil.
    // Ruta: GET /api/users/me 
    @GetMapping("/me")
    // Se asume que el ID del usuario se obtendrá del token JWT (principal)
    public ResponseEntity<User> getMyProfile(/* @AuthenticationPrincipal Long userId */) {
        // Implementación futura: Obtener ID del token de seguridad
        Long tempUserId = 1L; // SIMULACIÓN de ID de usuario
        User user = userService.obtenerPerfilLogueado(tempUserId);
        return ResponseEntity.ok(user);
    }

    // 🚩 Endpoint para actualizar el perfil del usuario LOGUEADO
    // Ruta: PUT /api/users/me
    @PutMapping("/me")
    public ResponseEntity<String> updateMyProfile(/* @AuthenticationPrincipal Long userId, @RequestBody User updatedUser */) {
        // Lógica de actualización va aquí, usando el servicio.
        return ResponseEntity.ok("Perfil actualizado correctamente. (Lógica pendiente)");
    }
}