package com.ssd.appssd.networking;

public class NotificationData {
    public static final int INVITACION = 0;
    public static final int CANCELAR_LLAMADA = 1;
    public static final int CONTESTAR = 2;

    private int type;
    private Object data;

    public NotificationData(int type, Object data) {
        this.type = type;
        this.data = data;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }
}
