package hsa.de.core;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

import hsa.de.R;
import hsa.de.feature_animals.AddAnimalActivity;

public class HomeActivity extends AppCompatActivity {

    private BottomNavigationView bottomNavigation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home); // z.B. activity_main

        bottomNavigation = findViewById(R.id.bottomNavigation);
        bottomNavigation.setSelectedItemId(R.id.home);

        bottomNavigation.setOnItemSelectedListener(
                new NavigationBarView.OnItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                        int id = item.getItemId();

                        if (id == R.id.home) {
                            return true;

                        } else if (id == R.id.map) {
                            startActivity(new Intent(getApplicationContext(), MapActivity.class));
                            finish();
                            return true;

                        } else if (id == R.id.add) {
                            startActivity(new Intent(getApplicationContext(), AddAnimalActivity.class));
                            finish();
                            return true;

                        } else if (id == R.id.library) {
                            startActivity(new Intent(getApplicationContext(), LibraryActivity.class));
                            finish();
                            return true;

                        } else if (id == R.id.settings) {
                            startActivity(new Intent(getApplicationContext(), SettingsActivity.class));
                            finish();
                            return true;
                        }

                        return false;
                    }
                }
        );
    }
}

