package com.app.dao;

import java.time.LocalDate;
import java.util.List;

import com.app.model.entity.Tarea;

public interface TareaDao extends GenericDAO<Tarea, Integer> {
    // Aquí podríamos agregar métodos específicos para Tarea si es necesario
    List<Tarea> findByPendiente(boolean pendiente);
    List<Tarea> findRetrasada(LocalDate fecha);
}