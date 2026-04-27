package com.app.controller;

import com.app.dao.HuespedDAO;
import com.app.model.entity.Huesped;
import com.app.view.View;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Queue;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class HuespedControllerTest {

    private FakeView view;
    private InMemoryHuespedDAO huespedDAO;
    private HuespedController controller;

    @BeforeEach
    void setUp() {
        view = new FakeView();
        huespedDAO = new InMemoryHuespedDAO();
        controller = new HuespedController(view, huespedDAO);
    }

    @Test
    void shouldCreateGuestWhenEmailIsUnique() {
        view.enqueueInputs("Juan Perez", "juan@mail.com");

        controller.crearHuesped();

        assertTrue(view.messagesContain("Huésped creado con ID:"));
        assertEquals(1, huespedDAO.findAll().size());
        Huesped huesped = huespedDAO.findAll().get(0);
        assertEquals("Juan Perez", huesped.getNombre());
        assertEquals("juan@mail.com", huesped.getEmail());
        assertTrue(huesped.isActivo());
    }

    @Test
    void shouldRejectGuestWhenEmailAlreadyExists() {
        huespedDAO.save(new Huesped(0, "Maria", "maria@mail.com", true));
        view.enqueueInputs("Juan Perez", "maria@mail.com");

        controller.crearHuesped();

        assertTrue(view.errorsContain("Ya existe un huésped con ese email."));
        assertEquals(1, huespedDAO.findAll().size());
    }

    @Test
    void shouldUpdateGuestActivationState() {
        Huesped huesped = huespedDAO.save(new Huesped(0, "Juan Perez", "juan@mail.com", true));
        view.enqueueInputs(String.valueOf(huesped.getId()));
        view.enqueueConfirmations(true);

        controller.toggleActivo();

        Huesped updated = huespedDAO.findById(huesped.getId()).orElseThrow();
        assertTrue(view.messagesContain("Huésped desactivado."));
        assertTrue(!updated.isActivo());
    }

    @Test
    void shouldRejectGuestWhenUpdatingEmailToDuplicate() {
        huespedDAO.save(new Huesped(0, "Maria", "maria@mail.com", true));
        Huesped juan = huespedDAO.save(new Huesped(0, "Juan", "juan@mail.com", true));

        view.enqueueInputs(String.valueOf(juan.getId()), "Juan Nuevo", "maria@mail.com");

        controller.actualizarHuesped();

        assertTrue(view.errorsContain("Ya existe otro huésped con ese email."));
        assertEquals("juan@mail.com", huespedDAO.findById(juan.getId()).orElseThrow().getEmail());
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
}
