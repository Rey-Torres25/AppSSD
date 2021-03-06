package com.ssd.appssd.objects;
import java.io.Serializable;

public class User implements Serializable {

    private String nombre, correo, imageURL, correoPadre;
    private boolean verificado, correoEnviado;

    public User(String nombre, String correo, String imageURL, String correoPadre, boolean verificado, boolean correoEnviado, String FCM_Token, String User_id) {
        this.nombre = nombre;
        this.correo = correo;
        this.imageURL = imageURL;
        this.verificado = verificado;
        this.correoPadre = correoPadre;
        this.correoEnviado = correoEnviado;

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

}