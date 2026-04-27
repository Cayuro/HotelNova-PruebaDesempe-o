package com.app.controller;

import com.app.dao.ReservaDAO;
import com.app.dao.HabitacionDAO;
import com.app.dao.HuespedDAO;
import com.app.model.entity.Reserva;
import com.app.model.entity.Habitacion;
import com.app.model.entity.Huesped;
import com.app.view.View;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;

/**
 * Controlador de Reservas.
 * Orquesta la lógica de negocio para crear, actualizar y consultar reservas.
 * 
 * Reglas de negocio aplicadas:
 * - RN-02: Huesped debe estar activo
 * - RN-03: Fechas válidas (checkIn < checkOut)
 * - RN-04: No solapamiento de reservas en la misma habitación
 * - RN-05: Restricciones en check-out
 * - RN-06: Cálculo correcto del costo
 */
public class ReservationController {

    private final View view;
    private final ReservaDAO reservaDAO;
    private final HabitacionDAO habitacionDAO;
    private final HuespedDAO huespedDAO;

    // Inyección de dependencias
    public ReservationController(View view, ReservaDAO reservaDAO, 
                                 HabitacionDAO habitacionDAO, HuespedDAO huespedDAO) {
        this.view = view;
        this.reservaDAO = reservaDAO;
        this.habitacionDAO = habitacionDAO;
        this.huespedDAO = huespedDAO;
    }

    // ── Menú principal ──
    public void run() {
        String[] menuOptions = {
            "Crear reserva", "Listar todas", "Buscar por ID",
            "Buscar por habitación", "Buscar por huésped", "Buscar por estado",
            "Cambiar estado", "Eliminar reserva", "Salir"
        };

        boolean running = true;
        while (running) {
            view.showMenu(menuOptions, "Gestión de Reservas");
            int choice = view.getMenuChoice();

            switch (choice) {
                case 1 -> crearReserva();
                case 2 -> listarTodas();
                case 3 -> buscarPorId();
                case 4 -> buscarPorHabitacion();
                case 5 -> buscarPorHuesped();
                case 6 -> buscarPorEstado();
                case 7 -> cambiarEstado();
                case 8 -> eliminarReserva();
                case 9 -> running = false;
                default -> view.showError("Opción no válida");
            }
        }
        view.showMessage("¡Hasta luego!");
    }

    // ── Crear reserva (operación más crítica) ──
    public void crearReserva() {
        try {
            // Paso 1: Leer entrada del usuario
            String idHabitacionStr = view.askInput("ID de la habitación");
            String idHuespedStr = view.askInput("ID del huésped");
            String checkInStr = view.askInput("Fecha check-in (yyyy-MM-dd)");
            String checkOutStr = view.askInput("Fecha check-out (yyyy-MM-dd)");

            // Paso 2: Parsear IDs y fechas
            int idHabitacion = Integer.parseInt(idHabitacionStr);
            int idHuesped = Integer.parseInt(idHuespedStr);
            LocalDate checkIn = LocalDate.parse(checkInStr);
            LocalDate checkOut = LocalDate.parse(checkOutStr);

            // Paso 3: Validar fechas (RN-03)
            if (!checkIn.isBefore(checkOut)) {
                view.showError("Check-in debe ser anterior a check-out.");
                return;
            }

            // Paso 4: Verificar que habitación existe y está activa (RN-01 validación)
            Optional<Habitacion> optHabitacion = habitacionDAO.findById(idHabitacion);
            if (optHabitacion.isEmpty()) {
                view.showError("No existe habitación con ID " + idHabitacion);
                return;
            }
            Habitacion habitacion = optHabitacion.get();
            if (!habitacion.isActiva()) {
                view.showError("La habitación " + habitacion.getNumero() + " no está activa.");
                return;
            }

            // Paso 5: Verificar que huésped existe y está activo (RN-02)
            Optional<Huesped> optHuesped = huespedDAO.findById(idHuesped);
            if (optHuesped.isEmpty()) {
                view.showError("No existe huésped con ID " + idHuesped);
                return;
            }
            Huesped huesped = optHuesped.get();
            if (!huesped.isActivo()) {
                view.showError("El huésped " + huesped.getNombre() + " no está activo.");
                return;
            }

            // Paso 6: Verificar solapamiento (RN-04)
            if (reservaDAO.existsOverlap(idHabitacion, checkIn, checkOut)) {
                view.showError("Existe solapamiento: la habitación ya está reservada en esas fechas.");
                return;
            }

            // Paso 7: Calcular costo (RN-06)
            long noches = ChronoUnit.DAYS.between(checkIn, checkOut);
            BigDecimal costoBase = habitacion.getPrecioPorNoche()
                .multiply(BigDecimal.valueOf(noches));
            BigDecimal tasaImpuesto = new BigDecimal("0.15"); // 15% impuesto
            BigDecimal impuesto = costoBase.multiply(tasaImpuesto);
            BigDecimal total = costoBase.add(impuesto);

            // Paso 8: Crear objeto Reserva en estado BOOKED
            Reserva nueva = new Reserva(0, idHabitacion, idHuesped, checkIn, checkOut, "BOOKED", total);

            // Paso 9: Persistir
            reservaDAO.save(nueva);
            view.showMessage("Reserva creada exitosamente con ID: " + nueva.getId());
            view.showMessage("Habitación: " + habitacion.getNumero());
            view.showMessage("Huésped: " + huesped.getNombre());
            view.showMessage("Noches: " + noches);
            view.showMessage("Costo base: $" + costoBase);
            view.showMessage("Impuesto (15%): $" + impuesto);
            view.showMessage("Total: $" + total);

        } catch (NumberFormatException e) {
            view.showError("ID inválido. Debe ser un número entero.");
        } catch (DateTimeParseException e) {
            view.showError("Fecha inválida. Usa formato yyyy-MM-dd.");
        }
    }

    // ── Listar todas las reservas ──
    public void listarTodas() {
        List<Reserva> reservas = reservaDAO.findAll();
        if (reservas.isEmpty()) {
            view.showMessage("No hay reservas registradas.");
        } else {
            view.showMessage("=== LISTADO DE RESERVAS ===");
            for (Reserva r : reservas) {
                mostrarReserva(r);
            }
        }
    }

    // ── Buscar por ID ──
    public void buscarPorId() {
        String input = view.askInput("ID de la reserva");
        try {
            int id = Integer.parseInt(input);
            Optional<Reserva> reserva = reservaDAO.findById(id);
            if (reserva.isPresent()) {
                view.showMessage("=== DETALLES DE RESERVA ===");
                mostrarReserva(reserva.get());
            } else {
                view.showError("No se encontró reserva con ID " + id);
            }
        } catch (NumberFormatException e) {
            view.showError("ID inválido: " + input);
        }
    }

    // ── Buscar por habitación ──
    public void buscarPorHabitacion() {
        String input = view.askInput("ID de la habitación");
        try {
            int idHabitacion = Integer.parseInt(input);
            List<Reserva> reservas = reservaDAO.findByHabitacion(idHabitacion);
            if (reservas.isEmpty()) {
                view.showMessage("No hay reservas para la habitación " + idHabitacion);
            } else {
                view.showMessage("=== RESERVAS DE HABITACIÓN " + idHabitacion + " ===");
                for (Reserva r : reservas) {
                    mostrarReserva(r);
                }
            }
        } catch (NumberFormatException e) {
            view.showError("ID inválido: " + input);
        }
    }

    // ── Buscar por huésped ──
    public void buscarPorHuesped() {
        String input = view.askInput("ID del huésped");
        try {
            int idHuesped = Integer.parseInt(input);
            List<Reserva> reservas = reservaDAO.findByHuesped(idHuesped);
            if (reservas.isEmpty()) {
                view.showMessage("No hay reservas para el huésped " + idHuesped);
            } else {
                view.showMessage("=== RESERVAS DEL HUÉSPED " + idHuesped + " ===");
                for (Reserva r : reservas) {
                    mostrarReserva(r);
                }
            }
        } catch (NumberFormatException e) {
            view.showError("ID inválido: " + input);
        }
    }

    // ── Buscar por estado ──
    public void buscarPorEstado() {
        String estado = view.askInput("Estado (BOOKED/CHECKED_IN/CHECKED_OUT/CANCELLED)");
        List<Reserva> reservas = reservaDAO.findByEstado(estado.toUpperCase());
        if (reservas.isEmpty()) {
            view.showMessage("No hay reservas con estado " + estado);
        } else {
            view.showMessage("=== RESERVAS CON ESTADO " + estado + " ===");
            for (Reserva r : reservas) {
                mostrarReserva(r);
            }
        }
    }

    // ── Cambiar estado de reserva ──
    public void cambiarEstado() {
        String idStr = view.askInput("ID de la reserva");
        try {
            int id = Integer.parseInt(idStr);
            Optional<Reserva> opt = reservaDAO.findById(id);
            if (opt.isEmpty()) {
                view.showError("No se encontró reserva con ID " + id);
                return;
            }

            Reserva reserva = opt.get();
            view.showMessage("Estado actual: " + reserva.getEstado());
            String nuevoEstado = view.askInput("Nuevo estado (BOOKED/CHECKED_IN/CHECKED_OUT/CANCELLED)");

            if (view.confirm("¿Cambiar estado a " + nuevoEstado + "?")) {
                boolean ok = reservaDAO.updateEstado(id, nuevoEstado.toUpperCase());
                if (ok) {
                    view.showMessage("Estado actualizado a: " + nuevoEstado);
                } else {
                    view.showError("No se pudo actualizar el estado.");
                }
            }
        } catch (NumberFormatException e) {
            view.showError("ID inválido.");
        }
    }

    // ── Eliminar reserva ──
    public void eliminarReserva() {
        String input = view.askInput("ID de la reserva a eliminar");
        try {
            int id = Integer.parseInt(input);
            if (view.confirm("¿Confirmar eliminación de la reserva " + id + "?")) {
                boolean ok = reservaDAO.deleteById(id);
                view.showMessage(ok ? "Reserva eliminada." : "No se encontró la reserva.");
            }
        } catch (NumberFormatException e) {
            view.showError("ID inválido.");
        }
    }

    // ── Utilidad: mostrar una reserva con sus detalles ──
    private void mostrarReserva(Reserva r) {
        try {
            Optional<Habitacion> h = habitacionDAO.findById(r.getIdHabitacion());
            Optional<Huesped> u = huespedDAO.findById(r.getIdHuesped());

            String numHabitacion = h.map(Habitacion::getNumero).orElse("N/A");
            String nomHuesped = u.map(Huesped::getNombre).orElse("N/A");

            view.showMessage("ID: " + r.getId() + " | Hab: " + numHabitacion + " | Huésped: " + nomHuesped +
                    " | CheckIn: " + r.getCheckIn() + " | CheckOut: " + r.getCheckOut() +
                    " | Estado: " + r.getEstado() + " | Total: $" + r.getTotal());
        } catch (Exception e) {
            view.showMessage("ID: " + r.getId() + " | Habitación: " + r.getIdHabitacion() +
                    " | Huésped: " + r.getIdHuesped() + " | Estado: " + r.getEstado());
        }
    }
}
