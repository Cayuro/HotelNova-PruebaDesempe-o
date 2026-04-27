package com.app.model.entity;

import java.math.BigDecimal;
import java.time.LocalDate;

public class Reserva {
    private int id;
    private int idHabitacion;
    private int idHuesped;
    private LocalDate checkIn;
    private LocalDate checkOut;
    private String estado;
    private BigDecimal total;

    public Reserva() {}

    public Reserva(int id, int idHabitacion, int idHuesped, LocalDate checkIn,
                   LocalDate checkOut, String estado, BigDecimal total) {
        this.id = id;
        this.idHabitacion = idHabitacion;
        this.idHuesped = idHuesped;
        this.checkIn = checkIn;
        this.checkOut = checkOut;
        this.estado = estado;
        this.total = total;
    }

    // Getters
    public int getId() {
        return id;
    }

    public int getIdHabitacion() {
        return idHabitacion;
    }

    public int getIdHuesped() {
        return idHuesped;
    }

    public LocalDate getCheckIn() {
        return checkIn;
    }

    public LocalDate getCheckOut() {
        return checkOut;
    }

    public String getEstado() {
        return estado;
    }

    public BigDecimal getTotal() {
        return total;
    }

    // Setters
    public void setId(int id) {
        this.id = id;
    }

    public void setIdHabitacion(int idHabitacion) {
        this.idHabitacion = idHabitacion;
    }

    public void setIdHuesped(int idHuesped) {
        this.idHuesped = idHuesped;
    }

    public void setCheckIn(LocalDate checkIn) {
        this.checkIn = checkIn;
    }

    public void setCheckOut(LocalDate checkOut) {
        this.checkOut = checkOut;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public void setTotal(BigDecimal total) {
        this.total = total;
    }

    @Override
    public String toString() {
        return "Reserva{" +
                "id=" + id +
                ", idHabitacion=" + idHabitacion +
                ", idHuesped=" + idHuesped +
                ", checkIn=" + checkIn +
                ", checkOut=" + checkOut +
                ", estado='" + estado + '\'' +
                ", total=" + total +
                '}';
    }
}