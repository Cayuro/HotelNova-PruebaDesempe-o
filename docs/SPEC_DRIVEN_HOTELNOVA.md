# HotelNova - Especificacion Funcional y Tecnica (Spec-Driven Development)

## 0. Alcance
Este documento define la especificacion base para la prueba de desempeno de HotelNova.

Contexto de negocio:
- HotelNova necesita centralizar habitaciones, huespedes, usuarios y reservas.
- Se requiere eliminar duplicidad, inconsistencias de disponibilidad y falta de control por roles.

Objetivo tecnico:
- Java SE 17 o superior.
- Interfaz con JOptionPane.
- Persistencia con JDBC (PostgreSQL o MySQL).
- Arquitectura por capas: controller, service, dao, model.
- Manejo de archivos (.properties y CSV), logging y pruebas JUnit 5.

## 1. Arquitectura por Capas

### 1.1 Capas obligatorias
- View: menus modales JOptionPane, mensajes y tablas de texto.
- Controller: orquesta casos de uso y manejo de errores para UI.
- Service: reglas de negocio, validaciones y transacciones.
- DAO: persistencia JDBC y consultas especializadas.
- Model: entidades y enums del dominio.

### 1.2 Regla de responsabilidades
- La logica de negocio vive en service.
- Controller no ejecuta SQL ni calculos de negocio complejos.
- DAO no decide reglas de negocio, solo responde preguntas de datos.

### 1.3 Trazas operativas
- Cada operacion CRUD debe dejar traza en consola o log simulando llamadas HTTP.
- Ejemplo: `POST /habitaciones`, `PATCH /reservas/{id}/checkout`.

## 2. Modelo de Dominio

### 2.1 Habitacion
- id: Integer
- numero: String (unico)
- tipo: String (SINGLE, DOUBLE, SUITE)
- capacidad: Integer
- precioPorNoche: BigDecimal
- estado: String (DISPONIBLE, OCUPADA)
- isActiva: boolean
- createdAt: LocalDateTime
- updatedAt: LocalDateTime (recomendado)

### 2.2 Huesped
- id: Integer
- fullName: String
- email: String
- phone: String
- active: boolean
- createdAt: LocalDateTime
- updatedAt: LocalDateTime

### 2.3 Usuario
- id: Integer
- username: String (unico)
- passwordHash: String (no plaintext)
- role: String (ADMIN, RECEPCIONISTA)
- active: boolean
- lastLoginAt: LocalDateTime (opcional)
- createdAt: LocalDateTime
- updatedAt: LocalDateTime

### 2.4 Reserva
- id: Integer
- roomId: Integer (FK -> Habitacion.id)
- guestId: Integer (FK -> Huesped.id)
- checkInDate: LocalDate
- checkOutDate: LocalDate
- status: String (BOOKED, CHECKED_IN, CHECKED_OUT, CANCELLED)
- taxRateApplied: BigDecimal
- totalCost: BigDecimal
- createdByUserId: Integer (FK -> Usuario.id)
- createdAt: LocalDateTime
- updatedAt: LocalDateTime

## 3. Reglas de Negocio

### RN-01 Numero de habitacion unico
- `numero` no se repite.
- Validar en service + constraint UNIQUE en base de datos.

### RN-02 Disponibilidad de habitacion
- Solo habitaciones activas y en estado DISPONIBLE pueden reservarse.

### RN-03 Huesped activo para reservar
- Solo `huesped.active = true` puede crear reservas.

### RN-04 Fechas validas
- `checkInDate < checkOutDate`.
- No se aceptan fechas nulas.

### RN-05 No solapamiento
- Para la misma habitacion no se permiten reservas solapadas.
- Formula: `A.checkIn < B.checkOut` y `B.checkIn < A.checkOut`.
- Aplica al menos para estados BOOKED y CHECKED_IN.

### RN-06 Check-out valido
- No se permite check-out sin reserva en estado CHECKED_IN.

### RN-07 Calculo de costo
- `nights = dias(checkInDate, checkOutDate)`.
- `subtotal = nights * precioPorNoche`.
- `total = subtotal + (subtotal * iva)`.
- `iva` se toma desde `config.properties`.

### RN-08 Seguridad de credenciales
- Password almacenada como hash (ejemplo BCrypt).
- Login valida username + password + estado activo + rol.

## 4. Casos de Uso

### UC-01 Login
Actor: Usuario del sistema

Precondiciones:
- Usuario existe y esta activo.
- Credenciales validas.

Postcondiciones:
- Sesion autenticada con rol.
- Se registra `lastLoginAt`.

### UC-02 Gestion de Habitaciones
Actor: ADMIN o RECEPCIONISTA

Alcance:
- Crear, editar, activar/desactivar.
- Listar y filtrar por tipo o estado.

### UC-03 Gestion de Huespedes
Actor: RECEPCIONISTA

Alcance:
- CRUD de huespedes con `active=true` por defecto.

### UC-04 Gestion de Usuarios
Actor: ADMIN

Alcance:
- CRUD de usuarios.
- Asignacion de rol y estado.
- Password siempre en hash.

### UC-05 Crear Reserva / Check-in
Actor: RECEPCIONISTA

Reglas:
- Validar habitacion, huesped, fechas y solapamiento.
- Insertar reserva y actualizar estado de habitacion.

### UC-06 Check-out
Actor: RECEPCIONISTA

Reglas:
- Solo reservas CHECKED_IN.
- Calcular total final con IVA.
- Actualizar reserva y liberar habitacion.

### UC-07 Exportaciones CSV
Actor: ADMIN

Salidas:
- `habitaciones_export.csv`.
- `reservas_activas.csv`.

## 5. Contratos de Capa (interfaces objetivo)

### 5.1 Service
- `AuthService.login(username, password)`
- `HabitacionService.create/update/activate/deactivate/find/filter`
- `HuespedService.register/update/activate/deactivate/find`
- `UsuarioService.create/update/deactivate/find`
- `ReservaService.createReservation/checkIn/checkOut/find`
- `ExportService.exportHabitaciones/exportReservasActivas`

### 5.2 DAO
- GenericDAO con CRUD base.
- DAO concretos con consultas especializadas:
  - HabitacionDAO: `existsByNumero`, `findByTipo`, `findByEstado`, `findByActiva`.
  - HuespedDAO: `existsByEmail`, `existsActivoById`, `findByActivo`.
  - UsuarioDAO: `findByUsername`, `existsByUsername`.
  - ReservaDAO: `existsOverlap`, `findByHabitacion`, `findByHuesped`, `findByEstado`, `updateStatus`.

### 5.3 Controller
- Controllers solo coordinan UI/service y muestran mensajes con JOptionPane.
- Manejan errores de negocio en mensajes amigables.

## 6. Persistencia y SQL (criterios)
- PreparedStatement en todas las consultas.
- try-with-resources para Connection/Statement/ResultSet.
- Sin concatenacion SQL para parametros.
- Constraints minimos:
  - UNIQUE (`habitaciones.numero`, `usuarios.username`, opcional `huespedes.email`).
  - FKs (`reservas.room_id`, `reservas.guest_id`, `reservas.created_by_user_id`).
  - CHECK (`check_in < check_out`).
- Indices minimos para rendimiento:
  - `reservas(room_id)`, `reservas(guest_id)`, `reservas(status)`, `reservas(check_in, check_out)`.

## 7. Transacciones JDBC

### TX-01 Reserva / Check-in
Secuencia minima:
1. `setAutoCommit(false)`.
2. Insertar o actualizar reserva a estado correspondiente.
3. Actualizar habitacion a `OCUPADA`.
4. `commit()`.
5. Ante error: `rollback()` y excepcion de negocio.

### TX-02 Check-out
Secuencia minima:
1. `setAutoCommit(false)`.
2. Validar estado CHECKED_IN.
3. Calcular total con IVA.
4. Actualizar reserva a CHECKED_OUT con total final.
5. Actualizar habitacion a `DISPONIBLE`.
6. `commit()`.
7. Ante error: `rollback()`.

## 8. Configuracion, archivos y logs

### 8.1 `config.properties`
Parametros obligatorios:
- `db.url=jdbc:<dbms>://<host>:<port>/<database>`
- `db.user=<username>`
- `db.password=<password>`
- `horaCheckIn=15`
- `horaCheckOut=12`
- `iva=0.19`

### 8.2 Exportaciones
- `habitaciones_export.csv`: listado completo de habitaciones.
- `reservas_activas.csv`: reservas en estado activo/en curso.

### 8.3 Logging
- Archivo `app.log` con errores tecnicos y eventos relevantes.
- Trazas de operaciones CRUD y autenticacion.

## 9. Excepciones de Dominio

Jerarquia recomendada:
- `BusinessException` (base)
- `ValidationException`
- `AuthenticationException`
- `AuthorizationException`
- `NotFoundException`
- `DuplicateRoomNumberException`
- `DuplicateEntityException`
- `InactiveGuestException`
- `InvalidReservationDateException`
- `ReservationOverlapException`
- `RoomNotAvailableException`
- `InvalidReservationStateException`
- `ReportExportException`
- `DataAccessException` (wrapper de SQLException)

Contrato:
- DAO convierte `SQLException -> DataAccessException`.
- Service traduce a excepciones de negocio.
- Controller/View muestran mensaje claro y registran detalle tecnico.

## 10. Pruebas JUnit 5

Cobertura minima obligatoria:
- Unicidad de numero de habitacion.
- Disponibilidad de habitacion antes de reservar.
- Huesped activo para reservar.
- Fechas validas (`checkIn < checkOut`).
- No solapamiento de reservas.
- Check-out invalido sin reserva activa.
- Calculo de costo final con IVA desde properties.

Lineamientos:
- Usar `assertEquals`, `assertThrows`, `assertTrue`.
- Ejecutar con `mvn test`.
- Mantener trazabilidad `requisito -> test`.

## 11. Definicion de Terminado (DoD)
- Arquitectura por capas implementada (controller/service/dao/model/view).
- Login con roles y password hash funcionando.
- CRUD de habitaciones, huespedes, usuarios y reservas operativo.
- Check-in/check-out transaccionales y consistentes.
- Exportaciones CSV funcionando.
- Configuracion leida desde properties.
- Logging en `app.log` habilitado.
- Reglas de negocio criticas validadas por pruebas en verde.
- Documentacion en `target/docsFinal` actualizada por iteracion.

## 12. Documentacion Viva en target/docsFinal
En cada incremento se debe actualizar:
- `MODELS_NOTEBOOK.md`
- `DAOS_NOTEBOOK.md`
- `CONTROLLERS_NOTEBOOK.md`
- `SPEC_ALIGNMENT_NOTEBOOK.md`
- `TESTS_NOTEBOOK.md` (recomendado)
- `MIGRATIONS_NOTEBOOK.md` (recomendado)

## 13. Regla de trabajo
- Este proyecto se trabaja en modo tutor: primero decision de diseno, luego implementacion.
- Las implementaciones deben mantener trazabilidad con este documento.
