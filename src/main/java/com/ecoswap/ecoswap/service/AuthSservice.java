package com.ecoswap.ecoswap.service;

import com.ecoswap.ecoswap.dto.UsuarioLoginDTO;
import com.ecoswap.ecoswap.dto.UsuarioRegistroDTO;
import com.ecoswap.ecoswap.model.User;

public interface AuthSservice {
    User registrarUsuario(UsuarioRegistroDTO registroDTO);
    String loginUsuario(UsuarioLoginDTO loginDTO);
}
