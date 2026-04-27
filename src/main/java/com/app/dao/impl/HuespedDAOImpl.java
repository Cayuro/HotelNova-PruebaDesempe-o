package com.app.dao.impl;

import com.app.dao.HuespedDAO;
import com.app.model.entity.Huesped;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class HuespedDAOImpl extends GenericDAOImpl<Huesped, Integer> implements HuespedDAO {

    private static final String INSERT = "INSERT INTO huespedes (nombre, email, activo) VALUES (?, ?, ?)";
    private static final String UPDATE = "UPDATE huespedes SET nombre=?, email=?, activo=? WHERE id=?";
    private static final String DELETE = "DELETE FROM huespedes WHERE id=?";
    private static final String FIND_BY_ID = "SELECT id, nombre, email, activo FROM huespedes WHERE id=?";
    private static final String FIND_ALL = "SELECT id, nombre, email, activo FROM huespedes";
    private static final String FIND_BY_EMAIL = "SELECT id, nombre, email, activo FROM huespedes WHERE email=?";
    private static final String EXISTS_EMAIL = "SELECT COUNT(*) FROM huespedes WHERE email=?";
    private static final String EXISTS_ACTIVO_BY_ID = "SELECT COUNT(*) FROM huespedes WHERE id=? AND activo = TRUE";
    private static final String FIND_BY_ACTIVO = "SELECT id, nombre, email, activo FROM huespedes WHERE activo=?";

    @Override
    protected Huesped mapRow(ResultSet rs) throws SQLException {
        return new Huesped(
                rs.getInt("id"),
                rs.getString("nombre"),
                rs.getString("email"),
                rs.getBoolean("activo")
        );
    }

    @Override protected String getInsertSQL() { return INSERT; }
    @Override protected String getUpdateSQL() { return UPDATE; }
    @Override protected String getDeleteSQL() { return DELETE; }
    @Override protected String getFindByIdSQL() { return FIND_BY_ID; }
    @Override protected String getFindAllSQL() { return FIND_ALL; }

    @Override
    protected void setInsertParams(PreparedStatement ps, Huesped entity) throws SQLException {
        ps.setString(1, entity.getNombre());
        ps.setString(2, entity.getEmail());
        ps.setBoolean(3, entity.isActivo());
    }

    @Override
    protected void setUpdateParams(PreparedStatement ps, Huesped entity) throws SQLException {
        ps.setString(1, entity.getNombre());
        ps.setString(2, entity.getEmail());
        ps.setBoolean(3, entity.isActivo());
        ps.setInt(4, entity.getId());
    }

    @Override
    protected void setDeleteParam(PreparedStatement ps, Integer id) throws SQLException {
        ps.setInt(1, id);
    }

    @Override
    protected void setFindByIdParam(PreparedStatement ps, Integer id) throws SQLException {
        ps.setInt(1, id);
    }

    @Override
    public Huesped save(Huesped entity) {
        try (Connection conn = cm.getConnection();
             PreparedStatement ps = conn.prepareStatement(INSERT, Statement.RETURN_GENERATED_KEYS)) {

            setInsertParams(ps, entity);
            ps.executeUpdate();

            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    entity.setId(keys.getInt(1));
                }
            }
            return entity;
        } catch (SQLException e) {
            throw new RuntimeException("Error en save(Huesped)", e);
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
    public boolean existsActivoById(Integer idHuesped) {
        try (Connection conn = cm.getConnection();
             PreparedStatement ps = conn.prepareStatement(EXISTS_ACTIVO_BY_ID)) {

            ps.setInt(1, idHuesped);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() && rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error en existsActivoById", e);
        }
    }

    @Override
    public List<Huesped> findByActivo(boolean activo) {
        List<Huesped> list = new ArrayList<>();
        try (Connection conn = cm.getConnection();
             PreparedStatement ps = conn.prepareStatement(FIND_BY_ACTIVO)) {

            ps.setBoolean(1, activo);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapRow(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error en findByActivo", e);
        }
        return list;
    }

    public List<Huesped> findByEmail(String email) {
        List<Huesped> list = new ArrayList<>();
        try (Connection conn = cm.getConnection();
             PreparedStatement ps = conn.prepareStatement(FIND_BY_EMAIL)) {

            ps.setString(1, email);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapRow(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error en findByEmail", e);
        }
        return list;
    }
}
