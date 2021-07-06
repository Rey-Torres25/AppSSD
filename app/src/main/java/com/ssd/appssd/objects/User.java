package com.ssd.appssd.objects;

public class User {

    private String nombre, correo, imageURL, token;

    public User(String nombre, String correo, String imageURL, String token) {
        this.nombre = nombre;
        this.correo = correo;
        this.imageURL = imageURL;
        this.token = token;
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

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
