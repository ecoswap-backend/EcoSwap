package com.ecoswap.ecoswap.service;

import com.ecoswap.ecoswap.Repositories.UserRepository;
import com.ecoswap.ecoswap.dto.UsuarioLoginDTO;
import com.ecoswap.ecoswap.dto.UsuarioRegistroDTO;
import com.ecoswap.ecoswap.exception.InvalidCredentialsException; 
import com.ecoswap.ecoswap.exception.UserAlreadyExistsException; 
import com.ecoswap.ecoswap.model.User;
import com.ecoswap.ecoswap.security.JwtTokenProvider;

import org.springframework.security.authentication.AuthenticationManager; 
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken; 
import org.springframework.security.core.Authentication; 
import org.springframework.security.core.context.SecurityContextHolder; 
import org.springframework.security.core.AuthenticationException; 
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final AuthenticationManager authenticationManager; 

    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder, 
                       JwtTokenProvider jwtTokenProvider, AuthenticationManager authenticationManager) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtTokenProvider = jwtTokenProvider;
        this.authenticationManager = authenticationManager;
    }

    @Transactional
    public User registrarUsuario(UsuarioRegistroDTO registroDTO) {
    
        if (userRepository.findByMail(registroDTO.getMail()) != null) {
            throw new UserAlreadyExistsException("El correo electrónico ya está registrado.");
        }
        
        User newUser = new User();
        newUser.setNombre(registroDTO.getNombre());
        newUser.setMail(registroDTO.getMail());
        
        String encodedPassword = passwordEncoder.encode(registroDTO.getContrasena());
        newUser.setContrasena(encodedPassword);
        
        // La imagen de perfil es opcional en el DTO, pero se inicializa
        if (registroDTO.getImagenPerfil() != null) {
            // Nota: Aquí no se maneja la subida de imagen de registro, solo se guarda la URL si existe. 
            // Si quieres que el registro también soporte MultipartFile, debes actualizar el DTO, Controller y este Service.
            newUser.setImagenPerfil(registroDTO.getImagenPerfil());
        }
        newUser.setPuntos(0); 

        return userRepository.save(newUser);
    }
    
    public String loginUsuario(UsuarioLoginDTO loginDTO) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                    loginDTO.getMail(), 
                    loginDTO.getContrasena()
                )
            );

            SecurityContextHolder.getContext().setAuthentication(authentication);

            return jwtTokenProvider.generarToken(loginDTO.getMail());
            
        } catch (AuthenticationException e) {
            throw new InvalidCredentialsException("Correo o contraseña incorrectos.");
        }
    }
}