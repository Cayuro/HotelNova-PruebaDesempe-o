package com.app.service;

import com.app.model.entity.Habitacion;

import java.util.List;
import java.util.Optional;

public interface HabitacionService {
    Habitacion crear(Habitacion habitacion);
    List<Habitacion> listarTodas();
    Optional<Habitacion> buscarPorId(int idHabitacion);
    boolean actualizar(Habitacion habitacion);
    boolean eliminar(int idHabitacion);
    List<Habitacion> listarActivas();
}
