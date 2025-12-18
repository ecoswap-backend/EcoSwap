package com.ecoswap.ecoswap.service;

import com.ecoswap.ecoswap.Repositories.UserRepository;
import com.ecoswap.ecoswap.dto.UsuarioLoginDTO;
import com.ecoswap.ecoswap.dto.UsuarioRegistroDTO;
import com.ecoswap.ecoswap.exception.InvalidCredentialsException;
import com.ecoswap.ecoswap.exception.UserAlreadyExistsException;
import com.ecoswap.ecoswap.model.User;
import com.ecoswap.ecoswap.security.JwtTokenProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtTokenProvider jwtTokenProvider;

    @Mock
    private AuthenticationManager authenticationManager;

    @InjectMocks
    private AuthService authService;

    private UsuarioRegistroDTO registroDTO;
    private UsuarioLoginDTO loginDTO;
    private User mockUser;

    @BeforeEach
    void setUp() {
        registroDTO = new UsuarioRegistroDTO();
        registroDTO.setNombre("Test User");
        registroDTO.setMail("test@example.com");
        registroDTO.setContrasena("password123");

        loginDTO = new UsuarioLoginDTO("test@example.com", "password123");

        mockUser = new User();
        mockUser.setId(1L);
        mockUser.setNombre("Test User");
        mockUser.setMail("test@example.com");
        mockUser.setContrasena("encodedPassword");
        mockUser.setPuntos(0);
    }

    @Test
    void registrarUsuario_Success() {
        when(userRepository.findByMail(registroDTO.getMail())).thenReturn(null);
        when(passwordEncoder.encode(registroDTO.getContrasena())).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(mockUser);

        User result = authService.registrarUsuario(registroDTO);

        assertNotNull(result);
        assertEquals("Test User", result.getNombre());
        assertEquals("test@example.com", result.getMail());
        verify(userRepository).save(any(User.class));
        verify(passwordEncoder).encode("password123");
    }

    @Test
    void registrarUsuario_UserAlreadyExists() {
        when(userRepository.findByMail(registroDTO.getMail())).thenReturn(mockUser);

        assertThrows(UserAlreadyExistsException.class, () -> {
            authService.registrarUsuario(registroDTO);
        });

        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void registrarUsuario_WithImagenPerfil() {
        registroDTO.setImagenPerfil("profile.jpg");
        when(userRepository.findByMail(registroDTO.getMail())).thenReturn(null);
        when(passwordEncoder.encode(registroDTO.getContrasena())).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(mockUser);

        User result = authService.registrarUsuario(registroDTO);

        assertNotNull(result);
        verify(userRepository).save(any(User.class));
    }

    @Test
    void loginUsuario_Success() {
        Authentication mockAuth = mock(Authentication.class);
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(mockAuth);
        when(jwtTokenProvider.generarToken(loginDTO.getMail())).thenReturn("jwt-token-123");

        String token = authService.loginUsuario(loginDTO);

        assertNotNull(token);
        assertEquals("jwt-token-123", token);
        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(jwtTokenProvider).generarToken(loginDTO.getMail());
    }

    @Test
    void loginUsuario_InvalidCredentials() {
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new org.springframework.security.authentication.BadCredentialsException("Bad credentials"));

        assertThrows(InvalidCredentialsException.class, () -> {
            authService.loginUsuario(loginDTO);
        });

        verify(jwtTokenProvider, never()).generarToken(anyString());
    }
}
