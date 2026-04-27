package com.app.controller;

import com.app.dao.HabitacionDAO;
import com.app.dao.HuespedDAO;
import com.app.dao.ReservaDAO;
import com.app.model.entity.Habitacion;
import com.app.model.entity.Huesped;
import com.app.model.entity.Reserva;
import com.app.view.View;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Queue;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ReservationControllerTest {

    private FakeView view;
    private InMemoryHabitacionDAO habitacionDAO;
    private InMemoryHuespedDAO huespedDAO;
    private InMemoryReservaDAO reservaDAO;
    private ReservationController controller;

    @BeforeEach
    void setUp() {
        view = new FakeView();
        habitacionDAO = new InMemoryHabitacionDAO();
        huespedDAO = new InMemoryHuespedDAO();
        reservaDAO = new InMemoryReservaDAO();
        controller = new ReservationController(view, reservaDAO, habitacionDAO, huespedDAO);

        habitacionDAO.save(new Habitacion(0, "101", "Doble", new BigDecimal("100.00"), true));
        huespedDAO.save(new Huesped(0, "Juan Perez", "juan@mail.com", true));
    }

    @Test
    void shouldCreateReservationWhenRulesPass() {
        view.enqueueInputs("1", "1", "2026-05-01", "2026-05-05");

        controller.crearReserva();

        assertTrue(view.messagesContain("Reserva creada exitosamente con ID:"));
        assertTrue(reservaDAO.findAll().size() == 1);

        Reserva reserva = reservaDAO.findAll().get(0);
        assertEquals("BOOKED", reserva.getEstado());
        assertEquals(1, reserva.getIdHabitacion());
        assertEquals(1, reserva.getIdHuesped());
        assertEquals(0, reserva.getCheckIn().compareTo(LocalDate.of(2026, 5, 1)));
        assertEquals(0, reserva.getCheckOut().compareTo(LocalDate.of(2026, 5, 5)));
        assertEquals(0, reserva.getTotal().compareTo(new BigDecimal("476.00")));
    }

    @Test
    void shouldRejectReservationWhenDatesAreInvalid() {
        view.enqueueInputs("1", "1", "2026-05-05", "2026-05-01");

        controller.crearReserva();

        assertTrue(view.errorsContain("Check-in debe ser anterior a check-out."));
        assertTrue(reservaDAO.findAll().isEmpty());
    }

    @Test
    void shouldRejectReservationForInactiveGuest() {
        huespedDAO.save(new Huesped(0, "Maria Inactiva", "maria@mail.com", false));
        view.enqueueInputs("1", "2", "2026-05-01", "2026-05-05");

        controller.crearReserva();

        assertTrue(view.errorsContain("El huésped Maria Inactiva no está activo."));
        assertTrue(reservaDAO.findAll().isEmpty());
    }

    @Test
    void shouldRejectReservationWhenRoomIsInactive() {
        Habitacion habitacion = habitacionDAO.findById(1).orElseThrow();
        habitacion.setActiva(false);
        habitacionDAO.update(habitacion);

        view.enqueueInputs("1", "1", "2026-05-01", "2026-05-05");

        controller.crearReserva();

        assertTrue(view.errorsContain("La habitación 101 no está activa."));
        assertTrue(reservaDAO.findAll().isEmpty());
    }

    @Test
    void shouldRejectReservationWhenOverlapExists() {
        reservaDAO.save(new Reserva(0, 1, 1,
                LocalDate.of(2026, 5, 2),
                LocalDate.of(2026, 5, 6),
                "BOOKED",
                new BigDecimal("460.00")));

        view.enqueueInputs("1", "1", "2026-05-04", "2026-05-08");

        controller.crearReserva();

        assertTrue(view.errorsContain("Existe solapamiento: la habitación ya está reservada en esas fechas."));
        assertEquals(1, reservaDAO.findAll().size());
    }

    @Test
    void shouldCheckInOnlyFromBookedState() {
        Reserva reserva = reservaDAO.save(new Reserva(0, 1, 1,
                LocalDate.of(2026, 5, 1),
                LocalDate.of(2026, 5, 5),
                "BOOKED",
                new BigDecimal("460.00")));

        view.enqueueInputs(String.valueOf(reserva.getId()));
        view.enqueueConfirmations(true);

        controller.checkIn();

        Reserva updated = reservaDAO.findById(reserva.getId()).orElseThrow();
        assertEquals("CHECKED_IN", updated.getEstado());
        assertTrue(view.messagesContain("actualizada a CHECKED_IN"));
    }

    @Test
    void shouldRejectCheckInWhenReservationIsNotBooked() {
        Reserva reserva = reservaDAO.save(new Reserva(0, 1, 1,
                LocalDate.of(2026, 5, 1),
                LocalDate.of(2026, 5, 5),
                "CHECKED_IN",
                new BigDecimal("460.00")));

        view.enqueueInputs(String.valueOf(reserva.getId()));

        controller.checkIn();

        assertTrue(view.errorsContain("Solo una reserva BOOKED puede pasar a CHECKED_IN."));
        assertEquals("CHECKED_IN", reservaDAO.findById(reserva.getId()).orElseThrow().getEstado());
    }

    @Test
    void shouldCheckOutOnlyFromCheckedInState() {
        Reserva reserva = reservaDAO.save(new Reserva(0, 1, 1,
                LocalDate.of(2026, 5, 1),
                LocalDate.of(2026, 5, 5),
                "CHECKED_IN",
                new BigDecimal("460.00")));

        view.enqueueInputs(String.valueOf(reserva.getId()));
        view.enqueueConfirmations(true);

        controller.checkOut();

        Reserva updated = reservaDAO.findById(reserva.getId()).orElseThrow();
        assertEquals("CHECKED_OUT", updated.getEstado());
        assertTrue(view.messagesContain("actualizada a CHECKED_OUT"));
    }

    @Test
    void shouldRejectCheckOutWhenReservationIsNotCheckedIn() {
        Reserva reserva = reservaDAO.save(new Reserva(0, 1, 1,
                LocalDate.of(2026, 5, 1),
                LocalDate.of(2026, 5, 5),
                "BOOKED",
                new BigDecimal("460.00")));

        view.enqueueInputs(String.valueOf(reserva.getId()));

        controller.checkOut();

        assertTrue(view.errorsContain("Solo una reserva CHECKED_IN puede pasar a CHECKED_OUT."));
        assertEquals("BOOKED", reservaDAO.findById(reserva.getId()).orElseThrow().getEstado());
    }

    private static class FakeView implements View {
        private final Queue<String> inputs = new LinkedList<>();
        private final Queue<Boolean> confirmations = new LinkedList<>();
        private final List<String> messages = new ArrayList<>();
        private final List<String> errors = new ArrayList<>();

        void enqueueInputs(String... values) {
            for (String value : values) {
                inputs.add(value);
            }
        }

        void enqueueConfirmations(boolean... values) {
            for (boolean value : values) {
                confirmations.add(value);
            }
        }

        boolean messagesContain(String fragment) {
            return messages.stream().anyMatch(msg -> msg.contains(fragment));
        }

        boolean errorsContain(String fragment) {
            return errors.stream().anyMatch(msg -> msg.contains(fragment));
        }

        @Override
        public void showMessage(String msg) {
            messages.add(msg);
        }

        @Override
        public void showError(String msg) {
            errors.add(msg);
        }

        @Override
        public void showUsuarios(List<com.app.model.entity.Usuario> usuarios) {
        }

        @Override
        public void showUsuario(com.app.model.entity.Usuario usuario) {
        }

        @Override
        public String askInput(String prompt) {
            return inputs.isEmpty() ? "" : inputs.poll();
        }

        @Override
        public boolean confirm(String question) {
            return confirmations.isEmpty() || confirmations.poll();
        }

        @Override
        public void showMenu(String[] options, String title) {
        }

        @Override
        public int getMenuChoice() {
            return 0;
        }
    }

    private static class InMemoryHabitacionDAO implements HabitacionDAO {
        private final Map<Integer, Habitacion> storage = new HashMap<>();
        private int sequence = 1;

        @Override
        public Habitacion save(Habitacion entity) {
            if (entity.getId() <= 0) {
                entity.setId(sequence++);
            }
            storage.put(entity.getId(), entity);
            return entity;
        }

        @Override
        public Optional<Habitacion> findById(Integer id) {
            return Optional.ofNullable(storage.get(id));
        }

        @Override
        public List<Habitacion> findAll() {
            return new ArrayList<>(storage.values());
        }

        @Override
        public boolean update(Habitacion entity) {
            storage.put(entity.getId(), entity);
            return true;
        }

        @Override
        public boolean deleteById(Integer id) {
            return storage.remove(id) != null;
        }

        @Override
        public Optional<Habitacion> findByNumero(String numero) {
            return storage.values().stream().filter(h -> h.getNumero().equals(numero)).findFirst();
        }

        @Override
        public boolean existsByNumero(String numero) {
            return storage.values().stream().anyMatch(h -> h.getNumero().equals(numero));
        }

        @Override
        public List<Habitacion> findByActiva(boolean activa) {
            return storage.values().stream().filter(h -> h.isActiva() == activa).toList();
        }
    }

    private static class InMemoryHuespedDAO implements HuespedDAO {
        private final Map<Integer, Huesped> storage = new HashMap<>();
        private int sequence = 1;

        @Override
        public Huesped save(Huesped entity) {
            if (entity.getId() <= 0) {
                entity.setId(sequence++);
            }
            storage.put(entity.getId(), entity);
            return entity;
        }

        @Override
        public Optional<Huesped> findById(Integer id) {
            return Optional.ofNullable(storage.get(id));
        }

        @Override
        public List<Huesped> findAll() {
            return new ArrayList<>(storage.values());
        }

        @Override
        public boolean update(Huesped entity) {
            storage.put(entity.getId(), entity);
            return true;
        }

        @Override
        public boolean deleteById(Integer id) {
            return storage.remove(id) != null;
        }

        @Override
        public boolean existsByEmail(String email) {
            return storage.values().stream().anyMatch(h -> h.getEmail().equals(email));
        }

        @Override
        public boolean existsActivoById(Integer idHuesped) {
            Huesped huesped = storage.get(idHuesped);
            return huesped != null && huesped.isActivo();
        }

        @Override
        public List<Huesped> findByActivo(boolean activo) {
            return storage.values().stream().filter(h -> h.isActivo() == activo).toList();
        }
    }

    private static class InMemoryReservaDAO implements ReservaDAO {
        private final Map<Integer, Reserva> storage = new HashMap<>();
        private int sequence = 1;

        @Override
        public Reserva save(Reserva entity) {
            if (entity.getId() <= 0) {
                entity.setId(sequence++);
            }
            storage.put(entity.getId(), entity);
            return entity;
        }

        @Override
        public Optional<Reserva> findById(Integer id) {
            return Optional.ofNullable(storage.get(id));
        }

        @Override
        public List<Reserva> findAll() {
            return new ArrayList<>(storage.values());
        }

        @Override
        public boolean update(Reserva entity) {
            storage.put(entity.getId(), entity);
            return true;
        }

        @Override
        public boolean deleteById(Integer id) {
            return storage.remove(id) != null;
        }

        @Override
        public boolean existsOverlap(int idHabitacion, LocalDate checkIn, LocalDate checkOut) {
            return storage.values().stream()
                    .filter(r -> r.getIdHabitacion() == idHabitacion)
                    .filter(r -> "BOOKED".equalsIgnoreCase(r.getEstado()) || "CHECKED_IN".equalsIgnoreCase(r.getEstado()))
                    .anyMatch(r -> checkIn.isBefore(r.getCheckOut()) && checkOut.isAfter(r.getCheckIn()));
        }

        @Override
        public List<Reserva> findByHabitacion(int idHabitacion) {
            return storage.values().stream().filter(r -> r.getIdHabitacion() == idHabitacion).toList();
        }

        @Override
        public List<Reserva> findByHuesped(int idHuesped) {
            return storage.values().stream().filter(r -> r.getIdHuesped() == idHuesped).toList();
        }

        @Override
        public List<Reserva> findByEstado(String estado) {
            return storage.values().stream().filter(r -> r.getEstado().equalsIgnoreCase(estado)).toList();
        }

        @Override
        public boolean updateEstado(int idReserva, String estado) {
            Reserva reserva = storage.get(idReserva);
            if (reserva == null) {
                return false;
            }
            reserva.setEstado(estado);
            return true;
        }
    }
}
