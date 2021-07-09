package com.ssd.appssd.adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;
import com.ssd.appssd.ChatActivity;
import com.ssd.appssd.R;
import com.ssd.appssd.objects.User;

import java.util.List;

public class UserAdapterGrupo extends RecyclerView.Adapter<UserAdapterGrupo.ViewHolder> {
    private Context mContext;
    private List<User> mUsers;
    UserGrupoListener userGrupoListener;

    public UserAdapterGrupo(Context mContext, List<User> mUsers, UserGrupoListener userGrupoListener) {
        this.mUsers = mUsers;
        this.mContext = mContext;
        this.userGrupoListener = userGrupoListener;
    }

    @NonNull
    @Override
    public UserAdapterGrupo.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.user_item, parent, false);
        return new UserAdapterGrupo.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserAdapterGrupo.ViewHolder holder, int position) {
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
                userGrupoListener.onDialogChange(1);
                Intent intent = new Intent(mContext, ChatActivity.class);
                intent.putExtra("correo", user.getCorreo());
                mContext.startActivity(intent);
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