package com.ecoswap.ecoswap.service;

import com.ecoswap.ecoswap.dto.UserUpdateDTO;
import com.ecoswap.ecoswap.model.Item;
import com.ecoswap.ecoswap.model.User;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface IUserService {
    User obtenerUsuarioPorId(Long id);
    User obtenerPerfilLogueado(String userEmail);
    User actualizarPerfil(String userEmail, UserUpdateDTO updateDTO, MultipartFile imagenPerfil);
    List<Item> obtenerItemsPublicadosPorUsuario(String userEmail);
}
