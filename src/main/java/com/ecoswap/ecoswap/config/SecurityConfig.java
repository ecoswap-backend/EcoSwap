package com.ecoswap.ecoswap.config;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager; // NUEVO IMPORT
import org.springframework.security.authentication.ProviderManager; // NUEVO IMPORT
import org.springframework.security.authentication.dao.DaoAuthenticationProvider; // NUEVO IMPORT
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy; // NUEVO IMPORT
import org.springframework.security.core.userdetails.UserDetailsService; // NUEVO IMPORT
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter; // NUEVO IMPORT

import com.ecoswap.ecoswap.security.JwtAuthenticationFilter;

@Configuration
@EnableWebSecurity 
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final UserDetailsService userDetailsService; // CustomUserDetailsService

    public SecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter, UserDetailsService userDetailsService) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
        this.userDetailsService = userDetailsService;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(); 
    }
    
    // 💡 Bean necesario para que Spring Security maneje la autenticación de login
    @Bean
    public AuthenticationManager authenticationManager(PasswordEncoder passwordEncoder) {
        DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider();
        authenticationProvider.setUserDetailsService(userDetailsService);
        authenticationProvider.setPasswordEncoder(passwordEncoder);
        return new ProviderManager(authenticationProvider);
    }
    
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable()) 
            .sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)) // 💡 Usar JWT (sin sesiones)
            .authorizeHttpRequests(authorize -> authorize
                // Rutas públicas (Registro, Login, Ver Artículos/Filtros)
                .requestMatchers("/api/auth/**", "/api/items/**").permitAll() 
                // Rutas protegidas (Perfil del usuario logueado, crear/reservar/completar items)
                .requestMatchers("/api/users/me").authenticated()
                .requestMatchers("/api/items").authenticated() // Asumimos que el POST está protegido
                .requestMatchers("/api/items/*/reserve").authenticated() 
                .requestMatchers("/api/items/*/complete").authenticated() 
                // Cualquier otra solicitud requiere autenticación
                .anyRequest().authenticated()
            );

        // 💡 Añadir el filtro JWT antes del filtro de autenticación por defecto de Spring
        http.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
            
        return http.build();
    }
}