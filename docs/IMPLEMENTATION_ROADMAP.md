# HotelNova - Roadmap de Implementacion (STEP 2 a STEP 9)

Este roadmap esta alineado con tu estructura actual: MVC + DAO, sin capa service separada.

## STEP 2 - Estructura de Proyecto Maven

Estructura objetivo:

```text
src/
  main/
    java/
      com/app/
        controller/
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
  test/
    java/
      com/app/
        controller/
        dao/
```

Paquetes minimos requeridos:
- controller
- dao
- model
- view
- exception
- util

## STEP 3 - Modelos

Checklist:
- Crear Room, Guest, User, Reservation segun especificacion.
- Evitar logica de negocio pesada dentro de entidades.
- Incluir constructores validos, equals/hashCode/toString cuando aplique.
- Si usas DTO, separar model/entity de model/dto.

## STEP 4 - DAO JDBC

Checklist tecnico:
- PreparedStatement en todas las queries.
- try-with-resources en Connection, PreparedStatement y ResultSet.
- SQL parametrizado, sin concatenacion.
- Conversion SQLException -> DataAccessException.

DAO a implementar:
- RoomDAO + RoomDAOImpl
- GuestDAO + GuestDAOImpl
- UserDAO + UserDAOImpl
- ReservationDAO + ReservationDAOImpl

## STEP 5 - Logica de negocio en Controller + DAO

Regla clave:
- No habra capa service separada.
- Las reglas de negocio se orquestan en controller y se apoyan en DAO para consultas/validaciones de persistencia.

Checklist:
- Validar RN-01 a RN-07.
- Crear transacciones para createReservation/checkIn/checkOut.
- Usar setAutoCommit(false), commit(), rollback().
- Traducir errores de infraestructura a excepciones de negocio.

## STEP 6 - Controllers + View (JOptionPane)

Responsabilidad:
- Controller: orquestacion, validaciones de negocio y manejo de excepciones.
- View: entrada/salida y mensajes (JOptionPane o consola).

Checklist:
- Menus de Room, Guest, Reservation, Reports.
- Manejo de excepciones de negocio con mensajes comprensibles.
- Logging de errores tecnicos.

## STEP 7 - Utilities

Componentes:
- ConfigReader: lectura de config.properties/database.properties.
- CsvExporter: exportacion de reportes.
- AppLogger: centralizar logs en archivo.

Configuracion sugerida:
- tax.rate en config.properties
- report.output.dir en config.properties

## STEP 8 - Tests JUnit 5

Casos minimos:
- overlapping reservations
- invalid dates
- guest inactive
- room uniqueness
- check-out flow
- cost calculation

Criterio:
- Tests enfocados en controller (reglas de negocio) y DAO (persistencia).
- DAO probado con dobles o base temporal segun estrategia.

## STEP 9 - README Final

El README debe incluir:
- Explicacion de arquitectura MVC + DAO.
- Prerrequisitos y setup.
- Scripts SQL (y como ejecutarlos).
- Como correr aplicacion.
- Como correr tests.
- Problemas comunes y troubleshooting.

## Matriz de Trazabilidad

- RN-01 -> RoomController.create/update + RoomDAO.existsByRoomNumber + UNIQUE en DB + test room uniqueness
- RN-02 -> ReservationController.create + ReservationDAO.existsOverlap + test overlapping reservations
- RN-03 -> ReservationController.create + GuestDAO.existsActiveById + test guest inactive
- RN-04 -> ReservationController.create + test invalid dates
- RN-05 -> ReservationController.checkOut + test check-out flow
- RN-06 -> ReservationController.create/checkOut + config.properties + test cost calculation
- RN-07 -> ReservationController transactional methods + tests de errores y rollback

## Regla Final de Trabajo (Tutoria)
- Este roadmap se ejecuta en modo tutoria: primero explicacion, luego ejecucion.
- No se genera codigo por defecto; se genera solo si el usuario lo solicita de forma explicita.
- En cada paso se debe explicar objetivo, decisiones y resultado esperado antes de implementar.

Explain all architectural decisions of this project clearly, as if I were defending it in a technical interview.

Focus on:
- Layered architecture
- Why business logic is in service layer
- DAO pattern benefits
- Transaction management
- Exception handling strategy