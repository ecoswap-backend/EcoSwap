package com.ecoswap.ecoswap.controller;

import com.ecoswap.ecoswap.dto.UserUpdateDTO;
import com.ecoswap.ecoswap.model.User;
import com.ecoswap.ecoswap.model.Item; // Importar Item
import com.ecoswap.ecoswap.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.util.List; // Importar List

@RestController 
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<User> getPublicProfile(@PathVariable Long id) {
        User user = userService.obtenerUsuarioPorId(id);
        return ResponseEntity.ok(user);
    }

    // ENDPOINT PARA OBTENER EL PERFIL LOGUEADO
    @GetMapping("/me")
    public ResponseEntity<User> getMyProfile(@AuthenticationPrincipal UserDetails userDetails) {
        // *** CAMBIO: Comprobación de seguridad para evitar NullPointerException ***
        if (userDetails == null) {
            return ResponseEntity.status(403).build(); // 403 Forbidden si no hay usuario autenticado
        }
        
        String userEmail = userDetails.getUsername(); 
        User user = userService.obtenerPerfilLogueado(userEmail);
        return ResponseEntity.ok(user);
    }

    // ENDPOINT NUEVO: OBTENER ARTÍCULOS PUBLICADOS POR EL USUARIO
    @GetMapping("/me/items")
    public ResponseEntity<List<Item>> getMyPublishedItems(@AuthenticationPrincipal UserDetails userDetails) {
        if (userDetails == null) {
            return ResponseEntity.status(403).build();
        }
        
        String userEmail = userDetails.getUsername();
        // Llama al método en UserService (que ahora devuelve una lista vacía y no falla)
        List<Item> items = userService.obtenerItemsPublicadosPorUsuario(userEmail); 
        return ResponseEntity.ok(items);
    }
    
    // ... (El resto de los métodos como updateMyProfile son iguales)
    @PutMapping(value = "/me", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<User> updateMyProfile(
        @Valid @ModelAttribute UserUpdateDTO updateDTO,
        @RequestPart(value = "imagenPerfil", required = false) MultipartFile imagenPerfil,
        @AuthenticationPrincipal UserDetails userDetails) {

        if (userDetails == null) {
            return ResponseEntity.status(403).build();
        }

        String userEmail = userDetails.getUsername();
        User updatedUser = userService.actualizarPerfil(userEmail, updateDTO, imagenPerfil);

        return ResponseEntity.ok(updatedUser);
    }
}