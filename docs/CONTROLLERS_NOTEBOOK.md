# HotelNova - Guion de Controllers

Este documento explica como se orquesta cada caso de uso en el estilo actual del proyecto.

## 1. Regla general del Controller
El controller recibe entradas, valida pasos basicos, consulta al DAO y devuelve respuestas a la vista.

Hace:
- Pedir datos.
- Validar formato basico.
- Consultar al DAO.
- Aplicar reglas de negocio simples.
- Mostrar mensajes.

No hace:
- SQL directo.
- Persistencia cruda.
- Logica de presentacion compleja.

## 2. UsuarioController
Ubicacion: `src/main/java/com/app/controller/UsuarioController.java`

Casos de uso actuales:
- listar todos
- buscar por id
- crear usuario
- actualizar usuario
- eliminar usuario

Que debes aprender de el:
- Como separar menu, lectura de datos y operacion concreta.
- Como validar campos vacios.
- Como verificar existencia antes de crear.
- Como confirmar una eliminacion.

## 3. TareaController
Ubicacion: `src/main/java/com/app/controller/TareaController.java`

Casos de uso actuales:
- listar todas
- buscar por id
- crear tarea
- actualizar tarea
- eliminar tarea
- filtrar pendientes
- filtrar retrasadas

Que debes aprender de el:
- Como agregar opciones especiales ademas del CRUD.
- Como convertir texto a fecha.
- Como manejar errores de formato.

## 4. Como deberia pensarse un controller de hotel
Para cada entidad hotelera, el controller deberia seguir esta secuencia:

### 4.1 Habitacion
- leer numero, tipo, precio, estado
- validar que numero no este repetido
- guardar y avisar resultado

### 4.2 Huesped
- leer nombre, email, activo
- validar email o unicidad si aplica
- guardar y avisar resultado

### 4.3 Reserva
- leer habitacion, huesped, checkIn, checkOut
- validar fechas
- validar que la habitacion exista y este activa
- validar que el huesped exista y este activo
- validar solapamiento
- calcular total
- guardar la reserva

## 5. Flujo mental recomendado para una reserva
1. El usuario elige crear reserva.
2. El controller pide los datos.
3. El controller valida fechas.
4. El controller consulta DAO de huesped.
5. El controller consulta DAO de habitacion.
6. El controller consulta DAO de reserva para ver conflicto.
7. Si todo esta bien, construye el objeto.
8. Si se puede, lo persiste.
9. Se informa resultado.

## 6. Como leer cualquier controller
Preguntate:
- Que entrada recibe?
- Que validacion hace antes de tocar el DAO?
- Que excepcion o mensaje muestra si algo falla?
- Que flujo de negocio esta resolviendo?

## 7. Idea clave
El controller es el lugar donde el negocio se vuelve una secuencia entendible.
No es el lugar para SQL.
No es el lugar para demasiada logica interna.
