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

/**
 * Activity zum Bearbeiten eines bestehenden Tieres
 * Lädt die aktuellen Tierdaten aus Firestore und erlaubt deren Änderung
 */
public class EditAnimalActivity extends AppCompatActivity {

    private static final String TAG = "EditAnimalActivity";

    private FirebaseFirestore db;

    private EditText name;
    private EditText info;
    private EditText enclosure;

    private Button save_animal;

    private String animalId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_animal);

        // Firestore initialisieren
        db = FirebaseFirestore.getInstance();

        // Views verbinden
        name = findViewById(R.id.name);
        info = findViewById(R.id.info);
        enclosure = findViewById(R.id.enclosure);
        save_animal = findViewById(R.id.button);

        // Tier-ID aus dem Intent holen
        animalId = getIntent().getStringExtra("animalId");

        // Sicherheitsprüfung: ohne ID kein Bearbeiten möglich
        if (animalId == null) {
            Toast.makeText(this,
                    "Kein Tier zum Bearbeiten gefunden",
                    Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        // Aktuelle Tierdaten aus Firestore laden
        loadAnimal();


        // Klick auf "Speichern"
        save_animal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateAnimal();
            }
        });
    }

    /**
     * Lädt das Tier-Dokument aus Firestore
     * und setzt die Werte in die Eingabefelder
     */
    private void loadAnimal() {
        db.collection("animals")
                .document(animalId)
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot document) {

                        // Prüfen, ob das Dokument existiert
                        if (document.exists()) {

                            // Felder aus Firestore in die EditTexts setzen
                            name.setText(document.getString("name"));
                            info.setText(document.getString("info"));

                            // enclosureNr ist in Firestore als Number (Long) gespeichert
                            Long enclosureNr = document.getLong("enclosureNr");
                            if (enclosureNr != null) {
                                enclosure.setText(String.valueOf(enclosureNr));
                            }

                        } else {
                            Toast.makeText(EditAnimalActivity.this,
                                    "Tier nicht gefunden",
                                    Toast.LENGTH_LONG).show();
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

    /**
     * Validiert die Eingaben und aktualisiert das Tier
     * im Firestore-Dokument.
     */
    private void updateAnimal() {

        // Texte aus den Eingabefeldern lesen
        String nameText = name.getText().toString().trim();
        String infoText = info.getText().toString().trim();
        String enclosureText = enclosure.getText().toString().trim();

        // Eingabevalidierung
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

        // Gehege-Nummer in int umwandeln
        int enclosureNr;
        try {
            enclosureNr = Integer.parseInt(enclosureText);
        } catch (NumberFormatException e) {
            enclosure.setError("Bitte eine Zahl eingeben");
            return;
        }

        // Neue Tierdaten als Map vorbereiten
        Map<String, Object> animal = new HashMap<>();
        animal.put("name", nameText);
        animal.put("info", infoText);
        animal.put("enclosureNr", enclosureNr);

        // Firestore-Dokument aktualisieren
        db.collection("animals")
                .document(animalId)
                .update(animal)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Toast.makeText(EditAnimalActivity.this,
                                "Tier aktualisiert!",
                                Toast.LENGTH_SHORT).show();
                        finish(); // zurück zur vorherigen Activity
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
