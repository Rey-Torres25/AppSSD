package com.ssd.appssd.adapter;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;
import com.ssd.appssd.R;
import com.ssd.appssd.objects.User;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

public class DialogAdapter extends RecyclerView.Adapter<DialogAdapter.ViewHolder> {

    private Context mContext;
    private ArrayList<String> userSaved = new ArrayList<>();
    private List<User> mUsers;
    DialogListener dialogListener;

    public DialogAdapter(Context mContext, List<User> mUsers, DialogListener dialogListener) {
        this.mUsers = mUsers;
        this.mContext = mContext;
        this.dialogListener = dialogListener;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.user_item_dialog, parent, false);
        return new DialogAdapter.ViewHolder(view);
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
                if(holder.checkBox.isChecked()){
                    holder.checkBox.setChecked(false);
                    for(int i=0; i<userSaved.size();i++){
                        if(userSaved.get(i).equals(user.getCorreo()))
                            userSaved.remove(i);
                    }
                }else{
                    holder.checkBox.setChecked(true);
                    userSaved.add(user.getCorreo());
                }
                dialogListener.onDialogChange(userSaved);
            }
        });

        holder.checkBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(holder.checkBox.isChecked()){
                    userSaved.add(user.getCorreo());
                }else{
                    for(int i=0; i<userSaved.size();i++){
                        if(userSaved.get(i).equals(user.getCorreo()))
                            userSaved.remove(i);
                    }
                }
                dialogListener.onDialogChange(userSaved);
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
        public CheckBox checkBox;
        public ViewHolder(View itemView) {
            super(itemView);

            checkBox = itemView.findViewById(R.id.check);
            username = itemView.findViewById(R.id.username);
            profile_image = itemView.findViewById(R.id.profile_image);
        }
    }
}
