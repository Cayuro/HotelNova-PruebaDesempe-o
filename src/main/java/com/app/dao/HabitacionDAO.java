package com.app.dao;

import com.app.model.entity.Habitacion;

import java.util.List;
import java.util.Optional;

public interface HabitacionDAO extends GenericDAO<Habitacion, Integer> {
    Optional<Habitacion> findByNumero(String numero);
    boolean existsByNumero(String numero);
    List<Habitacion> findByActiva(boolean activa);
}
