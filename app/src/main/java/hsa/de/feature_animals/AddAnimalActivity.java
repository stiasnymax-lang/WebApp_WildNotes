package hsa.de.feature_animals;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

import hsa.de.core.HomeActivity;
import hsa.de.core.LibraryActivity;
import hsa.de.core.MapActivity;
import hsa.de.R;
import hsa.de.core.SettingsActivity;

public class AddAnimalActivity extends AppCompatActivity {

    private static final String TAG = "AddAnimalActivity";
    private FirebaseFirestore db;
    private EditText name;
    private EditText info;
    private EditText enclosure;
    private Button create_animal;
    private NavigationBarView bottomNavigation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_animal);

        db = FirebaseFirestore.getInstance();

        name = findViewById(R.id.name);
        info = findViewById(R.id.info);
        enclosure = findViewById(R.id.enclosure);
        create_animal = findViewById(R.id.button);

        create_animal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveAnimalToFirestore();
            }
        });
        bottomNavigation = findViewById(R.id.bottomNavigation);
        bottomNavigation.setSelectedItemId(R.id.add);

        bottomNavigation.setOnItemSelectedListener(
                new NavigationBarView.OnItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                        int id = item.getItemId();

                        if (id == R.id.add) {
                            return true;
                        }
                        else if (id == R.id.home) {
                            startActivity(new Intent(getApplicationContext(), HomeActivity.class));
                            finish();
                            return true;

                        } else if (id == R.id.settings) {
                            startActivity(new Intent(getApplicationContext(), SettingsActivity.class));
                            finish();
                            return true;

                        } else if (id == R.id.map) {
                            startActivity(new Intent(getApplicationContext(), MapActivity.class));
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

    private void saveAnimalToFirestore() {
        String nameText = name.getText().toString().trim();
        String infoText = info.getText().toString().trim();
        String enclosureText = enclosure.getText().toString().trim();

        if (nameText.isEmpty()) {
            name.setError("Bitte Name eingeben");
            return;
        }
        if (infoText.isEmpty()) {
            info.setError("Bitte Info eingeben");
            return;
        }
        if (enclosureText.isEmpty()) {
            enclosure.setError("Bitte Gehege-Nr. eingeben");
            return;
        }

        int enclosureNr;
        try {
            enclosureNr = Integer.parseInt(enclosureText);
        } catch (NumberFormatException e) {
            enclosure.setError("Bitte eine Zahl eingeben");
            return;
        }

        Map<String, Object> animal = new HashMap<>();
        animal.put("name", nameText);
        animal.put("info", infoText);
        animal.put("enclosureNr", enclosureNr);

        db.collection("animals")
                .add(animal)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Log.d(TAG, "Document added with ID: " + documentReference.getId());
                        Toast.makeText(AddAnimalActivity.this,
                                "Tier gespeichert!", Toast.LENGTH_SHORT).show();

                        name.setText("");
                        info.setText("");
                        enclosure.setText("");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(Exception e) {
                        Log.w(TAG, "Error adding document", e);
                        Toast.makeText(AddAnimalActivity.this,
                                "Fehler beim Speichern: " + e.getMessage(),
                                Toast.LENGTH_LONG).show();
                    }
                });
    }
}
