package com.app.service;

import com.app.model.entity.Huesped;

import java.util.List;
import java.util.Optional;

public interface HuespedService {
    Huesped crear(Huesped huesped);
    List<Huesped> listarTodos();
    Optional<Huesped> buscarPorId(int idHuesped);
    boolean actualizar(Huesped huesped);
    boolean eliminar(int idHuesped);
    List<Huesped> listarActivos();
    boolean toggleActivo(int idHuesped);
}
