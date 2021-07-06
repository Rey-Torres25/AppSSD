package com.ssd.appssd;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;

import com.google.firebase.firestore.FirebaseFirestore;

import com.ssd.appssd.objects.Standar;

public class RegistroUsuario extends AppCompatActivity {

    private EditText name;
    private EditText email;
    private EditText password, password2;
    private Button bRegister;
    private FirebaseAuth mAuth;
    private FirebaseFirestore mStore;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro_usuario);

        name = findViewById(R.id.name);
        email = findViewById(R.id.email);
        password = findViewById(R.id.password);
        password2 = findViewById(R.id.password2);
        bRegister = findViewById(R.id.btnRegister2);

        mAuth = FirebaseAuth.getInstance();
        mStore = FirebaseFirestore.getInstance();

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
                        password2.setError(null);
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
                        password2.setError(null);
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
                        password2.setError(null);
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

            mAuth.createUserWithEmailAndPassword(email.getText().toString(),
                    password.getText().toString()).addOnCompleteListener(task -> {
                        if (task.isSuccessful()){
                            mAuth.getCurrentUser().sendEmailVerification().addOnCompleteListener(task12 -> {
                                if(task12.isSuccessful()){
                                    Standar user = new Standar(name.getText().toString(), email.getText().toString(), "", "", "");
                                    mStore.collection("UsuarioEstandar")
                                            .document(email.getText().toString())
                                            .set(user)
                                            .addOnCompleteListener(task1 -> {
                                                if(task1.isSuccessful()){
                                                    Toast.makeText(RegistroUsuario.this, "Te has registrado correctamente. Favor de checar tu correo por verificación",
                                                            Toast.LENGTH_LONG).show();
                                                    mAuth.signOut();
                                                }else{
                                                    Toast.makeText(RegistroUsuario.this, task1.getException().getMessage(),
                                                            Toast.LENGTH_LONG).show();
                                                }
                                            });
                                }else{
                                    Toast.makeText(RegistroUsuario.this, task12.getException().getMessage(),
                                            Toast.LENGTH_LONG).show();
                                }
                            });
                        }
                    });
        }
    }

}