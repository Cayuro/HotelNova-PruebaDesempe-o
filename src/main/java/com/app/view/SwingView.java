package com.app.view;

import com.app.model.entity.Tarea;
import com.app.model.entity.Usuario;
import javax.swing.JOptionPane;
import java.util.List;

/**
 * Vista basada en JOptionPane (Swing).
 * Reutiliza la lógica de formato de BaseView.
 */
public class SwingView extends BaseView {

    private String[] currentOptions = new String[0];
    private String currentTitle = "Menu";

    @Override
    public void showMessage(String msg) {
        JOptionPane.showMessageDialog(null, msg, "Información",
                JOptionPane.INFORMATION_MESSAGE);
    }

    @Override
    public void showError(String msg) {
        JOptionPane.showMessageDialog(null, msg, "Error",
                JOptionPane.ERROR_MESSAGE);
    }

    @Override
    public void showUsuarios(List<Usuario> usuarios) {
        JOptionPane.showMessageDialog(null,
                formatUsuarios(usuarios), "Lista de Usuarios",
                JOptionPane.PLAIN_MESSAGE);
    }

    @Override
    public void showUsuario(Usuario usuario) {
        JOptionPane.showMessageDialog(null,
                formatUsuario(usuario), "Detalle de Usuario",
                JOptionPane.PLAIN_MESSAGE);
    }

    @Override
    public void showTareas(List<Tarea> tareas) {
        JOptionPane.showMessageDialog(null,
                formatTareas(tareas), "Lista de Tareas",
                JOptionPane.PLAIN_MESSAGE);
    }

    @Override
    public void showTarea(Tarea tarea) {
        JOptionPane.showMessageDialog(null,
                formatTarea(tarea), "Detalle de Tarea",
                JOptionPane.PLAIN_MESSAGE);
    }

    @Override
    public String askInput(String prompt) {
        String value = JOptionPane.showInputDialog(null, prompt);
        return value == null ? "" : value.trim();
    }

    @Override
    public boolean confirm(String question) {
        int r = JOptionPane.showConfirmDialog(null, question,
                "Confirmar", JOptionPane.YES_NO_OPTION);
        return r == JOptionPane.YES_OPTION;
    }

    @Override
    public void showMenu(String[] options, String title) {
        this.currentOptions = options;
        this.currentTitle = title;
    }

    @Override
    public int getMenuChoice() {
        if (currentOptions == null || currentOptions.length == 0) {
            return -1;
        }

        String[] opts = currentOptions;
        Object sel = JOptionPane.showInputDialog(
            null, "Selecciona una opcion:", currentTitle,
                JOptionPane.PLAIN_MESSAGE, null, opts, opts[0]);
        if (sel == null) return opts.length;
        for (int i = 0; i < opts.length; i++) {
            if (opts[i].equals(sel)) return i + 1;
        }
        return -1;
    }
}