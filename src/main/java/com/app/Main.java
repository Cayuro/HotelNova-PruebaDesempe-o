package com.app;

import com.app.config.AppConfig;
import com.app.controller.TareaController;
import com.app.controller.UsuarioController;
import com.app.dao.TareaDao;
import com.app.dao.UsuarioDAO;
import com.app.dao.impl.TareaDAOImpl;
import com.app.dao.impl.UsuarioDAOImpl;
import com.app.view.ConsoleView;
import com.app.view.SwingView;
import com.app.view.View;

public class Main {

    public static void main(String[] args) {

        AppConfig config = AppConfig.getInstance();

        // ── Factory: elige la vista según app.properties ──
        View view = createView(config.getViewType());

        // ── Inyección de dependencias ──
        UsuarioDAO          usuarioDAO  = new UsuarioDAOImpl();
        TareaDao            tareaDAO    = new TareaDAOImpl();
        UsuarioController   usuarioCtrl = new UsuarioController(view, usuarioDAO);
        TareaController     tareaCtrl   = new TareaController(view, tareaDAO);

        view.showMessage("Bienvenido a " + config.getAppName());

        boolean running = true;
        while (running) {
            String[] modules = {"Gestion de Usuarios", "Gestion de Tareas", "Salir"};
            view.showMenu(modules, "Menu Principal");

            int choice = view.getMenuChoice();
            switch (choice) {
                case 1 -> usuarioCtrl.run();
                case 2 -> tareaCtrl.run();
                case 3 -> running = false;
                default -> view.showError("Opcion no valida");
            }
        }

        view.showMessage("Aplicacion finalizada.");
    }

    private static View createView(String type) {
        return switch (type.toLowerCase()) {
            case "swing" -> new SwingView();
            default       -> new ConsoleView();
        };
    }
}