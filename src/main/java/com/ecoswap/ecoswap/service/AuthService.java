package com.ecoswap.ecoswap.service;

import com.ecoswap.ecoswap.Repositories.UserRepository;
import com.ecoswap.ecoswap.dto.UsuarioLoginDTO;
import com.ecoswap.ecoswap.dto.UsuarioRegistroDTO;
import com.ecoswap.ecoswap.model.User;
import com.ecoswap.ecoswap.security.JwtTokenProvider;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder; 
    private final JwtTokenProvider jwtTokenProvider; 

    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtTokenProvider jwtTokenProvider) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtTokenProvider = jwtTokenProvider; 
    }

   
    public String loginUsuario(UsuarioLoginDTO loginDTO) {
        User user = userRepository.findByMail(loginDTO.getMail());

        if (user == null || !passwordEncoder.matches(loginDTO.getContrasena(), user.getContrasena())) {
            throw new RuntimeException("Credenciales inválidas.");
        }
        
        return jwtTokenProvider.generarToken(user.getMail());
    }

    public User registrarUsuario(UsuarioRegistroDTO registroDTO) {
        
        throw new UnsupportedOperationException("Unimplemented method 'registrarUsuario'");
    }
}