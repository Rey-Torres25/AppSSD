package com.ssd.appssd.fragmentsAdmin;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.ssd.appssd.ChatActivity;
import com.ssd.appssd.ChatGrupal;
import com.ssd.appssd.R;
import com.ssd.appssd.adapter.DialogAdapter;
import com.ssd.appssd.adapter.DialogListener;
import com.ssd.appssd.adapter.GrupoAdapter;
import com.ssd.appssd.adapter.ListAdapter;
import com.ssd.appssd.adapter.UserAdapter;
import com.ssd.appssd.components.ComponentMessage;
import com.ssd.appssd.objects.Admin;
import com.ssd.appssd.objects.Chat;
import com.ssd.appssd.objects.Grupo;
import com.ssd.appssd.objects.Message;
import com.ssd.appssd.objects.User;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ChatFragmentAdmin extends Fragment implements DialogListener {


    private RecyclerView recyclerView, recyclerView2, recyclerViewGrupo;
    private UserAdapter userAdapter;
    private DialogAdapter dialogAdapter;
    private GrupoAdapter grupoAdapter;
    private ArrayList<String> seleccion;
    private List<User> mUsers;
    private ArrayList<Grupo> mGrupos;
    private FirebaseFirestore mStore;
    private FirebaseAuth mAuth;
    private AlertDialog dialog;
    private LayoutInflater inflater2;
    private View dialogView;
    private FirebaseUser mUser;
    private DialogListener dialogListener;
    private EditText nombre_grupo;

    public ChatFragmentAdmin() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_chat_admin, container, false);
        // Inflate the layout for this fragment
        setHasOptionsMenu(true);



        recyclerViewGrupo = view.findViewById(R.id.recyclerviewgrupos);
        recyclerViewGrupo.setHasFixedSize(true);
        recyclerViewGrupo.setLayoutManager(new LinearLayoutManager(getContext()));

        recyclerView = view.findViewById(R.id.recyclerview);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));



        mStore = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();

        seleccion = new ArrayList<>();
        mUsers = new ArrayList<>();
        mGrupos = new ArrayList<>();

        readUsers();
        readGrupos();

        dialogListener = new DialogListener() {
            @Override
            public void onDialogChange(ArrayList<String> userSaved) {
                seleccion = userSaved;
                if(seleccion.isEmpty()){
                    dialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(false);
                }else{
                    if(!nombre_grupo.getText().toString().isEmpty()){
                        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(true);
                    }
                }
            }
        };

        return view;
    }


    private void readUsersDialog(){
        mStore.collection("Administrador")
                .document(mUser.getEmail())
                .collection("Usuarios")
                .orderBy("timestamp", Query.Direction.ASCENDING)
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
                        dialogAdapter = new DialogAdapter(getContext(), mUsers, dialogListener);
                        recyclerView2.setAdapter(dialogAdapter);
                    }
                });
    }


    private void readUsers(){
        mStore.collection("Administrador")
                .document(mUser.getEmail())
                .collection("Usuarios")
                .orderBy("timestamp", Query.Direction.ASCENDING)
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
                        userAdapter = new UserAdapter(getContext(), mUsers);
                        recyclerView.setAdapter(userAdapter);
                    }
                });
    }

    private void readGrupos(){
        mStore.collection("Grupos")
                .whereArrayContains("usuarios", mUser.getEmail())
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, FirebaseFirestoreException error) {
                        if(error != null){
                            Log.w("TAG", "Listen failed.", error);
                            return;
                        }
                        mGrupos.clear();
                        for(QueryDocumentSnapshot documentSnapshot : value){
                            if(documentSnapshot.exists()){
                                Grupo grupo = documentSnapshot.toObject(Grupo.class);
                                mGrupos.add(grupo);
                            }
                        }
                        grupoAdapter = new GrupoAdapter(getContext(), mGrupos);
                        recyclerViewGrupo.setAdapter(grupoAdapter);
                    }
                });
    }
    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.chat_admin_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch(item.getItemId()){
            case R.id.grupo:
                inflater2 = getActivity().getLayoutInflater();
                dialogView = inflater2.inflate(R.layout.make_group_dialog, null);
                nombre_grupo = (EditText) dialogView.findViewById(R.id.nombre_grupo);
                recyclerView2 = dialogView.findViewById(R.id.recyclerview2);
                recyclerView2.setHasFixedSize(true);
                recyclerView2.setLayoutManager(new LinearLayoutManager(getContext()));
                readUsersDialog();
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext(), R.style.CustomAlertDialog);
                builder.setTitle(getString(R.string.grupo));
                builder.setView(dialogView);
                builder.setPositiveButton(getString(R.string.option_grupo), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        seleccion.add(mUser.getEmail());
                        seleccion.sort(String::compareTo);
                        DocumentReference grupoReference = mStore.collection("Grupos").document();
                        Grupo grupo = new Grupo(seleccion, nombre_grupo.getText().toString(), grupoReference.getId());
                        mStore.collection("Grupos")
                                .document(grupoReference.getId())
                                .set(grupo)
                                .addOnSuccessListener(documentReference -> {
                                    Intent intent = new Intent(getContext(), ChatGrupal.class);
                                    intent.putExtra("id", grupo.getId());
                                    intent.putStringArrayListExtra("usuarios", grupo.getUsuarios());
                                    getContext().startActivity(intent);
                                    Toast.makeText(getContext(), getString(R.string.grupo_creado), Toast.LENGTH_SHORT).show();
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
                dialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(false);
                nombre_grupo.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                    }
                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(true);
                        if(seleccion.size()>0){
                            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(true);
                        }else{
                            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(false);
                        }
                    }

                    @Override
                    public void afterTextChanged(Editable s) {
                        if(nombre_grupo.getText().toString().isEmpty()){
                            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(false);
                        }
                    }
                });

        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onDialogChange(ArrayList<String> userSaved) {
    }

}