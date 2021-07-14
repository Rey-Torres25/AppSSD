package com.ssd.appssd;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GetTokenResult;
import com.ssd.appssd.utilities.Constants;

import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;

import com.google.firebase.auth.FirebaseAuth;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

public class RegistroUsuario extends AppCompatActivity {

    private EditText name;
    private EditText email;
    private EditText password, password2;
    private Button bRegister;
    private FirebaseAuth mAuth;
    private String Token = "";
    private String FCM_Token = "";
    private String User_id = "";
    private PreferenceManager preferenceManager;
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
            AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.CustomAlertDialog);
            builder.setTitle("Token");
            final EditText input = new EditText(this);
            input.setInputType(InputType.TYPE_CLASS_TEXT);
            input.setTextColor(getResources().getColor(R.color.white));
            input.setHint("Ingrese el token...");
            input.setHintTextColor(getResources().getColor(R.color.color));
            builder.setView(input);
            builder.setPositiveButton(getString(R.string.registrar_dialog_confirmacion), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    if(!input.getText().toString().isEmpty()){
                        Token = input.getText().toString();
                        mStore.collection("Tokens")
                                .document(Token)
                                .get()
                                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                    @Override
                                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                                        if(documentSnapshot.exists()){
                                            if(!documentSnapshot.getBoolean("EstaActivado")){
                                                mAuth.createUserWithEmailAndPassword(email.getText().toString(),
                                                        password.getText().toString()).addOnCompleteListener(task -> {
                                                            if(task.isSuccessful()){
                                                                mAuth.getCurrentUser().sendEmailVerification().addOnCompleteListener(task2 -> {
                                                                    if (task2.isSuccessful()) {
                                                                        Map<String, Object> map = new HashMap<>();
                                                                        map.put("nombre", name.getText().toString());
                                                                        map.put("correo", email.getText().toString());
                                                                        map.put("imageURL", "default");
                                                                        map.put("token", Token);
                                                                        map.put("timestamp", FieldValue.serverTimestamp());
                                                                        mStore.collection("Administrador")
                                                                                .document(email.getText().toString())
                                                                                .set(map)
                                                                                .addOnSuccessListener(documentReference1 ->{
                                                                                   mStore.collection("Tokens")
                                                                                   .document(Token)
                                                                                   .update("EstaActivado", true)
                                                                                   .addOnCompleteListener(task3 ->{
                                                                                      if(task3.isSuccessful()){
                                                                                          Toast.makeText(RegistroUsuario.this, "Te has registrado correctamente. Favor de checar tu correo por verificación",
                                                                                                  Toast.LENGTH_LONG).show();
                                                                                          mAuth.signOut();
                                                                                          Intent intent = new Intent(RegistroUsuario.this, MainActivity.class);
                                                                                          startActivity(intent);
                                                                                          finish();                                                                                     }else{
                                                                                          Toast.makeText(RegistroUsuario.this, task3.getException().getMessage(),
                                                                                                  Toast.LENGTH_LONG).show();
                                                                                      }
                                                                                   });
                                                                                });
                                                                    }
                                                                });
                                                            }
                                                        });
                                            }else{
                                                Toast.makeText(RegistroUsuario.this, getString(R.string.token_en_uso),
                                                        Toast.LENGTH_LONG).show();
                                            }
                                        }else{
                                            Toast.makeText(RegistroUsuario.this, getString(R.string.token_no_existe),
                                                    Toast.LENGTH_LONG).show();
                                        }
                                    }
                                });
                    }
                }
            });
            builder.setNegativeButton(getString(R.string.cancelar_dialog), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });
            AlertDialog dialog = builder.create();
            dialog.show();
            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(false);
            input.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    dialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(true);
                }

                @Override
                public void afterTextChanged(Editable s) {
                    if(input.getText().toString().isEmpty()){
                        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(false);
                    }
                }
            });
        }
    }

    /*private void sendFCMTokenToDataBase(String FCM_Token) {
        FirebaseUser mUser = FirebaseAuth.getInstance().getCurrentUser();
        mUser.getIdToken(true).addOnCompleteListener(new OnCompleteListener<GetTokenResult>() {
            @Override
            public void onComplete(@NonNull Task<GetTokenResult> task) {
                if (task.isSuccessful()){
                    String FCM_Token = task.getResult().getToken();
                     mStore.collection("Usuarios").document("FCM_Token");
                }else{

                }
            }
        });


    }*/

}