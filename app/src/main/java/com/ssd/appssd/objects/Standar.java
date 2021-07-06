package com.ssd.appssd.objects;

public class Standar extends User {

    private String correoPadre;

    public Standar(String nombre, String correo, String url, String token, String correoPadre) {
        super(nombre, correo, url,token);
        this.correoPadre = correoPadre;
    }

    public Standar() {

    }

    public String getCorreoPadre() {
        return correoPadre;
    }

    public void setCorreoPadre(String correoPadre) {
        this.correoPadre = correoPadre;
    }
}
