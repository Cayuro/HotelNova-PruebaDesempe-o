# HotelNova - Tests Notebook

Fecha: 2026-04-27
Objetivo: trazabilidad de requisitos del spec contra pruebas automatizadas.

## Cobertura actual

### Reglas de reserva
- RN-04 Fechas validas: cubierto en `ReservationControllerTest.shouldRejectReservationWhenDatesAreInvalid`.
- RN-03 Huesped activo: cubierto en `ReservationControllerTest.shouldRejectReservationForInactiveGuest`.
- RN-02 Disponibilidad basica (habitacion activa): cubierto en `ReservationControllerTest.shouldRejectReservationWhenRoomIsInactive`.
- RN-05 No solapamiento: cubierto en `ReservationControllerTest.shouldRejectReservationWhenOverlapExists`.
- RN-06 Check-out valido: cubierto en `ReservationControllerTest.shouldRejectCheckOutWhenReservationIsNotCheckedIn`.
- Flujo estado check-in/check-out: cubierto en tests de transicion en `ReservationControllerTest`.
- RN-07 Costo con IVA: cubierto en `ReservationControllerTest.shouldCreateReservationWhenRulesPass` (con `iva=0.19`).

### Reglas de habitaciones
- RN-01 Numero unico: cubierto en `HabitacionControllerTest.shouldRejectRoomWhenNumberAlreadyExists`.
- Alta valida de habitacion: cubierto en `HabitacionControllerTest.shouldCreateRoomWhenNumberIsUnique`.

### Reglas de huespedes
- Email unico: cubierto en `HuespedControllerTest.shouldRejectGuestWhenEmailAlreadyExists`.
- Activar/desactivar: cubierto en `HuespedControllerTest.shouldUpdateGuestActivationState`.

### Login y seguridad
- Login valido: cubierto en `AuthServiceImplTest.shouldLoginWhenCredentialsAreValidAndUserIsActive`.
- Password invalido: cubierto en `AuthServiceImplTest.shouldRejectLoginWhenPasswordIsInvalid`.
- Usuario inactivo: cubierto en `AuthServiceImplTest.shouldRejectLoginWhenUserIsInactive`.

## Pendientes de test

1. Pruebas unitarias de `UsuarioController` para alta/edicion con role y hash.
2. Pruebas de autorizacion por rol en `Main` (menu condicionado por ADMIN/RECEPCIONISTA).
3. Pruebas de transacciones JDBC reales (integracion con base temporal).
4. Pruebas de exportacion CSV y validacion de contenido de archivo.
5. Pruebas de logging de eventos y errores.

## Nota

La suite se ejecuta con `mvn test` y actualmente corre en JUnit 5 con Surefire 3.2.5.
