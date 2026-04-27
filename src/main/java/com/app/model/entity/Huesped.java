package com.app.model.entity;

public class Huesped extends Usuario{
    boolean activo;
    public Huesped() {
        super();
        this.activo = true;
    }
    Huesped(int id, String nombre, String email) {
        super(id, nombre, email);
        this.activo = true;
    }
    public Huesped(int id, String nombre, String email, boolean activo) {
        super(id, nombre, email);
        this.activo = activo;
    }

    public boolean isActivo(){
        return activo;
    }
    public void setActivo(boolean activo) {
        this.activo = activo;
    }
}

