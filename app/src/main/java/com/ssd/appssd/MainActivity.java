package com.ssd.appssd;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.ssd.appssd.RegistroUsuario;
import android.app.Activity;

import com.google.firebase.firestore.DocumentReference;
import com.ssd.appssd.utilities.Constants;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.Editable;
import android.text.TextWatcher;

import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;


import com.google.android.gms.tasks.OnCompleteListener;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.ssd.appssd.objects.User;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;


public class MainActivity extends AppCompatActivity {

    //atributos de tipo privado
    private EditText mEditTextEmail, password, confirm_password;
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
                            finish();
                        }else{
                            mStore.collection("Usuarios").
                                    document(mUser.getEmail()).
                                    get().addOnSuccessListener(documentSnapshot1 -> {
                                        if(documentSnapshot1.exists()) {
                                            Toast.makeText(MainActivity.this, "¡Bienvenid@, "+documentSnapshot1.get("nombre")+"!",
                                                    Toast.LENGTH_SHORT).show();
                                            Intent userEs = new Intent(MainActivity.this, MenuUsuario.class);
                                            startActivity(userEs);
                                            finish();
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
        }else{
            int item = spinner.getSelectedItemPosition();
            if(item == 0) {
                iniciarSesionDataBase(MenuAdmin.class,"Administrador");
            }else{
                iniciarSesionDataBase(MenuUsuario.class, "Usuarios");
            }
        }
    }

    private void iniciarSesionDataBase(Class clase, String collection) {
        mStore.collection(collection)
                .document(mEditTextEmail.getText().toString())
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if(documentSnapshot.exists()){
                            if(collection.equals("Usuarios")){
                                if(documentSnapshot.getBoolean("correoEnviado")){
                                    mAuth.signInWithEmailAndPassword(mEditTextEmail.getText().toString(), mEditTextPassword.getText().toString())
                                            .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                                                @Override
                                                public void onSuccess(AuthResult authResult) {
                                                    if(mAuth.getCurrentUser().isEmailVerified()){
                                                        mStore.collection(collection)
                                                                .document(mAuth.getCurrentUser().getEmail())
                                                                .get()
                                                                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                                                    @Override
                                                                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                                        User user = task.getResult().toObject(User.class);
                                                                        if(!user.isVerificado()){
                                                                            mStore.collection(collection)
                                                                                    .document(mAuth.getCurrentUser().getEmail())
                                                                                    .update("verificado", true)
                                                                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                                        @Override
                                                                                        public void onSuccess(Void unused) {
                                                                                            mStore.collection("Administrador")
                                                                                                    .document(user.getCorreoPadre())
                                                                                                    .collection("Usuarios")
                                                                                                    .document(mAuth.getCurrentUser().getEmail())
                                                                                                    .update("verificado", true)
                                                                                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                                                        @Override
                                                                                                        public void onSuccess(Void unused) {
                                                                                                            Intent intent = new Intent(MainActivity.this, clase);
                                                                                                            Toast.makeText(MainActivity.this, String.format(getString(R.string.bienvenido_usuario)+documentSnapshot.get("nombre")+"!"),
                                                                                                                    Toast.LENGTH_SHORT).show();
                                                                                                            startActivity(intent);
                                                                                                            finish();
                                                                                                        }
                                                                                                    });
                                                                                        }
                                                                                    });
                                                                        }else{
                                                                            Intent intent = new Intent(MainActivity.this, clase);
                                                                            Toast.makeText(MainActivity.this, String.format(getString(R.string.bienvenido_usuario) +documentSnapshot.get("nombre")+"!"),
                                                                                    Toast.LENGTH_SHORT).show();
                                                                            startActivity(intent);
                                                                            finish();
                                                                        }
                                                                    }
                                                                });

                                                    }else{
                                                        Toast.makeText(MainActivity.this, "Su correo no ha sido verificado, favor de verificar.",
                                                                Toast.LENGTH_LONG).show();
                                                    }
                                                }
                                            });
                                }else{
                                    LayoutInflater inflater = getLayoutInflater();
                                    View dialogView = inflater.inflate(R.layout.dialog_confirmar_password_usuario, null);
                                    AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this, R.style.CustomAlertDialog);
                                    builder.setTitle(getString(R.string.confirmar_contrasena));
                                    builder.setView(dialogView);
                                    password = (EditText) dialogView.findViewById(R.id.password);
                                    password.setText(mEditTextPassword.getText().toString());
                                    confirm_password = (EditText) dialogView.findViewById(R.id.confirm_password);
                                    builder.setPositiveButton("Confirmar", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            mAuth.createUserWithEmailAndPassword(mEditTextEmail.getText().toString(), password.getText().toString())
                                                    .addOnCompleteListener(task ->{
                                                        if(task.isSuccessful()){
                                                            mAuth.getCurrentUser().sendEmailVerification()
                                                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                        @Override
                                                                        public void onComplete(@NonNull Task<Void> task) {
                                                                            if(task.isSuccessful()){
                                                                                mStore.collection(collection)
                                                                                        .document(mAuth.getCurrentUser().getEmail())
                                                                                        .update("correoEnviado", true)
                                                                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                                            @Override
                                                                                            public void onSuccess(Void unused) {
                                                                                                Toast.makeText(MainActivity.this, "Se ha enviado un correo de confirmación. Favor de confirmar",
                                                                                                        Toast.LENGTH_SHORT).show();
                                                                                            }
                                                                                        });
                                                                            }else{
                                                                                Toast.makeText(MainActivity.this, task.getException().getMessage(),
                                                                                        Toast.LENGTH_SHORT).show();
                                                                            }
                                                                        }
                                                                    });
                                                        }else{
                                                            mAuth.signInWithEmailAndPassword(mEditTextEmail.getText().toString(), password.getText().toString())
                                                                    .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                                                                        @Override
                                                                        public void onSuccess(AuthResult authResult) {
                                                                            mAuth.getCurrentUser().delete();
                                                                            mAuth.createUserWithEmailAndPassword(mEditTextEmail.getText().toString(), mEditTextPassword.getText().toString())
                                                                                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                                                                        @Override
                                                                                        public void onComplete(@NonNull Task<AuthResult> task) {
                                                                                            if(task.isSuccessful()){
                                                                                                mAuth.getCurrentUser().sendEmailVerification()
                                                                                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                                            @Override
                                                                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                                                                if(task.isSuccessful()){
                                                                                                                    mStore.collection(collection)
                                                                                                                            .document(mAuth.getCurrentUser().getEmail())
                                                                                                                            .update("correoEnviado", true)
                                                                                                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                                                                                @Override
                                                                                                                                public void onSuccess(Void unused) {
                                                                                                                                    Toast.makeText(MainActivity.this, "Se ha enviado un correo de confirmación. Favor de confirmar",
                                                                                                                                            Toast.LENGTH_SHORT).show();
                                                                                                                                }
                                                                                                                            });
                                                                                                                }else{
                                                                                                                    Toast.makeText(MainActivity.this, task.getException().getMessage(),
                                                                                                                            Toast.LENGTH_SHORT).show();
                                                                                                                }
                                                                                                            }
                                                                                                        });
                                                                                            }
                                                                                        }
                                                                                    });
                                                                        }
                                                                    });
                                                        }
                                                    });
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
                                    password.addTextChangedListener(new TextWatcher() {
                                        @Override
                                        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                                        }
                                        @Override
                                        public void onTextChanged(CharSequence s, int start, int before, int count) {
                                            if(!confirm_password.getText().toString().isEmpty()){
                                                if(confirm_password.getText().toString().equals(password.getText().toString())) {
                                                    dialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(true);
                                                    confirm_password.setError(null);
                                                }else{
                                                    dialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(false);
                                                    confirm_password.setError(getString(R.string.no_coinciden));
                                                }
                                            }
                                        }
                                        @Override
                                        public void afterTextChanged(Editable s) {
                                            if(password.getText().toString().isEmpty()){
                                                dialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(false);
                                            }
                                        }
                                    });
                                    confirm_password.addTextChangedListener(new TextWatcher() {
                                        @Override
                                        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                                        }
                                        @Override
                                        public void onTextChanged(CharSequence s, int start, int before, int count) {
                                            if(!password.getText().toString().isEmpty()){
                                                if(confirm_password.getText().toString().equals(password.getText().toString())) {
                                                    dialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(true);
                                                    confirm_password.setError(null);
                                                }else{
                                                    dialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(false);
                                                    confirm_password.setError(getString(R.string.no_coinciden));
                                                }
                                            }
                                        }
                                        @Override
                                        public void afterTextChanged(Editable s) {
                                            if(confirm_password.getText().toString().isEmpty()) {
                                                dialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(false);
                                            }
                                        }
                                    });
                                }
                            }else{
                                mAuth.signInWithEmailAndPassword(mEditTextEmail.getText().toString(), mEditTextPassword.getText().toString())
                                        .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                                            @Override
                                            public void onSuccess(AuthResult authResult) {
                                                if(mAuth.getCurrentUser().isEmailVerified()){
                                                    Intent intent = new Intent(MainActivity.this, clase);
                                                    Toast.makeText(MainActivity.this, String.format(getString(R.string.bienvenido_usuario)+documentSnapshot.get("nombre")+"!"),
                                                            Toast.LENGTH_SHORT).show();
                                                    startActivity(intent);
                                                    finish();
                                                }else{
                                                    Toast.makeText(MainActivity.this, "Su correo no ha sido verificado, favor de verificar.",
                                                            Toast.LENGTH_LONG).show();
                                                }
                                            }
                                        });
                            }
                        }else{
                            Toast.makeText(MainActivity.this, getString(R.string.no_existe_usuario),
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }


    public void registerUser(View view){
        //Esto sirve para ingresar a la actividad RegistroUsuario
        Intent registro = new Intent(MainActivity.this, RegistroUsuario.class);
        startActivity(registro);
    }

}
