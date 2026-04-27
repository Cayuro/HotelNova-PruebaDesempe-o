package com.app.dao;

import com.app.model.entity.Reserva;

import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

public interface ReservaDAO extends GenericDAO<Reserva, Integer> {
    boolean existsOverlap(int idHabitacion, LocalDate checkIn, LocalDate checkOut);
    List<Reserva> findByHabitacion(int idHabitacion);
    List<Reserva> findByHuesped(int idHuesped);
    List<Reserva> findByEstado(String estado);
    boolean updateEstado(int idReserva, String estado);
    
    // Métodos transaccionales que aceptan Connection explícita
    boolean updateEstadoWithConnection(Connection conn, int idReserva, String estado) throws SQLException;
}
