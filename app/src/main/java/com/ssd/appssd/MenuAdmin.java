package com.ssd.appssd;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.ssd.appssd.fragmentsAdmin.ChatFragmentAdmin;
import com.ssd.appssd.fragmentsAdmin.ListUsers;
import com.ssd.appssd.fragmentsAdmin.PerfilFragmentAdmin;
import com.ssd.appssd.fragmentsAdmin.RecordsFragmentAdmin;

public class MenuAdmin extends AppCompatActivity {
    private Toolbar toolbar;
    private BottomNavigationView bottomNavigationView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_admin);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottom_navigation);
        if(bottomNavigationView.getSelectedItemId() == R.id.registros) {
            bottomNavigationView.setSelectedItemId(R.id.registros);
            showSelectedFragment(new RecordsFragmentAdmin());
            getSupportActionBar().setTitle(String.format(getString(R.string.registros_toolbar)));
        }
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                if(item.getItemId() == R.id.registros){
                    showSelectedFragment(new RecordsFragmentAdmin());
                    getSupportActionBar().setTitle(String.format(getString(R.string.registros_toolbar)));
                }

                if(item.getItemId() == R.id.usuarios){
                    showSelectedFragment(new ListUsers());
                    getSupportActionBar().setTitle(String.format(getString(R.string.usuarios_toolbar)));
                }

                if(item.getItemId() == R.id.chat){
                    showSelectedFragment(new ChatFragmentAdmin());
                    getSupportActionBar().setTitle(String.format(getString(R.string.chat_toolbar)));
                }

                if(item.getItemId() == R.id.perfil){
                    showSelectedFragment(new PerfilFragmentAdmin());
                    getSupportActionBar().setTitle(String.format(getString(R.string.perfil_toolbar)));
                }
                return true;
            }
        });

        onCreateOptionsMenu(toolbar.getMenu());
    }

    private void showSelectedFragment(Fragment fragment){
        getSupportFragmentManager().beginTransaction().replace(R.id.container, fragment)
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                .commit();
    }


}