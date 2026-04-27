package com.app.dao;

import java.util.List;

import com.app.model.entity.Huesped;

public interface HuespedDAO extends GenericDAO<Huesped, Integer> {
    boolean updateActivo(int id, boolean nuevoEstado);
    boolean existsByEmail(String email);
    boolean existsActivoById(Integer idHuesped);
    List<Huesped> findByActivo(boolean activo);
}
