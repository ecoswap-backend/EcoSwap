package com.ecoswap.ecoswap.service;

import com.ecoswap.ecoswap.Repositories.UserRepository;
import com.ecoswap.ecoswap.dto.UserUpdateDTO;
import com.ecoswap.ecoswap.exception.InvalidOperationException;
import com.ecoswap.ecoswap.exception.ResourceNotFoundException;
import com.ecoswap.ecoswap.model.Item;
import com.ecoswap.ecoswap.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    private User mockUser;

    @BeforeEach
    void setUp() {
        mockUser = new User();
        mockUser.setId(1L);
        mockUser.setNombre("Test User");
        mockUser.setMail("test@example.com");
        mockUser.setContrasena("encodedPassword");
        mockUser.setPuntos(100);
        mockUser.setArticulosCreados(new ArrayList<>());
    }

    @Test
    void obtenerUsuarioPorId_Success() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(mockUser));

        User result = userService.obtenerUsuarioPorId(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Test User", result.getNombre());
        verify(userRepository).findById(1L);
    }

    @Test
    void obtenerUsuarioPorId_NotFound() {
        when(userRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> {
            userService.obtenerUsuarioPorId(999L);
        });
    }

    @Test
    void obtenerPerfilLogueado_Success() {
        when(userRepository.findByMail("test@example.com")).thenReturn(mockUser);

        User result = userService.obtenerPerfilLogueado("test@example.com");

        assertNotNull(result);
        assertEquals("test@example.com", result.getMail());
        verify(userRepository).findByMail("test@example.com");
    }

    @Test
    void obtenerPerfilLogueado_NotFound() {
        when(userRepository.findByMail("notfound@example.com")).thenReturn(null);

        assertThrows(ResourceNotFoundException.class, () -> {
            userService.obtenerPerfilLogueado("notfound@example.com");
        });
    }

    @Test
    void actualizarPerfil_UpdateNombre() {
        UserUpdateDTO updateDTO = new UserUpdateDTO();
        updateDTO.setNombre("Updated Name");

        when(userRepository.findByMail("test@example.com")).thenReturn(mockUser);
        when(userRepository.save(any(User.class))).thenReturn(mockUser);

        User result = userService.actualizarPerfil("test@example.com", updateDTO, null);

        assertNotNull(result);
        verify(userRepository).save(any(User.class));
    }

    @Test
    void actualizarPerfil_UpdateMail_NewMailAlreadyExists() {
        UserUpdateDTO updateDTO = new UserUpdateDTO();
        updateDTO.setMail("existing@example.com");

        User existingUser = new User();
        existingUser.setMail("existing@example.com");

        when(userRepository.findByMail("test@example.com")).thenReturn(mockUser);
        when(userRepository.findByMail("existing@example.com")).thenReturn(existingUser);

        assertThrows(InvalidOperationException.class, () -> {
            userService.actualizarPerfil("test@example.com", updateDTO, null);
        });
    }

    @Test
    void actualizarPerfil_UpdatePassword_Success() {
        UserUpdateDTO updateDTO = new UserUpdateDTO();
        updateDTO.setContrasenaActual("oldPassword");
        updateDTO.setNuevaContrasena("newPassword");

        when(userRepository.findByMail("test@example.com")).thenReturn(mockUser);
        when(passwordEncoder.matches("oldPassword", "encodedPassword")).thenReturn(true);
        when(passwordEncoder.encode("newPassword")).thenReturn("newEncodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(mockUser);

        User result = userService.actualizarPerfil("test@example.com", updateDTO, null);

        assertNotNull(result);
        verify(passwordEncoder).encode("newPassword");
        verify(userRepository).save(any(User.class));
    }

    @Test
    void actualizarPerfil_UpdatePassword_WrongCurrentPassword() {
        UserUpdateDTO updateDTO = new UserUpdateDTO();
        updateDTO.setContrasenaActual("wrongPassword");
        updateDTO.setNuevaContrasena("newPassword");

        when(userRepository.findByMail("test@example.com")).thenReturn(mockUser);
        when(passwordEncoder.matches("wrongPassword", "encodedPassword")).thenReturn(false);

        assertThrows(InvalidOperationException.class, () -> {
            userService.actualizarPerfil("test@example.com", updateDTO, null);
        });

        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void actualizarPerfil_DeleteImage() {
        UserUpdateDTO updateDTO = new UserUpdateDTO();
        updateDTO.setEliminarImagenActual("true");

        mockUser.setImagenPerfil("old-image.jpg");

        when(userRepository.findByMail("test@example.com")).thenReturn(mockUser);
        when(userRepository.save(any(User.class))).thenReturn(mockUser);

        User result = userService.actualizarPerfil("test@example.com", updateDTO, null);

        assertNotNull(result);
        verify(userRepository).save(any(User.class));
    }

    @Test
    void obtenerItemsPublicadosPorUsuario_Success() {
        List<Item> items = new ArrayList<>();
        Item item = new Item();
        item.setId(1L);
        item.setTitulo("Test Item");
        items.add(item);
        mockUser.setArticulosCreados(items);

        when(userRepository.findByMail("test@example.com")).thenReturn(mockUser);

        List<Item> result = userService.obtenerItemsPublicadosPorUsuario("test@example.com");

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Test Item", result.get(0).getTitulo());
    }

    @Test
    void obtenerItemsPublicadosPorUsuario_UserNotFound() {
        when(userRepository.findByMail("notfound@example.com")).thenReturn(null);

        assertThrows(ResourceNotFoundException.class, () -> {
            userService.obtenerItemsPublicadosPorUsuario("notfound@example.com");
        });
    }
}
