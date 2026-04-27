package com.app.model.entity;

import java.math.BigDecimal;

public class Habitacion {
    private int id;
    private String numero;
    private String tipo;
    private BigDecimal precioNoche;
    private boolean disponible;

    public Habitacion() {}

    public Habitacion(int id, String numero, String tipo, BigDecimal precio, boolean disponible) {
        this.id = id;
        this.numero = numero;
        this.tipo = tipo;
        this.precioNoche = precio;
        this.disponible = disponible;
    }

    // Getters / Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNumero() {
        return numero;
    }

    public void setNumero(String numero) {
        this.numero = numero;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public BigDecimal getPrecioNoche() {
        return precioNoche;
    }

    public void setPrecioNoche(BigDecimal precioNoche) {
        this.precioNoche = precioNoche;
    }

    public boolean isDisponible() {
        return disponible;
    }

    public void setDisponible(boolean disponible) {
        this.disponible = disponible;
    }

    
}
