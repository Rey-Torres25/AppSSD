package com.ssd.appssd.objects;

import com.google.firebase.Timestamp;

public class Tabla {
    String correo, nombre;
    Timestamp fecha;

    public Tabla(String nombre, String correo, Timestamp fecha){
        this.nombre = nombre;
        this.correo = correo;
        this.fecha = fecha;
    }

    public Tabla() {

    }
    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    public Timestamp getFecha() {
        return fecha;
    }

    public void setFecha(Timestamp fecha) {
        this.fecha = fecha;
    }
}
