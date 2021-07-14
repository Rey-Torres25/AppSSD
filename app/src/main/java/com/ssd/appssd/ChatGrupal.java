package com.ssd.appssd;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
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
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.ssd.appssd.adapter.DialogAdapter;
import com.ssd.appssd.adapter.DialogListener;
import com.ssd.appssd.adapter.UserAdapter;
import com.ssd.appssd.adapter.UserAdapterGrupo;
import com.ssd.appssd.adapter.UserGrupoListener;
import com.ssd.appssd.components.ComponentMessage;
import com.ssd.appssd.objects.Admin;
import com.ssd.appssd.objects.Chat;
import com.ssd.appssd.objects.Grupo;
import com.ssd.appssd.objects.Message;
import com.ssd.appssd.objects.User;

import java.util.ArrayList;
import java.util.List;
import java.util.zip.Inflater;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatGrupal extends AppCompatActivity implements DialogListener, UserGrupoListener {

    TextView username;

    FirebaseUser fUser;
    FirebaseFirestore fStore;
    FirebaseAuth fAuth;
    FirebaseStorage fStorage;
    private List<User> mUsers;
    private DialogListener dialogListener;
    private UserGrupoListener userGrupoListener;
    ArrayList<String> lista_usuarios, seleccion, prueba;
    RecyclerView recyclerView;
    String id;
    ImageButton btnSend;
    EditText textSend;
    DialogAdapter dialogAdapter;
    LinearLayout padre;
    List<String> messages;
    Intent intent;

    private UserAdapterGrupo userAdapterGrupo;
    private AlertDialog dialog;
    private LayoutInflater inflater;
    private View dialogView;

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

        lista_usuarios = new ArrayList<>();
        prueba = new ArrayList<>();
        mUsers = new ArrayList<>();
        seleccion = new ArrayList<>();
        messages = new ArrayList<>();

        intent = getIntent();
        lista_usuarios = intent.getStringArrayListExtra("usuarios");
        id = intent.getStringExtra("id");
        btnSend.setOnClickListener(v -> {
            String msg = textSend.getText().toString();
            if (!msg.matches("")) {
                Message mensaje = new Message(fUser.getEmail(), msg, Timestamp.now());
                fStore.collection("Grupos").document(id).collection("TODO").add(mensaje);
            } else {
                Toast.makeText(ChatGrupal.this, String.format(getString(R.string.No_mensaje_vacio)),
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

        userGrupoListener = new UserGrupoListener() {
            @Override
            public void onDialogChange(int userSelected) {
                if(userSelected==1){
                    finish();
                    dialog.cancel();
                }
            }
        };
        dialogListener = new DialogListener() {
            @Override
            public void onDialogChange(ArrayList<String> userSaved) {
                seleccion = userSaved;
                if(seleccion.isEmpty()){
                    dialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(false);
                }else{
                    dialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(true);
                }
            }
        };

    }


    private void readUsers(){
        fStore.collection("Administrador")
                .document(fUser.getEmail())
                .collection("Usuarios")
                .whereIn("correo", lista_usuarios)
                .orderBy("timestamp", Query.Direction.ASCENDING)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, FirebaseFirestoreException error) {
                        if(error != null) {
                            Log.w("TAG", "Listen failed.", error);
                            return;
                        }
                        mUsers.clear();
                        for(QueryDocumentSnapshot documentSnapshot : value){
                            if(documentSnapshot.exists()){
                                User user = documentSnapshot.toObject(User.class);
                                mUsers.add(user);
                            }
                        }
                        userAdapterGrupo = new UserAdapterGrupo(ChatGrupal.this, mUsers, userGrupoListener);
                        recyclerView.setAdapter(userAdapterGrupo);
                    }
                });

    }

    private void addUsersDialog(){
            fStore.collection("Administrador")
                    .document(fUser.getEmail())
                    .collection("Usuarios")
                    .whereNotIn("correo", lista_usuarios)
                    .addSnapshotListener(new EventListener<QuerySnapshot>() {
                        @Override
                        public void onEvent(@Nullable QuerySnapshot value, FirebaseFirestoreException error) {
                            if(error != null) {
                                Log.w("TAG", "Listen failed.", error);
                                return;
                            }
                            mUsers.clear();
                            for(QueryDocumentSnapshot documentSnapshot : value){
                                if(documentSnapshot.exists()){
                                    User user = documentSnapshot.toObject(User.class);
                                    mUsers.add(user);
                                }
                                dialogAdapter = new DialogAdapter(ChatGrupal.this, mUsers, dialogListener);
                                recyclerView.setAdapter(dialogAdapter);
                            }

                        }
                    });
    }

    private void deleteUsersDialog(){
        if(lista_usuarios.size()>1){
            fStore.collection("Administrador")
                    .document(fUser.getEmail())
                    .collection("Usuarios")
                    .whereIn("correo", lista_usuarios)
                    .addSnapshotListener(new EventListener<QuerySnapshot>() {
                        @Override
                        public void onEvent(@Nullable QuerySnapshot value, FirebaseFirestoreException error) {
                            if(error != null){
                                Log.w("TAG", "Listen failed.", error);
                                return;
                            }
                            mUsers.clear();
                            for(QueryDocumentSnapshot documentSnapshot : value){
                                if(documentSnapshot.exists()){
                                    User user = documentSnapshot.toObject(User.class);
                                    mUsers.add(user);
                                }
                            }
                            dialogAdapter = new DialogAdapter(ChatGrupal.this, mUsers, dialogListener);
                            recyclerView.setAdapter(dialogAdapter);
                        }
                    });
        }else{
            Toast.makeText(ChatGrupal.this, getString(R.string.no_users_delete),
                    Toast.LENGTH_SHORT).show();
        }
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
        fStore.collection("Administrador")
                .document(fUser.getEmail())
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if(documentSnapshot.exists()) {
                            MenuInflater inflater = getMenuInflater();
                            inflater.inflate(R.menu.chat_grupal_item, menu);
                        }
                    }
                });
        return true;
    }


    //Método al clickear ícono de video llamada
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int i = item.getItemId();
        if(i == R.id.agregar){
            inflater = getLayoutInflater();
            dialogView = inflater.inflate(R.layout.user_dialog_chat, null);
            recyclerView = dialogView.findViewById(R.id.add_recyclerview);
            recyclerView.setHasFixedSize(true);
            recyclerView.setLayoutManager(new LinearLayoutManager(ChatGrupal.this));
            addUsersDialog();
            AlertDialog.Builder builderAdd = new AlertDialog.Builder(ChatGrupal.this, R.style.CustomAlertDialog);
            builderAdd.setTitle(getString(R.string.agregar_usuario_grupo));
            builderAdd.setView(dialogView);
            builderAdd.setPositiveButton(getString(R.string.agregar_usuario_dialog), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    for(int i = 0;i<seleccion.size();i++){
                        lista_usuarios.add(seleccion.get(i));
                        fStore.collection("Grupos")
                                .document(id)
                                .update("usuarios", FieldValue.arrayUnion(seleccion.get(i)));
                    }
                    Toast.makeText(ChatGrupal.this, getString(R.string.participante_agregado), Toast.LENGTH_SHORT).show();
                }
            });
            builderAdd.setNegativeButton(getString(R.string.cancelar_dialog), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });
            dialog = builderAdd.create();
            dialog.show();
            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(false);
        }else if(i == R.id.eliminar){
            inflater = getLayoutInflater();
            dialogView = inflater.inflate(R.layout.user_dialog_chat, null);
            recyclerView = dialogView.findViewById(R.id.add_recyclerview);
            recyclerView.setHasFixedSize(true);
            recyclerView.setLayoutManager(new LinearLayoutManager(ChatGrupal.this));
            deleteUsersDialog();
            AlertDialog.Builder builderDelete = new AlertDialog.Builder(ChatGrupal.this, R.style.CustomAlertDialog);
            builderDelete.setTitle(getString(R.string.eliminar_usuario_grupo));
            builderDelete.setView(dialogView);
            builderDelete.setPositiveButton(getString(R.string.eliminar_usuario), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    for(int i=0;i<seleccion.size();i++){
                        for(int j=0;j<lista_usuarios.size();j++){
                            if(seleccion.get(i).equals(lista_usuarios.get(j))){
                                lista_usuarios.remove(j);
                            }
                        }
                        fStore.collection("Grupos")
                                .document(id)
                                .update("usuarios", FieldValue.arrayRemove(seleccion.get(i)));
                    }
                    Toast.makeText(ChatGrupal.this, getString(R.string.participante_eliminado),
                            Toast.LENGTH_SHORT).show();
                }
            });
            builderDelete.setNegativeButton(getString(R.string.cancelar_dialog), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });
            dialog = builderDelete.create();
            dialog.show();
            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(false);

        }else if(i == R.id.ver_usuarios){
            inflater = getLayoutInflater();
            dialogView = inflater.inflate(R.layout.user_dialog_chat, null);
            recyclerView = dialogView.findViewById(R.id.add_recyclerview);
            recyclerView.setHasFixedSize(true);
            recyclerView.setLayoutManager(new LinearLayoutManager(ChatGrupal.this));
            readUsers();
            AlertDialog.Builder builderAdd = new AlertDialog.Builder(ChatGrupal.this, R.style.CustomAlertDialog);
            builderAdd.setTitle(getString(R.string.see_users));
            builderAdd.setView(dialogView);
            builderAdd.setNegativeButton(getString(R.string.cancelar_dialog), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });
            dialog = builderAdd.create();
            dialog.show();
        }else if(i == R.id.eliminar_grupo){
            AlertDialog.Builder builder = new AlertDialog.Builder(ChatGrupal.this, R.style.CustomAlertDialog);
            builder.setTitle(getString(R.string.confirm_delete_group));
            builder.setPositiveButton(getString(R.string.eliminar_usuario), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    fStore.collection("Grupos")
                            .document(id)
                            .delete()
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {
                                    Toast.makeText(ChatGrupal.this, getString(R.string.eliminado_con_exito), Toast.LENGTH_SHORT).show();
                                    finish();
                                }
                            });


                }
            });
            builder.setNegativeButton(getString(R.string.cancelar_dialog), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });

            dialog = builder.create();
            dialog.show();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onDialogChange(ArrayList<String> userSaved) {
    }

    @Override
    public void onDialogChange(int userSelected) {
    }
}