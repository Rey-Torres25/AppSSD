package com.ssd.appssd;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.dropbox.core.util.Collector;
import com.ssd.appssd.globals.Global;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.ssd.appssd.components.ComponentMessage;
import com.ssd.appssd.objects.Admin;
import com.ssd.appssd.objects.Message;
import com.ssd.appssd.objects.User;
import com.ssd.appssd.objects.Chat;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.ResourceBundle;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatActivity extends AppCompatActivity {

    CircleImageView profile_image;
    TextView username;
    FirebaseUser fUser;
    FirebaseFirestore fStore;
    FirebaseAuth fAuth;
    FirebaseStorage fStorage;
    StorageReference storageReference;
    String chatID;
    ImageView llamar;
    DocumentReference chat;
    private LinearLayout layout;
    private String tokenLlamada = "";
    private String miToken = "";

    ImageButton btnSend;
    EditText textSend;

    LinearLayout padre;
    List<String> messages;
    Intent intent;
    final long ONE_MEGABYTE = 1024 * 1024;
    ScrollView scrollView; //barra de scroll para el chat
    Runnable paginar;    //Evento para redireccionar hasta abajo el chat
    private ResourceBundle documentSnapshot;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

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
        profile_image = findViewById(R.id.profile_image);
        username = findViewById(R.id.username);
        btnSend = findViewById(R.id.btnSend);
        textSend = findViewById(R.id.text_send);

        intent = getIntent();
        String userCorreo = intent.getStringExtra("correo");
        messages = new ArrayList<>();

        btnSend.setOnClickListener(v -> {
            String msg = textSend.getText().toString();
            if (!msg.matches("")) {
                Message mensaje = new Message(fUser.getEmail(), msg, Timestamp.now());
                fStore.collection("Chats").document(chatID).collection("TODO").add(mensaje);
            } else {
                Toast.makeText(ChatActivity.this, String.format(getString(R.string.No_mensaje_vacio)),
                        Toast.LENGTH_SHORT).show();
            }
            textSend.setText("");
        });

        fStore = FirebaseFirestore.getInstance();
        fStore.collection("Usuarios")
                .document(userCorreo)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        DocumentSnapshot documentSnapshot = task.getResult();
                        if (documentSnapshot.exists()) {
                            User user = task.getResult().toObject(User.class);
                            username.setText(user.getNombre());
                            if (user.getImageURL().equals("default")) {
                                profile_image.setImageResource(R.drawable.perfil_without);
                            } else {
                                storageReference = fStorage.getReference().child("images/" + user.getCorreo() + "/profile_picture");
                                storageReference.getBytes(ONE_MEGABYTE)
                                        .addOnSuccessListener(new OnSuccessListener<byte[]>() {
                                            @Override
                                            public void onSuccess(byte[] bytes) {
                                                Glide.with(ChatActivity.this)
                                                        .load(bytes)
                                                        .into(profile_image);
                                            }
                                        });
                            }
                        } else {
                            fStore.collection("Administrador")
                                    .document(userCorreo)
                                    .get()
                                    .addOnCompleteListener(task2 -> {
                                        if (task2.isSuccessful()) {
                                            DocumentSnapshot documentSnapshot1 = task2.getResult();
                                            if (documentSnapshot1.exists()) {
                                                Admin admin = task2.getResult().toObject(Admin.class);
                                                username.setText(admin.getNombre());
                                                if (admin.getImageURL().equals("default")) {
                                                    profile_image.setImageResource(R.drawable.perfil_without);
                                                } else {
                                                    storageReference = fStorage.getReference().child("images/" + admin.getCorreo() + "/profile_picture");
                                                    storageReference.getBytes(ONE_MEGABYTE)
                                                            .addOnSuccessListener(new OnSuccessListener<byte[]>() {
                                                                @Override
                                                                public void onSuccess(byte[] bytes) {
                                                                    Glide.with(ChatActivity.this)
                                                                            .load(bytes)
                                                                            .into(profile_image);
                                                                }
                                                            });
                                                }
                                            }
                                        }
                                    });
                        }
                    }
                });

        FirebaseMessaging.getInstance().getToken().addOnSuccessListener(s -> miToken = s);

        ArrayList<String> usuariosF = new ArrayList<>();
        usuariosF.add(userCorreo);
        usuariosF.add(fUser.getEmail());
        usuariosF.sort(String::compareTo);
        fStore.collection("Chats").whereEqualTo("usuarios", usuariosF)
                .get().addOnSuccessListener(queryDocumentSnapshots -> {
            if (queryDocumentSnapshots.isEmpty()) {
                ArrayList<String> usuarios = new ArrayList<>();
                usuarios.add(userCorreo);
                usuarios.add(fUser.getEmail());
                usuarios.sort(String::compareTo);
                Chat chat = new Chat(usuarios);
                fStore.collection("Chats").add(chat).addOnSuccessListener(documentReference -> {
                    chatID = documentReference.getId();
                    getMessages(documentReference.getId());
                });
            } else {
                chatID = queryDocumentSnapshots.getDocuments().get(0).getId();
                getMessages(chatID);
            }
        });
    }



    private void getMessages(String id) {
        fStore.
                collection("Chats").
                document(id).
                collection("TODO").
                orderBy("fecha").
                addSnapshotListener((value, error) -> {
                    List<Message> messagesL = value.toObjects(Message.class);
                    for (int i = 0; i < messagesL.size(); i++) {
                        if (!messages.contains(value.getDocuments().get(i).getId())) {
                            messages.add(value.getDocuments().get(i).getId());
                            crearMensaje(messagesL.get(i));
                        }
                    }
                });
    }

    private void crearMensaje(Message message) {
        ComponentMessage msg = new ComponentMessage(ChatActivity.this, message);
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
        User user = (User) getIntent().getSerializableExtra("usuarios");
            Intent llamada = new Intent(ChatActivity.this, Llamar.class);
            llamada.putExtra("usuarios", getString(Integer.parseInt("id_administradpr")));
            llamada.putExtra("nombre", user.getNombre());
            llamada.putExtra("nombreMio", Global.user.getNombre());
            llamada.putExtra("myToken", tokenLlamada);
            startActivity(llamada);

        return super.onOptionsItemSelected(item);
    }

    public void getChat() {
        DocumentReference chatID = FirebaseFirestore.getInstance().document("Chats/" + Global.referencia);
        FirebaseMessaging.getInstance().getToken().addOnSuccessListener(new OnSuccessListener<String>() {
            @Override
            public void onSuccess(String s) {
                tokenLlamada = s;
                HashMap<String, String> data = new HashMap<>();
                data.put("idusuario", s);
                chatID.set(data, SetOptions.mergeFields("idusuario"));
            }
        });

    }

    //Token llamada es myToken yo haciendo la llamada
    //document snapshot get string token de la otra persona

}