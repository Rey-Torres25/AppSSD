package com.ssd.appssd.networking;

import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.ssd.appssd.LlamadaEntrante;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;


public class ServicioMensajeria extends FirebaseMessagingService {

        @Override
        public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
            super.onMessageReceived(remoteMessage);

            Map<String, String> notification = remoteMessage.getData();
            int type = Integer.parseInt(notification.get("type"));
            switch (type) {
                case NotificationData.INVITACION:
                    try {
                        JSONObject data = new JSONObject(notification.get("data"));
                        Intent llamada = new Intent(this, LlamadaEntrante.class);
                        llamada.putExtra("nombre", (String) data.get("nombre"));
                        llamada.putExtra("url", (String)data.get("url"));
                        llamada.putExtra("invitador", (String)data.get("invitador"));
                        llamada.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(llamada);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    break;
                case NotificationData.CANCELAR_LLAMADA:
                    Intent cancelar = new Intent("FEEDBACK");
                    cancelar.putExtra("type", "Cancel");
                    LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(cancelar);
                    break;
                case NotificationData.CONTESTAR:
                    Intent aceptar = new Intent("FEEDBACK");
                    aceptar.putExtra("type", "Aceptar");
                    LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(aceptar);
                    break;
                default:
                    return;
            }


        }

        @Override
        public void onNewToken(@NonNull String s) {
            super.onNewToken(s);
        }

    }



