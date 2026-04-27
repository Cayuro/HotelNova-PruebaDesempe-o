package com.app.dao;

import java.util.List;
import java.time.LocalDate;

import com.app.model.entity.Reserva;

public interface ReservationDAO extends GenericDAO<Reserva, Integer>{
    boolean existsOverlappingReservation(int idHabitacion, LocalDate checkIn, LocalDate checkOut);
    List<Reserva> findByHuespedId(int idHuesped);
    List<Reserva> findByHabitacionId(int idHabitacion);
    boolean updateEstado(int id, String nuevoEstado);
}
