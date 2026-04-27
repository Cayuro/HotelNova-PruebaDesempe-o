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

CREATE INDEX IF NOT EXISTS idx_nombre ON usuarios(nombre);

CREATE TABLE IF NOT EXISTS tareas (
id SERIAL PRIMARY KEY,
titulo VARCHAR(120) NOT NULL,
pendiente BOOLEAN NOT NULL DEFAULT TRUE,
fecha_limite DATE NOT NULL
);

CREATE INDEX IF NOT EXISTS idx_tareas_pendiente ON tareas(pendiente);
CREATE INDEX IF NOT EXISTS idx_tareas_fecha_limite ON tareas(fecha_limite);


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