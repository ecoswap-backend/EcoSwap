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

    /**
     * Endpoint para registrar un nuevo usuario.
     * Ruta: POST /api/auth/register
     */
    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody UsuarioRegistroDTO registroDTO) {
        try {
            User newUser = authService.registrarUsuario(registroDTO);
            // Devuelve el usuario creado con el estado 201 CREATED
            return new ResponseEntity<>(newUser, HttpStatus.CREATED);
            
        } catch (RuntimeException e) {
            // Error: Email ya registrado o validación fallida
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * Endpoint para el inicio de sesión.
     * Ruta: POST /api/auth/login
     * Devuelve el Token JWT si las credenciales son correctas.
     */
    @PostMapping("/login")
    public ResponseEntity<?> loginUser(@RequestBody UsuarioLoginDTO loginDTO) {
        try {
            // Llama al servicio para autenticar y generar el token
            String jwtToken = authService.loginUsuario(loginDTO);
            
            // 💡 NOTA: En producción, se devuelve un objeto DTO con el token,
            // pero por simplicidad, devolvemos el token como texto.
            return ResponseEntity.ok(jwtToken);
            
        } catch (RuntimeException e) {
            // Error: Credenciales inválidas
            return new ResponseEntity<>(e.getMessage(), HttpStatus.UNAUTHORIZED);
        }
    }
}