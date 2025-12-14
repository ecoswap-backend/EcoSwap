package com.ecoswap.ecoswap.service;

import com.ecoswap.ecoswap.Repositories.UserRepository;
import com.ecoswap.ecoswap.dto.UserUpdateDTO;
import com.ecoswap.ecoswap.exception.InvalidOperationException;
import com.ecoswap.ecoswap.exception.ResourceNotFoundException;
import com.ecoswap.ecoswap.model.Item;
import com.ecoswap.ecoswap.model.User;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;

@Service
public class UserService {

    private final UserRepository userRepository; 
    private final PasswordEncoder passwordEncoder; 
    private final Path storageLocation = Paths.get("uploads").toAbsolutePath().normalize();

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        
        try {
            Files.createDirectories(this.storageLocation);
        } catch (IOException e) {
            System.err.println("No se pudo crear el directorio de almacenamiento: " + e.getMessage());
        }
    }
  
    private String storeFile(MultipartFile file) {
        if (file == null || file.isEmpty()) return null;
        try {
            String fileName = UUID.randomUUID().toString() + "_" + file.getOriginalFilename();
            Path targetLocation = this.storageLocation.resolve(fileName);
            Files.copy(file.getInputStream(), targetLocation);
            return fileName; 
        } catch (IOException ex) {
            throw new InvalidOperationException("No se pudo almacenar el archivo: " + ex.getMessage());
        }
    }
    
    private void deleteFile(String fileName) {
        if (fileName != null) {
            try {
                Path filePath = this.storageLocation.resolve(fileName).normalize();
                Files.deleteIfExists(filePath);
            } catch (IOException e) {
                System.err.println("Error al eliminar archivo: " + e.getMessage());
            }
        }
    }

    @Transactional(readOnly = true)
    public User obtenerUsuarioPorId(Long id) {
        return userRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado con ID: " + id));
    }

    @Transactional(readOnly = true) 
    public User obtenerPerfilLogueado(String userEmail) {
        User user = userRepository.findByMail(userEmail);
        if (user == null) {
            throw new ResourceNotFoundException("Usuario logueado no encontrado con correo: " + userEmail);
        }
        return user;
    }

    @Transactional
    public User actualizarPerfil(String userEmail, UserUpdateDTO updateDTO, MultipartFile imagenPerfil) {
        User user = obtenerPerfilLogueado(userEmail);
        String currentEmail = user.getMail();

        if (updateDTO.getNombre() != null) {
            user.setNombre(updateDTO.getNombre());
        }

        if (updateDTO.getMail() != null && !updateDTO.getMail().equalsIgnoreCase(currentEmail)) {
            if (userRepository.findByMail(updateDTO.getMail()) != null) {
                throw new InvalidOperationException("El nuevo correo ya está en uso.");
            }
            user.setMail(updateDTO.getMail());
        }

        if (updateDTO.getContrasenaActual() != null && updateDTO.getNuevaContrasena() != null) {
            if (!passwordEncoder.matches(updateDTO.getContrasenaActual(), user.getContrasena())) {
                throw new InvalidOperationException("Contraseña actual incorrecta.");
            }
            
            String encodedPassword = passwordEncoder.encode(updateDTO.getNuevaContrasena());
            user.setContrasena(encodedPassword);
        }

        if (imagenPerfil != null && !imagenPerfil.isEmpty()) {
            deleteFile(user.getImagenPerfil());
            String newImagePath = storeFile(imagenPerfil);
            user.setImagenPerfil(newImagePath);
        } else if ("true".equalsIgnoreCase(updateDTO.getEliminarImagenActual())) {
            deleteFile(user.getImagenPerfil());
            user.setImagenPerfil(null);
        }
        
        return userRepository.save(user);
    }

    /**
     * @param userEmail 
     * @return 
     */
    @Transactional(readOnly = true) 
    public List<Item> obtenerItemsPublicadosPorUsuario(String userEmail) {
        User user = userRepository.findByMail(userEmail);
        if (user == null) {
            throw new ResourceNotFoundException("Usuario logueado no encontrado con correo: " + userEmail);
        }
      
        user.getArticulosCreados().size(); 
        return user.getArticulosCreados();
    }
}