package com.app.model.entity;

import java.time.LocalDateTime;

public class Huesped extends Persona{
    private boolean activo;
    private String phone;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public Huesped(){}
    
    public Huesped(int id, String nombre, String email, boolean estado){
        super(id, nombre, email);
        this.activo = estado;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }
    
    public Huesped(int id, String nombre, String email){
        super(id, nombre, email);
        this.activo = true;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    // getters
    public boolean isActivo() {
        return activo;
    }

    public String getPhone() {
        return phone;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    //setters
    public void setActivo(boolean activo) {
        this.activo = activo;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    @Override
    public String toString() {
        return "Huesped{" +
                "id=" + getId() +
                ", nombre='" + getNombre() + '\'' +
                ", email='" + getEmail() + '\'' +
                ", phone='" + phone + '\'' +
                ", activo=" + activo +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }
}