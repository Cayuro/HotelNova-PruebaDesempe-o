package com.app.service.impl;

import com.app.dao.UsuarioDAO;
import com.app.model.entity.Usuario;
import com.app.service.AuthService;
import com.app.util.PasswordHasher;

import java.util.Optional;

public class AuthServiceImpl implements AuthService {

    private final UsuarioDAO usuarioDAO;

    public AuthServiceImpl(UsuarioDAO usuarioDAO) {
        this.usuarioDAO = usuarioDAO;
    }

    @Override
    public Optional<Usuario> login(String username, String rawPassword) {
        if (username == null || username.isBlank() || rawPassword == null || rawPassword.isBlank()) {
            return Optional.empty();
        }

        Optional<Usuario> opt = usuarioDAO.findByUsername(username.trim());
        if (opt.isEmpty()) {
            return Optional.empty();
        }

        Usuario usuario = opt.get();
        if (!usuario.isActivo()) {
            return Optional.empty();
        }

        if (!PasswordHasher.matches(rawPassword, usuario.getPasswordHash())) {
            return Optional.empty();
        }

        usuarioDAO.updateLastLoginAt(usuario.getId());
        usuario.setLastLoginAt(java.time.LocalDateTime.now());
        return Optional.of(usuario);
    }
}
