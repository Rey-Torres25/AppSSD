package com.ssd.appssd.objects;

public class User {

    private String nombre, correo;
    private boolean admin;

    public User() {

    }

    public String getNombre() {
        return nombre;
    }

    public String getCorreo(){
        return correo;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public void setCorreo(String correo){
        this.correo = correo;
    }
    //
    public boolean isAdmin() {
        return admin;
    }

    public void setAdmin(boolean admin) {
        this.admin = admin;
    }
}
