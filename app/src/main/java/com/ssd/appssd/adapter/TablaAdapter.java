package com.ssd.appssd.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.protobuf.StringValue;
import com.ssd.appssd.R;
import com.ssd.appssd.objects.Tabla;

import org.jetbrains.annotations.NotNull;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class TablaAdapter extends RecyclerView.Adapter<TablaAdapter.ViewHolder> {

    Context context;
    List<Tabla> tabla_list;

    public TablaAdapter(Context context, List<Tabla> tabla_list) {
        this.context = context;
        this.tabla_list = tabla_list;
    }

    @NotNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_table_layout, parent, false);
        return new TablaAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull TablaAdapter.ViewHolder holder, int position) {
        if(tabla_list != null && tabla_list.size() > 0){
            Tabla tabla = tabla_list.get(position);
            SimpleDateFormat sfd = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
            holder.correo_item.setText(tabla.getNombre());
            holder.fecha_item.setText(sfd.format(new Date(tabla.getFecha().toDate().toString())));
        }else{
            return;
        }
    }

    @Override
    public int getItemCount() {
        return tabla_list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView correo_item, fecha_item;
        public ViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            correo_item = itemView.findViewById(R.id.correo_item);
            fecha_item = itemView.findViewById(R.id.fecha_item);
        }

    }
}
