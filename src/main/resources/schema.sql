-- ==========================================
-- PostgreSQL Schema
-- ==========================================
-- 1. Crear la base de datos manualmente:
-- CREATE DATABASE appdb;
-- 2. Conectarse a la base de datos (en psql: \c appdb)


CREATE TABLE IF NOT EXISTS usuarios (
    id      SERIAL PRIMARY KEY,
    nombre  VARCHAR(100) NOT NULL,
    email   VARCHAR(150) NOT NULL UNIQUE
);


CREATE TABLE IF NOT EXISTS tareas (
id SERIAL PRIMARY KEY,
titulo VARCHAR(120) NOT NULL,
pendiente BOOLEAN NOT NULL DEFAULT TRUE,
fecha_limite DATE NOT NULL
);


CREATE TABLE IF NOT EXISTS huespedes (
    id      SERIAL PRIMARY KEY,
    nombre  VARCHAR(120) NOT NULL,
    email   VARCHAR(150) NOT NULL UNIQUE,
    activo  BOOLEAN NOT NULL DEFAULT TRUE
);


CREATE TABLE IF NOT EXISTS habitaciones (
    id               SERIAL PRIMARY KEY,
    numero           VARCHAR(20) NOT NULL UNIQUE,
    tipo             VARCHAR(30) NOT NULL,
    precio_por_noche NUMERIC(10,2) NOT NULL,
    activa           BOOLEAN NOT NULL DEFAULT TRUE
);


CREATE TABLE IF NOT EXISTS reservas (
    id            SERIAL PRIMARY KEY,
    id_habitacion INT NOT NULL,
    id_huesped    INT NOT NULL,
    check_in      DATE NOT NULL,
    check_out     DATE NOT NULL,
    estado        VARCHAR(20) NOT NULL,
    total         NUMERIC(12,2) NOT NULL,
    CONSTRAINT fk_reserva_habitacion FOREIGN KEY (id_habitacion) REFERENCES habitaciones(id),
    CONSTRAINT fk_reserva_huesped FOREIGN KEY (id_huesped) REFERENCES huespedes(id),
    CONSTRAINT chk_fechas_reserva CHECK (check_in < check_out)
);

CREATE INDEX IF NOT EXISTS idx_habitaciones_activa ON habitaciones(activa);
CREATE INDEX IF NOT EXISTS idx_tareas_fecha_limite ON tareas(fecha_limite);
CREATE INDEX IF NOT EXISTS idx_huespedes_activo ON huespedes(activo);
CREATE INDEX IF NOT EXISTS idx_tareas_pendiente ON tareas(pendiente);
CREATE INDEX IF NOT EXISTS idx_tareas_fecha_limite ON tareas(fecha_limite);
CREATE INDEX IF NOT EXISTS idx_nombre ON usuarios(nombre);


-- ==========================================
-- MySQL Schema (Referencia)
-- ==========================================
-- CREATE DATABASE IF NOT EXISTS appdb;
-- USE appdb;

-- CREATE TABLE IF NOT EXISTS usuarios (
--     id      INT AUTO_INCREMENT PRIMARY KEY,
--     nombre  VARCHAR(100) NOT NULL,
--     email   VARCHAR(150) NOT NULL UNIQUE,
--     INDEX idx_nombre (nombre)
-- );

-- CREATE TABLE IF NOT EXISTS tareas (
-- id INT AUTO_INCREMENT PRIMARY KEY,
-- titulo VARCHAR(120) NOT NULL,
-- pendiente BOOLEAN NOT NULL DEFAULT TRUE,
-- fecha_limite DATE NOT NULL
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