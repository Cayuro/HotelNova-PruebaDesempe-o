# HotelNova - Plantillas Guiadas de Implementacion

Este documento no es codigo final. Es una guia para que entiendas la logica y puedas construir cada pieza por tu cuenta.

Reglas de uso:
- Primero entiende el objetivo de cada plantilla.
- Luego intenta escribir tu propia version.
- Si algo no te cierra, vuelves a esta guia y comparas.
- La prioridad de estudio es: model -> dao -> controller.
- View queda en segundo plano.

## 1. Mapa mental general

Cada entidad del hotel sigue el mismo camino:

1. Modelo
- Representa los datos.
- No debe contener logica de negocio pesada.

2. DAO
- Habla con la base de datos.
- Consulta, inserta, actualiza y elimina.
- Tambien expone consultas especiales del negocio.

3. Controller
- Recibe la entrada del usuario.
- Valida reglas de negocio.
- Coordina el flujo entre vistas y DAO.

## 2. Plantilla de modelo

### 2.1 Estructura generica
Piensa en el modelo como una ficha de datos.

Elementos que normalmente debe tener:
- Identificador.
- Atributos de negocio.
- Constructor vacio.
- Constructor completo.
- Getters y setters.
- toString para depuracion.

### 2.2 Preguntas que debes hacerte antes de modelar
- Que pregunta del negocio responde esta entidad?
- Que datos son obligatorios?
- Que datos son calculados?
- Que datos son estados?
- Cuales deben ser unicos?

### 2.3 Ejemplo conceptual para Habitacion
Piensa en estos datos:
- id
- numero
- tipo
- precioPorNoche
- activa

Reglas mentales:
- numero debe ser unico.
- activa indica si la habitacion esta disponible como entidad operativa.

### 2.4 Ejemplo conceptual para Huesped
Piensa en estos datos:
- id
- nombre
- email
- activo

Reglas mentales:
- activo indica si puede reservar.
- email puede ser candidato a unico si tu negocio lo decide.

### 2.5 Ejemplo conceptual para Reserva
Piensa en estos datos:
- id
- idHabitacion
- idHuesped
- checkIn
- checkOut
- estado
- total

Reglas mentales:
- checkIn debe ser antes que checkOut.
- estado controla el ciclo de vida de la reserva.
- total se calcula con noches y precio de la habitacion.

## 3. Plantilla de DAO genérico

### 3.1 Que resuelve
El DAO generico evita que repitas lo mismo para cada entidad:
- buscar por id
- listar todos
- actualizar
- eliminar

### 3.2 Que no resuelve
No debe resolver:
- consultas especificas del negocio.
- reglas de negocio.
- validaciones complejas del dominio.

### 3.3 Como pensarlo
Antes de escribir un DAO concreto, pregunta:
- Que columnas tiene mi tabla?
- Como convierto una fila SQL a un objeto?
- Como lleno un PreparedStatement?
- Cuales son mis consultas especiales?

## 4. Plantilla de DAO concreto

### 4.1 Estructura mental
Todo DAO concreto debe responder estas cuatro ideas:
- como mapear la fila.
- como construir el SQL base.
- como poner parametros.
- que consultas extra necesita.

### 4.2 Bloques que normalmente tendra
- constantes SQL
- metodo de mapeo
- metodos heredados del DAO generico
- save con generated keys
- metodos especificos de negocio

### 4.3 Plantilla conceptual para HabitacionDAO
Preguntas de negocio:
- como busco por numero?
- como verifico que numero no exista?
- como filtro activas o inactivas?

Lo importante aqui:
- numero es una llave natural de negocio.
- active/activa te ayuda a saber si la habitacion esta disponible.

### 4.4 Plantilla conceptual para HuespedDAO
Preguntas de negocio:
- como verifico si existe por email?
- como veo solo los activos?
- como se si un huesped activo puede reservar?

Aqui la idea clave es:
- el DAO responde si existe o si esta activo.
- el controller decide si eso permite o no continuar.

### 4.5 Plantilla conceptual para ReservaDAO
Preguntas de negocio:
- como busco reservas por habitacion?
- como busco reservas por huesped?
- como detecto solapamiento?
- como cambio el estado de una reserva?

Este DAO es el mas importante del sistema porque conecta todo.

## 5. Plantilla de consultas especiales

### 5.1 Consulta por habitacion
Uso ideal:
- ver si una habitacion ya esta asignada.
- ver historial de reservas de una habitacion.

Piensa en dos variantes:
- una que devuelva todas las reservas.
- otra que devuelva solo la reserva activa.

### 5.2 Consulta por huesped
Uso ideal:
- ver la reserva de un huesped.
- ver historial del huesped.

Piensa en dos variantes:
- una que devuelva todas las reservas.
- otra que devuelva solo la reserva actual.

### 5.3 Consulta de solapamiento
Uso ideal:
- antes de crear una reserva nueva.

Logica mental:
- si la habitacion ya tiene una reserva cuya fecha se cruza con la nueva, no se permite continuar.

## 6. Plantilla de Controller

### 6.1 Rol del controller
El controller no habla SQL.
El controller organiza el caso de uso.

### 6.2 Flujo general de cualquier caso de uso
1. Pedir datos a la vista.
2. Validar entrada basica.
3. Consultar al DAO si hace falta.
4. Aplicar reglas de negocio.
5. Llamar al DAO para persistir.
6. Mostrar mensaje de resultado.

### 6.3 Plantilla mental para crear entidad
Ejemplo de orden:
- leer datos
- validar que no esten vacios
- validar unicidad
- construir objeto
- guardar
- informar resultado

### 6.4 Plantilla mental para crear reserva
Este es el flujo mas importante.

Orden sugerido:
1. Recibir idHabitacion, idHuesped, checkIn, checkOut.
2. Verificar que las fechas tengan sentido.
3. Verificar que la habitacion exista.
4. Verificar que la habitacion este activa.
5. Verificar que el huesped exista.
6. Verificar que el huesped este activo.
7. Verificar que no haya solapamiento.
8. Calcular total.
9. Crear la reserva.
10. Guardar la reserva.
11. Mostrar resultado.

### 6.5 Plantilla mental para check-in
Preguntas:
- existe la reserva?
- esta en estado BOOKED?
- se puede pasar a CHECKED_IN?

### 6.6 Plantilla mental para check-out
Preguntas:
- existe la reserva?
- esta en estado CHECKED_IN?
- se puede pasar a CHECKED_OUT?

## 7. Contratos actuales que ya tienes bien encaminados

### GenericDAO
Te da el CRUD base.
Eso esta bien porque evita repetir codigo.

### HabitacionDAO
Ya tiene sentido de negocio:
- buscar por numero
- verificar existencia
- filtrar por activa

### HuespedDAO
Tambien esta bien orientado:
- buscar por email
- verificar si esta activo
- filtrar activos

### ReservaDAO
Es el mas delicado:
- buscar por habitacion
- buscar por huesped
- buscar por estado
- detectar solapamiento
- actualizar estado

## 8. Lo que debes recordar al implementar

- DAO responde preguntas de datos.
- Controller decide si una respuesta permite continuar.
- Modelo solo guarda estado de la entidad.
- La reserva es el centro del negocio.
- Habitacion y Huesped son validaciones previas a Reserva.

## 9. Ruta de estudio recomendada

Orden para que no te pierdas:
1. Entender model.
2. Entender GenericDAO.
3. Entender un DAO simple como Usuario.
4. Entender HabitacionDAO.
5. Entender HuespedDAO.
6. Entender ReservaDAO.
7. Entender el flujo completo de crear reserva.

## 10. Regla final del tutor
- No escribas codigo hasta que puedas explicar en voz alta que hace cada metodo.
- Si no puedes explicar el flujo, todavia no lo implementes.
- La meta no es copiar una solucion, sino entender el patron y repetirlo con confianza.
