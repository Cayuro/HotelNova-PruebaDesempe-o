# HotelNova - Especificacion Funcional y Tecnica (Spec-Driven Development)

## 0. Alcance
Este documento define la especificacion base para construir HotelNova con Java SE 17, arquitectura MVC + DAO y JDBC sin ORM.

Objetivo:
- Tener una especificacion cerrada antes de codificar.
- Evitar cambios improvisados durante desarrollo.
- Garantizar trazabilidad entre reglas de negocio, casos de uso, interfaces y pruebas.
- Mantener el proyecto lo mas apegado posible a la estructura actual: model, view, controller, dao.

## 1. Entidades Core y Atributos

### 1.1 Room (Habitacion)
- id: Integer
- roomNumber: String (unico)
- type: String (SINGLE, DOUBLE, SUITE)
- pricePerNight: BigDecimal
- active: boolean
- createdAt: LocalDateTime
- updatedAt: LocalDateTime

### 1.2 Guest (Huesped)
- id: Integer
- fullName: String
- email: String
- phone: String
- active: boolean
- createdAt: LocalDateTime
- updatedAt: LocalDateTime

### 1.3 User (Usuario del sistema)
- id: Integer
- username: String (unico)
- passwordHash: String
- role: String (ADMIN, RECEPTIONIST)
- active: boolean
- lastLoginAt: LocalDateTime (opcional)
- createdAt: LocalDateTime
- updatedAt: LocalDateTime

### 1.4 Reservation (Reserva)
- id: Integer
- roomId: Integer (FK -> Room.id)
- guestId: Integer (FK -> Guest.id)
- checkInDate: LocalDate
- checkOutDate: LocalDate
- status: String (BOOKED, CHECKED_IN, CHECKED_OUT, CANCELLED)
- taxRateApplied: BigDecimal
- totalCost: BigDecimal
- createdByUserId: Integer (FK -> User.id)
- createdAt: LocalDateTime
- updatedAt: LocalDateTime

## 2. Relaciones
- Room 1..N Reservation
- Guest 1..N Reservation
- User 1..N Reservation (quien registra la reserva)

Restricciones de integridad:
- Reservation.roomId debe existir en Room.
- Reservation.guestId debe existir en Guest.
- Reservation.createdByUserId debe existir en User.
- No se permite eliminar fisicamente Room/Guest/User con reservas historicas; usar active=false.

## 3. Reglas de Negocio

### RN-01: Numero de habitacion unico
- roomNumber no se repite.
- Validacion en controller/dao + constraint UNIQUE en base de datos.

### RN-02: No reservas solapadas para la misma habitacion
- Dadas dos reservas A y B de la misma habitacion, no puede existir interseccion de fechas.
- Solapamiento: A.checkIn < B.checkOut y B.checkIn < A.checkOut.
- Aplicar para estados BOOKED y CHECKED_IN.

### RN-03: Huesped activo para reservar
- Solo Guest.active=true puede crear reservas.

### RN-04: Fechas validas
- checkInDate < checkOutDate.
- No se aceptan fechas nulas.

### RN-05: No check-out sin reserva activa
- Check-out solo para reservas en estado CHECKED_IN.

### RN-06: Calculo de costo total
- nights = dias entre checkInDate y checkOutDate.
- subtotal = nights * room.pricePerNight.
- total = subtotal + (subtotal * taxRate).
- taxRate se obtiene de config.properties.

### RN-07: Transaccionalidad critica
Operaciones criticas con transaccion:
- Create reservation
- Check-in
- Check-out
- Cambios de estado que impacten disponibilidad/costo

Nota de implementacion:
- Como no habra capa service separada, estas reglas se orquestan en controller y se apoyan en DAO para validaciones de persistencia y consultas de negocio.

## 4. Casos de Uso

### UC-01 Crear habitacion
Actor: Admin o Recepcionista autorizado

Precondiciones:
- roomNumber informado
- roomNumber no existe
- pricePerNight > 0

Postcondiciones:
- Habitacion persistida en estado active=true

Errores:
- DuplicateRoomNumberException
- ValidationException

### UC-02 Registrar huesped
Actor: Recepcionista

Precondiciones:
- fullName y email requeridos
- email valido y no vacio

Postcondiciones:
- Huesped persistido en estado active=true

Errores:
- ValidationException
- DuplicateEntityException (si se define email unico)

### UC-03 Crear reserva
Actor: Recepcionista

Precondiciones:
- Habitacion existente y activa
- Huesped existente y activo
- Fechas validas
- No solapamiento

Flujo principal:
1. Validar entidad room.
2. Validar entidad guest.
3. Validar fechas.
4. Verificar solapamiento.
5. Calcular costo total.
6. Persistir reserva en BOOKED.

Postcondiciones:
- Reserva creada con totalCost calculado.

Errores:
- InactiveGuestException
- RoomNotAvailableException
- ReservationOverlapException
- InvalidReservationDateException

### UC-04 Check-in
Actor: Recepcionista

Precondiciones:
- Reserva en estado BOOKED
- Fecha actual dentro de politica permitida (definir tolerancia)

Postcondiciones:
- Estado -> CHECKED_IN

Errores:
- InvalidReservationStateException
- BusinessException

### UC-05 Check-out
Actor: Recepcionista

Precondiciones:
- Reserva en estado CHECKED_IN

Postcondiciones:
- Estado -> CHECKED_OUT

Errores:
- InvalidReservationStateException

### UC-06 Exportar reportes CSV
Actor: Admin

Entradas:
- tipo de reporte
- rango de fechas (opcional)

Postcondiciones:
- archivo CSV generado en ruta configurada

Errores:
- ReportExportException
- IOException envuelta como excepcion de dominio

## 5. Interfaces de Controller (casos de uso)

Estas interfaces representan los casos de uso de aplicacion en un estilo MVC sin capa service separada.

```java
package com.app.controller;

import com.app.model.entity.Room;
import com.app.model.entity.Guest;
import com.app.model.entity.Reservation;
import com.app.model.entity.User;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface RoomControllerContract {
    Room create(Room room);
    Room update(Room room);
    boolean deactivate(Integer roomId);
    Optional<Room> findById(Integer roomId);
    Optional<Room> findByRoomNumber(String roomNumber);
    List<Room> findAll();
}

public interface GuestControllerContract {
    Guest register(Guest guest);
    Guest update(Guest guest);
    boolean activate(Integer guestId);
    boolean deactivate(Integer guestId);
    Optional<Guest> findById(Integer guestId);
    List<Guest> findAllActive();
    List<Guest> findAll();
}

public interface UserControllerContract {
    User create(User user);
    User update(User user);
    Optional<User> findByUsername(String username);
    Optional<User> findById(Integer userId);
    boolean deactivate(Integer userId);
}

public interface ReservationControllerContract {
    Reservation create(Integer roomId, Integer guestId, LocalDate checkIn, LocalDate checkOut, Integer createdByUserId);
    Reservation checkIn(Integer reservationId);
    Reservation checkOut(Integer reservationId);
    Optional<Reservation> findById(Integer reservationId);
    List<Reservation> findByRoom(Integer roomId);
    List<Reservation> findByGuest(Integer guestId);
    List<Reservation> findByDateRange(LocalDate from, LocalDate to);
}

public interface ReportControllerContract {
    String exportReservationsCsv(LocalDate from, LocalDate to);
    String exportRevenueCsv(LocalDate from, LocalDate to);
    String exportOccupancyCsv(LocalDate from, LocalDate to);
}
```

## 6. Interfaces DAO (solo contrato)

```java
package com.app.dao;

import com.app.model.entity.Room;
import com.app.model.entity.Guest;
import com.app.model.entity.User;
import com.app.model.entity.Reservation;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface GenericDAO<T, ID> {
    T save(T entity);
    Optional<T> findById(ID id);
    List<T> findAll();
    boolean update(T entity);
    boolean deleteById(ID id);
}

public interface RoomDAO extends GenericDAO<Room, Integer> {
    Optional<Room> findByRoomNumber(String roomNumber);
    boolean existsByRoomNumber(String roomNumber);
    List<Room> findActive();
}

public interface GuestDAO extends GenericDAO<Guest, Integer> {
    List<Guest> findByActive(boolean active);
    boolean existsActiveById(Integer guestId);
}

public interface UserDAO extends GenericDAO<User, Integer> {
    Optional<User> findByUsername(String username);
    boolean existsByUsername(String username);
}

public interface ReservationDAO extends GenericDAO<Reservation, Integer> {
    boolean existsOverlap(Integer roomId, LocalDate checkIn, LocalDate checkOut);
    List<Reservation> findByRoom(Integer roomId);
    List<Reservation> findByGuest(Integer guestId);
    List<Reservation> findByDateRange(LocalDate from, LocalDate to);
    List<Reservation> findActiveByRoom(Integer roomId); // BOOKED/CHECKED_IN
    boolean updateStatus(Integer reservationId, String status);
}
```

## 7. Excepciones de Dominio

Jerarquia propuesta:
- BusinessException (base)
- ValidationException
- NotFoundException
- DuplicateEntityException
- DuplicateRoomNumberException
- InactiveGuestException
- InvalidReservationDateException
- ReservationOverlapException
- RoomNotAvailableException
- InvalidReservationStateException
- ReportExportException
- DataAccessException (wrapper de SQLException en capa DAO)

Contrato:
- DAO lanza DataAccessException.
- Controller valida reglas, traduce errores y lanza BusinessException.
- View captura BusinessException y muestra mensajes amigables.

## 8. Requisitos No Funcionales
- Java SE 17
- JDBC con PreparedStatement
- try-with-resources en acceso a BD
- Logs a archivo para errores tecnicos
- UI con JOptionPane en capa view/controller
- Pruebas JUnit 5 para reglas criticas

## 9. Definicion de Terminado (DoD)
- Todos los casos de uso implementados.
- Todas las RN validadas en controller (apoyado por DAO).
- Transacciones aplicadas en operaciones criticas.
- Excepciones de dominio mapeadas a mensajes de UI.
- Reportes CSV funcionales.
- Tests de reglas de negocio pasando.
- README actualizado con setup + run + tests.

## 10. Regla Final de Acompanamiento (Modo Tutor)
- Regla obligatoria para este proyecto: antes de escribir codigo, se debe explicar el por que, el para que y el como de cada paso.
- Todo el proceso es modo tutor, se explica y se discuten alternativas, y esperas a que el usuario llegue a las respuestas con su logica, ayudando así a mejorar el aprendizaje y la comprension profunda del proyecto.
- Durante el trabajo normal, se prioriza guia pedagogica y explicaciones paso a paso.
- No se debe generar codigo automaticamente; solo se escribe codigo cuando el usuario lo pida de forma explicita.
- La prioridad tecnica de aprendizaje es: model -> dao -> controller -> pruebas. View se trata como prioridad secundaria.
