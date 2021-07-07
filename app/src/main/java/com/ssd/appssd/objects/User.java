package com.ssd.appssd.objects;

public class User {

    private String nombre, correo, imageURL, correoPadre;

    public User(String nombre, String correo, String imageURL, String correoPadre) {
        this.nombre = nombre;
        this.correo = correo;
        this.imageURL = imageURL;
        this.correoPadre = correoPadre;
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

    public String getCorreoPadre() {
        return correoPadre;
    }

    public void setCorreoPadre(String correoPadre) {
        this.correoPadre = correoPadre;
    }
}
