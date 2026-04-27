package com.app.service;

import com.app.dao.UsuarioDAO;
import com.app.model.entity.Usuario;
import com.app.service.impl.AuthServiceImpl;
import com.app.util.PasswordHasher;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AuthServiceImplTest {

    private InMemoryUsuarioDAO usuarioDAO;
    private AuthService authService;

    @BeforeEach
    void setUp() {
        usuarioDAO = new InMemoryUsuarioDAO();
        authService = new AuthServiceImpl(usuarioDAO);
    }

    @Test
    void shouldLoginWhenCredentialsAreValidAndUserIsActive() {
        Usuario u = new Usuario(0, "Admin", "admin@mail.com");
        u.setUsername("admin");
        u.setPasswordHash(PasswordHasher.hash("123456"));
        u.setRole("ADMIN");
        u.setActivo(true);
        usuarioDAO.save(u);

        Optional<Usuario> logged = authService.login("admin", "123456");

        assertTrue(logged.isPresent());
        assertTrue(usuarioDAO.lastLoginUpdatedFor(u.getId()));
    }

    @Test
    void shouldRejectLoginWhenPasswordIsInvalid() {
        Usuario u = new Usuario(0, "Admin", "admin@mail.com");
        u.setUsername("admin");
        u.setPasswordHash(PasswordHasher.hash("123456"));
        u.setRole("ADMIN");
        u.setActivo(true);
        usuarioDAO.save(u);

        Optional<Usuario> logged = authService.login("admin", "bad-password");

        assertFalse(logged.isPresent());
    }

    @Test
    void shouldRejectLoginWhenUserIsInactive() {
        Usuario u = new Usuario(0, "Recepcion", "recep@mail.com");
        u.setUsername("recep");
        u.setPasswordHash(PasswordHasher.hash("clave"));
        u.setRole("RECEPCIONISTA");
        u.setActivo(false);
        usuarioDAO.save(u);

        Optional<Usuario> logged = authService.login("recep", "clave");

        assertFalse(logged.isPresent());
    }

    private static class InMemoryUsuarioDAO implements UsuarioDAO {
        private final Map<Integer, Usuario> storage = new HashMap<>();
        private final List<Integer> lastLoginUpdatedIds = new ArrayList<>();
        private int sequence = 1;

        @Override
        public Usuario save(Usuario entity) {
            if (entity.getId() <= 0) {
                entity.setId(sequence++);
            }
            storage.put(entity.getId(), entity);
            return entity;
        }

        @Override
        public Optional<Usuario> findById(Integer id) {
            return Optional.ofNullable(storage.get(id));
        }

        @Override
        public List<Usuario> findAll() {
            return new ArrayList<>(storage.values());
        }

        @Override
        public boolean update(Usuario entity) {
            storage.put(entity.getId(), entity);
            return true;
        }

        @Override
        public boolean deleteById(Integer id) {
            return storage.remove(id) != null;
        }

        @Override
        public List<Usuario> findByNombre(String nombre) {
            return storage.values().stream().filter(u -> u.getNombre().contains(nombre)).toList();
        }

        @Override
        public Optional<Usuario> findByUsername(String username) {
            return storage.values().stream().filter(u -> username.equals(u.getUsername())).findFirst();
        }

        @Override
        public boolean existsByUsername(String username) {
            return storage.values().stream().anyMatch(u -> username.equals(u.getUsername()));
        }

        @Override
        public boolean existsByEmail(String email) {
            return storage.values().stream().anyMatch(u -> email.equals(u.getEmail()));
        }

        @Override
        public boolean updateLastLoginAt(int idUsuario) {
            if (!storage.containsKey(idUsuario)) {
                return false;
            }
            lastLoginUpdatedIds.add(idUsuario);
            Usuario u = storage.get(idUsuario);
            u.setLastLoginAt(java.time.LocalDateTime.now());
            return true;
        }

        boolean lastLoginUpdatedFor(int id) {
            return lastLoginUpdatedIds.contains(id);
        }
    }
}
