package com.ssd.appssd;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.ssd.appssd.networking.NotificationData;
import com.ssd.appssd.networking.PushNotification;
import com.ssd.appssd.networking.RetrofitInstance;

import org.jitsi.meet.sdk.JitsiMeetActivity;
import org.jitsi.meet.sdk.JitsiMeetConferenceOptions;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Llamar extends AppCompatActivity {
    private String inviterToken = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_llamar);

        TextView nombre = findViewById(R.id.llamar_nombre);
        nombre.setText(getIntent().getStringExtra("nombre"));

        HashMap<String, String> info = new HashMap<>();
        info.put("nombre", getIntent().getStringExtra("nombreMio"));
        info.put("url", getIntent().getStringExtra("urlMio"));
        info.put("invitador", getIntent().getStringExtra("myToken"));

        CircleImageView colgar = findViewById(R.id.llamar_colgar);
        colgar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NotificationData data = new NotificationData(NotificationData.CANCELAR_LLAMADA, "");
                PushNotification notification = new PushNotification(data, getIntent().getStringExtra("usuario"));
                enviarMensaje(notification, cancelacion);
            }
        });

        NotificationData data = new NotificationData(NotificationData.INVITACION, info);
        PushNotification notification = new PushNotification(data, getIntent().getStringExtra("usuario"));
        enviarMensaje(notification, invitacion);
    }

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
    }

    private void enviarMensaje(PushNotification notification, Callback<ResponseBody> callback) {
        try {
            RetrofitInstance.getRetrofit();
            RetrofitInstance.getApi().postNotification(notification).enqueue(callback);
        } catch (Exception e) {
            Toast.makeText(Llamar.this, getString(R.string.error_al_conectar_llamada), Toast.LENGTH_LONG).show();
            finish();
        }
    }

    private Callback<ResponseBody> invitacion = new Callback<ResponseBody>() {
        @Override
        public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
            if(response.isSuccessful()) {
                Toast.makeText(Llamar.this, getString(R.string.llamando), Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(Llamar.this, getString(R.string.error_al_conectar_llamada), Toast.LENGTH_LONG).show();
                finish();
            }
        }

        @Override
        public void onFailure(@NonNull Call<ResponseBody> call, Throwable t) {
            Toast.makeText(Llamar.this, getString(R.string.error_al_conectar_llamada), Toast.LENGTH_LONG).show();
            finish();
        }
    };
    private Callback<ResponseBody> cancelacion = new Callback<ResponseBody>() {
        @Override
        public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
            if(response.isSuccessful()) {
                //Toast.makeText(Llamar.this, "Se cancelo", Toast.LENGTH_LONG).show();
                finish();
            } else {
                Toast.makeText(Llamar.this, getString(R.string.error_al_conectar_llamada), Toast.LENGTH_LONG).show();
                finish();
            }
        }

        @Override
        public void onFailure(@NonNull Call<ResponseBody> call, Throwable t) {
            Toast.makeText(Llamar.this, getString(R.string.error_al_conectar_llamada), Toast.LENGTH_LONG).show();
            finish();
        }
    };

    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if(intent.getStringExtra("type").matches("Cancel")) {
                Toast.makeText(Llamar.this, getString(R.string.cancelo_llamada), Toast.LENGTH_LONG).show();
                finish();
            } else if(intent.getStringExtra("type").matches("Aceptar")) {
                JitsiMeetConferenceOptions options = null;
                try {
                    options = new JitsiMeetConferenceOptions.Builder()
                            .setServerURL(new URL("https://meet.jit.si"))
                            .setRoom(getIntent().getStringExtra("myToken"))
                            .setAudioMuted(false)
                            .setVideoMuted(true)
                            .setAudioOnly(true)
                            .setWelcomePageEnabled(true)
                            .build();
                } catch (MalformedURLException e) {
                    Toast.makeText(Llamar.this, getString(R.string.error_al_conectar_llamada), Toast.LENGTH_LONG).show();
                }
                JitsiMeetActivity.launch(Llamar.this, options);
                finish();
            }
        }
    };

    @Override
    protected void onStart() {
        super.onStart();
        LocalBroadcastManager.getInstance(getApplicationContext()).registerReceiver(receiver, new IntentFilter("FEEDBACK"));
    }

    @Override
    protected void onStop() {
        super.onStop();
        LocalBroadcastManager.getInstance(getApplicationContext()).unregisterReceiver(receiver);
    }
}
