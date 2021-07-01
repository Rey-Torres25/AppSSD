package com.ssd.appssd.objects;

public class User {

    private String nombre, correo, imageURL;
    private boolean admin;

    public User(String nombre, String correo, String imageURL, Boolean admin) {
        this.nombre = nombre;
        this.correo = correo;
        this.imageURL = imageURL;
        this.admin = admin;
    }

    public User(){

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

    public String getImageURL() {
        return imageURL;
    }

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }

    public boolean isAdmin() {
        return admin;
    }

    public void setAdmin(boolean admin) {
        this.admin = admin;
    }
}
