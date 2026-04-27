# HotelNova - Roadmap de Implementacion (STEP 2 a STEP 10)

Este roadmap esta alineado con la prueba de desempeno: controller + service + dao + model, con UI JOptionPane y JDBC.

## STEP 2 - Estructura de Proyecto Maven

Estructura objetivo:

```text
src/
  main/
    java/
      com/app/
        controller/
        service/
          impl/
        dao/
          impl/
        model/
          entity/
          dto/          (opcional)
        view/
        exception/
        util/
        db/
        config/
    resources/
      app.properties
      database.properties
      config.properties
      schema.sql
      logging.properties
      app.log
  test/
    java/
      com/app/
        controller/
        service/
        dao/
```

Paquetes minimos requeridos:
- controller
- service
- dao
- model
- view
- exception
- util

## STEP 3 - Modelos de Dominio

Checklist:
- Crear Habitacion, Huesped, Usuario, Reserva segun especificacion actualizada.
- Incluir campos requeridos por la prueba: capacidad/estado habitacion, role/passwordHash en usuario, trazabilidad en reserva.
- Evitar logica de negocio pesada dentro de entidades.
- Incluir constructores validos, equals/hashCode/toString cuando aplique.
- Si usas DTO, separar model/entity de model/dto.

## STEP 4 - Schema SQL y Migraciones

Checklist tecnico:
- Definir schema objetivo con UNIQUE, FK, CHECK e indices de reservas.
- Asegurar columnas para autenticacion, roles y trazabilidad de reserva.
- Versionar cambios de esquema para evitar perdida de datos.

## STEP 5 - DAO JDBC

Checklist tecnico:
- PreparedStatement en todas las queries.
- try-with-resources en Connection, PreparedStatement y ResultSet.
- SQL parametrizado, sin concatenacion.
- Conversion SQLException -> DataAccessException.

DAO a implementar:
- HabitacionDAO + HabitacionDAOImpl
- HuespedDAO + HuespedDAOImpl
- UsuarioDAO + UsuarioDAOImpl
- ReservationDAO + ReservationDAOImpl

## STEP 6 - Service Layer (logica de negocio)

Regla clave:
- La logica de negocio vive en service.
- Service coordina validaciones, calculos y transacciones usando DAO.

Checklist:
- Validar RN-01 a RN-07.
- Agregar seguridad de autenticacion (RN-08): login, roles y password hash.
- Crear transacciones para createReservation/checkIn/checkOut.
- Usar setAutoCommit(false), commit(), rollback().
- Traducir errores de infraestructura a excepciones de negocio.

## STEP 7 - Controllers + View (JOptionPane)

Responsabilidad:
- Controller: orquestacion de UI y delegacion en service.
- View: entrada/salida y mensajes (JOptionPane o consola).

Checklist:
- Menus de Habitaciones, Huespedes, Usuarios, Reservas, Exportaciones.
- Manejo de excepciones de negocio con mensajes comprensibles.
- Helpers de tablas de texto con etiquetas [ACTIVO]/[INACTIVO] y [DISPONIBLE]/[OCUPADA].
- Logging de errores tecnicos.

## STEP 8 - Utilities y Archivos

Componentes:
- ConfigReader: lectura de config.properties/database.properties.
- CsvExporter: exportacion de habitaciones y reservas activas.
- AppLogger: centralizar logs en archivo.

Configuracion obligatoria:
- db.url, db.user, db.password
- horaCheckIn, horaCheckOut
- iva

## STEP 9 - Tests JUnit 5

Casos minimos:
- validacion de numero de habitacion unico
- validacion de disponibilidad de habitacion
- validacion de huesped activo
- validacion de fechas invalidas
- validacion de no solapamiento
- validacion de check-out sin reserva activa
- validacion de costo final con IVA desde properties

Criterio:
- Tests enfocados en service (reglas de negocio) y DAO (persistencia).
- DAO probado con dobles o base temporal segun estrategia.

## STEP 10 - Documentacion Final y Trazabilidad

Checklist:
- Actualizar README con setup/run/tests.
- Mantener actualizado `target/docsFinal` por iteracion:
  - MODELS_NOTEBOOK.md
  - DAOS_NOTEBOOK.md
  - CONTROLLERS_NOTEBOOK.md
  - SPEC_ALIGNMENT_NOTEBOOK.md
  - TESTS_NOTEBOOK.md (recomendado)

## Matriz de Trazabilidad

- RN-01 -> HabitacionService.create/update + HabitacionDAO.existsByNumero + UNIQUE DB + test room uniqueness
- RN-02 -> ReservaService.create/checkIn + HabitacionDAO/ReservaDAO + test room availability
- RN-03 -> ReservaService.create + HuespedDAO.existsActivoById + test guest inactive
- RN-04 -> ReservaService.create + test invalid dates
- RN-05 -> ReservaService.checkOut + test check-out without active reservation
- RN-06 -> ReservaService.checkOut + config.properties(iva) + test cost calculation
- RN-07 -> ReservaService transaccional + tests de rollback
- RN-08 -> AuthService.login + UsuarioDAO.findByUsername + test login/roles/hash

## Regla Final de Trabajo (Tutoria)
- Este roadmap se ejecuta en modo tutoria: primero explicacion, luego ejecucion.
- No se genera codigo por defecto; se genera solo si el usuario lo solicita de forma explicita.
- En cada paso se debe explicar objetivo, decisiones y resultado esperado antes de implementar.

Nota:
- Este roadmap reemplaza la variante sin service layer para cumplir el enunciado de la prueba.