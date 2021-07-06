package com.ssd.appssd.objects;

import java.util.ArrayList;

public class Admin extends User {

    private ArrayList<String> hijos;

    public Admin() {

    }

    public Admin(String nombre, String correo, String url, String token ,ArrayList<String> hijos) {
        super(nombre, correo, url, token);
        this.hijos = hijos;
    }

    public ArrayList<String> getHijos() {
        return hijos;
    }

    public void setHijos(ArrayList<String> hijos) {
        this.hijos = hijos;
    }
}
