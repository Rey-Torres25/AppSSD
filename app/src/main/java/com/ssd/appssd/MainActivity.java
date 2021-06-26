package com.ssd.appssd;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.ssd.appssd.objects.User;

public class MainActivity extends AppCompatActivity {

    //atributos de tipo privado
    private EditText mEditTextEmail;
    private EditText mEditTextPassword;

    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mEditTextEmail = findViewById(R.id.editTextTextEmailAddress);
        mEditTextPassword = findViewById(R.id.editTextTextPassword);



    }

    public void iniciarSesion(View v) {
        //Esto sirve para ingresar a la actividad iniciarSesion
        boolean emailEmpty = mEditTextEmail.getText().toString().isEmpty();
        boolean passwordEmpty = mEditTextPassword.getText().toString().isEmpty();

        if(emailEmpty || passwordEmpty) {
            if(emailEmpty) {
                mEditTextEmail.setError(getString(R.string.error_vacio_edit_text));
            }
            if(passwordEmpty) {
                mEditTextPassword.setError(getString(R.string.error_vacio_edit_text));
            }
        } else {
            FirebaseAuth.getInstance().signInWithEmailAndPassword(mEditTextEmail.getText().toString(),
                    mEditTextPassword.getText().toString()).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                @Override
                public void onSuccess(AuthResult authResult) {
                    if(authResult.getUser().isEmailVerified()) {
                        FirebaseFirestore.getInstance().collection("Usuarios").document(authResult.getUser().getUid())
                                .get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                            @Override
                            public void onSuccess(DocumentSnapshot documentSnapshot) {
                                user = documentSnapshot.toObject(User.class);
                                Toast.makeText(MainActivity.this, "Hola " + user.getNombre(), Toast.LENGTH_LONG).show();
                            }

                        });
                        Intent iniciarSesion = new Intent(MainActivity.this, Menu_principal_usuario.class);
                        startActivity(iniciarSesion);
                    } else {
                        Toast.makeText(MainActivity.this, R.string.autentificacion_error, Toast.LENGTH_LONG).show();
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(MainActivity.this, e.getMessage(),Toast.LENGTH_LONG).show();
                }
            });
        }

    }

    public void registerUser(View view){
        //Esto sirve para ingresar a la actividad RegistroUsuario
        Intent registro = new Intent(MainActivity.this, RegistroUsuario.class);
        startActivity(registro);
    }

}