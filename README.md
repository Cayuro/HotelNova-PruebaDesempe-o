# 🏨 HotelNova - Sistema de Gestión de Reservas

## 📋 Descripción del Proyecto

**HotelNova** es un sistema de gestión integral para hoteles desarrollado en **Java SE 17** con arquitectura por capas (MVC-DAO-JDBC). Centraliza la administración de:

- 🛏️ **Habitaciones** - Inventario de cuartos con tipos, capacidades y precios
- 👥 **Huéspedes** - Registro de clientes del hotel
- 🔐 **Usuarios** - Sistema de autenticación con roles (ADMIN, RECEPCIONISTA)
- 📅 **Reservas** - Gestión completa del ciclo de vida de reservas

El sistema implementa validaciones de negocio rigurosas, control de disponibilidad en tiempo real, y exportación de datos en formato CSV.

---

## 🏗️ Arquitectura

```
┌─────────────────────────────────────────────────────────┐
│  VIEW (JOptionPane UI)                                  │
│  - Menus interactivos                                   │
│  - Entrada/salida de datos                              │
└────────────────┬────────────────────────────────────────┘
                 │
┌────────────────▼────────────────────────────────────────┐
│  CONTROLLER                                              │
│  - Orquestación de casos de uso                          │
│  - Traducción UI → Service                              │
│  - Manejo de errores para presentación                   │
└────────────────┬────────────────────────────────────────┘
                 │
┌────────────────▼────────────────────────────────────────┐
│  SERVICE (Lógica de Negocio)                             │
│  - Validaciones RN-01 a RN-08                           │
│  - Cálculos de costos                                   │
│  - Transacciones ACID                                   │
│  - Coordinación de DAOs                                 │
└────────────────┬────────────────────────────────────────┘
                 │
┌────────────────▼────────────────────────────────────────┐
│  DAO (Acceso a Datos)                                    │
│  - HabitacionDAO, HuespedDAO, UsuarioDAO, ReservaDAO   │
│  - PreparedStatements + try-with-resources             │
│  - SQL parametrizado sin concatenación                  │
└────────────────┬────────────────────────────────────────┘
                 │
┌────────────────▼────────────────────────────────────────┐
│  DATABASE (MySQL / PostgreSQL)                           │
│  - Constraints UNIQUE, FK, CHECK                        │
│  - Índices en reservas y disponibilidad                 │
└─────────────────────────────────────────────────────────┘
```

---

## 📦 Estructura del Proyecto

```
new_practice/
├── pom.xml                          # Configuración Maven
├── README.md                        # Este archivo
├── docs/                            # Documentación del proyecto
│   ├── CONSTITUTION.md              # Reglas inquebrantables
│   ├── SPEC_DRIVEN_HOTELNOVA.md    # Especificación funcional
│   ├── IMPLEMENTATION_ROADMAP.md    # Plan de implementación
│   ├── MODELS_NOTEBOOK.md           # Guía de entidades
│   ├── DAOS_NOTEBOOK.md             # Especificación DAOs
│   └── CONTROLLERS_NOTEBOOK.md      # Especificación Controllers
│
├── src/main/java/com/app/
│   ├── Main.java                    # Punto de entrada
│   ├── config/
│   │   └── AppConfig.java           # Configuración centralizada
│   ├── controller/
│   │   ├── HabitacionController.java
│   │   ├── HuespedController.java
│   │   ├── UsuarioController.java
│   │   └── ReservationController.java
│   ├── service/
│   │   ├── impl/
│   │   │   ├── HabitacionServiceImpl.java
│   │   │   ├── HuespedServiceImpl.java
│   │   │   ├── UsuarioServiceImpl.java
│   │   │   ├── ReservaServiceImpl.java
│   │   │   └── AuthServiceImpl.java
│   ├── dao/
│   │   ├── GenericDAO.java
│   │   ├── HabitacionDAO.java
│   │   ├── HuespedDAO.java
│   │   ├── ReservaDAO.java
│   │   ├── UsuarioDAO.java
│   │   └── impl/
│   │       ├── HabitacionDAOImpl.java
│   │       ├── HuespedDAOImpl.java
│   │       ├── ReservaDAOImpl.java
│   │       └── UsuarioDAOImpl.java
│   ├── model/
│   │   └── entity/
│   │       ├── Usuario.java
│   │       ├── Huesped.java
│   │       ├── Habitacion.java
│   │       └── Reserva.java
│   ├── view/                        # Componentes UI (JOptionPane)
│   ├── util/
│   │   ├── CsvExporter.java         # Exportación CSV
│   │   └── ConfigReader.java        # Lectura de propiedades
│   ├── db/
│   │   └── ConnectionManager.java   # Pool de conexiones
│   └── exception/
│       └── BusinessException.java
│
├── src/main/resources/
│   ├── app.properties               # Propiedades de aplicación
│   ├── database.properties          # Credenciales DB
│   └── schema.sql                   # Script de crear tablas
│
└── src/test/java/com/app/
    ├── controller/                  # Tests de controllers
    ├── service/                     # Tests de servicios
    └── dao/                         # Tests de DAOs
```

---

## 🎯 Características Principales

### ✅ Gestión de Habitaciones
- Crear, editar, activar/desactivar habitaciones
- Tipos: SINGLE, DOUBLE, SUITE
- Control de disponibilidad en tiempo real
- Listado filtrable por tipo o estado

### ✅ Gestión de Huéspedes
- CRUD completo de clientes
- Validación de contacto (email, teléfono)
- Estado activo/inactivo

### ✅ Sistema de Usuarios y Autenticación
- Roles: ADMIN, RECEPCIONISTA
- Contraseñas hasheadas (seguridad)
- Autenticación obligatoria al iniciar

### ✅ Reservas Inteligentes
- Detección automática de disponibilidad
- Prevención de solapamientos
- Cálculo de costos con IVA configurable
- Estados: BOOKED → CHECKED_IN → CHECKED_OUT (o CANCELLED)

### ✅ Exportación de Datos
- **habitaciones_export.csv** - Listado completo de todas las habitaciones
- **reservas_activas.csv** - Solo reservas en curso (BOOKED o CHECKED_IN)
- Archivos en carpeta `exports/`

---

## 📋 Reglas de Negocio (RN-01 a RN-08)

| Código | Descripción | Validación en |
|--------|-------------|---------------|
| **RN-01** | Número de habitación único | Service + DB UNIQUE |
| **RN-02** | Solo habitaciones activas y DISPONIBLES se pueden reservar | ReservaService |
| **RN-03** | Solo huéspedes activos pueden crear reservas | ReservaService |
| **RN-04** | Fechas válidas: checkIn < checkOut | ReservaService |
| **RN-05** | No se permiten reservas solapadas | ReservaService |
| **RN-06** | Check-out válido solo si hay reserva CHECKED_IN | ReservaService |
| **RN-07** | Costo = noches × precioNoche × (1 + IVA) | ReservaService |
| **RN-08** | Seguridad: passwords hasheadas, roles obligatorios | AuthService |

---

## 🚀 Instalación y Configuración

### Requisitos Previos
- **Java 17+** (OpenJDK o Eclipse Temurin)
- **Maven 3.8+**
- **MySQL 8.0+** o **PostgreSQL 13+**

### Paso 1: Clonar/Descargar el Proyecto
```bash
cd /ruta/al/proyecto
```

### Paso 2: Configurar Base de Datos

#### Opción A: MySQL
```bash
# Crear base de datos
mysql -u root -p
CREATE DATABASE hotelnova;
USE hotelnova;
SOURCE src/main/resources/schema.sql;
```

#### Opción B: PostgreSQL
```bash
psql -U postgres
CREATE DATABASE hotelnova;
\c hotelnova
\i src/main/resources/schema.sql
```

### Paso 3: Configurar Credenciales

**`src/main/resources/database.properties`:**
```properties
# MySQL
db.driver=com.mysql.cj.jdbc.Driver
db.url=jdbc:mysql://localhost:3306/hotelnova
db.user=root
db.password=your_password

# PostgreSQL (descomenta si usas PG)
# db.driver=org.postgresql.Driver
# db.url=jdbc:postgresql://localhost:5432/hotelnova
# db.user=postgres
# db.password=your_password
```

**`src/main/resources/app.properties`:**
```properties
app.name=HotelNova
app.version=1.0.0
iva=0.16
horaCheckIn=14:00
horaCheckOut=11:00
```

### Paso 4: Compilar y Ejecutar

```bash
# Compilar
mvn clean compile

# Ejecutar tests
mvn test

# Ejecutar la aplicación
mvn exec:java -Dexec.mainClass="com.app.Main"
```

---

## 💾 Exportación CSV

### habitaciones_export.csv
**Contenido:** Todas las habitaciones del sistema.

```csv
ID,Número,Tipo,Capacidad,Precio/Noche,Estado,Activa,Creada,Actualizada
1,101,SINGLE,1,50.00,DISPONIBLE,true,2026-04-20 08:00:00,2026-04-20 08:00:00
2,102,DOUBLE,2,75.00,DISPONIBLE,true,2026-04-20 08:00:00,2026-04-20 08:00:00
3,103,SUITE,4,150.00,OCUPADA,true,2026-04-20 08:00:00,2026-04-21 10:30:00
...
```

**Ubicación:** `exports/habitaciones_export.csv`

**Generado desde:** Menú → Reportes → Exportar Habitaciones

### reservas_activas.csv
**Contenido:** Solo reservas en estado BOOKED o CHECKED_IN.

```csv
ID,Habitación,Huésped,Check-in,Check-out,Estado,Costo Total,Creada por,Creada
1,101,Juan Pérez,2026-04-22,2026-04-25,CHECKED_IN,225.00,admin,2026-04-20 09:15:00
2,102,María García,2026-04-23,2026-04-26,BOOKED,225.00,recepcionista,2026-04-21 10:30:00
...
```

**Ubicación:** `exports/reservas_activas.csv`

**Generado desde:** Menú → Reportes → Exportar Reservas Activas

---

## 🧪 Testing

El proyecto incluye tests JUnit 5 para:

- ✅ Validación de habitación única
- ✅ Validación de disponibilidad de habitación
- ✅ Validación de huésped activo
- ✅ Validación de fechas inválidas
- ✅ Prevención de solapamientos
- ✅ Cálculo de costo con IVA
- ✅ Check-out válido

Ejecutar tests:
```bash
mvn test
```

Ver reporte de cobertura:
```bash
mvn jacoco:report
open target/site/jacoco/index.html
```

---

## 🔐 Seguridad

- 🔒 **Contraseñas hasheadas** - No se almacenan en texto plano
- 🔑 **Control de Roles** - ADMIN vs RECEPCIONISTA con permisos diferenciados
- 🛡️ **Validación JDBC** - PreparedStatements contra inyección SQL
- 📝 **Auditoría** - Campo `createdByUserId` en reservas para trazabilidad
- 🔄 **Transacciones ACID** - Rollback automático en errores

---

## 📊 Diagrama de Casos de Uso

```
┌─────────────────┐
│   Usuario ADMIN  │
└────────┬────────┘
         │
    ┌────┴─────────────────────┬─────────────────┐
    │                          │                 │
    ▼                          ▼                 ▼
 [Gestión Usuarios]     [Gestión Habitaciones]  [Ver Reportes]
    │                          │                 │
    ▼                          ▼                 ▼
 CRUD Usuarios         Crear/Editar/Activar  Exportar Habitaciones
                       de Habitaciones       Exportar Reservas Activas


┌──────────────────────┐
│ Usuario RECEPCIONISTA │
└──────────┬───────────┘
           │
    ┌──────┴─────────────────────┬──────────────────┐
    │                            │                  │
    ▼                            ▼                  ▼
 [Gestión Huéspedes]    [Gestión Reservas]  [Ver Reportes]
    │                            │                  │
    ▼                            ▼                  ▼
 CRUD Huéspedes          Crear Reserva        Exportar
                         Check-in/Check-out   Habitaciones
                         Listar Activas       Reservas Activas
```

---

## 📝 Convenciones de Código

- **Nombres en Español** para entidades y métodos (según especificación)
- **CamelCase** para variables y métodos
- **UPPER_CASE** para constantes y enums
- **SQL parametrizado** - PreparedStatement en todos los DAO
- **Try-with-resources** para AutoCloseable (Connection, Statement, ResultSet)
- **Logging** - Simulando trazas HTTP (POST, GET, PATCH)

---

## 🐛 Solución de Problemas

### "Connection refused"
**Problema:** Base de datos no accesible.
```bash
# Verificar que MySQL/PostgreSQL está corriendo
mysql -u root -p
# o
psql -U postgres
```

### "No existe la base de datos"
```bash
# Crear y cargar schema
mysql -u root -p < src/main/resources/schema.sql
```

### "No se reconoce `mvn`"
```bash
# Descargar Maven desde https://maven.apache.org/download.cgi
# Agregar a PATH: export PATH=$PATH:/ruta/a/apache-maven/bin
```

### Tests fallan
```bash
# Limpiar y reconstruir
mvn clean test -DskipTests=false
```

---

## 📚 Documentación Adicional

- [CONSTITUTION.md](docs/CONSTITUTION.md) - Reglas arquitectónicas inquebrantables
- [SPEC_DRIVEN_HOTELNOVA.md](docs/SPEC_DRIVEN_HOTELNOVA.md) - Especificación detallada
- [MODELS_NOTEBOOK.md](docs/MODELS_NOTEBOOK.md) - Guía de entidades
- [DAOS_NOTEBOOK.md](docs/DAOS_NOTEBOOK.md) - Especificación de acceso a datos
- [CONTROLLERS_NOTEBOOK.md](docs/CONTROLLERS_NOTEBOOK.md) - Flujo de casos de uso
- [IMPLEMENTATION_ROADMAP.md](docs/IMPLEMENTATION_ROADMAP.md) - Plan de implementación

---

## 📜 Licencia

Este proyecto es parte de una prueba de desempeño en Java. Uso educativo.

---

## 👨‍💻 Autor

Desarrollado como ejercicio de arquitectura por capas y JDBC con Java SE 17.

**Última actualización:** 27 de abril de 2026

<!-- ---

## 🚢 Roadmap Futuro

- [ ] API REST (Spring Boot)
- [ ] Autenticación JWT
- [ ] Dashboard web (React/Angular)
- [ ] Reportes avanzados (JasperReports)
- [ ] Integración con PMS externos
- [ ] Mobile app (React Native)
- [ ] Dockerización -->
