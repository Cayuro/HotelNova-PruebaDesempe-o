package com.app.service.impl;

import com.app.config.AppConfig;
import com.app.dao.HabitacionDAO;
import com.app.dao.HuespedDAO;
import com.app.dao.ReservaDAO;
import com.app.model.entity.Habitacion;
import com.app.model.entity.Huesped;
import com.app.model.entity.Reserva;
import com.app.service.ReservaService;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;

public class ReservaServiceImpl implements ReservaService {

    private final BigDecimal taxRate;

    private final ReservaDAO reservaDAO;
    private final HabitacionDAO habitacionDAO;
    private final HuespedDAO huespedDAO;

    public ReservaServiceImpl(ReservaDAO reservaDAO, HabitacionDAO habitacionDAO, HuespedDAO huespedDAO) {
        this.reservaDAO = reservaDAO;
        this.habitacionDAO = habitacionDAO;
        this.huespedDAO = huespedDAO;
        this.taxRate = AppConfig.getInstance().getIvaRate();
    }

    @Override
    public Reserva crearReserva(int idHabitacion, int idHuesped, LocalDate checkIn, LocalDate checkOut) {
        validarFechas(checkIn, checkOut);

        Habitacion habitacion = habitacionDAO.findById(idHabitacion)
                .orElseThrow(() -> new IllegalArgumentException("No existe habitación con ID " + idHabitacion));

        if (!habitacion.isActiva()) {
            throw new IllegalStateException("La habitación " + habitacion.getNumero() + " no está activa.");
        }

        Huesped huesped = huespedDAO.findById(idHuesped)
                .orElseThrow(() -> new IllegalArgumentException("No existe huésped con ID " + idHuesped));

        if (!huesped.isActivo()) {
            throw new IllegalStateException("El huésped " + huesped.getNombre() + " no está activo.");
        }

        if (reservaDAO.existsOverlap(idHabitacion, checkIn, checkOut)) {
            throw new IllegalStateException("Existe solapamiento: la habitación ya está reservada en esas fechas.");
        }

        BigDecimal total = calcularTotal(habitacion.getPrecioPorNoche(), checkIn, checkOut);
        Reserva nueva = new Reserva(0, idHabitacion, idHuesped, checkIn, checkOut, "BOOKED", total);

        return reservaDAO.save(nueva);
    }

    @Override
    public Reserva checkIn(int idReserva) {
        Reserva reserva = reservaDAO.findById(idReserva)
                .orElseThrow(() -> new IllegalArgumentException("No existe reserva con ID " + idReserva));

        if (!"BOOKED".equalsIgnoreCase(reserva.getEstado())) {
            throw new IllegalStateException("Solo una reserva BOOKED puede pasar a CHECKED_IN.");
        }

        boolean updated = reservaDAO.updateEstado(idReserva, "CHECKED_IN");
        if (!updated) {
            throw new IllegalStateException("No se pudo actualizar la reserva a CHECKED_IN.");
        }

        reserva.setEstado("CHECKED_IN");
        return reserva;
    }

    @Override
    public Reserva checkOut(int idReserva) {
        Reserva reserva = reservaDAO.findById(idReserva)
                .orElseThrow(() -> new IllegalArgumentException("No existe reserva con ID " + idReserva));

        if (!"CHECKED_IN".equalsIgnoreCase(reserva.getEstado())) {
            throw new IllegalStateException("Solo una reserva CHECKED_IN puede pasar a CHECKED_OUT.");
        }

        boolean updated = reservaDAO.updateEstado(idReserva, "CHECKED_OUT");
        if (!updated) {
            throw new IllegalStateException("No se pudo actualizar la reserva a CHECKED_OUT.");
        }

        reserva.setEstado("CHECKED_OUT");
        return reserva;
    }

    @Override
    public List<Reserva> listarTodas() {
        return reservaDAO.findAll();
    }

    @Override
    public Optional<Reserva> buscarPorId(int idReserva) {
        return reservaDAO.findById(idReserva);
    }

    @Override
    public List<Reserva> buscarPorHabitacion(int idHabitacion) {
        return reservaDAO.findByHabitacion(idHabitacion);
    }

    @Override
    public List<Reserva> buscarPorHuesped(int idHuesped) {
        return reservaDAO.findByHuesped(idHuesped);
    }

    @Override
    public List<Reserva> buscarPorEstado(String estado) {
        return reservaDAO.findByEstado(estado.toUpperCase());
    }

    @Override
    public boolean cambiarEstado(int idReserva, String nuevoEstado) {
        return reservaDAO.updateEstado(idReserva, nuevoEstado.toUpperCase());
    }

    @Override
    public boolean eliminarReserva(int idReserva) {
        return reservaDAO.deleteById(idReserva);
    }

    private void validarFechas(LocalDate checkIn, LocalDate checkOut) {
        if (checkIn == null || checkOut == null) {
            throw new IllegalArgumentException("Las fechas de check-in y check-out son requeridas.");
        }
        if (!checkIn.isBefore(checkOut)) {
            throw new IllegalArgumentException("Check-in debe ser anterior a check-out.");
        }
    }

    private BigDecimal calcularTotal(BigDecimal precioPorNoche, LocalDate checkIn, LocalDate checkOut) {
        long noches = ChronoUnit.DAYS.between(checkIn, checkOut);
        BigDecimal costoBase = precioPorNoche.multiply(BigDecimal.valueOf(noches));
        BigDecimal impuesto = costoBase.multiply(taxRate);
        return costoBase.add(impuesto);
    }
}
