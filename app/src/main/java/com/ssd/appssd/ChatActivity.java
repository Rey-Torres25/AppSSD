package com.ssd.appssd;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.ssd.appssd.adapter.MessageAdapter;
import com.ssd.appssd.objects.User;
import com.ssd.appssd.objects.Chat;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatActivity extends AppCompatActivity {

    CircleImageView profile_image;
    TextView username;

    FirebaseUser fUser;
    FirebaseFirestore fStore;
    FirebaseAuth fAuth;

    ImageButton btnSend;
    EditText textSend;

    MessageAdapter messageAdapter;
    List<Chat> mchat;

    RecyclerView recyclerView;
    Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        //Firebase
        fAuth = FirebaseAuth.getInstance();
        fUser = fAuth.getCurrentUser();
        fStore = FirebaseFirestore.getInstance();

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        recyclerView = findViewById(R.id.recyler_View);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);

        profile_image = findViewById(R.id.profile_image);
        username = findViewById(R.id.username);
        btnSend = findViewById(R.id.btnSend);
        textSend = findViewById(R.id.text_send);


        intent = getIntent();
        String userCorreo = intent.getStringExtra("correo");

        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String msg = textSend.getText().toString();
                if(!msg.equals("")){
                }else{
                    Toast.makeText(ChatActivity.this, "No puedes enviar un mensaje vacío",
                            Toast.LENGTH_SHORT).show();
                }
                textSend.setText("");
            }
        });

        fStore = FirebaseFirestore.getInstance();
        fStore.collection("Usuarios")
                .document(userCorreo)
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if(task.isSuccessful()){
                            User user = task.getResult().toObject(User.class);
                            username.setText(user.getNombre());
                            if(user.getImageURL().equals("default")){
                                profile_image.setImageResource(R.drawable.perfil_without);
                            }else{
                                Glide.with(ChatActivity.this).load(user.getImageURL()).into(profile_image);
                            }
                        }
                    }
                });
    }

}