package com.ecoswap.ecoswap.controller;

import com.ecoswap.ecoswap.dto.UsuarioRegistroDTO;
import com.ecoswap.ecoswap.dto.UsuarioLoginDTO;
import com.ecoswap.ecoswap.model.User;
import com.ecoswap.ecoswap.service.AuthService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController 
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public ResponseEntity<User> registerUser(@RequestBody UsuarioRegistroDTO registroDTO) {
       
        User newUser = authService.registrarUsuario(registroDTO);
        return new ResponseEntity<>(newUser, HttpStatus.CREATED);
    }

    @PostMapping("/login")
    public ResponseEntity<String> loginUser(@RequestBody UsuarioLoginDTO loginDTO) {
        
        String jwtToken = authService.loginUsuario(loginDTO);
        return ResponseEntity.ok(jwtToken);
    }
}