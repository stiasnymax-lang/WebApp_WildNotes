package hsa.de.feature_events;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

import hsa.de.R;

/**
 * Activity zum Anlegen eines neuen Events für ein bestimmtes Tier
 * Das Event wird unter animals/{animalId}/events in Firestore gespeichert
 */
public class AddEventActivity extends AppCompatActivity {

    private static final String TAG = "AddEventActivity";

    private FirebaseFirestore db;

    private EditText Name;
    private EditText Info;
    private EditText Date;

    private Button create_event;

    private String animalId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_event);

        db = FirebaseFirestore.getInstance();

        // animalId aus dem Intent holen
        animalId = getIntent().getStringExtra("animalId");

        // Sicherheitsprüfung: ohne animalId kann kein Event angelegt werden
        if (animalId == null || animalId.isEmpty()) {
            Toast.makeText(this,
                    "Fehler: animalId fehlt",
                    Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        Name = findViewById(R.id.event_name);
        Info = findViewById(R.id.event_info);
        Date = findViewById(R.id.event_date);
        create_event = findViewById(R.id.button);

        // Klick auf "Event erstellen"
        create_event.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveEventToFirestore();
            }
        });
    }

    /**
     * Liest die Eingaben aus den Feldern,
     * validiert sie und speichert das Event in Firestore
     */
    private void saveEventToFirestore() {

        // Texte aus den Eingabefeldern lesen
        String nameText = Name.getText().toString().trim();
        String infoText = Info.getText().toString().trim();
        String dateText = Date.getText().toString().trim();

        // Eingabevalidierung
        if (nameText.isEmpty()) {
            Name.setError("Bitte Namen eingeben");
            return;
        }
        if (infoText.isEmpty()) {
            Info.setError("Bitte Info eingeben");
            return;
        }
        if (dateText.isEmpty()) {
            Date.setError("Bitte Datum eingeben");
            return;
        }

        // Event-Daten vorbereiten
        Map<String, Object> event = new HashMap<>();
        event.put("name", nameText);
        event.put("info", infoText);
        event.put("date", dateText);

        // Event unter animals/{animalId}/events speichern
        db.collection("animals")
                .document(animalId)
                .collection("events")
                .add(event)
                .addOnSuccessListener(documentReference -> {
                    Log.d(TAG, "Event added with ID: " + documentReference.getId());
                    Toast.makeText(AddEventActivity.this,
                            "Event gespeichert!",
                            Toast.LENGTH_SHORT).show();
                    finish(); // zurück zur vorherigen Activity
                })
                .addOnFailureListener(e -> {
                    Log.w(TAG, "Error adding event", e);
                    Toast.makeText(AddEventActivity.this,
                            "Fehler beim Speichern: " + e.getMessage(),
                            Toast.LENGTH_LONG).show();
                });
    }
}
