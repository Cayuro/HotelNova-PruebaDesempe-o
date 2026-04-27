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

CREATE INDEX IF NOT EXISTS idx_habitaciones_activa ON habitaciones(activa);
CREATE INDEX IF NOT EXISTS idx_huespedes_activo ON huespedes(activo);
CREATE INDEX IF NOT EXISTS idx_nombre ON usuarios(nombre);
CREATE INDEX IF NOT EXISTS idx_usuarios_username ON usuarios(username);
CREATE INDEX IF NOT EXISTS idx_usuarios_role ON usuarios(role);
CREATE INDEX IF NOT EXISTS idx_usuarios_activo ON usuarios(activo);


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


-- CREATE TABLE IF NOT EXISTS huespedes (
--     id INT AUTO_INCREMENT PRIMARY KEY,
--     nombre VARCHAR(120) NOT NULL,
--     email VARCHAR(150) NOT NULL UNIQUE,
--     activo BOOLEAN NOT NULL DEFAULT TRUE,
--     INDEX idx_huespedes_activo (activo)
-- );

-- CREATE TABLE IF NOT EXISTS habitaciones (
--     id INT AUTO_INCREMENT PRIMARY KEY,
--     numero VARCHAR(20) NOT NULL UNIQUE,
--     tipo VARCHAR(30) NOT NULL,
--     precio_por_noche DECIMAL(10,2) NOT NULL,
--     activa BOOLEAN NOT NULL DEFAULT TRUE,
--     INDEX idx_habitaciones_activa (activa)
-- );

-- CREATE TABLE IF NOT EXISTS reservas (
--     id INT AUTO_INCREMENT PRIMARY KEY,
--     id_habitacion INT NOT NULL,
--     id_huesped INT NOT NULL,
--     check_in DATE NOT NULL,
--     check_out DATE NOT NULL,
--     estado VARCHAR(20) NOT NULL,
--     total DECIMAL(12,2) NOT NULL,
--     CONSTRAINT fk_reserva_habitacion FOREIGN KEY (id_habitacion) REFERENCES habitaciones(id),
--     CONSTRAINT fk_reserva_huesped FOREIGN KEY (id_huesped) REFERENCES huespedes(id),
--     CONSTRAINT chk_fechas_reserva CHECK (check_in < check_out),
--     INDEX idx_reservas_habitacion (id_habitacion),
--     INDEX idx_reservas_huesped (id_huesped),
--     INDEX idx_reservas_estado (estado),
--     INDEX idx_reservas_rango (check_in, check_out)
-- );