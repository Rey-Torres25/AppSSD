package com.ssd.appssd.fragmentsUsuario;

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

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.ssd.appssd.R;
import com.ssd.appssd.adapter.TablaAdapter;
import com.ssd.appssd.objects.Tabla;
import com.ssd.appssd.objects.User;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class RecordsFragment extends Fragment {

    public RecordsFragment() {
        // Required empty public constructor
    }

    RecyclerView recyclerView;
    TablaAdapter tablaAdapter;
    private FirebaseFirestore mStore;
    private FirebaseAuth mAuth;
    private FirebaseUser mUser;
    private List<Tabla> mTabla;
    private DatabaseReference databaseReference;
    private String correo;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_records, container, false);
        // Inflate the layout for this fragment
        databaseReference = FirebaseDatabase.getInstance().getReference();
        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        mStore = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
        mTabla= new ArrayList<>();

        readTable2();
        readTable();
        return view;
    }

    private void readTable2(){
        mStore.collection("Registros")
                .whereEqualTo("correo", mUser.getEmail())
                .orderBy("fecha", Query.Direction.ASCENDING)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable @org.jetbrains.annotations.Nullable QuerySnapshot value, @Nullable @org.jetbrains.annotations.Nullable FirebaseFirestoreException error) {
                        if(error != null){
                            Log.w("TAG", "Listen failed.", error);
                            return;
                        }
                        mTabla.clear();
                        for(QueryDocumentSnapshot documentSnapshot : value){
                            if(documentSnapshot.exists()){
                                Tabla tabla = documentSnapshot.toObject(Tabla.class);
                                mTabla.add(tabla);
                            }
                        }
                        tablaAdapter = new TablaAdapter(getContext(), mTabla);
                        recyclerView.setAdapter(tablaAdapter);
                    }
                });
    }
    private void readTable(){
        databaseReference.child("registros").child("entradas").child("usuarios").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                correo = snapshot.getValue(String.class);
                System.out.println("Joder = "+ correo);
                if(!correo.equals("null")){
                    mStore.collection("Usuarios")
                            .document(correo)
                            .get()
                            .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                @Override
                                public void onSuccess(DocumentSnapshot documentSnapshot) {
                                    if(documentSnapshot.exists()){
                                        User user = documentSnapshot.toObject(User.class);
                                        Map<String, Object> map = new HashMap<>();
                                        map.put("correo", user.getCorreo());
                                        map.put("nombre", user.getNombre());
                                        map.put("fecha", Timestamp.now());
                                        mStore.collection("Registros")
                                                .document()
                                                .set(map);
                                    }else{
                                        mStore.collection("Administrador")
                                                .document(correo)
                                                .get()
                                                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                                    @Override
                                                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                                                        if(documentSnapshot.exists()){
                                                            User user = documentSnapshot.toObject(User.class);
                                                            Map<String, Object> map = new HashMap<>();
                                                            map.put("correo", user.getCorreo());
                                                            map.put("nombre", user.getNombre());
                                                            map.put("fecha", Timestamp.now());
                                                            mStore.collection("Registros")
                                                                    .document()
                                                                    .set(map);
                                                        }
                                                    }
                                                });
                                    }
                                }
                            });
                }else{
                    System.out.println("no entra");
                }
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {
            }
        });
    }
}