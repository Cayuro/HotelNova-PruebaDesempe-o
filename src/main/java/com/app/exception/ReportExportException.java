package com.app.exception;

/**
 * Lanzada cuando falla la exportación de reportes.
 * Ejemplo: error al escribir CSV, permisos de carpeta, etc.
 */
public class ReportExportException extends BusinessException {
    public ReportExportException(String message) {
        super(message);
    }

    public ReportExportException(String message, Throwable cause) {
        super(message, cause);
    }
}
