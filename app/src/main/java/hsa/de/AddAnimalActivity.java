package hsa.de;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class AddAnimalActivity extends AppCompatActivity {
    private static final String TAG = "AddAnimalActivity";
    private FirebaseFirestore db;
    private EditText name;
    private EditText info;
    private EditText enclosure;
    private Button create_animal;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_animal); // <- Layout-Dateiname prüfen!

        db = FirebaseFirestore.getInstance();

        name = findViewById(R.id.name);
        info = findViewById(R.id.info);
        enclosure = findViewById(R.id.enclosure);
        create_animal = findViewById(R.id.button);

        create_animal.setOnClickListener(v -> saveAnimalToFirestore());
    }

    private void saveAnimalToFirestore() {
        String name = this.name.getText().toString().trim();
        String info = this.info.getText().toString().trim();
        String enclosureText = enclosure.getText().toString().trim();

        //Validierung ob was eingetragen
        if (name.isEmpty()) {
            this.name.setError("Bitte Name eingeben");
            return;
        }
        if (info.isEmpty()) {
            this.info.setError("Bitte Info eingeben");
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
        animal.put("name", name);
        animal.put("info", info);
        animal.put("enclosureNr", enclosureNr);

        db.collection("animals")
                .add(animal)
                .addOnSuccessListener((DocumentReference documentReference) -> {
                    Log.d(TAG, "Document added with ID: " + documentReference.getId());
                    Toast.makeText(this, "Tier gespeichert!", Toast.LENGTH_SHORT).show();

                    // Optional: Felder leeren
                    this.name.setText("");
                    this.info.setText("");
                    enclosure.setText("");
                })
                .addOnFailureListener(e -> {
                    Log.w(TAG, "Error adding document", e);
                    Toast.makeText(this, "Fehler beim Speichern: " + e.getMessage(), Toast.LENGTH_LONG).show();
                });
    }
}
