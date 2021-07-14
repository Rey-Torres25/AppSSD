package com.ssd.appssd;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;
import android.view.MenuItem;


import com.google.android.material.bottomnavigation.BottomNavigationView;

import com.ssd.appssd.fragmentsAdmin.RecordsFragmentAdmin;
import com.ssd.appssd.fragmentsUsuario.ChatFragment;
import com.ssd.appssd.fragmentsUsuario.PerfilFragment;
import com.ssd.appssd.fragmentsUsuario.RecordsFragment;

public class MenuUsuario extends AppCompatActivity {

    BottomNavigationView bottomNavigationView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_usuario);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottom_navigation);
        if(bottomNavigationView.getSelectedItemId() == R.id.registros) {
            bottomNavigationView.setSelectedItemId(R.id.registros);
            showSelectedFragment(new RecordsFragment());
            getSupportActionBar().setTitle("Registros");
        }
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                if(item.getItemId() == R.id.registros){
                    showSelectedFragment(new RecordsFragment());
                    getSupportActionBar().setTitle("Registros");
                }

                if(item.getItemId() == R.id.chat){
                    showSelectedFragment(new ChatFragment());
                    getSupportActionBar().setTitle("Chat");
                }

                if(item.getItemId() == R.id.perfil){
                    showSelectedFragment(new PerfilFragment());
                    getSupportActionBar().setTitle("Perfil");
                }
                return true;
            }
        });
    }

    private void showSelectedFragment(Fragment fragment){
        getSupportFragmentManager().beginTransaction().replace(R.id.container, fragment)
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                .commit();
    }
}