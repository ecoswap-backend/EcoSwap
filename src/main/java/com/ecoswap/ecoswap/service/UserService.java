package com.ecoswap.ecoswap.service;

import com.ecoswap.ecoswap.Repositories.UserRepository;
import com.ecoswap.ecoswap.model.User;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User obtenerUsuarioPorId(Long id) {
        Optional<User> userOptional = userRepository.findById(id);
    
        if (userOptional.isEmpty()) {
            throw new RuntimeException("Usuario no encontrado con ID: " + id);
        }
        
        return userOptional.get();
    }
    
    public User obtenerPerfilLogueado(Long userId) {
    
        return obtenerUsuarioPorId(userId);
    }

}