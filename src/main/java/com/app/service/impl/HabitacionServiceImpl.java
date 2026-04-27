package com.app.service.impl;

import com.app.dao.HabitacionDAO;
import com.app.model.entity.Habitacion;
import com.app.service.HabitacionService;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public class HabitacionServiceImpl implements HabitacionService {

    private final HabitacionDAO habitacionDAO;

    public HabitacionServiceImpl(HabitacionDAO habitacionDAO) {
        this.habitacionDAO = habitacionDAO;
    }

    @Override
    public Habitacion crear(Habitacion habitacion) {
        if (habitacion.getNumero() == null || habitacion.getNumero().isBlank()) {
            throw new IllegalArgumentException("Número es requerido.");
        }
        if (habitacionDAO.existsByNumero(habitacion.getNumero())) {
            throw new IllegalStateException("Ya existe una habitación con número " + habitacion.getNumero());
        }
        if (habitacion.getTipo() == null || habitacion.getTipo().isBlank()) {
            throw new IllegalArgumentException("Tipo es requerido.");
        }
        if (habitacion.getPrecioPorNoche() == null || habitacion.getPrecioPorNoche().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("El precio debe ser mayor a 0.");
        }

        return habitacionDAO.save(habitacion);
    }

    @Override
    public List<Habitacion> listarTodas() {
        return habitacionDAO.findAll();
    }

    @Override
    public Optional<Habitacion> buscarPorId(int idHabitacion) {
        return habitacionDAO.findById(idHabitacion);
    }

    @Override
    public boolean actualizar(Habitacion habitacion) {
        if (habitacion.getPrecioPorNoche() != null
                && habitacion.getPrecioPorNoche().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("El precio debe ser mayor a 0.");
        }

        Optional<Habitacion> existente = habitacionDAO.findByNumero(habitacion.getNumero());
        if (existente.isPresent() && existente.get().getId() != habitacion.getId()) {
            throw new IllegalStateException("Ya existe una habitación con número " + habitacion.getNumero());
        }

        return habitacionDAO.update(habitacion);
    }

    @Override
    public boolean eliminar(int idHabitacion) {
        return habitacionDAO.deleteById(idHabitacion);
    }

    @Override
    public List<Habitacion> listarActivas() {
        return habitacionDAO.findByActiva(true);
    }
}
