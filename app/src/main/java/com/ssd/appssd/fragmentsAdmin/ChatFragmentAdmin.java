package com.ssd.appssd.fragmentsAdmin;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.ssd.appssd.R;
import com.ssd.appssd.adapter.AdminAdapter;
import com.ssd.appssd.adapter.UserAdapter;
import com.ssd.appssd.objects.Admin;
import com.ssd.appssd.objects.User;

import java.util.ArrayList;
import java.util.List;

public class ChatFragmentAdmin extends Fragment {


    private RecyclerView recyclerView;
    private AdminAdapter userAdapter;
    private List<User> mUsers;
    private FirebaseFirestore mStore;
    private FirebaseAuth mAuth;
    private FirebaseUser mUser;

    public ChatFragmentAdmin() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_chat_admin, container, false);
        // Inflate the layout for this fragment

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
        mStore.collection("Usuarios")
                .whereEqualTo("correoPadre", mUser.getEmail())
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful()){
                            mUsers.clear();
                            for(QueryDocumentSnapshot documentSnapshot : task.getResult()){
                                User user = documentSnapshot.toObject(User.class);
                                assert user != null;
                                assert mUser != null;
                                if(!user.getCorreo().equals(mUser.getEmail())){
                                    mUsers.add(user);
                                }
                                userAdapter = new AdminAdapter(getContext(), mUsers);
                                recyclerView.setAdapter(userAdapter);
                            }
                        }
                    }
                });
    }
}