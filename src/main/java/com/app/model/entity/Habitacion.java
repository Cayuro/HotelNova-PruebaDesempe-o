package com.app.model.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class Habitacion {
    private int id;
    private String numero;
    private String tipo;           // SINGLE, DOUBLE, SUITE
    private int capacidad;
    private BigDecimal precioPorNoche;
    private String estado;         // DISPONIBLE, OCUPADA
    private boolean activa;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public Habitacion() {}

    public Habitacion(int id, String numero, String tipo, int capacidad, 
                     BigDecimal precioPorNoche, String estado, boolean activa) {
        this.id = id;
        this.numero = numero;
        this.tipo = tipo;
        this.capacidad = capacidad;
        this.precioPorNoche = precioPorNoche;
        this.estado = estado;
        this.activa = activa;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    // Getters
    public int getId() {
        return id;
    }

    public String getNumero() {
        return numero;
    }

    public String getTipo() {
        return tipo;
    }

    public int getCapacidad() {
        return capacidad;
    }

    public BigDecimal getPrecioPorNoche() {
        return precioPorNoche;
    }

    public String getEstado() {
        return estado;
    }

    public boolean isActiva() {
        return activa;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    // Setters
    public void setId(int id) {
        this.id = id;
    }

    public void setNumero(String numero) {
        this.numero = numero;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public void setCapacidad(int capacidad) {
        this.capacidad = capacidad;
    }

    public void setPrecioPorNoche(BigDecimal precioPorNoche) {
        this.precioPorNoche = precioPorNoche;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public void setActiva(boolean activa) {
        this.activa = activa;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    @Override
    public String toString() {
        return "Habitacion{" +
                "id=" + id +
                ", numero='" + numero + '\'' +
                ", tipo='" + tipo + '\'' +
                ", capacidad=" + capacidad +
                ", precioPorNoche=" + precioPorNoche +
                ", estado='" + estado + '\'' +
                ", activa=" + activa +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }
}