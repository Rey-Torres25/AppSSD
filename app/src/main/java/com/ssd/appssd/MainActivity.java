package com.ssd.appssd;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
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
    private Button bLogin;
    private User user;
    TextView forgotPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mEditTextEmail = findViewById(R.id.editTextTextEmailAddress);
        mEditTextPassword = findViewById(R.id.editTextTextPassword);
        bLogin = findViewById(R.id.btnSignIn);
        forgotPassword = findViewById(R.id.se_me_olvido_contraseña);


        bLogin.setClickable(false);
        mEditTextEmail.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(!mEditTextPassword.getText().toString().isEmpty()){
                    bLogin.setBackgroundColor(Color.parseColor("#156c9e"));
                    bLogin.setTextColor(Color.parseColor("#FFFFFF"));
                    bLogin.setClickable(true);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                if(mEditTextEmail.getText().toString().isEmpty()){
                    bLogin.setBackgroundColor(Color.parseColor("#093d56"));
                    bLogin.setTextColor(Color.parseColor("#666666"));
                    bLogin.setClickable(false);
                }
            }
        });

        //Recuperar contraseña
        forgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, Recuperar_contrasena.class);
                startActivity(intent);
                finish();
            }
        });

        mEditTextPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!mEditTextEmail.getText().toString().isEmpty()) {
                    bLogin.setBackgroundColor(Color.parseColor("#156c9e"));
                    bLogin.setTextColor(Color.parseColor("#FFFFFF"));
                    bLogin.setClickable(true);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                if(mEditTextPassword.getText().toString().isEmpty()){
                    bLogin.setBackgroundColor(Color.parseColor("#093d56"));
                    bLogin.setTextColor(Color.parseColor("#666666"));
                    bLogin.setClickable(false);
                }
            }
        });
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
                        Intent iniciarSesion = new Intent(MainActivity.this, MenuUsuario.class);
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