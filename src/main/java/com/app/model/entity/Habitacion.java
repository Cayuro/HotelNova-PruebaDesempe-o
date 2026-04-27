package com.app.model.entity;

import java.math.BigDecimal;

public class Habitacion {
    private int id;
    private String numero;
    private String tipo;
    private BigDecimal precioPorNoche;
    private boolean activa;

    public Habitacion() {}

    public Habitacion(int id, String numero, String tipo, BigDecimal precioPorNoche, boolean activa) {
        this.id = id;
        this.numero = numero;
        this.tipo = tipo;
        this.precioPorNoche = precioPorNoche;
        this.activa = activa;
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

    public BigDecimal getPrecioPorNoche() {
        return precioPorNoche;
    }

    public boolean isActiva() {
        return activa;
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

    public void setPrecioPorNoche(BigDecimal precioPorNoche) {
        this.precioPorNoche = precioPorNoche;
    }

    public void setActiva(boolean activa) {
        this.activa = activa;
    }

    @Override
    public String toString() {
        return "Habitacion{" +
                "id=" + id +
                ", numero='" + numero + '\'' +
                ", tipo='" + tipo + '\'' +
                ", precioPorNoche=" + precioPorNoche +
                ", activa=" + activa +
                '}';
    }
}