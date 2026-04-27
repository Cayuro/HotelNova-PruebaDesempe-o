package com.app.dao.impl;

import com.app.dao.ReservaDAO;
import com.app.model.entity.Reserva;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class ReservaDAOImpl extends GenericDAOImpl<Reserva, Integer> implements ReservaDAO {

    private static final String INSERT = "INSERT INTO reservas (id_habitacion, id_huesped, check_in, check_out, estado, total) VALUES (?, ?, ?, ?, ?, ?)";
    private static final String UPDATE = "UPDATE reservas SET id_habitacion=?, id_huesped=?, check_in=?, check_out=?, estado=?, total=? WHERE id=?";
    private static final String DELETE = "DELETE FROM reservas WHERE id=?";
    private static final String FIND_BY_ID = "SELECT id, id_habitacion, id_huesped, check_in, check_out, estado, total FROM reservas WHERE id=?";
    private static final String FIND_ALL = "SELECT id, id_habitacion, id_huesped, check_in, check_out, estado, total FROM reservas";
    private static final String EXISTS_OVERLAP = "SELECT COUNT(*) FROM reservas WHERE id_habitacion = ? AND estado IN ('BOOKED', 'CHECKED_IN') AND ? < check_out AND ? > check_in";
    private static final String FIND_BY_HABITACION = "SELECT id, id_habitacion, id_huesped, check_in, check_out, estado, total FROM reservas WHERE id_habitacion = ? ORDER BY check_in DESC";
    private static final String FIND_BY_HUESPED = "SELECT id, id_habitacion, id_huesped, check_in, check_out, estado, total FROM reservas WHERE id_huesped = ? ORDER BY check_in DESC";
    private static final String FIND_BY_ESTADO = "SELECT id, id_habitacion, id_huesped, check_in, check_out, estado, total FROM reservas WHERE estado = ? ORDER BY check_in DESC";
    private static final String UPDATE_ESTADO = "UPDATE reservas SET estado=? WHERE id=?";

    @Override
    protected Reserva mapRow(ResultSet rs) throws SQLException {
        return new Reserva(
                rs.getInt("id"),
                rs.getInt("id_habitacion"),
                rs.getInt("id_huesped"),
                rs.getDate("check_in").toLocalDate(),
                rs.getDate("check_out").toLocalDate(),
                rs.getString("estado"),
                rs.getBigDecimal("total")
        );
    }

    @Override protected String getInsertSQL() { return INSERT; }
    @Override protected String getUpdateSQL() { return UPDATE; }
    @Override protected String getDeleteSQL() { return DELETE; }
    @Override protected String getFindByIdSQL() { return FIND_BY_ID; }
    @Override protected String getFindAllSQL() { return FIND_ALL; }

    @Override
    protected void setInsertParams(PreparedStatement ps, Reserva entity) throws SQLException {
        ps.setInt(1, entity.getIdHabitacion());
        ps.setInt(2, entity.getIdHuesped());
        ps.setDate(3, java.sql.Date.valueOf(entity.getCheckIn()));
        ps.setDate(4, java.sql.Date.valueOf(entity.getCheckOut()));
        ps.setString(5, entity.getEstado());
        ps.setBigDecimal(6, entity.getTotal());
    }

    @Override
    protected void setUpdateParams(PreparedStatement ps, Reserva entity) throws SQLException {
        ps.setInt(1, entity.getIdHabitacion());
        ps.setInt(2, entity.getIdHuesped());
        ps.setDate(3, java.sql.Date.valueOf(entity.getCheckIn()));
        ps.setDate(4, java.sql.Date.valueOf(entity.getCheckOut()));
        ps.setString(5, entity.getEstado());
        ps.setBigDecimal(6, entity.getTotal());
        ps.setInt(7, entity.getId());
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
    public Reserva save(Reserva entity) {
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
            throw new RuntimeException("Error en save(Reserva)", e);
        }
    }

    @Override
    public boolean existsOverlap(int idHabitacion, LocalDate checkIn, LocalDate checkOut) {
        try (Connection conn = cm.getConnection();
             PreparedStatement ps = conn.prepareStatement(EXISTS_OVERLAP)) {

            ps.setInt(1, idHabitacion);
            ps.setDate(2, java.sql.Date.valueOf(checkIn));
            ps.setDate(3, java.sql.Date.valueOf(checkOut));

            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() && rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error en existsOverlap", e);
        }
    }

    @Override
    public List<Reserva> findByHabitacion(int idHabitacion) {
        List<Reserva> list = new ArrayList<>();
        try (Connection conn = cm.getConnection();
             PreparedStatement ps = conn.prepareStatement(FIND_BY_HABITACION)) {

            ps.setInt(1, idHabitacion);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapRow(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error en findByHabitacion", e);
        }
        return list;
    }

    @Override
    public List<Reserva> findByHuesped(int idHuesped) {
        List<Reserva> list = new ArrayList<>();
        try (Connection conn = cm.getConnection();
             PreparedStatement ps = conn.prepareStatement(FIND_BY_HUESPED)) {

            ps.setInt(1, idHuesped);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapRow(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error en findByHuesped", e);
        }
        return list;
    }

    @Override
    public List<Reserva> findByEstado(String estado) {
        List<Reserva> list = new ArrayList<>();
        try (Connection conn = cm.getConnection();
             PreparedStatement ps = conn.prepareStatement(FIND_BY_ESTADO)) {

            ps.setString(1, estado);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapRow(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error en findByEstado", e);
        }
        return list;
    }

    @Override
    public boolean updateEstado(int idReserva, String estado) {
        try (Connection conn = cm.getConnection();
             PreparedStatement ps = conn.prepareStatement(UPDATE_ESTADO)) {

            ps.setString(1, estado);
            ps.setInt(2, idReserva);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Error en updateEstado", e);
        }
    }
}
