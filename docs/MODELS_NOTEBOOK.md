# HotelNova - Guion de Modelos

Este documento resume que representa cada entidad y que debes observar cuando la leas.

## 1. Usuario
Ubicacion: `src/main/java/com/app/model/entity/Usuario.java`

Que representa:
- Persona base del sistema.
- Sirve como clase padre para otros perfiles si quieres reutilizar id, nombre y email.

Que aporta:
- Identificador.
- Nombre.
- Email.
- Constructor vacio y constructor completo.
- Getters, setters y toString.

Que debes entender:
- Es una entidad simple de datos.
- No tiene logica de negocio.

## 2. Huesped
Ubicacion: `src/main/java/com/app/model/entity/Huesped.java`

Que representa:
- Cliente del hotel.
- Extiende Usuario para reutilizar datos comunes.

Atributo extra:
- activo

Regla mental:
- Un huesped activo puede reservar.
- Si no esta activo, el controller no debe permitir la reserva.

Que debes entender:
- Hereda id, nombre y email.
- agrega un estado propio del negocio hotelero.

## 3. Habitacion
Ubicacion: `src/main/java/com/app/model/entity/Habitacion.java`

Que representa:
- Unidad fisica reservable del hotel.

Atributos actuales:
- id
- numero
- tipo
- precioPorNoche
- activa

Reglas mentales:
- numero debe ser unico.
- precioPorNoche es clave para calcular totales.
- activa indica si la habitacion puede usarse.

Que debes entender:
- Esta entidad ya tiene sentido de hotel.
- Es uno de los puntos base para validar disponibilidad.

## 4. Reserva
Ubicacion: `src/main/java/com/app/model/entity/Reserva.java`

Que representa:
- Relacion entre huesped y habitacion en un rango de fechas.

Atributos actuales:
- id
- idHabitacion
- idHuesped
- checkIn
- checkOut
- estado
- total

Reglas mentales:
- checkIn debe ir antes que checkOut.
- estado controla el ciclo de vida.
- total se calcula con noches y precio de la habitacion.

Que debes entender:
- Es la entidad mas importante del hotel.
- Conecta a huesped y habitacion.
- Es el centro de las validaciones de negocio.

## 5. Tarea
Ubicacion: `src/main/java/com/app/model/entity/Tarea.java`

Que representa:
- Ejemplo funcional previo del proyecto.
- Te sirve para ver como se estructura una entidad completa en el estilo actual del repo.

Que debes aprender de ella:
- Constructor.
- Getters/setters.
- Relacion con su DAO y controller.

## 6. Idea clave para todos los modelos
- El modelo guarda estado.
- El modelo no decide reglas complejas.
- El modelo no conoce SQL.
- El modelo solo representa la forma de los datos.

## 7. Como leer cualquier modelo
Preguntate siempre:
- Que datos guarda?
- Cual es su identidad?
- Que estados puede tener?
- Que otros objetos lo usan?
- Que validaciones dependen de el?
