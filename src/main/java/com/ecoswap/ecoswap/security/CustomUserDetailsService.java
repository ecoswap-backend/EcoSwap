package com.ecoswap.ecoswap.security;

import com.ecoswap.ecoswap.Repositories.UserRepository;
import com.ecoswap.ecoswap.model.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service 
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String mail) throws UsernameNotFoundException {
        User user = userRepository.findByMail(mail);
        
        if (user == null) {
            throw new UsernameNotFoundException("Usuario no encontrado con el correo: " + mail);
        }
    
        // Retorna un UserDetails de Spring Security con un rol simple
        return new org.springframework.security.core.userdetails.User(
                user.getMail(), 
                user.getContrasena(), 
                Collections.singletonList(() -> "ROLE_USER") 
        );
    }
}