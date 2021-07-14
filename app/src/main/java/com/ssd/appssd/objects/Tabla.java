package com.ssd.appssd.objects;

import com.google.firebase.Timestamp;

public class Tabla {
    String correo;
    Timestamp fecha;

    public Tabla(String correo, Timestamp fecha){
        this.correo = correo;
        this.fecha = fecha;
    }

    public Tabla() {

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
