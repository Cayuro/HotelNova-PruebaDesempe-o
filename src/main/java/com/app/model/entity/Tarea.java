package com.app.model.entity;

import java.time.LocalDate;

public class Tarea {
    private int id;
    private String titulo;
    private boolean pendiente;
    private LocalDate fechaLimite;

    public Tarea() {}
    public Tarea(int id, String nombre, boolean pendiente) {
        this.id     = id;
        this.titulo = nombre;
        this.pendiente  = pendiente;
        this.fechaLimite = LocalDate.now().plusDays(7); // Por defecto, 7 días para completar
    }
    public Tarea(int int1, String string, boolean boolean1, LocalDate localDate) {
        this.id     = int1;
        this.titulo = string;
        this.pendiente  = boolean1;
        this.fechaLimite = localDate;
    }
    
    //Getters 
    public int getId() {return id;}
    public String getTitulo() {return titulo;}
    public boolean isPendiente() {return pendiente;}
    public LocalDate getFechaLimite() {return fechaLimite;}

    //setters
    public void setId(int id) {this.id = id;}
    public void setTitulo(String titulo) {this.titulo = titulo;}
    public void setPendiente(boolean pendiente) {this.pendiente = pendiente;}
    public void setFechaLimite(LocalDate fechaLimite) {this.fechaLimite = fechaLimite;}
    
}
