package hsa.de.feature_animals;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

import hsa.de.R;

public class EditAnimalActivity extends AppCompatActivity {

    private static final String TAG = "EditAnimalActivity";

    private FirebaseFirestore db;
    private EditText name;
    private EditText info;
    private EditText enclosure;
    private Button save_animal;

    private String animalId; // Firestore Document-ID

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_animal);

        db = FirebaseFirestore.getInstance();

        name = findViewById(R.id.name);
        info = findViewById(R.id.info);
        enclosure = findViewById(R.id.enclosure);
        save_animal = findViewById(R.id.button);

        // ID aus Intent holen
        animalId = getIntent().getStringExtra("animalId");

        if (animalId == null) {
            Toast.makeText(this, "Kein Tier zum Bearbeiten gefunden", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        // Tierdaten laden
        loadAnimal();

        save_animal.setText("Änderungen speichern");

        save_animal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateAnimal();
            }
        });
    }

    // 🔽 Tier aus Firestore laden
    private void loadAnimal() {
        db.collection("animals")
                .document(animalId)
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot document) {
                        if (document.exists()) {
                            name.setText(document.getString("name"));
                            info.setText(document.getString("info"));

                            Long enclosureNr = document.getLong("enclosureNr");
                            if (enclosureNr != null) {
                                enclosure.setText(String.valueOf(enclosureNr));
                            }
                        } else {
                            Toast.makeText(EditAnimalActivity.this,
                                    "Tier nicht gefunden", Toast.LENGTH_LONG).show();
                            finish();
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(Exception e) {
                        Toast.makeText(EditAnimalActivity.this,
                                "Fehler beim Laden: " + e.getMessage(),
                                Toast.LENGTH_LONG).show();
                    }
                });
    }

    // 🔼 Tier aktualisieren
    private void updateAnimal() {
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
                .document(animalId)
                .update(animal)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Toast.makeText(EditAnimalActivity.this,
                                "Tier aktualisiert!", Toast.LENGTH_SHORT).show();
                        finish(); // zurück zur MainActivity
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(Exception e) {
                        Log.w(TAG, "Error updating document", e);
                        Toast.makeText(EditAnimalActivity.this,
                                "Fehler beim Speichern: " + e.getMessage(),
                                Toast.LENGTH_LONG).show();
                    }
                });
    }
}
