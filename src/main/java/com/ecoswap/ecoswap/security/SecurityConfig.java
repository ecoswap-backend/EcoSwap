package com.ecoswap.ecoswap.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod; 
import org.springframework.security.authentication.AuthenticationManager; 
import org.springframework.security.authentication.ProviderManager; 
import org.springframework.security.authentication.dao.DaoAuthenticationProvider; 
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer; 
import org.springframework.security.config.http.SessionCreationPolicy; 
import org.springframework.security.core.userdetails.UserDetailsService; 
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity 
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final UserDetailsService userDetailsService; 

    public SecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter, UserDetailsService userDetailsService) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
        this.userDetailsService = userDetailsService;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(); 
    }
    
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
            .csrf(AbstractHttpConfigurer::disable) 
            .sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)) 
            .authorizeHttpRequests(authorize -> authorize
                
                // Rutas Públicas: Auth, Listado de Artículos y Perfiles Públicos
                .requestMatchers("/api/auth/**").permitAll() 
                .requestMatchers(HttpMethod.GET, "/api/items").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/items/{itemId}").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/users/{id}").permitAll() 
                .requestMatchers("/uploads/**").permitAll()
                // Rutas Protegidas (Requieren autenticación)
                // Se asegura que todas las operaciones de artículos (POST, PUT, DELETE, /reserve, /complete)
                // y la gestión de perfil logueado estén protegidas.
                .anyRequest().authenticated()
            );

        http.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
            
        return http.build();
    }
}