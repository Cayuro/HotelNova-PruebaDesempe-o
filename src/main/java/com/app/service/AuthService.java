package com.app.service;

import com.app.model.entity.Usuario;

import java.util.Optional;

public interface AuthService {
    Optional<Usuario> login(String username, String rawPassword);
}
