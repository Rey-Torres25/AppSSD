package com.ssd.appssd.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ssd.appssd.ChatGrupal;
import com.ssd.appssd.R;
import com.ssd.appssd.objects.Grupo;

import java.util.ArrayList;
import java.util.List;

public class GrupoAdapter extends RecyclerView.Adapter<GrupoAdapter.ViewHolder> {
    private Context mContext;
    private ArrayList<Grupo> mGrupos;

    public GrupoAdapter(Context mContext, ArrayList<Grupo> mGrupos) {
        this.mGrupos = mGrupos;
        this.mContext = mContext;
    }

    @NonNull
    @Override
    public GrupoAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.grupo_item, parent, false);
        return new GrupoAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull GrupoAdapter.ViewHolder holder, int position) {
        Grupo grupo = mGrupos.get(position);
        holder.username.setText(grupo.getNombre());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, ChatGrupal.class);
                intent.putExtra("id", grupo.getId());
                intent.putStringArrayListExtra("usuarios", grupo.getUsuarios());
                mContext.startActivity(intent);
            }
        });
    }


    @Override
    public int getItemCount() {
        return mGrupos.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        public TextView username;
        public ViewHolder(View itemView) {
            super(itemView);
            username = itemView.findViewById(R.id.username);
        }
    }
}
