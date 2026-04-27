package com.app.dao;

import com.app.model.entity.Usuario;
import java.util.List;
import java.util.Optional;

/**
 * Extiende GenericDAO añadiendo búsquedas específicas de Usuario.
 */
public interface UsuarioDAO extends GenericDAO<Usuario, Integer> {
    List<Usuario> findByNombre(String nombre);
    Optional<Usuario> findByUsername(String username);
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
    boolean updateLastLoginAt(int idUsuario);
}