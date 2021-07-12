package com.ssd.appssd.objects;

public class User {

    private String nombre, correo, imageURL, correoPadre, FCM_Token, User_id;
    private boolean verificado, correoEnviado;

    public User(String nombre, String correo, String imageURL, String correoPadre, boolean verificado, boolean correoEnviado, String FCM_Token, String User_id) {
        this.nombre = nombre;
        this.correo = correo;
        this.imageURL = imageURL;
        this.verificado = verificado;
        this.correoPadre = correoPadre;
        this.correoEnviado = correoEnviado;
        this.FCM_Token = FCM_Token;
        this.User_id = User_id;
    }

    public User() {

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

    public boolean isVerificado() {
        return verificado;
    }

    public void setVerificado(boolean verificado) {
        this.verificado = verificado;
    }

    public boolean isCorreoEnviado() {
        return correoEnviado;
    }

    public void setCorreoEnviado(boolean correoEnviado) {
        this.correoEnviado = correoEnviado;
    }

    public String getFCM_Token() {
        return FCM_Token;
    }

    public void setFCM_Token(String FCM_Token) {
        this.FCM_Token = FCM_Token;
    }

    public String getUser_id() {
        return User_id;
    }

    public void setUser_id(String User_id) {
        this.User_id = User_id;
    }

}