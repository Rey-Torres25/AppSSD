package com.ssd.appssd.fragmentsUsuario;

import android.app.RecoverableSecurityException;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.ssd.appssd.MainActivity;
import com.ssd.appssd.MenuAdmin;
import com.ssd.appssd.R;
import com.ssd.appssd.objects.Admin;
import com.ssd.appssd.objects.Standar;
import com.ssd.appssd.objects.User;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.HashMap;

import static android.app.Activity.RESULT_OK;

public class PerfilFragment extends Fragment {

    private Button btnLogOut, btnUpload;
    private FirebaseAuth mAuth;
    private FirebaseFirestore mStore;
    private FirebaseStorage storage;
    private StorageReference storageReference;
    private ImageView photo;
    private EditText editNombre, editCorreo;
    private Uri path;
    private Standar user;
    private long ONE_MEGABYTE = 1024*1024;
    private static final int SELECT_FILE = 1;
    private EditText token;
    private Button registrarToken;

    public PerfilFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_perfil, container, false);
        btnLogOut = (Button) view.findViewById(R.id.btnLogOut);
        btnUpload = (Button) view.findViewById(R.id.upload);
        editNombre = view.findViewById(R.id.editNombre);
        editCorreo = view.findViewById(R.id.editCorreo);
        photo = view.findViewById(R.id.profile_image);
        token = view.findViewById(R.id.editToken);
        registrarToken = view.findViewById(R.id.btnRegistrarToken);

        mAuth = FirebaseAuth.getInstance();
        mStore = FirebaseFirestore.getInstance();
        DocumentReference documentReference = mStore.collection("UsuarioEstandar").document(mAuth.getCurrentUser().getEmail());
        documentReference
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    user = documentSnapshot.toObject(Standar.class);
                    editNombre.setText(user.getNombre());
                    editCorreo.setText(user.getCorreo());
                    if(!user.getImageURL().equals("default")){
                        storageReference = storageReference.child("images/"+user.getCorreo()+"/profile_picture");
                        storageReference
                                .getBytes(ONE_MEGABYTE)
                                .addOnSuccessListener(new OnSuccessListener<byte[]>() {
                                    @Override
                                    public void onSuccess(byte[] bytes) {
                                        Glide.with(getActivity())
                                                .load(bytes)
                                                .into(photo);
                                    }
                                });
                    }else{
                        photo.setImageResource(R.drawable.perfil_without);
                    }
                });

        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

        registrarToken.setOnClickListener(v -> {

            if(!token.getText().toString().isEmpty()) {

                mStore.collection("Tokens").document(token.getText().toString()).get().
                        addOnSuccessListener(documentSnapshot -> {
                            if(documentSnapshot.exists()) {
                                if(!documentSnapshot.getBoolean("EstaActivado")) {
                                    Admin admin = new Admin(user.getNombre(), user.getCorreo(), user.getImageURL(), token.getText().toString(), null);
                                    mStore.collection("UsuarioEstandar").document(user.getCorreo()).delete().addOnSuccessListener(unused -> {
                                        mStore.collection("Administrador").document(user.getCorreo()).set(admin).addOnSuccessListener(documentReference1 -> {
                                            mStore.collection("Tokens").document(token.getText().toString()).update("EstaActivado", true).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void unused) {
                                                    Toast.makeText(getContext(), getString(R.string.registro_token_correctamente), Toast.LENGTH_LONG).show();
                                                    Intent menuAdmin = new Intent(getContext(), MenuAdmin.class);
                                                    getActivity().finish();
                                                    startActivity(menuAdmin);
                                                }
                                            });

                                        });
                                    });
                                } else {
                                    Toast.makeText(getContext(), getString(R.string.token_en_uso), Toast.LENGTH_LONG).show();
                                }

                            } else {
                                Toast.makeText(getContext(), getString(R.string.token_no_existe), Toast.LENGTH_LONG).show();
                            }
                        });

            } else {
                token.setError(getString(R.string.error_vacio_edit_text));
            }


        });

        btnUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                storageReference = storageReference.child("images/"+user.getCorreo()+"/profile_picture");
                photo.setDrawingCacheEnabled(true);
                photo.buildDrawingCache();
                Bitmap bitmap = ((BitmapDrawable) photo.getDrawable()).getBitmap();
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                byte[] data = baos.toByteArray();
                UploadTask uploadTask = storageReference.putBytes(data);
                uploadTask.addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getActivity(), "Hubo un error al subir tu imagen",
                                Toast.LENGTH_SHORT).show();
                    }
                }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        documentReference
                                .update("imageURL", storageReference.toString())
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {
                                        Toast.makeText(getActivity(), "Tu imagen se ha subido correctamente",
                                                Toast.LENGTH_SHORT).show();
                                        btnUpload.setVisibility(View.INVISIBLE);
                                        btnUpload.setEnabled(false);
                                    }
                                });
                    }
                });
            }
        });

        photo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                abrirGaleria();
            }
        });
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

    public void abrirGaleria(){
        Intent intent = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent.createChooser(intent, "Seleccione una imagen"),
                10);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK){
            path = data.getData();
            photo.setImageURI(path);
            btnUpload.setVisibility(View.VISIBLE);
            btnUpload.setEnabled(true);


        }
    }


}