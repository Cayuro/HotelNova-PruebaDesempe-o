package com.app.controller;

import com.app.dao.HabitacionDAO;
import com.app.model.entity.Habitacion;
import com.app.view.View;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Queue;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class HabitacionControllerTest {

    private FakeView view;
    private InMemoryHabitacionDAO habitacionDAO;
    private HabitacionController controller;

    @BeforeEach
    void setUp() {
        view = new FakeView();
        habitacionDAO = new InMemoryHabitacionDAO();
        controller = new HabitacionController(view, habitacionDAO);
    }

    @Test
    void shouldCreateRoomWhenNumberIsUnique() {
        view.enqueueInputs("101", "Doble", "120.50");

        controller.crearHabitacion();

        assertTrue(view.messagesContain("Habitación creada con ID:"));
        assertEquals(1, habitacionDAO.findAll().size());
        Habitacion habitacion = habitacionDAO.findAll().get(0);
        assertEquals("101", habitacion.getNumero());
        assertEquals("Doble", habitacion.getTipo());
        assertEquals(0, habitacion.getPrecioPorNoche().compareTo(new BigDecimal("120.50")));
    }

    @Test
    void shouldRejectRoomWhenNumberAlreadyExists() {
        habitacionDAO.save(new Habitacion(0, "101", "Doble", new BigDecimal("120.50"), true));
        view.enqueueInputs("101", "Suite", "150.00");

        controller.crearHabitacion();

        assertTrue(view.errorsContain("Ya existe una habitación con número 101"));
        assertEquals(1, habitacionDAO.findAll().size());
    }

    @Test
    void shouldRejectRoomWhenPriceIsInvalid() {
        view.enqueueInputs("102", "Simple", "0");

        controller.crearHabitacion();

        assertTrue(view.errorsContain("El precio debe ser mayor a 0."));
        assertTrue(habitacionDAO.findAll().isEmpty());
    }

    @Test
    void shouldListOnlyActiveRooms() {
        habitacionDAO.save(new Habitacion(0, "101", "Doble", new BigDecimal("120.50"), true));
        habitacionDAO.save(new Habitacion(0, "102", "Suite", new BigDecimal("250.00"), false));

        controller.listarDisponibles();

        assertTrue(view.messagesContain("=== HABITACIONES DISPONIBLES ==="));
        assertTrue(view.messagesContain("Número: 101"));
        assertTrue(view.messagesContain("Activa: SÍ"));
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

        @Override
        public boolean updateEstadoWithConnection(Connection conn, int idHabitacion, String estado)
                throws SQLException {
            // TODO Auto-generated method stub
            throw new UnsupportedOperationException("Unimplemented method 'updateEstadoWithConnection'");
        }
    }
}
