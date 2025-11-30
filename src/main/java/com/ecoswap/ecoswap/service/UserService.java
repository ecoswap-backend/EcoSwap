package com.ecoswap.ecoswap.service;

import com.ecoswap.ecoswap.Repositories.UserRepository;
import com.ecoswap.ecoswap.model.User;

import org.apache.velocity.exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private final UserRepository userRepository; 

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User obtenerUsuarioPorId(Long id) {
   
        return userRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado con ID: " + id));
    }

    public User obtenerPerfilLogueado(String userEmail) {

        User user = userRepository.findByMail(userEmail);
        if (user == null) {
            throw new ResourceNotFoundException("Usuario logueado no encontrado con correo: " + userEmail);
        }

        return user;
    }

}