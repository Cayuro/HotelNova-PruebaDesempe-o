# HotelNova - Guion de DAOs

Este documento explica para que sirve cada DAO y como pensar sus metodos.

## 1. Regla general del DAO
Un DAO responde preguntas sobre datos.

Hace:
- Buscar.
- Guardar.
- Actualizar.
- Eliminar.
- Ejecutar consultas especiales.

No hace:
- Reglas de negocio completas.
- Decisiones de flujo del usuario.
- Logica de presentacion.

## 2. GenericDAO
Ubicacion: `src/main/java/com/app/dao/GenericDAO.java`

Que resuelve:
- CRUD comun para cualquier entidad.

Metodos que aporta:
- save
- findById
- findAll
- update
- deleteById

Que debes entender:
- Es la base reutilizable.
- Evita repetir la misma estructura para cada entidad.

## 3. UsuarioDAO
Ubicacion: `src/main/java/com/app/dao/UsuarioDAO.java`

Que agrega sobre GenericDAO:
- findByNombre
- existsByEmail

Que debes entender:
- Es un ejemplo de DAO con consultas especificas del dominio.
- email sirve para validar unicidad antes de crear un usuario.

## 4. TareaDao
Ubicacion: `src/main/java/com/app/dao/TareaDao.java`

Que agrega sobre GenericDAO:
- findByPendiente
- findRetrasada

Que debes entender:
- Es un ejemplo de filtros de negocio.
- Te muestra como extender el contrato base con consultas propias.

## 5. HabitacionDAO
Ubicacion: `src/main/java/com/app/dao/HabitacionDAO.java`

Que agrega sobre GenericDAO:
- findByNumero
- existsByNumero
- findByActiva

Que debes entender:
- numero es el dato importante para unicidad.
- activa sirve para saber si una habitacion puede usarse.

## 6. HuespedDAO
Ubicacion: `src/main/java/com/app/dao/HuespedDAO.java`

Que agrega sobre GenericDAO:
- existsByEmail
- existsActivoById
- findByActivo

Que debes entender:
- email ayuda a evitar duplicados.
- activo es la regla clave para permitir reservas.

## 7. ReservaDAO
Ubicacion: `src/main/java/com/app/dao/ReservaDAO.java`

Que agrega sobre GenericDAO:
- existsOverlap
- findByHabitacion
- findByHuesped
- findByEstado
- updateEstado

Que debes entender:
- Es el DAO mas importante del hotel.
- Sirve para unir huesped, habitacion y fechas.
- Solapamiento es una de sus consultas mas criticas.

## 8. GenericDAOImpl
Ubicacion: `src/main/java/com/app/dao/impl/GenericDAOImpl.java`

Que resuelve:
- Implementacion base reutilizable del CRUD comun.

Piezas clave:
- mapRow
- SQL base
- setInsertParams
- setUpdateParams
- setDeleteParam
- setFindByIdParam

Que debes entender:
- La subclase concreta define el detalle de su tabla.
- La clase generica define el esqueleto del acceso a datos.

## 9. Como leer un DAO concreto
Preguntate:
- Que tabla toca?
- Que columnas usa?
- Como convierte una fila en objeto?
- Que consultas especiales necesito para el negocio?
- Que valida antes el controller y que valida el DAO?

## 10. Regla mental de capas
- DAO no decide si algo se puede reservar.
- DAO solo responde si existe, si esta activo o si hay conflicto.
- El controller decide si eso permite continuar.
