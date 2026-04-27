# HotelNova - Plan de Pruebas (JUnit 5)

## 1. Objetivo
Validar que las reglas de negocio criticas del sistema se cumplan de forma consistente.

## 2. Estrategia
- Pruebas unitarias sobre capa controller (donde vive la logica de negocio en este proyecto).
- Mocks/stubs para DAOs cuando la prueba sea puramente de negocio.
- Pruebas de integracion DAO opcionales contra base de datos de prueba.

## 3. Casos de Prueba Minimos

### TC-01 Room number uniqueness
Dado una habitacion existente con roomNumber=101,
cuando se intenta crear otra habitacion con roomNumber=101,
entonces se lanza DuplicateRoomNumberException.

### TC-02 Overlapping reservations
Dado una reserva activa en room 101 del 2026-05-01 al 2026-05-05,
cuando se intenta reservar la misma habitacion del 2026-05-04 al 2026-05-07,
entonces se lanza ReservationOverlapException.

### TC-03 Guest inactive
Dado un huesped inactive,
cuando se intenta crear una reserva para ese huesped,
entonces se lanza InactiveGuestException.

### TC-04 Invalid dates
Dado checkIn >= checkOut,
cuando se intenta crear reserva,
entonces se lanza InvalidReservationDateException.

### TC-05 Check-out flow
Dado reserva en BOOKED,
cuando se intenta check-out,
entonces se lanza InvalidReservationStateException.

Dado reserva en CHECKED_IN,
cuando se hace check-out,
entonces estado final es CHECKED_OUT.

### TC-06 Cost calculation
Dado nights=3, pricePerNight=100, taxRate=0.19,
cuando se calcula total,
entonces total=357.00.

Formula:
- subtotal = nights * pricePerNight
- total = subtotal + subtotal * taxRate

## 4. Criterios de Aceptacion
- Todas las pruebas criticas en verde.
- Cada regla de negocio tiene al menos una prueba positiva y una negativa.
- No hay dependencias con datos de produccion.

## 5. Nomenclatura sugerida
- shouldThrowWhenRoomNumberAlreadyExists
- shouldRejectOverlappingReservation
- shouldRejectReservationForInactiveGuest
- shouldRejectReservationWhenDatesAreInvalid
- shouldRejectCheckoutWhenReservationIsNotCheckedIn
- shouldCalculateReservationTotalCostUsingTaxRate

## 6. Regla Final de Aprendizaje
- Las pruebas se trabajan en modo tutor: primero se explica que valida cada test, por que existe y cual es el resultado esperado.
- No se escriben tests automaticamente por defecto; solo se implementan cuando el usuario lo pida explicitamente.

Improve test coverage to ensure all business rules are validated.

Tests must fail if:
- A room is duplicated
- A reservation overlaps
- Guest is inactive
- Dates are invalid
- Check-out is attempted without active reservation
- Cost calculation is incorrect

