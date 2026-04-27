package com.app.controller;

import com.app.service.HabitacionService;
import com.app.service.ReservaService;
import com.app.service.HuespedService;
import com.app.util.CsvExporter;
import com.app.view.View;

/**
 * Controlador de Reportes y Exportaciones.
 * Responsabilidades:
 * - Menú de exportaciones
 * - Exportar habitaciones a CSV
 * - Exportar reservas activas a CSV
 * 
 * Regla aplicada:
 * - Todas las exportaciones se guardan en carpeta exports/
 */
public class ReportesController {
    
    private final View view;
    private final CsvExporter csvExporter;
    
    public ReportesController(View view, 
                             HabitacionService habitacionService,
                             ReservaService reservaService,
                             HuespedService huespedService) {
        this.view = view;
        this.csvExporter = new CsvExporter(habitacionService, reservaService, huespedService);
    }
    
    /**
     * Menú principal de reportes
     */
    public void run() {
        boolean running = true;
        while (running) {
            String[] menuOptions = {
                "Exportar todas las Habitaciones",
                "Exportar Reservas Activas",
                "Ver carpeta de exportaciones",
                "Salir"
            };
            
            view.showMenu(menuOptions, "Reportes y Exportaciones");
            int choice = view.getMenuChoice();
            
            switch (choice) {
                case 1 -> exportarHabitaciones();
                case 2 -> exportarReservasActivas();
                case 3 -> mostrarRutaExportaciones();
                case 4 -> running = false;
                default -> view.showError("Opción no válida");
            }
        }
    }
    
    /**
     * Exporta todas las habitaciones a CSV
     */
    private void exportarHabitaciones() {
        try {
            view.showMessage("Procesando exportación de habitaciones...");
            String filePath = csvExporter.exportarHabitaciones();
            
            if (filePath != null) {
                view.showMessage("✓ Exportación completada exitosamente");
                view.showMessage("Ubicación: " + filePath);
            } else {
                view.showError("La exportación fue cancelada o hubo un error.");
            }
        } catch (Exception e) {
            view.showError("Error durante la exportación: " + e.getMessage());
        }
    }
    
    /**
     * Exporta reservas activas (BOOKED y CHECKED_IN) a CSV
     */
    private void exportarReservasActivas() {
        try {
            view.showMessage("Procesando exportación de reservas activas...");
            String filePath = csvExporter.exportarReservasActivas();
            
            if (filePath != null) {
                view.showMessage("✓ Exportación completada exitosamente");
                view.showMessage("Ubicación: " + filePath);
            } else {
                view.showError("La exportación fue cancelada o hubo un error.");
            }
        } catch (Exception e) {
            view.showError("Error durante la exportación: " + e.getMessage());
        }
    }
    
    /**
     * Muestra la ruta de la carpeta de exportaciones
     */
    private void mostrarRutaExportaciones() {
        String ruta = CsvExporter.obtenerRutaExports();
        view.showMessage("Los archivos exportados se guardan en: " + ruta + "/");
        view.showMessage("Archivos disponibles:");
        view.showMessage("  • habitaciones_export.csv - Listado completo de habitaciones");
        view.showMessage("  • reservas_activas.csv - Reservas en estado BOOKED o CHECKED_IN");
    }
}
