package com.ssd.appssd;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthActionCodeException;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.ssd.appssd.objects.User;

import java.util.Map;

public class RegistroUsuario extends AppCompatActivity {

    private EditText name;
    private EditText email;
    private EditText password, password2;
    private Button bRegister;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro_usuario);

        name = findViewById(R.id.name);
        email = findViewById(R.id.email);
        password = findViewById(R.id.password);
        password2 = findViewById(R.id.password2);
        bRegister = findViewById(R.id.btnRegister2);

        bRegister.setClickable(false);
        name.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(!email.getText().toString().isEmpty() && !password.getText().toString().isEmpty() && !password2.getText().toString().isEmpty()){
                    if(password2.getText().toString().equals(password.getText().toString())) {
                        bRegister.setBackgroundColor(getColor(R.color.pass_button));
                        bRegister.setTextColor(getColor(R.color.white));
                        bRegister.setClickable(true);
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                if(name.getText().toString().isEmpty()){
                    bRegister.setBackgroundColor(getColor(R.color.button_red));
                    bRegister.setTextColor(getColor(R.color.color));
                    bRegister.setClickable(false);
                }
            }
        });

        email.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(!name.getText().toString().isEmpty() && !password.getText().toString().isEmpty() && !password2.getText().toString().isEmpty()){
                    if(password2.getText().toString().equals(password.getText().toString())) {
                        bRegister.setBackgroundColor(getColor(R.color.pass_button));
                        bRegister.setTextColor(getColor(R.color.white));
                        bRegister.setClickable(true);
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                if(email.getText().toString().isEmpty()){
                    bRegister.setBackgroundColor(getColor(R.color.button_red));
                    bRegister.setTextColor(getColor(R.color.color));
                    bRegister.setClickable(false);
                }
            }
        });

        password.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(!email.getText().toString().isEmpty() && !name.getText().toString().isEmpty() && !password2.getText().toString().isEmpty()){
                    if(password2.getText().toString().equals(password.getText().toString())) {
                        bRegister.setBackgroundColor(getColor(R.color.pass_button));
                        bRegister.setTextColor(getColor(R.color.white));
                        bRegister.setClickable(true);
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                if(password.getText().toString().isEmpty()){
                    bRegister.setBackgroundColor(getColor(R.color.button_red));
                    bRegister.setTextColor(getColor(R.color.color));
                    bRegister.setClickable(false);
                }
            }
        });

        password2.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(!email.getText().toString().isEmpty() && !password.getText().toString().isEmpty() && !name.getText().toString().isEmpty()){
                    if(password2.getText().toString().equals(password.getText().toString())){
                        bRegister.setBackgroundColor(getColor(R.color.pass_button));
                        bRegister.setTextColor(getColor(R.color.white));
                        bRegister.setClickable(true);
                    }else{
                        bRegister.setBackgroundColor(getColor(R.color.button_red));
                        bRegister.setTextColor(getColor(R.color.color));
                        bRegister.setClickable(false);
                        password2.setError(getString(R.string.no_coinciden));
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                if(password2.getText().toString().isEmpty()) {
                    bRegister.setBackgroundColor(getColor(R.color.button_red));
                    bRegister.setTextColor(getColor(R.color.color));
                    bRegister.setClickable(false);
                }
            }
        });
    }

    public void registerUser(View v){

        boolean nameempty = name.getText().toString().isEmpty();
        boolean emailempty = email.getText().toString().isEmpty();
        boolean passwordempty = password.getText().toString().isEmpty();
        boolean passwordempty2 = password2.getText().toString().isEmpty();

        if (nameempty || emailempty || passwordempty || passwordempty2){
            if(nameempty) {
                name.setError(getString(R.string.error_vacio_edit_text));
            }
            if(emailempty) {
                email.setError(getString(R.string.error_vacio_edit_text));
            }
            if(passwordempty) {
                password.setError(getString(R.string.error_vacio_edit_text));
            }
            if(passwordempty2){
                password2.setError(getString(R.string.error_vacio_edit_text));
            }
        }else{
            //Linea para crear el correo con emai y contraseña
            FirebaseAuth.getInstance().createUserWithEmailAndPassword(email.getText().toString(),
                    password.getText().toString()).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                @Override
                public void onSuccess(AuthResult authResult) {
                    //mandar un correo para autentificar
                    authResult.getUser().sendEmailVerification().addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            //crear en la base de datos un usuario
                            User user = new User();
                            user.setNombre(name.getText().toString());
                            user.setCorreo(email.getText().toString());
                            user.setAdmin(false);
                            FirebaseFirestore.getInstance().collection("Usuarios").
                                    document(authResult.getUser().getUid()).set(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {
                                    Toast.makeText(RegistroUsuario.this, R.string.se_creo_usuario_correctamente, Toast.LENGTH_LONG).show();
                                }
                            });
                            //FirebaseFirestore.getInstance().collection("Usuarios").add()

                        }
                    });
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(RegistroUsuario.this, e.getMessage(), Toast.LENGTH_LONG).show();
                }
            });
            //Figma
        }
    }

}