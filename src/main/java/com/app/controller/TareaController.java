package com.app.controller;

import com.app.dao.TareaDao;
import com.app.model.entity.Tarea;
import com.app.view.View;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Optional;

public class TareaController {

    private final View view;
    private final TareaDao tareaDao;

    public TareaController(View view, TareaDao tareaDao) {
        this.view = view;
        this.tareaDao = tareaDao;
    }

    public void run() {
        String[] menuOptions = {
            "Listar todas", "Buscar por ID", "Crear tarea",
            "Actualizar tarea", "Eliminar tarea", "Filtrar pendientes",
            "Filtrar retrasadas", "Salir"
        };

        boolean running = true;
        while (running) {
            view.showMenu(menuOptions, "Gestion de Tareas");
            int choice = view.getMenuChoice();

            switch (choice) {
                case 1 -> listarTodas();
                case 2 -> buscarPorId();
                case 3 -> crearTarea();
                case 4 -> actualizarTarea();
                case 5 -> eliminarTarea();
                case 6 -> filtrarPendientes();
                case 7 -> filtrarRetrasadas();
                case 8 -> running = false;
                default -> view.showError("Opcion no valida");
            }
        }
    }

    private void listarTodas() {
        List<Tarea> tareas = tareaDao.findAll();
        if (tareas.isEmpty()) {
            view.showMessage("No hay tareas registradas.");
        } else {
            view.showTareas(tareas);
        }
    }

    private void buscarPorId() {
        String input = view.askInput("ID de la tarea");
        try {
            int id = Integer.parseInt(input);
            Optional<Tarea> tarea = tareaDao.findById(id);
            if (tarea.isPresent()) {
                view.showTarea(tarea.get());
            } else {
                view.showError("No se encontro tarea con ID " + id);
            }
        } catch (NumberFormatException e) {
            view.showError("ID invalido: " + input);
        }
    }

    private void crearTarea() {
        String titulo = view.askInput("Titulo de la tarea");
        String pendienteInput = view.askInput("Esta pendiente? (s/n)");
        String fechaInput = view.askInput("Fecha limite (yyyy-MM-dd)");

        if (titulo.isBlank() || fechaInput.isBlank()) {
            view.showError("Titulo y fecha limite son requeridos.");
            return;
        }

        try {
            boolean pendiente = parsePendiente(pendienteInput);
            LocalDate fechaLimite = LocalDate.parse(fechaInput);

            Tarea nueva = new Tarea(0, titulo, pendiente, fechaLimite);
            tareaDao.save(nueva);
            view.showMessage("Tarea creada con ID: " + nueva.getId());
        } catch (DateTimeParseException e) {
            view.showError("Fecha invalida. Usa formato yyyy-MM-dd.");
        }
    }

    private void actualizarTarea() {
        String input = view.askInput("ID de la tarea a actualizar");
        try {
            int id = Integer.parseInt(input);
            Optional<Tarea> opt = tareaDao.findById(id);
            if (opt.isEmpty()) {
                view.showError("No se encontro tarea con ID " + id);
                return;
            }

            Tarea t = opt.get();
            String titulo = view.askInput("Nuevo titulo [" + t.getTitulo() + "]");
            String pendienteInput = view.askInput("Pendiente (s/n) [" + (t.isPendiente() ? "s" : "n") + "]");
            String fechaInput = view.askInput("Nueva fecha limite (yyyy-MM-dd) [" + t.getFechaLimite() + "]");

            if (!titulo.isBlank()) t.setTitulo(titulo);
            if (!pendienteInput.isBlank()) t.setPendiente(parsePendiente(pendienteInput));
            if (!fechaInput.isBlank()) t.setFechaLimite(LocalDate.parse(fechaInput));

            boolean ok = tareaDao.update(t);
            view.showMessage(ok ? "Tarea actualizada." : "No se pudo actualizar.");
        } catch (NumberFormatException e) {
            view.showError("ID invalido.");
        } catch (DateTimeParseException e) {
            view.showError("Fecha invalida. Usa formato yyyy-MM-dd.");
        }
    }

    private void eliminarTarea() {
        String input = view.askInput("ID de la tarea a eliminar");
        try {
            int id = Integer.parseInt(input);
            if (view.confirm("Confirmar eliminacion de la tarea " + id + "?")) {
                boolean ok = tareaDao.deleteById(id);
                view.showMessage(ok ? "Tarea eliminada." : "No se encontro la tarea.");
            }
        } catch (NumberFormatException e) {
            view.showError("ID invalido.");
        }
    }

    private void filtrarPendientes() {
        String input = view.askInput("Mostrar pendientes? (s/n)");
        boolean pendiente = parsePendiente(input);
        List<Tarea> tareas = tareaDao.findByPendiente(pendiente);
        if (tareas.isEmpty()) {
            view.showMessage("No hay resultados para ese filtro.");
        } else {
            view.showTareas(tareas);
        }
    }

    private void filtrarRetrasadas() {
        String input = view.askInput("Fecha de corte (yyyy-MM-dd)");
        try {
            LocalDate fecha = LocalDate.parse(input);
            List<Tarea> tareas = tareaDao.findRetrasada(fecha);
            if (tareas.isEmpty()) {
                view.showMessage("No hay tareas retrasadas para esa fecha.");
            } else {
                view.showTareas(tareas);
            }
        } catch (DateTimeParseException e) {
            view.showError("Fecha invalida. Usa formato yyyy-MM-dd.");
        }
    }

    private boolean parsePendiente(String input) {
        String value = input == null ? "" : input.trim().toLowerCase();
        return value.equals("s") || value.equals("si") || value.equals("si") || value.equals("true") || value.equals("1");
    }
}
