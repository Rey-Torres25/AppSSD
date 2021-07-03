package com.ssd.appssd.objects;

import java.util.ArrayList;

public class Chat {

    private ArrayList<String> usuarios;

    public Chat(ArrayList<String> usuarios) {
        this.usuarios = usuarios;
    }

    public Chat() {

    }

    public ArrayList<String> getUsuarios() {
        return usuarios;
    }
}
