package com.app.dao.impl;

import com.app.dao.HabitacionDAO;
import com.app.model.entity.Habitacion;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class HabitacionDAOImpl extends GenericDAOImpl<Habitacion, Integer> implements HabitacionDAO {

    private static final String INSERT = "INSERT INTO habitaciones (numero, tipo, precio_por_noche, activa) VALUES (?, ?, ?, ?)";
    private static final String UPDATE = "UPDATE habitaciones SET numero=?, tipo=?, precio_por_noche=?, activa=? WHERE id=?";
    private static final String DELETE = "DELETE FROM habitaciones WHERE id=?";
    private static final String FIND_BY_ID = "SELECT id, numero, tipo, precio_por_noche, activa FROM habitaciones WHERE id=?";
    private static final String FIND_ALL = "SELECT id, numero, tipo, precio_por_noche, activa FROM habitaciones";
    private static final String FIND_BY_NUMERO = "SELECT id, numero, tipo, precio_por_noche, activa FROM habitaciones WHERE numero=?";
    private static final String EXISTS_NUMERO = "SELECT COUNT(*) FROM habitaciones WHERE numero=?";
    private static final String FIND_BY_ACTIVA = "SELECT id, numero, tipo, precio_por_noche, activa FROM habitaciones WHERE activa=?";

    @Override
    protected Habitacion mapRow(ResultSet rs) throws SQLException {
        BigDecimal precioPorNoche = rs.getBigDecimal("precio_por_noche");
        return new Habitacion(
                rs.getInt("id"),
                rs.getString("numero"),
                rs.getString("tipo"),
                precioPorNoche,
                rs.getBoolean("activa")
        );
    }

    @Override protected String getInsertSQL() { return INSERT; }
    @Override protected String getUpdateSQL() { return UPDATE; }
    @Override protected String getDeleteSQL() { return DELETE; }
    @Override protected String getFindByIdSQL() { return FIND_BY_ID; }
    @Override protected String getFindAllSQL() { return FIND_ALL; }

    @Override
    protected void setInsertParams(PreparedStatement ps, Habitacion entity) throws SQLException {
        ps.setString(1, entity.getNumero());
        ps.setString(2, entity.getTipo());
        ps.setBigDecimal(3, entity.getPrecioPorNoche());
        ps.setBoolean(4, entity.isActiva());
    }

    @Override
    protected void setUpdateParams(PreparedStatement ps, Habitacion entity) throws SQLException {
        ps.setString(1, entity.getNumero());
        ps.setString(2, entity.getTipo());
        ps.setBigDecimal(3, entity.getPrecioPorNoche());
        ps.setBoolean(4, entity.isActiva());
        ps.setInt(5, entity.getId());
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
    public Habitacion save(Habitacion entity) {
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
            throw new RuntimeException("Error en save(Habitacion)", e);
        }
    }

    @Override
    public Optional<Habitacion> findByNumero(String numero) {
        try (Connection conn = cm.getConnection();
             PreparedStatement ps = conn.prepareStatement(FIND_BY_NUMERO)) {

            ps.setString(1, numero);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? Optional.of(mapRow(rs)) : Optional.empty();
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error en findByNumero", e);
        }
    }

    @Override
    public boolean existsByNumero(String numero) {
        try (Connection conn = cm.getConnection();
             PreparedStatement ps = conn.prepareStatement(EXISTS_NUMERO)) {

            ps.setString(1, numero);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() && rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error en existsByNumero", e);
        }
    }

    @Override
    public List<Habitacion> findByActiva(boolean activa) {
        List<Habitacion> list = new ArrayList<>();
        try (Connection conn = cm.getConnection();
             PreparedStatement ps = conn.prepareStatement(FIND_BY_ACTIVA)) {

            ps.setBoolean(1, activa);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapRow(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error en findByActiva", e);
        }
        return list;
    }
}
