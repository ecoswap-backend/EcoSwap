package com.ecoswap.ecoswap.service;

import com.ecoswap.ecoswap.Repositories.UserRepository;
import com.ecoswap.ecoswap.model.User;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {

    private final UserRepository userRepository;
    
    // Inyectamos el repositorio
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    // 🚩 LÓGICA DE NEGOCIO: OBTENER PERFIL POR ID
    public User obtenerUsuarioPorId(Long id) {
        Optional<User> userOptional = userRepository.findById(id);
        
        // Manejo de error si el usuario no existe
        if (userOptional.isEmpty()) {
            throw new RuntimeException("Usuario no encontrado con ID: " + id);
        }
        
        return userOptional.get();
    }
    
    // 🚩 LÓGICA DE NEGOCIO: OBTENER PERFIL PROPIO (MÉTODO ESQUELETO)
    // Este método se usaría después de implementar JWT, obteniendo el ID del token.
    public User obtenerPerfilLogueado(Long userId) {
        // Por ahora, simplemente llama al método anterior
        return obtenerUsuarioPorId(userId);
    }

    // 🚩 LÓGICA DE NEGOCIO: ACTUALIZAR PERFIL (MÉTODO ESQUELETO)
    // Requisito: Gestionar nombre, mail, contraseña, imagen.
    /*
    public User actualizarPerfil(Long userId, User updatedUser) {
        User userToUpdate = obtenerUsuarioPorId(userId);
        
        // Aquí se implementarían las validaciones y el guardado
        userToUpdate.setNombre(updatedUser.getNombre());
        userToUpdate.setMail(updatedUser.getMail());
        // ... Lógica para cambiar contraseña (requiere PasswordEncoder)
        
        return userRepository.save(userToUpdate);
    }
    */
}