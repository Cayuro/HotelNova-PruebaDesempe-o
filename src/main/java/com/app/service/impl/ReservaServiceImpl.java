package com.app.service.impl;

import com.app.config.AppConfig;
import com.app.dao.HabitacionDAO;
import com.app.dao.HuespedDAO;
import com.app.dao.ReservaDAO;
import com.app.db.ConnectionManager;
import com.app.exception.InvalidReservationStateException;
import com.app.exception.TransactionException;
import com.app.model.entity.Habitacion;
import com.app.model.entity.Huesped;
import com.app.model.entity.Reserva;
import com.app.service.ReservaService;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
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
        Connection conn = null;
        try {
            // TX-01 Check-in: Validar → setAutoCommit(false) → Actualizar reserva → Actualizar habitación → commit()
            
            // Paso 1: Validaciones precondiciones
            Reserva reserva = reservaDAO.findById(idReserva)
                    .orElseThrow(() -> new IllegalArgumentException("No existe reserva con ID " + idReserva));

            if (!"BOOKED".equalsIgnoreCase(reserva.getEstado())) {
                throw new InvalidReservationStateException("Solo una reserva BOOKED puede pasar a CHECKED_IN.");
            }

            Habitacion habitacion = habitacionDAO.findById(reserva.getIdHabitacion())
                    .orElseThrow(() -> new IllegalArgumentException("No existe habitación con ID " + reserva.getIdHabitacion()));

            // Paso 2: Obtener conexión y desactivar autocommit
            conn = ConnectionManager.getInstance().getConnection();
            conn.setAutoCommit(false);

            try {
                // Paso 3: Actualizar reserva a CHECKED_IN
                boolean reservaUpdated = reservaDAO.updateEstadoWithConnection(conn, idReserva, "CHECKED_IN");
                if (!reservaUpdated) {
                    throw new TransactionException("No se pudo actualizar la reserva a CHECKED_IN.");
                }

                // Paso 4: Actualizar habitación a OCUPADA
                boolean habitacionUpdated = habitacionDAO.updateEstadoWithConnection(conn, habitacion.getId(), "OCUPADA");
                if (!habitacionUpdated) {
                    throw new TransactionException("No se pudo actualizar la habitación a OCUPADA.");
                }

                // Paso 5: commit()
                conn.commit();
                reserva.setEstado("CHECKED_IN");
                return reserva;

            } catch (SQLException | RuntimeException e) {
                // Paso 6: rollback() en error
                try {
                    conn.rollback();
                } catch (SQLException rollbackEx) {
                    throw new TransactionException("Error en rollback durante checkIn: " + rollbackEx.getMessage(), rollbackEx);
                }
                throw new TransactionException("Error en transacción de checkIn: " + e.getMessage(), e);
            }
        } catch (SQLException e) {
            throw new TransactionException("Error al obtener conexión para checkIn: " + e.getMessage(), e);
        } finally {
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException e) {
                    throw new TransactionException("Error al cerrar conexión: " + e.getMessage(), e);
                }
            }
        }
    }

    @Override
    public Reserva checkOut(int idReserva) {
        Connection conn = null;
        try {
            // TX-02 Check-out: Validar → setAutoCommit(false) → Calcular total → Actualizar reserva → Actualizar habitación → commit()
            
            // Paso 1: Validaciones precondiciones
            Reserva reserva = reservaDAO.findById(idReserva)
                    .orElseThrow(() -> new IllegalArgumentException("No existe reserva con ID " + idReserva));

            if (!"CHECKED_IN".equalsIgnoreCase(reserva.getEstado())) {
                throw new InvalidReservationStateException("Solo una reserva CHECKED_IN puede pasar a CHECKED_OUT.");
            }

            Habitacion habitacion = habitacionDAO.findById(reserva.getIdHabitacion())
                    .orElseThrow(() -> new IllegalArgumentException("No existe habitación con ID " + reserva.getIdHabitacion()));

            // Paso 2: Obtener conexión y desactivar autocommit
            conn = ConnectionManager.getInstance().getConnection();
            conn.setAutoCommit(false);

            try {
                // Paso 3: Calcular total final con IVA
                BigDecimal totalFinal = calcularTotal(habitacion.getPrecioPorNoche(), reserva.getCheckIn(), reserva.getCheckOut());
                reserva.setTotal(totalFinal);

                // Paso 4: Actualizar reserva a CHECKED_OUT con total final
                boolean reservaUpdated = reservaDAO.updateEstadoWithConnection(conn, idReserva, "CHECKED_OUT");
                if (!reservaUpdated) {
                    throw new TransactionException("No se pudo actualizar la reserva a CHECKED_OUT.");
                }

                // Paso 5: Actualizar habitación a DISPONIBLE
                boolean habitacionUpdated = habitacionDAO.updateEstadoWithConnection(conn, habitacion.getId(), "DISPONIBLE");
                if (!habitacionUpdated) {
                    throw new TransactionException("No se pudo actualizar la habitación a DISPONIBLE.");
                }

                // Paso 6: commit()
                conn.commit();
                reserva.setEstado("CHECKED_OUT");
                return reserva;

            } catch (SQLException | RuntimeException e) {
                // Paso 7: rollback() en error
                try {
                    conn.rollback();
                } catch (SQLException rollbackEx) {
                    throw new TransactionException("Error en rollback durante checkOut: " + rollbackEx.getMessage(), rollbackEx);
                }
                throw new TransactionException("Error en transacción de checkOut: " + e.getMessage(), e);
            }
        } catch (SQLException e) {
            throw new TransactionException("Error al obtener conexión para checkOut: " + e.getMessage(), e);
        } finally {
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException e) {
                    throw new TransactionException("Error al cerrar conexión: " + e.getMessage(), e);
                }
            }
        }
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
