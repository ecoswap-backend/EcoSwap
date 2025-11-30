package com.ecoswap.ecoswap.controller;

import com.ecoswap.ecoswap.model.User;
import com.ecoswap.ecoswap.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

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
      
        String userEmail = userDetails.getUsername(); 
        User user = userService.obtenerPerfilLogueado(userEmail);
        return ResponseEntity.ok(user);
    }

    @PutMapping("/me")
    public ResponseEntity<String> updateMyProfile() {
    
        return ResponseEntity.ok("Perfil actualizado correctamente. (Lógica pendiente)");
    }
}