package com.app.service.impl;

import com.app.dao.HuespedDAO;
import com.app.model.entity.Huesped;
import com.app.service.HuespedService;

import java.util.List;
import java.util.Optional;

public class HuespedServiceImpl implements HuespedService {

    private final HuespedDAO huespedDAO;

    public HuespedServiceImpl(HuespedDAO huespedDAO) {
        this.huespedDAO = huespedDAO;
    }

    @Override
    public Huesped crear(Huesped huesped) {
        if (huesped.getNombre() == null || huesped.getNombre().isBlank()
                || huesped.getEmail() == null || huesped.getEmail().isBlank()) {
            throw new IllegalArgumentException("Nombre y email son requeridos.");
        }
        if (huespedDAO.existsByEmail(huesped.getEmail())) {
            throw new IllegalStateException("Ya existe un huésped con ese email.");
        }
        return huespedDAO.save(huesped);
    }

    @Override
    public List<Huesped> listarTodos() {
        return huespedDAO.findAll();
    }

    @Override
    public Optional<Huesped> buscarPorId(int idHuesped) {
        return huespedDAO.findById(idHuesped);
    }

    @Override
    public boolean actualizar(Huesped huesped) {
        if (huesped.getEmail() != null && !huesped.getEmail().isBlank()) {
            Optional<Huesped> actual = huespedDAO.findById(huesped.getId());
            if (actual.isPresent()) {
                String emailActual = actual.get().getEmail();
                if (!huesped.getEmail().equals(emailActual) && huespedDAO.existsByEmail(huesped.getEmail())) {
                    throw new IllegalStateException("Ya existe otro huésped con ese email.");
                }
            }
        }
        return huespedDAO.update(huesped);
    }

    @Override
    public boolean eliminar(int idHuesped) {
        return huespedDAO.deleteById(idHuesped);
    }

    @Override
    public List<Huesped> listarActivos() {
        return huespedDAO.findByActivo(true);
    }

    @Override
    public boolean toggleActivo(int idHuesped) {
        Optional<Huesped> opt = huespedDAO.findById(idHuesped);
        if (opt.isEmpty()) {
            throw new IllegalArgumentException("No se encontró huésped con ID " + idHuesped);
        }
        Huesped h = opt.get();
        h.setActivo(!h.isActivo());
        return huespedDAO.update(h);
    }
}
