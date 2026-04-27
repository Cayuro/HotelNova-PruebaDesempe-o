package com.app.exception;

/**
 * Lanzada cuando se intenta crear una habitación con número ya existente.
 * Especialización de DuplicateEntityException para habitaciones.
 */
public class DuplicateRoomNumberException extends DuplicateEntityException {
    public DuplicateRoomNumberException(String roomNumber) {
        super("El número de habitación '" + roomNumber + "' ya existe.");
    }

    public DuplicateRoomNumberException(String roomNumber, Throwable cause) {
        super("El número de habitación '" + roomNumber + "' ya existe.", cause);
    }
}
