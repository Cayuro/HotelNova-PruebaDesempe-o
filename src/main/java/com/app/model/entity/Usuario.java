package com.app.model.entity;

import java.time.LocalDateTime;

public class Usuario extends Persona {

    private String username;
    private String passwordHash;
    private String role;
    private boolean activo;
    private LocalDateTime lastLoginAt;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public Usuario() {}

    public Usuario(int id, String nombre, String email) {
        super(id, nombre, email);
        this.username = nombre;
        this.passwordHash = "";
        this.role = "RECEPCIONISTA";
        this.activo = true;
    }

    public Usuario(int id, String nombre, String email, String username, String passwordHash,
                   String role, boolean activo, LocalDateTime lastLoginAt,
                   LocalDateTime createdAt, LocalDateTime updatedAt) {
        super(id, nombre, email);
        this.username = username;
        this.passwordHash = passwordHash;
        this.role = role;
        this.activo = activo;
        this.lastLoginAt = lastLoginAt;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    // Getters / Setters

    public String getUsername() { return username; }
    public String getPasswordHash() { return passwordHash; }
    public String getRole() { return role; }
    public boolean isActivo() { return activo; }
    public LocalDateTime getLastLoginAt() { return lastLoginAt; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }

    public void setUsername(String username) { this.username = username; }
    public void setPasswordHash(String passwordHash) { this.passwordHash = passwordHash; }
    public void setRole(String role) { this.role = role; }
    public void setActivo(boolean activo) { this.activo = activo; }
    public void setLastLoginAt(LocalDateTime lastLoginAt) { this.lastLoginAt = lastLoginAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    @Override
    public String toString() {
        return String.format(
                "Usuario{id=%d, nombre='%s', username='%s', email='%s', role='%s', activo=%s}",
                getId(), getNombre(), username, getEmail(), role, activo);
    }
}