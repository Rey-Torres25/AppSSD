package com.ssd.appssd.components;

import android.content.Context;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.ssd.appssd.R;
import com.ssd.appssd.objects.Message;

public class ComponentMessage extends RelativeLayout {


    public ComponentMessage(Context context, Message message) {
        super(context);
        String mi_correo = FirebaseAuth.getInstance().getCurrentUser().getEmail();

        if(message.getSender().matches(mi_correo)){
            inflate(context, R.layout.chat_item_right, this);
        } else {
            inflate(context, R.layout.chat_item_left, this);
        }

        TextView textView = findViewById(R.id.Mensaje);
        textView.setText(message.getMessage());

    }




}
