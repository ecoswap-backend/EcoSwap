package com.ecoswap.ecoswap.controller;

import com.ecoswap.ecoswap.dto.UserUpdateDTO;
import com.ecoswap.ecoswap.model.User;
import com.ecoswap.ecoswap.model.Item; 
import com.ecoswap.ecoswap.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.util.List; 

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

    @GetMapping("/me")
    public ResponseEntity<User> getMyProfile(@AuthenticationPrincipal UserDetails userDetails) {
        if (userDetails == null) {
            return ResponseEntity.status(403).build(); 
        }
        
        String userEmail = userDetails.getUsername(); 
        User user = userService.obtenerPerfilLogueado(userEmail);
        return ResponseEntity.ok(user);
    }

    @GetMapping("/me/items")
    public ResponseEntity<List<Item>> getMyPublishedItems(@AuthenticationPrincipal UserDetails userDetails) {
        if (userDetails == null) {
            return ResponseEntity.status(403).build();
        }
        
        String userEmail = userDetails.getUsername();
        List<Item> items = userService.obtenerItemsPublicadosPorUsuario(userEmail); 
        return ResponseEntity.ok(items);
    }
    
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