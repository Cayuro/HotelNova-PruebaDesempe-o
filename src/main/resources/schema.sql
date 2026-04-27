-- ==========================================
-- PostgreSQL Schema
-- ==========================================
-- 1. Crear la base de datos manualmente:
-- CREATE DATABASE appdb;
-- 2. Conectarse a la base de datos (en psql: \c appdb)


CREATE TABLE IF NOT EXISTS usuarios (
    id            SERIAL PRIMARY KEY,
    nombre        VARCHAR(100) NOT NULL,
    email         VARCHAR(150) NOT NULL UNIQUE,
    username      VARCHAR(80) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    role          VARCHAR(20) NOT NULL,
    activo        BOOLEAN NOT NULL DEFAULT TRUE,
    last_login_at TIMESTAMP NULL,
    created_at    TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at    TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT chk_usuarios_role CHECK (role IN ('ADMIN', 'RECEPCIONISTA'))
);


CREATE TABLE IF NOT EXISTS huespedes (
    id         SERIAL PRIMARY KEY,
    nombre     VARCHAR(120) NOT NULL,
    email      VARCHAR(150) NOT NULL UNIQUE,
    phone      VARCHAR(20) NULL,
    activo     BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);


CREATE TABLE IF NOT EXISTS habitaciones (
    id               SERIAL PRIMARY KEY,
    numero           VARCHAR(20) NOT NULL UNIQUE,
    tipo             VARCHAR(30) NOT NULL,
    capacidad        INT NOT NULL DEFAULT 1,
    precio_por_noche NUMERIC(10,2) NOT NULL,
    estado           VARCHAR(20) NOT NULL DEFAULT 'DISPONIBLE',
    activa           BOOLEAN NOT NULL DEFAULT TRUE,
    created_at       TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at       TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT chk_habitaciones_estado CHECK (estado IN ('DISPONIBLE', 'OCUPADA')),
    CONSTRAINT chk_habitaciones_tipo CHECK (tipo IN ('SINGLE', 'DOUBLE', 'SUITE')),
    CONSTRAINT chk_habitaciones_capacidad CHECK (capacidad > 0)
);


CREATE TABLE IF NOT EXISTS reservas (
    id               SERIAL PRIMARY KEY,
    id_habitacion    INT NOT NULL,
    id_huesped       INT NOT NULL,
    check_in         DATE NOT NULL,
    check_out        DATE NOT NULL,
    estado           VARCHAR(20) NOT NULL,
    tax_rate_applied NUMERIC(5,4) NOT NULL DEFAULT 0.0000,
    total            NUMERIC(12,2) NOT NULL,
    created_by_user_id INT NULL,
    created_at       TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at       TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_reserva_habitacion FOREIGN KEY (id_habitacion) REFERENCES habitaciones(id),
    CONSTRAINT fk_reserva_huesped FOREIGN KEY (id_huesped) REFERENCES huespedes(id),
    CONSTRAINT fk_reserva_usuario FOREIGN KEY (created_by_user_id) REFERENCES usuarios(id),
    CONSTRAINT chk_fechas_reserva CHECK (check_in < check_out),
    CONSTRAINT chk_reservas_estado CHECK (estado IN ('BOOKED', 'CHECKED_IN', 'CHECKED_OUT', 'CANCELLED'))
);

-- Indices de usuarios
CREATE INDEX IF NOT EXISTS idx_usuarios_username ON usuarios(username);
CREATE INDEX IF NOT EXISTS idx_usuarios_role ON usuarios(role);
CREATE INDEX IF NOT EXISTS idx_usuarios_activo ON usuarios(activo);
CREATE INDEX IF NOT EXISTS idx_nombre ON usuarios(nombre);

-- Indices de huespedes
CREATE INDEX IF NOT EXISTS idx_huespedes_activo ON huespedes(activo);
CREATE INDEX IF NOT EXISTS idx_huespedes_email ON huespedes(email);

-- Indices de habitaciones
CREATE INDEX IF NOT EXISTS idx_habitaciones_activa ON habitaciones(activa);
CREATE INDEX IF NOT EXISTS idx_habitaciones_estado ON habitaciones(estado);
CREATE INDEX IF NOT EXISTS idx_habitaciones_tipo ON habitaciones(tipo);

-- Indices de reservas (CRÍTICOS para existsOverlap y búsquedas)
CREATE INDEX IF NOT EXISTS idx_reservas_id_habitacion ON reservas(id_habitacion);
CREATE INDEX IF NOT EXISTS idx_reservas_id_huesped ON reservas(id_huesped);
CREATE INDEX IF NOT EXISTS idx_reservas_estado ON reservas(estado);
-- Índice compuesto para búsqueda de solapamientos (existsOverlap)
CREATE INDEX IF NOT EXISTS idx_reservas_habitacion_estado_fechas ON reservas(id_habitacion, estado, check_in, check_out);


-- ==========================================
-- MySQL Schema (Referencia)
-- ==========================================
-- CREATE DATABASE IF NOT EXISTS appdb;
-- USE appdb;

-- CREATE TABLE IF NOT EXISTS usuarios (
--     id INT AUTO_INCREMENT PRIMARY KEY,
--     nombre VARCHAR(100) NOT NULL,
--     email VARCHAR(150) NOT NULL UNIQUE,
--     username VARCHAR(80) NOT NULL UNIQUE,
--     password_hash VARCHAR(255) NOT NULL,
--     role VARCHAR(20) NOT NULL,
--     activo BOOLEAN NOT NULL DEFAULT TRUE,
--     last_login_at DATETIME NULL,
--     created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
--     updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
--     CONSTRAINT chk_usuarios_role CHECK (role IN ('ADMIN', 'RECEPCIONISTA')),
--     INDEX idx_nombre (nombre),
--     INDEX idx_usuarios_username (username),
--     INDEX idx_usuarios_role (role),
--     INDEX idx_usuarios_activo (activo)
-- );

-- -- Creación de la tabla HUESPEDES
-- CREATE TABLE IF NOT EXISTS huespedes (
--     id         INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
--     nombre     VARCHAR(120) NOT NULL,
--     email      VARCHAR(150) NOT NULL UNIQUE,
--     phone      VARCHAR(20) NULL,
--     activo     BOOLEAN NOT NULL DEFAULT TRUE,
--     created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
--     updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
-- );

-- -- Creación de la tabla HABITACIONES
-- CREATE TABLE IF NOT EXISTS habitaciones (
--     id               INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
--     numero           VARCHAR(20) NOT NULL UNIQUE,
--     tipo             VARCHAR(30) NOT NULL,
--     capacidad        INT NOT NULL DEFAULT 1,
--     precio_por_noche DECIMAL(10,2) NOT NULL,
--     estado           VARCHAR(20) NOT NULL DEFAULT 'DISPONIBLE',
--     activa           BOOLEAN NOT NULL DEFAULT TRUE,
--     created_at       TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
--     updated_at       TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
--     CONSTRAINT chk_habitaciones_estado CHECK (estado IN ('DISPONIBLE', 'OCUPADA')),
--     CONSTRAINT chk_habitaciones_tipo CHECK (tipo IN ('SINGLE', 'DOUBLE', 'SUITE')),
--     CONSTRAINT chk_habitaciones_capacidad CHECK (capacidad > 0)
-- );

-- -- Creación de la tabla RESERVAS
-- CREATE TABLE IF NOT EXISTS reservas (
--     id               INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
--     id_habitacion    INT NOT NULL,
--     id_huesped       INT NOT NULL,
--     check_in         DATE NOT NULL,
--     check_out        DATE NOT NULL,
--     estado           VARCHAR(20) NOT NULL,
--     tax_rate_applied DECIMAL(5,4) NOT NULL DEFAULT 0.0000,
--     total            DECIMAL(12,2) NOT NULL,
--     created_by_user_id INT NULL,
--     created_at       TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
--     updated_at       TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
--     CONSTRAINT fk_reserva_habitacion FOREIGN KEY (id_habitacion) REFERENCES habitaciones(id),
--     CONSTRAINT fk_reserva_huesped FOREIGN KEY (id_huesped) REFERENCES huespedes(id),
--     CONSTRAINT fk_reserva_usuario FOREIGN KEY (created_by_user_id) REFERENCES usuarios(id),
--     CONSTRAINT chk_fechas_reserva CHECK (check_in < check_out),
--     CONSTRAINT chk_reservas_estado CHECK (estado IN ('BOOKED', 'CHECKED_IN', 'CHECKED_OUT', 'CANCELLED'))
-- );