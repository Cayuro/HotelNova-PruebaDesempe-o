package com.app.dao.impl;

import java.time.LocalDate;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import com.app.dao.TareaDao;
import com.app.model.entity.Tarea;

public class TareaDAOImpl extends GenericDAOImpl<Tarea, Integer> implements TareaDao {
    // Aquí implementaríamos los métodos específicos de TareaDao
    // SQL --
    private static final String INSERT     = "INSERT INTO tareas (titulo, pendiente, fecha_limite) VALUES (?, ?,?)";
    private static final String UPDATE     = "UPDATE tareas SET titulo=?, pendiente=? WHERE id=?";
    private static final String DELETE     = "DELETE FROM tareas WHERE id=?";
    private static final String FIND_BY_ID = "SELECT * FROM tareas WHERE id=?";
    private static final String FIND_ALL   = "SELECT * FROM tareas";
    private static final String FIND_PENDIENTES = "SELECT * FROM tareas WHERE pendiente = ?";
    private static final String FIND_RETRASADAS = "SELECT * FROM tareas WHERE fecha_limite < ?";

    
    // ── Mapeo ResultSet → Entidad ──
    @Override
    protected Tarea mapRow(ResultSet rs) throws SQLException {
        return new Tarea(
            rs.getInt("id"),
            rs.getString("titulo"),
            rs.getBoolean("pendiente"),
            rs.getDate("fecha_limite").toLocalDate()
        );
    }
    // parametros SQL 
    @Override protected String getInsertSQL()   { return INSERT; }
    @Override protected String getUpdateSQL()   { return UPDATE; }
    @Override protected String getDeleteSQL()   { return DELETE; }
    @Override protected String getFindByIdSQL() { return FIND_BY_ID; }
    @Override protected String getFindAllSQL()  { return FIND_ALL; }


    // METODOS GENERICOS
    @Override protected void setInsertParams(PreparedStatement ps, Tarea t) throws SQLException {
        ps.setString(1, t.getTitulo());
        ps.setBoolean(2, t.isPendiente());
        ps.setDate(3, java.sql.Date.valueOf(t.getFechaLimite()));
     }

    @Override protected void setUpdateParams(PreparedStatement ps, Tarea t) throws SQLException {
        ps.setString(1, t.getTitulo());
        ps.setBoolean(2, t.isPendiente());
        ps.setInt(3, t.getId());
    }
    @Override protected void setDeleteParam(PreparedStatement ps, Integer id) throws SQLException {
        ps.setInt(1, id);
    }
    @Override protected void setFindByIdParam(PreparedStatement ps, Integer id) throws SQLException {
        ps.setInt(1, id);
    }

    // METODOS ESPECIFICOS DE TareaDao
    @Override
    public List<Tarea> findByPendiente(boolean pendiente) {
        List<Tarea> list = new ArrayList<>();
        try (Connection conn = cm.getConnection();
             PreparedStatement ps = conn.prepareStatement(FIND_PENDIENTES)) {

            ps.setBoolean(1, pendiente);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(mapRow(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error en findByPendiente", e);
        }
        return list;
    }

    @Override
    public Tarea save(Tarea entity) {
        try (Connection conn = cm.getConnection();
             PreparedStatement ps = conn.prepareStatement(
                     INSERT, Statement.RETURN_GENERATED_KEYS)) {

            setInsertParams(ps, entity);
            ps.executeUpdate();

            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) entity.setId(keys.getInt(1));
            }
            return entity;
        } catch (SQLException e) {
            throw new RuntimeException("Error en save(Tarea)", e);
        }
    }
    @Override
    public List<Tarea> findRetrasada(LocalDate fecha) {
        List<Tarea> list = new ArrayList<>();
        try (Connection conn = cm.getConnection();
             PreparedStatement ps = conn.prepareStatement(FIND_RETRASADAS)) {
                        
            ps.setDate(1, java.sql.Date.valueOf(fecha));
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(mapRow(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
            
        }
        return list;
    }

}
