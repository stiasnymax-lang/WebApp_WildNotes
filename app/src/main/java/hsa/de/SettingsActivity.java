package hsa.de;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

public class SettingsActivity extends AppCompatActivity {

    FirebaseAuth auth;
    Button button_logout;
    Button button_reset;
    TextView textView;
    FirebaseUser user;
    private BottomNavigationView bottomNavigation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        auth = FirebaseAuth.getInstance();
        button_logout = findViewById(R.id.logout);
        textView = findViewById(R.id.user_details);
        user = auth.getCurrentUser();
        button_reset = findViewById(R.id.reset_password);
        bottomNavigation = findViewById(R.id.bottomNavigation);
        bottomNavigation.setSelectedItemId(R.id.settings);


        if (user == null) {
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(intent);
            finish();
        }
        else {
            textView.setText(user.getEmail());
        }
        button_logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
                finish();
            }
        });

        button_reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (user != null && user.getEmail() != null) {
                    FirebaseAuth.getInstance().sendPasswordResetEmail(user.getEmail());
                    Toast.makeText(getApplicationContext(), "Password Reset E-Mail gesendet.", Toast.LENGTH_SHORT).show();
                    finish();
                }
                else{
                    Toast.makeText(getApplicationContext(), "Keine E-Mail verfügbar.", Toast.LENGTH_SHORT).show();
                }
            }

        });

        bottomNavigation.setOnItemSelectedListener(
                new NavigationBarView.OnItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                        int id = item.getItemId();

                        if (id == R.id.settings) {
                            return true;
                        }
                        else if (id == R.id.home) {
                            startActivity(new Intent(getApplicationContext(), HomeActivity.class));
                            finish();
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

                        }

                        return false;
                    }
                }
        );
    }


}
