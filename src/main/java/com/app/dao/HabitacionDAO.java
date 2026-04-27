package com.app.dao;

import com.app.model.entity.Habitacion;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public interface HabitacionDAO extends GenericDAO<Habitacion, Integer> {
    Optional<Habitacion> findByNumero(String numero);
    boolean existsByNumero(String numero);
    List<Habitacion> findByActiva(boolean activa);
    
    // Métodos transaccionales que aceptan Connection explícita
    boolean updateEstadoWithConnection(Connection conn, int idHabitacion, String estado) throws SQLException;
}
