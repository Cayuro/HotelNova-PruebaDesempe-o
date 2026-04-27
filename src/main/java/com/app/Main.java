package com.app;

// Controladores
import com.app.config.AppConfig;
import com.app.controller.HabitacionController;
import com.app.controller.HuespedController;
import com.app.controller.ReservationController;
import com.app.controller.UsuarioController;
import com.app.model.entity.Usuario;
import com.app.service.AuthService;
import com.app.service.impl.AuthServiceImpl;
import com.app.util.PasswordHasher;

// DAOS 
import com.app.dao.HabitacionDAO;
import com.app.dao.HuespedDAO;
import com.app.dao.ReservaDAO;
import com.app.dao.UsuarioDAO;

// implementaciones de DAO
import com.app.dao.impl.HabitacionDAOImpl;
import com.app.dao.impl.HuespedDAOImpl;
import com.app.dao.impl.ReservaDAOImpl;
import com.app.dao.impl.UsuarioDAOImpl;

// Vistas
import com.app.view.ConsoleView;
import com.app.view.SwingView;
import com.app.view.View;


public class Main {

    public static void main(String[] args) {

        AppConfig config = AppConfig.getInstance();

        // ── Factory: elige la vista según app.properties ──
        View view = createView(config.getViewType());

        // ── Inyección de dependencias ──
        
        // los DAOS se instancian directamente.
        HabitacionDAO     habitacionDAO = new HabitacionDAOImpl();
        HuespedDAO          huespedDAO  = new HuespedDAOImpl();
        ReservaDAO          reservaDAO  = new ReservaDAOImpl();
        UsuarioDAO          usuarioDAO  = new UsuarioDAOImpl();
        AuthService         authService = new AuthServiceImpl(usuarioDAO);

        // los controladores reciben la vista y los DOAS que necesitan
        ReservationController reservaController = new ReservationController(view, reservaDAO, habitacionDAO, huespedDAO);

        HabitacionController habitacionctrl = new HabitacionController(view, habitacionDAO);
        HuespedController   huespedController = new HuespedController(view,huespedDAO);
        UsuarioController   usuarioCtrl = new UsuarioController(view, usuarioDAO);

        view.showMessage("Bienvenido a " + config.getAppName());

        ensureBootstrapAdmin(view, usuarioDAO);

        Usuario loggedUser = requireLogin(view, authService);
        if (loggedUser == null) {
            view.showError("Demasiados intentos fallidos. Cerrando aplicación.");
            return;
        }

        view.showMessage("Sesión iniciada: " + loggedUser.getUsername() + " (" + loggedUser.getRole() + ")");

        boolean running = true;
        boolean isAdmin = loggedUser.getRole().equalsIgnoreCase("ADMIN");
        while (running) {

            String[] modules;
            if (!isAdmin) {
                modules = new String[] {
                    "Gestion de Reservas",
                    "Gestion de Huespedes",
                    "Gestion de Habitaciones",
                    "Salir"};
            } else {
                 modules = new String[] {
                    "Gestion de Usuarios",
                    "Gestion de Habitaciones", 
                    "Gestion de Reservas",
                    "Gestion de Huespedes",
                    "Salir"};
            }
         
            view.showMenu(modules, "Menu Principal");
            int choice = view.getMenuChoice();

            if (!isAdmin) {
            switch (choice) {
                case 1 -> reservaController.run();
                case 2 -> huespedController.run();
                case 3 -> habitacionctrl.run();
                case 4 -> running = false;
                default -> view.showError("Opcion no valida");
                }
            } else {
            switch (choice) {
                case 1 -> usuarioCtrl.run();
                case 2 -> habitacionctrl.run();
                case 3 -> reservaController.run();
                case 4 -> huespedController.run();
                case 5 -> running = false;
                default -> view.showError("Opcion no valida");
                }
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

    private static Usuario requireLogin(View view, AuthService authService) {
        int attempts = 3;
        for (int i = 1; i <= attempts; i++) {
            String username = view.askInput("Username");
            String password = view.askInput("Password");

            java.util.Optional<Usuario> opt = authService.login(username, password);
            if (opt.isPresent()) {
                return opt.get();
            }

            int remaining = attempts - i;
            if (remaining > 0) {
                view.showError("Credenciales inválidas o usuario inactivo. Intentos restantes: " + remaining);
            }
        }
        return null;
    }

    private static void ensureBootstrapAdmin(View view, UsuarioDAO usuarioDAO) {
        if (!usuarioDAO.findAll().isEmpty()) {
            return;
        }

        view.showMessage("No hay usuarios registrados. Debes crear un ADMIN inicial.");

        String nombre = view.askInput("Nombre del admin inicial");
        String email = view.askInput("Email del admin inicial");
        String username = view.askInput("Username del admin inicial");
        String password = view.askInput("Password del admin inicial");

        if (nombre.isBlank() || email.isBlank() || username.isBlank() || password.isBlank()) {
            view.showError("Bootstrap cancelado: todos los campos son obligatorios.");
            return;
        }

        Usuario admin = new Usuario(0, nombre, email);
        admin.setUsername(username);
        admin.setPasswordHash(PasswordHasher.hash(password));
        admin.setRole("ADMIN");
        admin.setActivo(true);

        usuarioDAO.save(admin);
        view.showMessage("Admin inicial creado. Ahora inicia sesión.");
    }
}