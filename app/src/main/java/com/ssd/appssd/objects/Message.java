package com.ssd.appssd.objects;

import com.google.firebase.Timestamp;

public class Message {

    private String sender;
    private String message;
    private Timestamp fecha;

    public Message(String sender, String message, Timestamp fecha) {
        this.sender = sender;
        this.message = message;
        this.fecha = fecha;
    }

    public Message()  {

    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Timestamp getFecha() {
        return fecha;
    }

    public void setFecha(Timestamp fecha) {
        this.fecha = fecha;
    }
}

