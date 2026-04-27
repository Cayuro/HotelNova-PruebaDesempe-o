package com.app.util;

import com.app.model.entity.Habitacion;
import com.app.model.entity.Reserva;
import com.app.model.entity.Huesped;
import com.app.service.HabitacionService;
import com.app.service.ReservaService;
import com.app.service.HuespedService;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

/**
 * Utilidad para exportar datos del sistema a archivos CSV.
 * Responsabilidades:
 * - Exportar listado completo de habitaciones
 * - Exportar reservas activas (BOOKED y CHECKED_IN)
 * - Crear carpeta exports/ si no existe
 * - Manejar excepciones de I/O
 */
public class CsvExporter {
    
    private static final String EXPORTS_DIR = "exports";
    private static final String HABITACIONES_FILE = "habitaciones_export.csv";
    private static final String RESERVAS_ACTIVAS_FILE = "reservas_activas.csv";
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    
    private final HabitacionService habitacionService;
    private final ReservaService reservaService;
    private final HuespedService huespedService;
    
    public CsvExporter(HabitacionService habitacionService, 
                       ReservaService reservaService,
                       HuespedService huespedService) {
        this.habitacionService = habitacionService;
        this.reservaService = reservaService;
        this.huespedService = huespedService;
        crearDirectorioExports();
    }
    
    /**
     * Crea la carpeta exports/ si no existe
     */
    private void crearDirectorioExports() {
        try {
            Files.createDirectories(Paths.get(EXPORTS_DIR));
            System.out.println("[INFO] Directorio exports/ verificado.");
        } catch (IOException e) {
            System.err.println("[ERROR] No se pudo crear carpeta exports/: " + e.getMessage());
        }
    }
    
    /**
     * Exporta todas las habitaciones a habitaciones_export.csv
     * Campos: ID, Número, Tipo, Capacidad (si existe), Precio/Noche, Activa
     * 
     * @return Path del archivo exportado o null si hay error
     */
    public String exportarHabitaciones() {
        try {
            List<Habitacion> habitaciones = habitacionService.listarTodas();
            
            String filePath = EXPORTS_DIR + File.separator + HABITACIONES_FILE;
            
            try (FileWriter writer = new FileWriter(filePath)) {
                // Encabezados
                writer.append("ID,Número,Tipo,Precio/Noche,Activa\n");
                
                // Datos
                for (Habitacion h : habitaciones) {
                    writer.append(String.format("%d,%s,%s,%.2f,%s\n",
                            h.getId(),
                            h.getNumero(),
                            h.getTipo(),
                            h.getPrecioPorNoche(),
                            h.isActiva() ? "SÍ" : "NO"
                    ));
                }
                
                System.out.println("[✓ EXPORTACIÓN EXITOSA] " + habitaciones.size() + 
                                   " habitaciones exportadas a " + filePath);
                return filePath;
                
            } catch (IOException e) {
                System.err.println("[✗ ERROR] No se pudo escribir archivo: " + e.getMessage());
                return null;
            }
            
        } catch (Exception e) {
            System.err.println("[✗ ERROR] Error al obtener habitaciones: " + e.getMessage());
            return null;
        }
    }
    
    /**
     * Exporta reservas activas (BOOKED o CHECKED_IN) a reservas_activas.csv
     * Campos: ID, Habitación #, Huésped, Check-in, Check-out, Estado, Costo Total
     * 
     * @return Path del archivo exportado o null si hay error
     */
    public String exportarReservasActivas() {
        try {
            // Obtener reservas en estados activos
            List<Reserva> reservasBooked = reservaService.buscarPorEstado("BOOKED");
            List<Reserva> reservasCheckedIn = reservaService.buscarPorEstado("CHECKED_IN");
            
            // Combinar listas
            reservasBooked.addAll(reservasCheckedIn);
            
            String filePath = EXPORTS_DIR + File.separator + RESERVAS_ACTIVAS_FILE;
            
            try (FileWriter writer = new FileWriter(filePath)) {
                // Encabezados
                writer.append("ID,Habitación #,Huésped,Check-in,Check-out,Estado,Costo Total\n");
                
                // Datos
                for (Reserva r : reservasBooked) {
                    String nombreHuesped = obtenerNombreHuesped(r.getIdHuesped());
                    
                    writer.append(String.format("%d,%s,%s,%s,%s,%s,%.2f\n",
                            r.getId(),
                            r.getIdHabitacion(),
                            nombreHuesped,
                            r.getCheckIn(),
                            r.getCheckOut(),
                            r.getEstado(),
                            r.getTotal()
                    ));
                }
                
                System.out.println("[✓ EXPORTACIÓN EXITOSA] " + reservasBooked.size() + 
                                   " reservas activas exportadas a " + filePath);
                return filePath;
                
            } catch (IOException e) {
                System.err.println("[✗ ERROR] No se pudo escribir archivo: " + e.getMessage());
                return null;
            }
            
        } catch (Exception e) {
            System.err.println("[✗ ERROR] Error al obtener reservas: " + e.getMessage());
            return null;
        }
    }
    
    /**
     * Obtiene el nombre del huésped por su ID
     * @param idHuesped ID del huésped
     * @return Nombre del huésped o "N/A" si no se encuentra
     */
    private String obtenerNombreHuesped(int idHuesped) {
        try {
            Optional<Huesped> huesped = huespedService.buscarPorId(idHuesped);
            return huesped.map(Huesped::getNombre).orElse("N/A");
        } catch (Exception e) {
            System.err.println("[WARN] No se pudo obtener nombre de huésped: " + e.getMessage());
            return "N/A";
        }
    }
    
    /**
     * Obtiene la ruta de la carpeta de exportaciones
     * @return Ruta de la carpeta exports/
     */
    public static String obtenerRutaExports() {
        return EXPORTS_DIR;
    }
}
