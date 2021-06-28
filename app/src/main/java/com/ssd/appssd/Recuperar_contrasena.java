package com.ssd.appssd;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class Recuperar_contrasena extends AppCompatActivity {

    Button btnRestorePassword;
    EditText EmailAdress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recuperar_contrasena);

        btnRestorePassword = findViewById(R.id.btnRestorePassword);
        EmailAdress = findViewById(R.id.EmailAdress);

        btnRestorePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validate();
            }
        });
    }
    public void validate(){
        String correo = EmailAdress.getText().toString().trim();

        if (correo.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(correo).matches()){
            EmailAdress.setError("Correo Invalido");
            return;
        }

        sendEmail(correo);
    }
    //Este metodo sirve para validar la accion de Olvide contraseña, para volver al Main Activity donde puede iniciar sesion
    @Override
    public void onBackPressed() {
        super.onBackPressed();

        Intent intent = new Intent (Recuperar_contrasena.this, MainActivity.class);
        startActivity(intent);
        finish();
    }
    public void sendEmail(String correo){
        FirebaseAuth auth = FirebaseAuth.getInstance();
        String correo2 = correo;

        auth.sendPasswordResetEmail(correo2)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                       if (task.isSuccessful()){
                           Toast.makeText(Recuperar_contrasena.this, "Correo enviado", Toast.LENGTH_SHORT).show();
                           Intent intent = new Intent(Recuperar_contrasena.this, MainActivity.class);
                           startActivity(intent);
                           finish();
                       }else{
                           Toast.makeText(Recuperar_contrasena.this, "Correo invalido",Toast.LENGTH_SHORT).show();
                       }
                    }
                });
    }
}