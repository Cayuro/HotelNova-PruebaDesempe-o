# HotelNova - Spec Alignment Notebook

Fecha: 2026-04-27
Objetivo: medir cobertura entre el caso de uso solicitado y la especificacion/documentacion actual.

## Update 2026-04-27 (v2)

Se alinearon los documentos fuente para eliminar contradicciones de arquitectura:
- `docs/SPEC_DRIVEN_HOTELNOVA.md` actualizado a arquitectura por capas con `service`.
- `docs/IMPLEMENTATION_ROADMAP.md` actualizado a STEP 2..10 con service layer, autenticacion, transacciones, archivos y pruebas.

Nuevo estado documental:
- Especificacion: ALINEADA con prueba de desempeno.
- Roadmap: ALINEADO con prueba de desempeno.
- Implementacion de codigo: aun en progreso (pendiente ejecutar roadmap actualizado).

## Update 2026-04-27 (v3 - fase 1 service)

Migracion incremental aplicada sin ruptura:
- Se creo `ReservaService` y `ReservaServiceImpl`.
- Se movio la logica de negocio de creacion de reserva desde `ReservationController` a service.
- `ReservationController` mantiene constructor compatible con DAOs existentes y crea el service internamente, por lo que `Main` no requiere cambios para seguir funcionando.

Impacto funcional:
- Sin cambios de flujo para el usuario final.
- Misma ruta de entrada en menu principal.
- Validaciones de reserva centralizadas en service para facilitar pruebas y siguientes extracciones.

Proximas fases recomendadas:
1. Extraer logica de `HabitacionController` a `HabitacionService`.
2. Extraer logica de `HuespedController` a `HuespedService`.
3. Extraer logica de `UsuarioController` a `UsuarioService` y preparar autenticacion.
4. Migrar impuesto de reserva a `config.properties` y quitar constante fija del service.

## Update 2026-04-27 (v4 - usuarios)

Avance aplicado en modulo de usuarios:
- Entidad `Usuario` extendida con `username`, `passwordHash`, `role`, `activo`, `lastLoginAt`, `createdAt`, `updatedAt`.
- DAO de usuarios actualizado para persistir y consultar los nuevos campos.
- `UsuarioController` actualizado para crear/editar usuarios con `username`, `password` (hash), `role` y estado activo.
- Se agrego utilitario `PasswordHasher` (SHA-256) para no almacenar password en texto plano.
- `schema.sql` actualizado en PostgreSQL y referencia MySQL para columnas nuevas de usuarios e indices asociados.

Compatibilidad:
- Se mantiene `nombre` y `email` para no romper la jerarquia actual (por ejemplo `Huesped extends Usuario`).

## Update 2026-04-27 (v5 - login y pruebas)

Avance aplicado:
- Login funcional con `AuthService` y validacion de `username + password hash + activo`.
- Bootstrap de ADMIN inicial cuando la tabla de usuarios esta vacia (evita bloqueo de acceso en primera ejecucion).
- Filtro de menu principal por rol (ADMIN vs RECEPCIONISTA) en `Main`.
- JUnit 5 habilitado correctamente en Maven Surefire y pruebas ejecutando en verde.

Alineacion adicional con spec:
- Calculo de costo de reserva ahora usa `iva` desde `app.properties` (se elimino dependencia de constante hardcodeada).

Pendientes principales de implementacion:
1. Migrar logica de `HabitacionController`, `HuespedController` y `UsuarioController` a capa service dedicada.
2. Implementar transacciones JDBC reales para check-in/check-out con `setAutoCommit(false)`, `commit` y `rollback`.
3. Completar exportaciones CSV (`habitaciones_export.csv`, `reservas_activas.csv`).
4. Estandarizar trazas tipo HTTP y logging tecnico en todos los casos de uso.
5. Cerrar brecha de schema en habitaciones/huespedes/reservas (capacidad, estado operativo, trazabilidad completa).

## Update 2026-04-27 (v6 - controllers a service)

Avance aplicado en capa de aplicacion:
- `HabitacionController` ahora delega validaciones y operaciones de negocio en `HabitacionService`.
- `HuespedController` ahora delega validaciones y operaciones de negocio en `HuespedService`.
- Ambos controladores mantienen constructor compatible con DAO para no romper wiring actual en `Main` y pruebas existentes.

Correcciones de regresion realizadas durante la migracion:
- Ajuste de mensaje de activacion/desactivacion de huesped segun estado final real.
- Ajuste de flujo de actualizacion de huesped para preservar validacion de email unico.

Validacion:
- Suite ejecutada con `mvn -q test`: en verde tras los ajustes.

Pendientes inmediatos:
1. Extraer logica de `UsuarioController` a `UsuarioService`.
2. Implementar transacciones JDBC explicitas para check-in/check-out.
3. Completar exportaciones CSV y pruebas asociadas.

## 1) Resumen ejecutivo

Estado general: PARCIALMENTE ALINEADO.

Fortalezas actuales:
- Base MVC + DAO funcional en usuarios/tareas.
- Entidades hoteleras base existentes (Habitacion, Huesped, Reserva).
- Schema con tablas nucleares y constraints iniciales (UNIQUE de numero y CHECK de fechas).

Brechas mayores:
- Falta definir y aplicar service layer (el spec actual dice que NO hay service layer).
- Autenticacion y roles incompletos respecto al caso de uso.
- Reserva no tiene trazabilidad completa (createdByUserId, taxRateApplied, timestamps, etc.).
- Flujos transaccionales de check-in/check-out no documentados al nivel requerido.
- Exportaciones CSV y logging en app.log no cerrados.
- Matriz de pruebas aun incompleta para todos los criterios pedidos.

## 2) Conflicto de arquitectura a resolver primero

Caso de uso nuevo exige: controller + service + dao + model.
Spec actual en docs/SPEC_DRIVEN_HOTELNOVA.md y docs/IMPLEMENTATION_ROADMAP.md mantiene: controller + dao (sin service layer).

Decision pendiente de arquitectura:
- Opcion A: mantener sin service y adaptar el caso de uso.
- Opcion B: introducir service layer y mover logica de negocio principal alli (recomendada para cumplir rubricado del caso de uso).

Sin esta decision, el spec queda ambiguo y puede generar implementaciones inconsistentes.

## 3) Matriz de cobertura (caso de uso vs estado actual)

### R1. Gestion de Habitaciones
Requerido:
- id, numero, tipo, capacidad, precioPorNoche, estado(DISPONIBLE/OCUPADA), isActiva, createdAt
- CRUD + filtros por tipo/estado
- numero unico

Estado actual: PARCIAL
- Existe: id, numero, tipo, precioPorNoche, activa
- Falta: capacidad, estado operativo (DISPONIBLE/OCUPADA), createdAt
- Numero unico: SI (DB)
- Filtros: no completados para estado operativo

Impacto:
- No se puede modelar ocupacion operativa con semantica completa.

### R2. Usuarios y autenticacion
Requerido:
- login con credenciales y roles ADMIN/RECEPCIONISTA
- password segura (hash, ej. BCrypt)
- logs de trazas tipo HTTP

Estado actual: BAJO
- Usuario actual: nombre/email basico
- Falta: username, passwordHash, role, active, lastLoginAt, autenticacion
- Hash de password: NO implementado
- Trazas HTTP simuladas: NO estandarizadas

### R3. Interfaz JOptionPane
Requerido:
- menus: Habitaciones, Huespedes, Usuarios, Reservas, Exportaciones
- mensajes de exito/error
- helper de tablas alineadas y etiquetas

Estado actual: PARCIAL
- Hay base de vista y controladores con menu para usuarios/tareas
- Falta menu integral de modulos hoteleros y helpers de tabla estandarizados

### R4. CRUD + JDBC + Transacciones
Requerido:
- DAOs por entidad hotelera
- transaccion check-in/check-out con commit/rollback
- try-with-resources

Estado actual: PARCIAL
- GenericDAO y GenericDAOImpl existentes
- DAO de Habitacion incompleto
- DAO de Reserva interface parcial y sin implementacion visible
- Transacciones de reserva/check-in/check-out no cerradas

### R5. Manejo de archivos
Requerido:
- config.properties (db, horas, iva)
- exportaciones CSV especificas
- app.log

Estado actual: PARCIAL
- Existen archivos properties base
- No se evidencia implementacion completa de CsvExporter ni logging consolidado

### R6. Excepciones y validaciones
Requerido:
- jerarquia de excepciones personalizadas
- reglas de negocio clave (unicidad, disponibilidad, activo, fechas, solapamiento, check-out valido)

Estado actual: PARCIAL
- Reglas descritas en spec
- Falta cierre implementativo y mapeo sistematico en todos los flujos

### R7. Pruebas JUnit 5
Requerido:
- pruebas de reglas criticas y calculo de costo con IVA de properties

Estado actual: PENDIENTE/PARCIAL
- Plan de pruebas documentado
- Cobertura de codigo real no demostrada para todos los escenarios solicitados

## 4) Gap puntual del schema frente al caso de uso

Tabla habitaciones:
- agregar capacidad
- agregar estado_operativo (DISPONIBLE/OCUPADA)
- agregar created_at (y opcional updated_at)

Tabla usuarios:
- migrar de nombre/email basico a username + password_hash + role + active + last_login_at + timestamps

Tabla reservas:
- agregar created_by_user_id (FK usuarios)
- agregar tax_rate_applied
- renombrar/normalizar total a total_cost
- agregar created_at y updated_at
- asegurar indices por id_habitacion, id_huesped, estado y rango de fechas

## 5) Recomendacion de alineacion del spec

1. Definir arquitectura final (con o sin service layer).
2. Congelar contrato de entidades (campos obligatorios + enums + estados).
3. Actualizar schema objetivo y versionarlo.
4. Actualizar contratos DAO/Service/Controller segun decision.
5. Actualizar plan de pruebas con trazabilidad requisito -> test.
6. Mantener esta bitacora actualizada por cada entrega.

## 6) Documentos de target/docsFinal a crear por iteracion

- MODELS_NOTEBOOK.md: cambios de atributos, invariantes y compatibilidad.
- DAOS_NOTEBOOK.md: queries nuevas, transacciones y mapping.
- CONTROLLERS_NOTEBOOK.md: flujos de casos de uso y manejo de errores.
- SPEC_ALIGNMENT_NOTEBOOK.md: estado de cobertura por requisito (este archivo).
- TESTS_NOTEBOOK.md (nuevo recomendado): mapa de pruebas implementadas vs requisitos.
- MIGRATIONS_NOTEBOOK.md (nuevo recomendado): cambios de schema y estrategia de migracion.

## 7) Semaforo actual

- Verde: base de proyecto, CRUD inicial, entidades hoteleras base.
- Amarillo: UI integral, DAOs hoteleros completos, exportaciones, logs.
- Rojo: autenticacion con roles + hash, transacciones completas de reserva/check-out, cobertura total de pruebas.

## 8) Proximo checkpoint sugerido

Checkpoint 1 (diseno):
- cerrar decision sobre service layer
- cerrar contrato final de entidades
- aprobar schema objetivo v2

Checkpoint 2 (infraestructura):
- migraciones SQL + DAOs base de Habitacion/Huesped/Reserva/Usuario v2

Checkpoint 3 (negocio y UX):
- login + roles + check-in/check-out transaccional + menu completo JOptionPane

Checkpoint 4 (calidad):
- logging, exportaciones CSV, suite JUnit 5 y trazabilidad final
