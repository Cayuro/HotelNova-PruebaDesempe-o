package com.app.model.entity;

public class Huesped extends Persona{
    private boolean activo;

    public Huesped(){}
    public Huesped(int id, String nombre, String email,boolean estado){
        super(id,nombre,email);
        this.activo = estado;
    }
    public Huesped(int id, String nombre, String email){
        super(id,nombre,email);
        this.activo = true;
    }

    // getters
   
    public boolean isActivo() {
        return activo;
    }
    //setters
    public void setActivo(boolean activo) {
        this.activo = activo;
    }

    @Override
    public String toString() {
        return "Huesped{" +
                "id=" + getId() +
                ", nombre='" + getNombre() + '\'' +
                ", email='" + getEmail() + '\'' +
                ", activo=" + activo +
                '}';
    }
}