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

    
    @GetMapping("/{id}")
    public ResponseEntity<?> getPublicProfile(@PathVariable Long id) {
        try {
            User user = userService.obtenerUsuarioPorId(id);
            
            return ResponseEntity.ok(user);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/me")
 
    public ResponseEntity<User> getMyProfile() {
        Long tempUserId = 1L; 
        User user = userService.obtenerPerfilLogueado(tempUserId);
        return ResponseEntity.ok(user);
    }

    @PutMapping("/me")
    public ResponseEntity<String> updateMyProfile() {
       
        return ResponseEntity.ok("Perfil actualizado correctamente. (Lógica pendiente)");
    }
}