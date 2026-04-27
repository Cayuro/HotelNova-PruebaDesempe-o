package com.app.controller;

import com.app.dao.HuespedDAO;
import com.app.model.entity.Huesped;
import com.app.service.HuespedService;
import com.app.service.impl.HuespedServiceImpl;
import com.app.view.View;

import java.util.List;
import java.util.Optional;

/**
 * Controlador de Huéspedes.
 * Recibe eventos de la Vista, orquesta el DAO y devuelve resultados.
 * 
 * Reglas aplicadas:
 * - Email único (validado por DAO)
 * - RN-02: Huesped debe estar en estado activo para poder reservar
 */
public class HuespedController {

    private final View view;
    private final HuespedService huespedService;

    // Compatibilidad con wiring actual basado en DAO
    public HuespedController(View view, HuespedDAO huespedDAO) {
        this(view, new HuespedServiceImpl(huespedDAO));
    }

    public HuespedController(View view, HuespedService huespedService) {
        this.view = view;
        this.huespedService = huespedService;
    }

    // ── Menú principal ──
    public void run() {
        String[] menuOptions = {
            "Crear huésped", "Listar todos", "Buscar por ID",
            "Actualizar huésped", "Eliminar huésped", "Listar activos",
            "Activar/Desactivar", "Salir"
        };

        boolean running = true;
        while (running) {
            view.showMenu(menuOptions, "Gestión de Huéspedes");
            int choice = view.getMenuChoice();

            switch (choice) {
                case 1 -> crearHuesped();
                case 2 -> listarTodos();
                case 3 -> buscarPorId();
                case 4 -> actualizarHuesped();
                case 5 -> eliminarHuesped();
                case 6 -> listarActivos();
                case 7 -> toggleActivo();
                case 8 -> running = false;
                default -> view.showError("Opción no válida");
            }
        }
        view.showMessage("¡Hasta luego!");
    }

    // ── Crear huésped ──
    public void crearHuesped() {
        String nombre = view.askInput("Nombre del huésped");
        String email = view.askInput("Email del huésped");

        try {
            Huesped nuevo = new Huesped(0, nombre, email, true);
            Huesped created = huespedService.crear(nuevo);
            view.showMessage("Huésped creado con ID: " + created.getId() +
                    " (Nombre: " + nombre + ", Email: " + email + ")");
        } catch (IllegalArgumentException | IllegalStateException e) {
            view.showError(e.getMessage());
        }
    }

    // ── Listar todos los huéspedes ──
    public void listarTodos() {
        List<Huesped> huespedes = huespedService.listarTodos();
        if (huespedes.isEmpty()) {
            view.showMessage("No hay huéspedes registrados.");
        } else {
            view.showMessage("=== LISTADO DE HUÉSPEDES ===");
            for (Huesped h : huespedes) {
                mostrarHuesped(h);
            }
        }
    }

    // ── Buscar por ID ──
    public void buscarPorId() {
        String input = view.askInput("ID del huésped");
        try {
            int id = Integer.parseInt(input);
            Optional<Huesped> huesped = huespedService.buscarPorId(id);
            if (huesped.isPresent()) {
                view.showMessage("=== DETALLES DEL HUÉSPED ===");
                mostrarHuesped(huesped.get());
            } else {
                view.showError("No se encontró huésped con ID " + id);
            }
        } catch (NumberFormatException e) {
            view.showError("ID inválido: " + input);
        }
    }

    // ── Actualizar huésped ──
    public void actualizarHuesped() {
        String input = view.askInput("ID del huésped a actualizar");
        try {
            int id = Integer.parseInt(input);
            Optional<Huesped> opt = huespedService.buscarPorId(id);
            if (opt.isEmpty()) {
                view.showError("No se encontró huésped con ID " + id);
                return;
            }

            Huesped h = opt.get();
            String nombre = view.askInput("Nuevo nombre [" + h.getNombre() + "]");
            String email = view.askInput("Nuevo email [" + h.getEmail() + "]");

            // Evita mutar la instancia original antes de validar reglas en service.
            Huesped toUpdate = new Huesped(h.getId(), h.getNombre(), h.getEmail(), h.isActivo());
            if (!nombre.isBlank()) toUpdate.setNombre(nombre);
            if (!email.isBlank()) toUpdate.setEmail(email);

            boolean ok = huespedService.actualizar(toUpdate);
            view.showMessage(ok ? "Huésped actualizado." : "No se pudo actualizar.");

        } catch (NumberFormatException e) {
            view.showError("ID inválido.");
        } catch (IllegalArgumentException | IllegalStateException e) {
            view.showError(e.getMessage());
        }
    }

    // ── Eliminar huésped ──
    public void eliminarHuesped() {
        String input = view.askInput("ID del huésped a eliminar");
        try {
            int id = Integer.parseInt(input);
            if (view.confirm("¿Confirmar eliminación del huésped " + id + "?")) {
                boolean ok = huespedService.eliminar(id);
                view.showMessage(ok ? "Huésped eliminado." : "No se encontró el huésped.");
            }
        } catch (NumberFormatException e) {
            view.showError("ID inválido.");
        }
    }

    // ── Listar huéspedes activos ──
    public void listarActivos() {
        List<Huesped> activos = huespedService.listarActivos();
        if (activos.isEmpty()) {
            view.showMessage("No hay huéspedes activos.");
        } else {
            view.showMessage("=== HUÉSPEDES ACTIVOS ===");
            for (Huesped h : activos) {
                mostrarHuesped(h);
            }
        }
    }

    // ── Activar/Desactivar huésped ──
    public void toggleActivo() {
        String input = view.askInput("ID del huésped");
        try {
            int id = Integer.parseInt(input);
            Optional<Huesped> opt = huespedService.buscarPorId(id);
            if (opt.isEmpty()) {
                view.showError("No se encontró huésped con ID " + id);
                return;
            }

            Huesped h = opt.get();
            String accion = h.isActivo() ? "desactivar" : "activar";
            if (view.confirm("¿" + accion.substring(0, 1).toUpperCase() + accion.substring(1) +
                    " al huésped " + h.getNombre() + "?")) {
                boolean ok = huespedService.toggleActivo(id);
                boolean activoFinal = huespedService.buscarPorId(id)
                        .map(Huesped::isActivo)
                        .orElse(h.isActivo());
                String estado = activoFinal ? "activado" : "desactivado";
                view.showMessage(ok ? "Huésped " + estado + "." : "No se pudo actualizar.");
            }
        } catch (NumberFormatException e) {
            view.showError("ID inválido.");
        } catch (IllegalArgumentException | IllegalStateException e) {
            view.showError(e.getMessage());
        }
    }

    // ── Utilidad: mostrar un huésped ──
    private void mostrarHuesped(Huesped h) {
        view.showMessage("ID: " + h.getId() + " | Nombre: " + h.getNombre() +
                " | Email: " + h.getEmail() + " | Activo: " + (h.isActivo() ? "SÍ" : "NO"));
    }
}
