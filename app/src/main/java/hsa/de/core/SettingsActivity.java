package hsa.de.core;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import hsa.de.MainActivity;
import hsa.de.R;
import hsa.de.feature_animals.AddAnimalActivity;

/**
 * Activity für die Einstellungen des Benutzers
 * Zeigt die eingeloggte E-Mail an und ermöglicht:
 * - Logout
 * - Zurücksetzen des Passworts per E-Mail
 */
public class SettingsActivity extends AppCompatActivity {

    // Firebase Authentication
    private FirebaseAuth auth;
    private FirebaseUser user;

    // UI-Elemente
    private Button button_logout;
    private Button button_reset;
    private TextView textView;
    private BottomNavigationView bottomNavigation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        // Firebase Auth initialisieren
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();

        button_logout = findViewById(R.id.logout);
        button_reset = findViewById(R.id.reset_password);
        textView = findViewById(R.id.user_details);

        bottomNavigation = findViewById(R.id.bottomNavigation);
        bottomNavigation.setSelectedItemId(R.id.settings);

        // Falls kein Benutzer eingeloggt ist → zurück zur Login-Seite
        if (user == null) {
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(intent);
            finish();
        } else {
            // E-Mail des eingeloggten Benutzers anzeigen
            textView.setText(user.getEmail());
        }

        /**
         * Button: Benutzer ausloggen
         * Meldet den User ab und leitet zur Login-Seite weiter
         */
        button_logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
                finish();
            }
        });

        /**
         * Button: Passwort zurücksetzen
         * Sendet eine Reset-E-Mail an die gespeicherte E-Mail-Adresse
         */
        button_reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (user != null && user.getEmail() != null) {
                    FirebaseAuth.getInstance()
                            .sendPasswordResetEmail(user.getEmail());

                    Toast.makeText(getApplicationContext(),
                            "Password-Reset-E-Mail gesendet.",
                            Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(getApplicationContext(),
                            "Keine E-Mail verfügbar.",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });

        /**
         * Bottom Navigation:
         * Navigation zwischen den Hauptbereichen der App
         */
        bottomNavigation.setOnItemSelectedListener(
                new NavigationBarView.OnItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                        int id = item.getItemId();

                        // Aktuelle Seite (Settings)
                        if (id == R.id.settings) {
                            return true;

                        } else if (id == R.id.home) {
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
