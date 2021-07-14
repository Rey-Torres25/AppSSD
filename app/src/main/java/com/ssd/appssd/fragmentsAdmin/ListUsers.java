package com.ssd.appssd.fragmentsAdmin;

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
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GetTokenResult;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.ssd.appssd.R;
import com.ssd.appssd.RegistroUsuario;
import com.ssd.appssd.adapter.ListAdapter;
import com.ssd.appssd.adapter.UserAdapter;
import com.ssd.appssd.objects.Admin;
import com.ssd.appssd.objects.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ListUsers extends Fragment {

    private RecyclerView recyclerView;
    private ListAdapter userAdapter;
    private List<User> mUsers;
    private FirebaseFirestore mStore;
    private FirebaseAuth mAuth;
    private FirebaseUser mUser;


    private EditText nombre, correo;
    public ListUsers() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View view = inflater.inflate(R.layout.fragment_list_users, container, false);

        setHasOptionsMenu(true);
        recyclerView = view.findViewById(R.id.recyclerview);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        mStore = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();

        mUsers = new ArrayList<>();

        readUsers();

        return view;
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
                        userAdapter = new ListAdapter(getContext(), mUsers, getActivity());
                        recyclerView.setAdapter(userAdapter);
                    }
                });
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.list_users_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch(item.getItemId()){
            case R.id.agregar:
                LayoutInflater inflater = getActivity().getLayoutInflater();
                View dialogView = inflater.inflate(R.layout.dialog_agregar_usuario, null);
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext(), R.style.CustomAlertDialog);
                builder.setTitle(getString(R.string.registrar_usuario));
                builder.setView(dialogView);
                nombre = (EditText) dialogView.findViewById(R.id.nombre);
                correo = (EditText) dialogView.findViewById(R.id.correo);
                builder.setPositiveButton(getString(R.string.registrar_dialog_confirmacion), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Map<String, Object> map = new HashMap<>();
                        map.put("nombre", nombre.getText().toString());
                        map.put("correo", correo.getText().toString());
                        map.put("imageURL", "default");
                        map.put("verificado", false);
                        map.put("correoEnviado", false);
                        map.put("correoPadre", mUser.getEmail());
                        map.put("timestamp", FieldValue.serverTimestamp());
                        mStore.collection("Usuarios")
                                .document(correo.getText().toString())
                                .set(map)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {
                                        Map<String, Object> map2 = new HashMap<>();
                                        map2.put("nombre", nombre.getText().toString());
                                        map2.put("correo", correo.getText().toString());
                                        map2.put("imageURL", "default");
                                        map2.put("verificado", false);
                                        map2.put("correoPadre", mUser.getEmail());
                                        map2.put("timestamp", FieldValue.serverTimestamp());
                                        mStore.collection("Administrador")
                                                .document(mUser.getEmail())
                                                .collection("Usuarios")
                                                .document(correo.getText().toString())
                                                .set(map2)
                                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void unused) {
                                                        Toast.makeText(getContext(), "Has registrado correctamente al usuario",
                                                                Toast.LENGTH_LONG).show();
                                                    }
                                                }).addOnFailureListener(new OnFailureListener() {
                                                    @Override
                                                    public void onFailure(@NonNull Exception e) {
                                                        Toast.makeText(getContext(), "Hubo un error con el registro",
                                                                Toast.LENGTH_LONG).show();
                                                    }
                                                });
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(getContext(), "Hubo un error con el registro",
                                                Toast.LENGTH_LONG).show();
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
                AlertDialog dialog = builder.create();
                dialog.show();
                    nombre.addTextChangedListener(new TextWatcher() {
                        @Override
                        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                        }
                        @Override
                        public void onTextChanged(CharSequence s, int start, int before, int count) {
                            if(!correo.getText().toString().isEmpty()){
                                dialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(true);
                            }
                        }

                        @Override
                        public void afterTextChanged(Editable s) {
                            if(nombre.getText().toString().isEmpty()){
                                dialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(false);
                            }
                        }
                    });

                    correo.addTextChangedListener(new TextWatcher() {
                        @Override
                        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                        }

                        @Override
                        public void onTextChanged(CharSequence s, int start, int before, int count) {
                            if(!nombre.getText().toString().isEmpty()){
                                dialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(true);
                            }
                        }
                        @Override
                        public void afterTextChanged(Editable s) {
                            if(correo.getText().toString().isEmpty()){
                                dialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(false);
                            }
                        }
                    });

        }
        return super.onOptionsItemSelected(item);
    }

}