package com.ssd.appssd.fragmentsUsuario;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.ssd.appssd.MainActivity;
import com.ssd.appssd.R;

public class PerfilFragment extends Fragment {

    private Button btnLogOut;
    private FirebaseAuth mAuth;
    private StorageReference mStorage;
    private ImageView photo;

    public PerfilFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_perfil, container, false);
        btnLogOut = (Button) view.findViewById(R.id.btnLogOut);
        mAuth = FirebaseAuth.getInstance();
        mStorage = FirebaseStorage.getInstance().getReference();
        
        btnLogOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAuth.signOut();
                Intent intent = new Intent(getActivity(), MainActivity.class);
                getActivity().finish();
                getActivity().startActivity(intent);
            }
        });
        return view;
    }

}