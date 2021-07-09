package com.ssd.appssd.objects;

import java.util.ArrayList;
import java.util.List;

public class Grupo {

    private List<String> usuarios;
    private String nombre, id;

    public Grupo(List<String> usuarios, String nombre, String id) {
        this.usuarios = usuarios;
        this.nombre = nombre;
        this.id = id;
    }

    public Grupo(){

    }

    public List<String> getUsuarios() {
        return usuarios;
    }

    public void setUsuarios(ArrayList<String> usuarios) {
        this.usuarios = usuarios;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
