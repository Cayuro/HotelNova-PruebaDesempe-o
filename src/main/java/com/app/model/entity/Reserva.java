package com.app.model.entity;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class Reserva {
    private int id;
    private int idHabitacion;
    private int idHuesped;
    private LocalDate checkIn;
    private LocalDate checkOut;
    private String estado;                 // BOOKED, CHECKED_IN, CHECKED_OUT, CANCELLED
    private BigDecimal taxRateApplied;
    private BigDecimal total;
    private Integer createdByUserId;       // FK a Usuario - quien creó la reserva
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public Reserva() {}

    public Reserva(int id, int idHabitacion, int idHuesped, LocalDate checkIn,
                   LocalDate checkOut, String estado, BigDecimal total) {
        this.id = id;
        this.idHabitacion = idHabitacion;
        this.idHuesped = idHuesped;
        this.checkIn = checkIn;
        this.checkOut = checkOut;
        this.estado = estado;
        this.taxRateApplied = BigDecimal.ZERO;
        this.total = total;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    public Reserva(int id, int idHabitacion, int idHuesped, LocalDate checkIn,
                   LocalDate checkOut, String estado, BigDecimal taxRateApplied,
                   BigDecimal total, Integer createdByUserId) {
        this.id = id;
        this.idHabitacion = idHabitacion;
        this.idHuesped = idHuesped;
        this.checkIn = checkIn;
        this.checkOut = checkOut;
        this.estado = estado;
        this.taxRateApplied = taxRateApplied;
        this.total = total;
        this.createdByUserId = createdByUserId;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
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

    public BigDecimal getTaxRateApplied() {
        return taxRateApplied;
    }

    public BigDecimal getTotal() {
        return total;
    }

    public Integer getCreatedByUserId() {
        return createdByUserId;
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

    public void setTaxRateApplied(BigDecimal taxRateApplied) {
        this.taxRateApplied = taxRateApplied;
    }

    public void setTotal(BigDecimal total) {
        this.total = total;
    }

    public void setCreatedByUserId(Integer createdByUserId) {
        this.createdByUserId = createdByUserId;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
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
                ", taxRateApplied=" + taxRateApplied +
                ", total=" + total +
                ", createdByUserId=" + createdByUserId +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }
}