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

import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LlamadaEntrante extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_llamada_entrante);



        TextView nombre = findViewById(R.id.llamada_entrante_nombre);
        nombre.setText(getIntent().getStringExtra("nombre"));
        CircleImageView colgar = findViewById(R.id.llamada_entrante_colgar);
        colgar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NotificationData data = new NotificationData(NotificationData.CANCELAR_LLAMADA, "");
                PushNotification notification = new PushNotification(data, getIntent().getStringExtra("invitador"));
                enviarMensaje(notification, cancelacion);
            }
        });

        CircleImageView llamar = findViewById(R.id.llamada_entrante_llamar);
        llamar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NotificationData data = new NotificationData(NotificationData.CONTESTAR, "");
                PushNotification notification = new PushNotification(data, getIntent().getStringExtra("invitador"));
                enviarMensaje(notification, aceptacion);
            }
        });

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
            Toast.makeText(LlamadaEntrante.this, getString(R.string.error_al_conectar_llamada), Toast.LENGTH_LONG).show();
            finish();
        }
    }

    private Callback<ResponseBody> aceptacion = new Callback<ResponseBody>() {
        @Override
        public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
            if(response.isSuccessful()) {
                try {
                    JitsiMeetConferenceOptions options = new JitsiMeetConferenceOptions.Builder()
                            .setServerURL(new URL("https://meet.jit.si"))
                            .setRoom(getIntent().getStringExtra("invitador"))
                            .setAudioMuted(false)
                            .setVideoMuted(true)
                            .setAudioOnly(true)
                            .setWelcomePageEnabled(true)
                            .build();
                    JitsiMeetActivity.launch(LlamadaEntrante.this, options);
                    finish();
                } catch (MalformedURLException e) {
                    Toast.makeText(LlamadaEntrante.this, getString(R.string.error_al_conectar_llamada), Toast.LENGTH_LONG).show();
                }

            } else {
                Toast.makeText(LlamadaEntrante.this, getString(R.string.error_al_conectar_llamada), Toast.LENGTH_LONG).show();
                finish();
            }
        }

        @Override
        public void onFailure(@NonNull Call<ResponseBody> call, Throwable t) {
            Toast.makeText(LlamadaEntrante.this, getString(R.string.error_al_conectar_llamada), Toast.LENGTH_LONG).show();
            finish();
        }
    };
    private Callback<ResponseBody> cancelacion = new Callback<ResponseBody>() {
        @Override
        public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
            if(response.isSuccessful()) {
                //Toast.makeText(LlamadaEntrante.this, "Se cancelo", Toast.LENGTH_LONG).show();
                finish();
            } else {
                Toast.makeText(LlamadaEntrante.this, getString(R.string.error_al_conectar_llamada), Toast.LENGTH_LONG).show();
                finish();
            }
        }

        @Override
        public void onFailure(@NonNull Call<ResponseBody> call, Throwable t) {
            Toast.makeText(LlamadaEntrante.this, getString(R.string.error_al_conectar_llamada), Toast.LENGTH_LONG).show();
            finish();
        }
    };

    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if(intent.getStringExtra("type").matches("Cancel")) {
                Toast.makeText(LlamadaEntrante.this, getString(R.string.cancelo_llamada), Toast.LENGTH_LONG).show();
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
