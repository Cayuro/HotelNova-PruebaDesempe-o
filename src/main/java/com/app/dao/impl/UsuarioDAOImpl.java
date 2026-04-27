package com.app.dao.impl;

import com.app.dao.UsuarioDAO;
import com.app.model.entity.Usuario;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class UsuarioDAOImpl extends GenericDAOImpl<Usuario, Integer>
        implements UsuarioDAO {

    // ── SQL ──
    private static final String INSERT     = "INSERT INTO usuarios (nombre, email, username, password_hash, role, activo) VALUES (?, ?, ?, ?, ?, ?)";
    private static final String UPDATE     = "UPDATE usuarios SET nombre=?, email=?, username=?, password_hash=?, role=?, activo=? WHERE id=?";
    private static final String DELETE     = "DELETE FROM usuarios WHERE id=?";
    private static final String FIND_BY_ID = "SELECT * FROM usuarios WHERE id=?";
    private static final String FIND_ALL   = "SELECT * FROM usuarios";
    private static final String FIND_BY_NAME = "SELECT * FROM usuarios WHERE nombre LIKE ?";
    private static final String FIND_BY_USERNAME = "SELECT * FROM usuarios WHERE username = ?";
    private static final String EXISTS_USERNAME = "SELECT COUNT(*) FROM usuarios WHERE username = ?";
    private static final String EXISTS_EMAIL = "SELECT COUNT(*) FROM usuarios WHERE email=?";
    private static final String UPDATE_LAST_LOGIN = "UPDATE usuarios SET last_login_at = CURRENT_TIMESTAMP WHERE id = ?";

    private static java.time.LocalDateTime toLocalDateTime(Timestamp timestamp) {
        return timestamp == null ? null : timestamp.toLocalDateTime();
    }

    // ── Mapeo ResultSet → Entidad ──
    @Override
    protected Usuario mapRow(ResultSet rs) throws SQLException {
        return new Usuario(
                rs.getInt("id"),
                rs.getString("nombre"),
                rs.getString("email"),
                rs.getString("username"),
                rs.getString("password_hash"),
                rs.getString("role"),
                rs.getBoolean("activo"),
                toLocalDateTime(rs.getTimestamp("last_login_at")),
                toLocalDateTime(rs.getTimestamp("created_at")),
                toLocalDateTime(rs.getTimestamp("updated_at"))
        );
    }

    // ── Parámetros SQL ──
    @Override protected String getInsertSQL()   { return INSERT; }
    @Override protected String getUpdateSQL()   { return UPDATE; }
    @Override protected String getDeleteSQL()   { return DELETE; }
    @Override protected String getFindByIdSQL() { return FIND_BY_ID; }
    @Override protected String getFindAllSQL()  { return FIND_ALL; }

    @Override
    protected void setInsertParams(PreparedStatement ps, Usuario u) throws SQLException {
        ps.setString(1, u.getNombre());
        ps.setString(2, u.getEmail());
        ps.setString(3, u.getUsername());
        ps.setString(4, u.getPasswordHash());
        ps.setString(5, u.getRole());
        ps.setBoolean(6, u.isActivo());
    }

    @Override
    protected void setUpdateParams(PreparedStatement ps, Usuario u) throws SQLException {
        ps.setString(1, u.getNombre());
        ps.setString(2, u.getEmail());
        ps.setString(3, u.getUsername());
        ps.setString(4, u.getPasswordHash());
        ps.setString(5, u.getRole());
        ps.setBoolean(6, u.isActivo());
        ps.setInt(7, u.getId());
    }
    @Override
    protected void setDeleteParam(PreparedStatement ps, Integer id) throws SQLException {
        ps.setInt(1, id);
    }
    @Override
    protected void setFindByIdParam(PreparedStatement ps, Integer id) throws SQLException {
        ps.setInt(1, id);
    }

    // ── save devuelve el objeto con el ID generado ──
    @Override
    public Usuario save(Usuario u) {
        try (Connection conn = cm.getConnection();
             PreparedStatement ps = conn.prepareStatement(
                     INSERT, Statement.RETURN_GENERATED_KEYS)) {

            setInsertParams(ps, u);
            ps.executeUpdate();

            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) u.setId(keys.getInt(1));
            }
            return u;
        } catch (SQLException e) {
            throw new RuntimeException("Error en save(Usuario)", e);
        }
    }

    // ── Métodos específicos de UsuarioDAO ──
    @Override
    public List<Usuario> findByNombre(String nombre) {
        List<Usuario> list = new ArrayList<>();
        try (Connection conn = cm.getConnection();
             PreparedStatement ps = conn.prepareStatement(FIND_BY_NAME)) {

            ps.setString(1, "%" + nombre + "%");
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(mapRow(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error en findByNombre", e);
        }
        return list;
    }

    @Override
    public Optional<Usuario> findByUsername(String username) {
        try (Connection conn = cm.getConnection();
             PreparedStatement ps = conn.prepareStatement(FIND_BY_USERNAME)) {

            ps.setString(1, username);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? Optional.of(mapRow(rs)) : Optional.empty();
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error en findByUsername", e);
        }
    }

    @Override
    public boolean existsByUsername(String username) {
        try (Connection conn = cm.getConnection();
             PreparedStatement ps = conn.prepareStatement(EXISTS_USERNAME)) {

            ps.setString(1, username);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() && rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error en existsByUsername", e);
        }
    }

    @Override
    public boolean existsByEmail(String email) {
        try (Connection conn = cm.getConnection();
             PreparedStatement ps = conn.prepareStatement(EXISTS_EMAIL)) {

            ps.setString(1, email);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() && rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error en existsByEmail", e);
        }
    }

    @Override
    public boolean updateLastLoginAt(int idUsuario) {
        try (Connection conn = cm.getConnection();
             PreparedStatement ps = conn.prepareStatement(UPDATE_LAST_LOGIN)) {
            ps.setInt(1, idUsuario);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Error en updateLastLoginAt", e);
        }
    }
}