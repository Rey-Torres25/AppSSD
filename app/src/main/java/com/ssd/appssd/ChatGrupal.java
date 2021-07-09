package com.ssd.appssd;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.ssd.appssd.components.ComponentMessage;
import com.ssd.appssd.objects.Admin;
import com.ssd.appssd.objects.Chat;
import com.ssd.appssd.objects.Grupo;
import com.ssd.appssd.objects.Message;
import com.ssd.appssd.objects.User;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatGrupal extends AppCompatActivity {

    TextView username;

    FirebaseUser fUser;
    FirebaseFirestore fStore;
    FirebaseAuth fAuth;
    FirebaseStorage fStorage;
    StorageReference storageReference;
    String chatID, id;
    ImageButton btnSend;
    EditText textSend;

    LinearLayout padre;
    List<String> messages;
    Intent intent;
    final long ONE_MEGABYTE = 1024 * 1024;
    ScrollView scrollView; //barra de scroll para el chat
    Runnable paginar;    //Evento para redireccionar hasta abajo el chat

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_grupal);

        scrollView = findViewById(R.id.scrollViewChat);
        fAuth = FirebaseAuth.getInstance();
        fUser = fAuth.getCurrentUser();
        fStore = FirebaseFirestore.getInstance();
        fStorage = FirebaseStorage.getInstance();


        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(v -> finish());

        padre = findViewById(R.id.mensajes);
        paginar = () -> {
            scrollView.fullScroll(View.FOCUS_DOWN);
        };

        username = findViewById(R.id.username);
        btnSend = findViewById(R.id.btnSend);
        textSend = findViewById(R.id.text_send);

        intent = getIntent();
        id = intent.getStringExtra("id");
        messages = new ArrayList<>();

        btnSend.setOnClickListener(v -> {
            String msg = textSend.getText().toString();
            if (!msg.matches("")) {
                Message mensaje = new Message(fUser.getEmail(), msg, Timestamp.now());
                fStore.collection("Grupos").document(id).collection("TODO").add(mensaje);
            } else {
                Toast.makeText(ChatGrupal.this, "No puedes enviar un mensaje vacío",
                        Toast.LENGTH_SHORT).show();
            }
            textSend.setText("");
        });

        fStore = FirebaseFirestore.getInstance();
        fStore.collection("Grupos")
                .document(id)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        DocumentSnapshot documentSnapshot = task.getResult();
                        if (documentSnapshot.exists()) {
                            Grupo grupo = task.getResult().toObject(Grupo.class);
                            username.setText(grupo.getNombre());
                        }
                    }
                });

        fStore.collection("Grupos")
                .whereEqualTo("id", id)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if(!queryDocumentSnapshots.isEmpty())
                        getMessages(id);
                });
    }


    private void getMessages(String id) {
        fStore.
                collection("Grupos")
                .document(id)
                .collection("TODO")
                .orderBy("fecha").
                addSnapshotListener((value, error) -> {
                    List<Message> messagesL = value.toObjects(Message.class);
                    for (int i = 0; i < messagesL.size(); i++) {
                        if(!messages.contains(value.getDocuments().get(i).getId())) {
                            messages.add(value.getDocuments().get(i).getId());
                            crearMensaje(messagesL.get(i));
                        }
                    }
                });
    }

    private void crearMensaje(Message message) {
        ComponentMessage msg = new ComponentMessage(ChatGrupal.this, message);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        int margen = (int) (10 * getResources().getDisplayMetrics().density);
        params.setMargins(margen, margen, margen, margen);
        msg.setLayoutParams(params);
        padre.addView(msg);
        scrollView.postDelayed(paginar, 100);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.video_call_menu, menu);
        return true;
    }


    //Método al clickear ícono de video llamada
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {


        return super.onOptionsItemSelected(item);
    }
}