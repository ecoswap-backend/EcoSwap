package com.ecoswap.ecoswap.security;

import com.ecoswap.ecoswap.Repositories.UserRepository;
import com.ecoswap.ecoswap.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String mail) throws UsernameNotFoundException {
        User user = userRepository.findByMail(mail);
        
        if (user == null) {
            throw new UsernameNotFoundException("Usuario no encontrado con el correo: " + mail);
        }
        
        // Mapear tu entidad User a la clase UserDetails de Spring Security
        return org.springframework.security.core.userdetails.User.builder()
                .username(user.getMail())
                .password(user.getContrasena()) // Contraseña ya hasheada
                .roles("USER") // Asignar un rol básico
                .build();
    }
}
