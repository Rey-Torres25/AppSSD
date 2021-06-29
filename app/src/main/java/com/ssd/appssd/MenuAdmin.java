package com.ssd.appssd;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;
import android.view.MenuItem;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.ssd.appssd.fragmentsAdmin.ChatFragmentAdmin;
import com.ssd.appssd.fragmentsAdmin.ListUsers;
import com.ssd.appssd.fragmentsAdmin.PerfilFragmentAdmin;
import com.ssd.appssd.fragmentsAdmin.RecordsFragmentAdmin;

public class MenuAdmin extends AppCompatActivity {

    BottomNavigationView bottomNavigationView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_admin);
        showSelectedFragment(new RecordsFragmentAdmin());
        bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                if(item.getItemId() == R.id.registros){
                    showSelectedFragment(new RecordsFragmentAdmin());
                }

                if(item.getItemId() == R.id.usuarios){
                    showSelectedFragment(new ListUsers());
                }

                if(item.getItemId() == R.id.chat){
                    showSelectedFragment(new ChatFragmentAdmin());
                }

                if(item.getItemId() == R.id.perfil){
                    showSelectedFragment(new PerfilFragmentAdmin());
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