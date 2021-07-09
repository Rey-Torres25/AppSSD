package com.ssd.appssd.components;

import android.content.Context;
import android.net.Uri;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;
import com.ssd.appssd.R;
import com.ssd.appssd.objects.Admin;
import com.ssd.appssd.objects.Message;
import com.ssd.appssd.objects.User;

public class ComponentMessage extends RelativeLayout {


    public ComponentMessage(Context context, Message message) {
        super(context);
        String mi_correo = FirebaseAuth.getInstance().getCurrentUser().getEmail();

        if(message.getSender().matches(mi_correo)){
            inflate(context, R.layout.chat_item_right, this);
        }else{
            DocumentReference documentReference = FirebaseFirestore.getInstance().collection("Usuarios")
                    .document(message.getSender());
            documentReference
                    .get()
                    .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                            if(documentSnapshot.exists()){
                                User user = documentSnapshot.toObject(User.class);
                                if(!user.getImageURL().equals("default")){
                                    StorageReference storageReference = FirebaseStorage.getInstance().getReference().child("images/"+user.getCorreo()+"/profile_picture");
                                    storageReference.child("images/"+user.getCorreo()+"/profile_picture");
                                    storageReference
                                            .getDownloadUrl()
                                            .addOnSuccessListener(new OnSuccessListener<Uri>() {
                                                @Override
                                                public void onSuccess(Uri uri) {
                                                    ImageView photo = findViewById(R.id.profile_image);
                                                    Picasso.with(context)
                                                            .load(uri)
                                                            .into(photo);
                                                }
                                            });
                                }else{
                                    ImageView photo = findViewById(R.id.profile_image);
                                    photo.setImageResource(R.drawable.perfil_without);
                                }
                            }else{
                                DocumentReference documentReference = FirebaseFirestore.getInstance().collection("Administrador")
                                        .document(message.getSender());
                                documentReference
                                        .get()
                                        .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                            @Override
                                            public void onSuccess(DocumentSnapshot documentSnapshot) {
                                                if(documentSnapshot.exists()){
                                                    Admin admin = documentSnapshot.toObject(Admin.class);
                                                    if(!admin.getImageURL().equals("default")){
                                                        StorageReference storageReference = FirebaseStorage.getInstance().getReference().child("images/"+admin.getCorreo()+"/profile_picture");
                                                        storageReference.child("images/"+admin.getCorreo()+"/profile_picture");
                                                        storageReference
                                                                .getDownloadUrl()
                                                                .addOnSuccessListener(new OnSuccessListener<Uri>() {
                                                                    @Override
                                                                    public void onSuccess(Uri uri) {
                                                                        ImageView photo = findViewById(R.id.profile_image);
                                                                        Picasso.with(context)
                                                                                .load(uri)
                                                                                .into(photo);
                                                                    }
                                                                });
                                                    }else{
                                                        ImageView photo = findViewById(R.id.profile_image);
                                                        photo.setImageResource(R.drawable.perfil_without);
                                                    }
                                                }
                                            }
                                        });
                            }
                        }
                    });
            inflate(context, R.layout.chat_item_left, this);
        }
        TextView textView = findViewById(R.id.Mensaje);
        textView.setText(message.getMessage());
    }




}
