package com.ssd.appssd.fragmentsUsuario;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.ssd.appssd.Llamar;
import com.ssd.appssd.R;
import com.ssd.appssd.adapter.GrupoAdapter;
import com.ssd.appssd.adapter.UserAdapter;
import com.ssd.appssd.objects.Admin;
import com.ssd.appssd.objects.Grupo;
import com.ssd.appssd.objects.User;

import java.util.ArrayList;
import java.util.List;

public class ChatFragment extends Fragment {

    private RecyclerView recyclerView, recyclerViewGrupo;
    private UserAdapter userAdapter;
    private GrupoAdapter grupoAdapter;
    private ArrayList<Grupo> mGrupos;
    private List<User> mUsers;
    private FirebaseFirestore mStore;
    private FirebaseAuth mAuth;
    private FirebaseUser mUser;

    public ChatFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_chat, container, false);
        // Inflate the layout for this fragment

        recyclerView = view.findViewById(R.id.recyclerview);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        recyclerViewGrupo = view.findViewById(R.id.recyclerviewgrupos);
        recyclerViewGrupo.setHasFixedSize(true);
        recyclerViewGrupo.setLayoutManager(new LinearLayoutManager(getContext()));

        mStore = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();

        mUsers = new ArrayList<>();
        mGrupos = new ArrayList<>();

        readGrupos();
        readUsers();

        return view;
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

    private void readUsers(){
        mStore.collection("Usuarios")
                .document(mUser.getEmail())
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if(task.isSuccessful()){
                            DocumentSnapshot documentSnapshot = task.getResult();
                            User user3 = documentSnapshot.toObject(User.class);
                            mStore.collection("Administrador")
                                    .document(user3.getCorreoPadre())
                                    .get()
                                    .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                            if(task.isSuccessful()){
                                                DocumentSnapshot documentSnapshot1 = task.getResult();
                                                if(documentSnapshot1.exists()){
                                                    User user4 = documentSnapshot1.toObject(User.class);
                                                    mStore.collection("Administrador")
                                                            .document(user4.getCorreo())
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
                                                                    mUsers.add(user4);
                                                                    for(QueryDocumentSnapshot documentSnapshot : value){
                                                                        if(documentSnapshot.exists()){
                                                                            User user = documentSnapshot.toObject(User.class);
                                                                            if(!user.getCorreo().equals(mUser.getEmail())){
                                                                                mUsers.add(user);
                                                                            }
                                                                        }
                                                                    }
                                                                    userAdapter = new UserAdapter(getContext(), mUsers);
                                                                    recyclerView.setAdapter(userAdapter);
                                                                }
                                                            });
                                                }else{
                                                }
                                            }else{
                                            }
                                        }
                                    });
                        }else{
                        }
                    }
                });

    }

}