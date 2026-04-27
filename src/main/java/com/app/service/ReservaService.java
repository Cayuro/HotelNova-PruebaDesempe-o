package com.app.service;

import com.app.model.entity.Reserva;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface ReservaService {
    Reserva crearReserva(int idHabitacion, int idHuesped, LocalDate checkIn, LocalDate checkOut);
    Reserva checkIn(int idReserva);
    Reserva checkOut(int idReserva);
    List<Reserva> listarTodas();
    Optional<Reserva> buscarPorId(int idReserva);
    List<Reserva> buscarPorHabitacion(int idHabitacion);
    List<Reserva> buscarPorHuesped(int idHuesped);
    List<Reserva> buscarPorEstado(String estado);
    boolean cambiarEstado(int idReserva, String nuevoEstado);
    boolean eliminarReserva(int idReserva);
}
