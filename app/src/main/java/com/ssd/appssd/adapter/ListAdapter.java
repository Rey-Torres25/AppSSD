package com.ssd.appssd.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;
import com.ssd.appssd.ChatActivity;
import com.ssd.appssd.R;
import com.ssd.appssd.objects.Admin;
import com.ssd.appssd.objects.Grupo;
import com.ssd.appssd.objects.User;

import java.util.List;

public class ListAdapter extends RecyclerView.Adapter<ListAdapter.ViewHolder> {
    private Context mContext;
    private Activity mActivity;
    private List<User> mUsers;

    public ListAdapter(Context mContext, List<User> mUsers, Activity mActivity) {
        this.mUsers = mUsers;
        this.mContext = mContext;
        this.mActivity = mActivity;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.user_item, parent, false);
        return new ListAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        User user = mUsers.get(position);
        holder.username.setText(user.getNombre());
        if(user.getImageURL().equals("default")){
            holder.profile_image.setImageResource(R.drawable.perfil_without);
        }else{
            FirebaseStorage firebaseStorage = FirebaseStorage.getInstance();
            StorageReference storageReference = firebaseStorage.getReference().child("images/"+user.getCorreo()+"/profile_picture");
            storageReference.getDownloadUrl()
                    .addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            Picasso.with(mContext)
                                    .load(uri)
                                    .into(holder.profile_image);
                        }
                    });

        }
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(mContext, R.style.CustomAlertDialog);
                builder.setTitle(mContext.getString(R.string.confirmar_eliminar));
                builder.setPositiveButton(mContext.getString(R.string.eliminar_usuario), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        FirebaseFirestore mStore = FirebaseFirestore.getInstance();
                        FirebaseAuth mAuth = FirebaseAuth.getInstance();
                        FirebaseUser mUser = mAuth.getCurrentUser();
                        mStore.collection("Usuarios")
                                .document(user.getCorreo())
                                .delete()
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {
                                        mStore.collection("Administrador")
                                                .document(mUser.getEmail())
                                                .collection("Usuarios")
                                                .document(user.getCorreo())
                                                .delete()
                                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void unused) {
                                                       mStore.collection("Chats")
                                                       .whereArrayContains("usuarios", user.getCorreo())
                                                               .get()
                                                               .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                                   @Override
                                                                   public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                                       if(task.isSuccessful()) {
                                                                           Toast.makeText(mContext, mContext.getString(R.string.usuario_eliminado), Toast.LENGTH_SHORT).show();
                                                                           for(QueryDocumentSnapshot documentSnapshot : task.getResult()){
                                                                               String id = documentSnapshot.getId();
                                                                               mStore.collection("Chats")
                                                                                       .document(id)
                                                                                       .delete()
                                                                                       .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                                           @Override
                                                                                           public void onSuccess(Void unused) {
                                                                                               mStore.collection("Grupos")
                                                                                                       .whereArrayContains("usuarios", user.getCorreo())
                                                                                                       .get()
                                                                                                       .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                                                                           @Override
                                                                                                           public void onComplete(@NonNull Task<QuerySnapshot> task){
                                                                                                               if(task.isSuccessful()){
                                                                                                                   for(QueryDocumentSnapshot documentSnapshot : task.getResult()){
                                                                                                                       Grupo grupo = documentSnapshot.toObject(Grupo.class);
                                                                                                                       List<String> list = grupo.getUsuarios();
                                                                                                                       for(int i=0; i<list.size();i++){
                                                                                                                           if(list.get(i).equals(user.getCorreo()))
                                                                                                                              list.remove(i);
                                                                                                                       }
                                                                                                                       mStore.collection("Grupos")
                                                                                                                               .document(documentSnapshot.getId())
                                                                                                                               .collection("TODO")
                                                                                                                               .whereEqualTo("sender", user.getCorreo())
                                                                                                                               .get()
                                                                                                                               .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                                                                                                   @Override
                                                                                                                                   public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                                                                                                       for(QueryDocumentSnapshot documentSnapshot4 : task.getResult()){
                                                                                                                                           mStore.collection("Grupos")
                                                                                                                                                   .document(documentSnapshot.getId())
                                                                                                                                                   .collection("TODO")
                                                                                                                                                   .document(documentSnapshot4.getId())
                                                                                                                                                   .delete();
                                                                                                                                       }
                                                                                                                                   }
                                                                                                                               });
                                                                                                                       mStore.collection("Grupos")
                                                                                                                               .document(documentSnapshot.getId())
                                                                                                                               .update("usuarios", list);
                                                                                                                   }
                                                                                                               }
                                                                                                           }
                                                                                                       });
                                                                                           }
                                                                                       });
                                                                           }
                                                                       }else{

                                                                       }
                                                                   }
                                                               });
                                                    }
                                                }).addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {

                                            }
                                        });
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                            }
                        });
                    }
                });
                builder.setNegativeButton(mContext.getString(R.string.cancelar_dialog), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return mUsers.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        public TextView username;
        public ImageView profile_image;

        public ViewHolder(View itemView) {
            super(itemView);

            username = itemView.findViewById(R.id.username);
            profile_image = itemView.findViewById(R.id.profile_image);
        }
    }
}
