package com.app.controller;

import com.app.dao.HabitacionDAO;
import com.app.model.entity.Habitacion;
import com.app.service.HabitacionService;
import com.app.service.impl.HabitacionServiceImpl;
import com.app.view.View;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

/**
 * Controlador de Habitaciones.
 * Recibe eventos de la Vista, orquesta el DAO y devuelve resultados.
 * 
 * Regla aplicada:
 * - RN-01: Número de habitación único
 */
public class HabitacionController {

    private final View view;
    private final HabitacionService habitacionService;

    // Compatibilidad con wiring actual basado en DAO
    public HabitacionController(View view, HabitacionDAO habitacionDAO) {
        this(view, new HabitacionServiceImpl(habitacionDAO));
    }

    public HabitacionController(View view, HabitacionService habitacionService) {
        this.view = view;
        this.habitacionService = habitacionService;
    }

    // ── Menú principal ──
    public void run() {
        String[] menuOptions = {
            "Crear habitación", "Listar todas", "Buscar por ID",
            "Actualizar habitación", "Eliminar habitación", "Listar disponibles",
            "Salir"
        };

        boolean running = true;
        while (running) {
            view.showMenu(menuOptions, "Gestión de Habitaciones");
            int choice = view.getMenuChoice();

            switch (choice) {
                case 1 -> crearHabitacion();
                case 2 -> listarTodas();
                case 3 -> buscarPorId();
                case 4 -> actualizarHabitacion();
                case 5 -> eliminarHabitacion();
                case 6 -> listarDisponibles();
                case 7 -> running = false;
                default -> view.showError("Opción no válida");
            }
        }
        view.showMessage("¡Hasta luego!");
    }

    // ── Crear habitación ──
    public void crearHabitacion() {
        try {
            String numero = view.askInput("Número de habitación");
            String tipo = view.askInput("Tipo (SINGLE/DOUBLE/SUITE)");
            String capacidadStr = view.askInput("Capacidad (número de huéspedes)");
            String precioStr = view.askInput("Precio por noche");

            int capacidad = Integer.parseInt(capacidadStr);
            BigDecimal precio = new BigDecimal(precioStr);
            Habitacion nueva = new Habitacion(0, numero, tipo, capacidad, precio, "DISPONIBLE", true);
            Habitacion created = habitacionService.crear(nueva);
            view.showMessage("Habitación creada con ID: " + created.getId() +
                    " (Número: " + numero + ", Tipo: " + tipo + ")");

        } catch (NumberFormatException e) {
            view.showError("Precio inválido. Usa formato decimal.");
        } catch (IllegalArgumentException | IllegalStateException e) {
            view.showError(e.getMessage());
        }
    }

    // ── Listar todas las habitaciones ──
    public void listarTodas() {
        List<Habitacion> habitaciones = habitacionService.listarTodas();
        if (habitaciones.isEmpty()) {
            view.showMessage("No hay habitaciones registradas.");
        } else {
            view.showMessage("=== LISTADO DE HABITACIONES ===");
            for (Habitacion h : habitaciones) {
                mostrarHabitacion(h);
            }
        }
    }

    // ── Buscar por ID ──
    public void buscarPorId() {
        String input = view.askInput("ID de la habitación");
        try {
            int id = Integer.parseInt(input);
            Optional<Habitacion> habitacion = habitacionService.buscarPorId(id);
            if (habitacion.isPresent()) {
                view.showMessage("=== DETALLES DE HABITACIÓN ===");
                mostrarHabitacion(habitacion.get());
            } else {
                view.showError("No se encontró habitación con ID " + id);
            }
        } catch (NumberFormatException e) {
            view.showError("ID inválido: " + input);
        }
    }

    // ── Actualizar habitación ──
    public void actualizarHabitacion() {
        String input = view.askInput("ID de la habitación a actualizar");
        try {
            int id = Integer.parseInt(input);
            Optional<Habitacion> opt = habitacionService.buscarPorId(id);
            if (opt.isEmpty()) {
                view.showError("No se encontró habitación con ID " + id);
                return;
            }

            Habitacion h = opt.get();
            String numero = view.askInput("Nuevo número [" + h.getNumero() + "]");
            String tipo = view.askInput("Nuevo tipo [" + h.getTipo() + "]");
            String precioStr = view.askInput("Nuevo precio [" + h.getPrecioPorNoche() + "]");
            String activoInput = view.askInput("Activa (s/n) [" + (h.isActiva() ? "s" : "n") + "]");

            if (!numero.isBlank()) h.setNumero(numero);
            if (!tipo.isBlank()) h.setTipo(tipo);
            if (!precioStr.isBlank()) {
                h.setPrecioPorNoche(new BigDecimal(precioStr));
            }
            if (!activoInput.isBlank()) {
                h.setActiva(activoInput.equalsIgnoreCase("s"));
            }

            boolean ok = habitacionService.actualizar(h);
            view.showMessage(ok ? "Habitación actualizada." : "No se pudo actualizar.");

        } catch (NumberFormatException e) {
            view.showError("Entrada inválida.");
        } catch (IllegalArgumentException | IllegalStateException e) {
            view.showError(e.getMessage());
        }
    }

    // ── Eliminar habitación ──
    public void eliminarHabitacion() {
        String input = view.askInput("ID de la habitación a eliminar");
        try {
            int id = Integer.parseInt(input);
            if (view.confirm("¿Confirmar eliminación de la habitación " + id + "?")) {
                boolean ok = habitacionService.eliminar(id);
                view.showMessage(ok ? "Habitación eliminada." : "No se encontró la habitación.");
            }
        } catch (NumberFormatException e) {
            view.showError("ID inválido.");
        }
    }

    // ── Listar habitaciones disponibles (activas) ──
    public void listarDisponibles() {
        List<Habitacion> disponibles = habitacionService.listarActivas();
        if (disponibles.isEmpty()) {
            view.showMessage("No hay habitaciones disponibles.");
        } else {
            view.showMessage("=== HABITACIONES DISPONIBLES ===");
            for (Habitacion h : disponibles) {
                mostrarHabitacion(h);
            }
        }
    }

    // ── Utilidad: mostrar una habitación ──
    private void mostrarHabitacion(Habitacion h) {
        view.showMessage("ID: " + h.getId() + " | Número: " + h.getNumero() +
                " | Tipo: " + h.getTipo() + " | Precio: $" + h.getPrecioPorNoche() +
                " | Activa: " + (h.isActiva() ? "SÍ" : "NO"));
    }
}
