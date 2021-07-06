package com.ssd.appssd;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;

import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;


import com.google.android.gms.tasks.OnCompleteListener;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.ssd.appssd.objects.User;

import java.lang.reflect.Array;
import java.util.Locale;


public class MainActivity extends AppCompatActivity {

    //atributos de tipo privado
    private EditText mEditTextEmail;
    private EditText mEditTextPassword;
    private Button bLogin;
    private FirebaseUser mUser;
    private FirebaseFirestore mStore;
    private FirebaseAuth mAuth;
    private Spinner spinner;
    TextView forgotPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        loadLocale();
        setContentView(R.layout.activity_main);

        //Firebase Instances
        mStore = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();

        if(mUser != null){
            mStore.collection("Administrador")
                    .document(mUser.getEmail())
                    .get()
                    .addOnSuccessListener(documentSnapshot -> {
                        if(documentSnapshot.exists()){
                            Toast.makeText(MainActivity.this, "¡Bienvenid@, "+documentSnapshot.get("nombre")+"!",
                                    Toast.LENGTH_SHORT).show();
                            Intent admin = new Intent(MainActivity.this, MenuAdmin.class);
                            startActivity(admin);
                        } else {
                            mStore.collection("UsuarioEstandar").
                                    document(mUser.getEmail()).
                                    get().addOnSuccessListener(documentSnapshot1 -> {
                                        if(documentSnapshot1.exists()) {
                                            Toast.makeText(MainActivity.this, "¡Bienvenid@, "+documentSnapshot.get("nombre")+"!",
                                                    Toast.LENGTH_SHORT).show();
                                            Intent userEs = new Intent(MainActivity.this, MenuUsuario.class);
                                            startActivity(userEs);
                                        }
                                    });
                        }
                    });
        }

        spinner = findViewById(R.id.options);
        spinner.getBackground().setColorFilter(getResources().getColor(R.color.black), PorterDuff.Mode.SRC_ATOP);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.options_user, R.layout.style_spinner);
        spinner.setAdapter(adapter);

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
        //Boton de cambiar idioma
        Button cambiarleng = findViewById(R.id.btnIdioma);
        cambiarleng.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            //Se muestra la lista de idiomas a cambiar
                showCambiarIdiomaDialog();
            }
        });


    }//Termina Acitivty Main

    private void showCambiarIdiomaDialog() {
        final String[] ListItems = {"English", "Español"};
        AlertDialog.Builder mBuilder = new AlertDialog.Builder(MainActivity.this);
        mBuilder.setTitle("Escoge el idioma...");
        mBuilder.setSingleChoiceItems(ListItems, -1, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int which) {

                if (which == 0){
                    //English
                    setLocale("en");
                    recreate();
                }
                else if (which == 1){
                    //Español
                    setLocale("es");
                    recreate();
                }
                dialogInterface.dismiss();
            }
        });
        AlertDialog mDialog = mBuilder.create();
        //Mostrar alerta
        mDialog.show();
    }

    private void setLocale(String idioma) {
        Locale locale = new Locale(idioma);
        Locale.setDefault(locale);
        Configuration configuration = new Configuration();
        configuration.locale = locale;
        getBaseContext().getResources().updateConfiguration(configuration, getBaseContext().getResources().getDisplayMetrics());
           //Guardar Datos
        SharedPreferences.Editor editor = getSharedPreferences("Settings", MODE_PRIVATE).edit();
        editor.putString("My_Lang", idioma);
        editor.apply();
    }
      //Cargar lenguaje guardado en preferencias
    public void loadLocale(){
        SharedPreferences prefs = getSharedPreferences("Settings", Activity.MODE_PRIVATE);
        String language = prefs.getString("My_Lang", "es");
        setLocale(language);
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
            mAuth.signInWithEmailAndPassword(mEditTextEmail.getText().toString(),
                    mEditTextPassword.getText().toString()).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if(task.isSuccessful()){
                        if(mAuth.getCurrentUser().isEmailVerified()){
                            int item = spinner.getSelectedItemPosition();
                            if(item == 0) {
                                iniciarSesionDataBase(MenuAdmin.class,"Administrador");
                            } else {
                                iniciarSesionDataBase(MenuUsuario.class, "UsuarioEstandar");
                            }

                        }else{
                            Toast.makeText(MainActivity.this, "Su correo no ha sido verificado, favor de verificar.",
                                    Toast.LENGTH_LONG).show();
                        }
                    }else{
                        Toast.makeText(MainActivity.this, task.getException().getMessage(), Toast.LENGTH_LONG).show();
                    }
                }
            });
        }

    }

    private void iniciarSesionDataBase(Class clase, String collection) {
        mStore.collection(collection)
                .document(mAuth.getCurrentUser().getEmail())
                .get()
                .addOnCompleteListener(task -> {
                    DocumentSnapshot documentSnapshot = task.getResult();
                    if(documentSnapshot.exists()){
                        Intent intent = new Intent(MainActivity.this, clase);
                        Toast.makeText(MainActivity.this, "¡Bienvenid@, "+documentSnapshot.get("nombre")+"!",
                                Toast.LENGTH_SHORT).show();
                        startActivity(intent);
                        finish();
                    }else{
                        Toast.makeText(MainActivity.this, getString(R.string.no_existe_usuario),
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }


    public void registerUser(View view){
        //Esto sirve para ingresar a la actividad RegistroUsuario
        Intent registro = new Intent(MainActivity.this, RegistroUsuario.class);
        startActivity(registro);
    }


}