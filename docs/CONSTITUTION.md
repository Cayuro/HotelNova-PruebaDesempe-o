# 📜 HotelNova - CONSTITUTION

## 1. Propósito

Este documento define las **reglas inquebrantables** que rigen el diseño, implementación y evolución del sistema HotelNova.

Toda implementación debe cumplir este documento.
En caso de conflicto entre código y Constitution:
👉 **la Constitution tiene prioridad.**

---

## 2. Arquitectura Oficial (OBLIGATORIA)

El sistema sigue estrictamente una arquitectura por capas:

```text
View → Controller → Service → DAO → Database
```

### Reglas:

* ❗ PROHIBIDO que la View invoque directamente DAOs
* ❗ PROHIBIDO que el Controller acceda directamente a DAOs
* ❗ PROHIBIDO que el DAO contenga lógica de negocio
* ❗ PROHIBIDO que el Service dependa de la View
* ❗ OBLIGATORIO que toda lógica de negocio viva en Service

---

## 3. Responsabilidades por capa

### 3.1 View

* Interacción con el usuario (JOptionPane o consola)
* Mostrar mensajes, menús y resultados
* NO contiene lógica de negocio

---

### 3.2 Controller

* Orquesta casos de uso
* Traduce inputs de la View hacia el Service
* Maneja errores hacia la View

❗ PROHIBIDO:

* Validar reglas de negocio
* Acceder directamente a DAO

---

### 3.3 Service

* Contiene TODA la lógica de negocio
* Aplica validaciones
* Coordina transacciones

✔ Ejemplos:

* Validar disponibilidad de habitación
* Validar fechas de reserva
* Calcular costo total

---

### 3.4 DAO

* Encargado exclusivamente de acceso a datos
* Ejecuta SQL (CRUD)
* Usa JDBC con try-with-resources

❗ PROHIBIDO:

* Validaciones de negocio
* Lógica de flujo

---

### 3.5 Model

* Representa entidades del dominio
* Define atributos, enums y relaciones

---

## 4. Contratos de Dominio (OBLIGATORIOS)

### 4.1 Habitación

Debe contener:

```text
id
numero (UNIQUE)
tipo
capacidad
precioPorNoche
estado → ENUM(DISPONIBLE, OCUPADA)
isActiva → boolean
createdAt
```

---

### 4.2 Usuario

```text
id
username (UNIQUE)
passwordHash
role → ENUM(ADMIN, RECEPCIONISTA)
activo → boolean
lastLoginAt
createdAt
updatedAt
```

---

### 4.3 Reserva

```text
id
habitacionId
huespedId
createdByUserId
fechaCheckIn
fechaCheckOut
estado → ENUM(ACTIVA, FINALIZADA)
totalCost
taxRateApplied
createdAt
updatedAt
```

---

## 5. Reglas de Negocio (OBLIGATORIAS)

* ❗ Número de habitación debe ser único
* ❗ No se puede reservar una habitación no disponible
* ❗ No se permiten reservas solapadas
* ❗ El huésped debe estar activo
* ❗ Check-in < Check-out
* ❗ No se permite check-out sin reserva activa

---

## 6. Transacciones (CRÍTICO)

### 6.1 Check-in (Reserva)

Flujo obligatorio:

```text
1. Validar reglas de negocio
2. setAutoCommit(false)
3. Insertar reserva
4. Actualizar habitación → OCUPADA
5. commit()
6. rollback() en error
```

---

### 6.2 Check-out

```text
1. Validar reserva activa
2. setAutoCommit(false)
3. Calcular costo:
   noches × precio + IVA (config.properties)
4. Actualizar reserva → FINALIZADA
5. Actualizar habitación → DISPONIBLE
6. commit()
7. rollback() en error
```

---

## 7. Seguridad (OBLIGATORIA)

* ❗ PROHIBIDO almacenar contraseñas en texto plano
* ✔ OBLIGATORIO usar hash (SHA-256 mínimo, ideal BCrypt)
* ✔ Validar usuario activo en login

---

## 8. Manejo de Archivos

### config.properties (OBLIGATORIO)

Debe contener:

```properties
db.url=
db.user=
db.password=
horaCheckIn=
horaCheckOut=
iva=
```

---

### Exportaciones CSV

* habitaciones_export.csv → todas las habitaciones
* reservas_activas.csv → solo reservas activas

---

## 9. Logging (OBLIGATORIO)

Cada operación debe generar trazas tipo HTTP:

```text
POST /reservas
GET /habitaciones
PATCH /reservas/{id}/checkout
DELETE /usuarios/{id}
```

Errores deben registrarse en:

```text
app.log
```

---

## 10. Excepciones

* ✔ Crear excepciones personalizadas
* ✔ Manejo con try/catch
* ✔ Mostrar errores en UI
* ✔ Log técnico en archivo

---

## 11. Testing (OBLIGATORIO)

Se deben cubrir:

* ✔ Validación de habitación única
* ✔ Validación de disponibilidad
* ✔ Validación de huésped activo
* ✔ Validación de fechas
* ✔ No solapamiento
* ✔ Flujo de check-out
* ✔ Cálculo de costo con IVA

Herramientas:

* JUnit 5

---

## 12. Navegación y Menú (CRÍTICO)

* ❗ PROHIBIDO mezclar lógica de roles con lógica de navegación
* ✔ Cada rol debe tener su propio flujo de menú

Ejemplo:

```text
ADMIN → Usuarios, Habitaciones, Reservas, Huespedes
RECEPCIONISTA → Reservas, Huespedes, Habitaciones
```

---

## 13. Trazabilidad

Cada requisito debe tener:

```text
Requisito → Implementación → Test
```

Ejemplo:

```text
Habitación única → HabitacionService → testHabitacionUnica()
```

---

## 14. Regla de Oro

👉 Si una lógica no está en Service, está mal ubicada.
👉 Si una validación no está centralizada, está incompleta.
👉 Si el flujo no es transaccional, está incorrecto.

---

## 15. Evolución del sistema

Cualquier cambio debe:

1. Actualizar el SPEC
2. Validar contra esta Constitution
3. Reflejarse en código
4. Cubrirse con pruebas

---

## 16. Criterio de aceptación final

El sistema se considera correcto solo si:

* Cumple arquitectura por capas
* Implementa reglas de negocio completas
* Maneja transacciones correctamente
* Tiene autenticación funcional
* Exporta archivos y registra logs
* Posee pruebas que validen el comportamiento

---

## 📌 Nota final

Este documento no es opcional.
Es el estándar mínimo de calidad del sistema HotelNova.

---
