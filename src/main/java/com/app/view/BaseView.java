package com.app.view;

import com.app.model.entity.Tarea;
import com.app.model.entity.Usuario;
import java.util.List;

/**
 * Comportamiento común a todas las vistas.
 * Evita duplicar lógica de formato entre ConsoleView y SwingView.
 */
public abstract class BaseView implements View {

    // ── Formato compartido para la lista de usuarios ──
    protected String formatUsuarios(List<Usuario> usuarios) {
        if (usuarios.isEmpty()) return "(Sin usuarios)";

        StringBuilder sb = new StringBuilder();
        sb.append(String.format("%-5s %-20s %-30s%n", "ID", "NOMBRE", "EMAIL"));
        sb.append("─".repeat(55)).append("\n");
        for (Usuario u : usuarios) {
            sb.append(String.format("%-5d %-20s %-30s%n",
                    u.getId(), u.getNombre(), u.getEmail()));
        }
        return sb.toString();
    }

    protected String formatUsuario(Usuario u) {
        return String.format("ID: %d%nNombre: %s%nEmail: %s",
                u.getId(), u.getNombre(), u.getEmail());
    }

    protected String formatTareas(List<Tarea> tareas) {
        if (tareas.isEmpty()) return "(Sin tareas)";

        StringBuilder sb = new StringBuilder();
        sb.append(String.format("%-5s %-25s %-12s %-12s%n", "ID", "TITULO", "PENDIENTE", "FECHA"));
        sb.append("-".repeat(60)).append("\n");
        for (Tarea t : tareas) {
            sb.append(String.format("%-5d %-25s %-12s %-12s%n",
                    t.getId(), t.getTitulo(), t.isPendiente() ? "SI" : "NO", t.getFechaLimite()));
        }
        return sb.toString();
    }

    protected String formatTarea(Tarea t) {
        return String.format("ID: %d%nTitulo: %s%nPendiente: %s%nFecha limite: %s",
                t.getId(), t.getTitulo(), t.isPendiente() ? "SI" : "NO", t.getFechaLimite());
    }

    protected String buildMenu(String[] options, String title) {
        StringBuilder sb = new StringBuilder();
        sb.append("╔═══ ").append(title).append(" ═══╗\n");
        for (int i = 0; i < options.length; i++) {
            sb.append(String.format("  %d. %s%n", i + 1, options[i]));
        }
        sb.append("╚" + "═".repeat(title.length() + 8) + "╝");
        return sb.toString();
    }
}