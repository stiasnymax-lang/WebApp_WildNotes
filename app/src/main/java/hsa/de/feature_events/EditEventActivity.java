package hsa.de.feature_events;

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
 * Activity zum Bearbeiten eines bestehenden Events
 * Das Event gehört zu einem bestimmten Tier und wird aus
 * animals/{animalId}/events/{eventId} geladen und aktualisiert
 */
public class EditEventActivity extends AppCompatActivity {

    private static final String TAG = "EditEventActivity";

    private FirebaseFirestore db;

    private EditText name;
    private EditText info;
    private EditText date;

    private Button save_event;

    // Firestore-IDs für Tier und Event
    private String animalId;
    private String eventId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_event);

        db = FirebaseFirestore.getInstance();

        name = findViewById(R.id.title);
        info = findViewById(R.id.description);
        date = findViewById(R.id.date);
        save_event = findViewById(R.id.button);

        // animalId aus dem Intent holen
        animalId = getIntent().getStringExtra("animalId");
        if (animalId == null || animalId.isEmpty()) {
            Toast.makeText(this,
                    "Fehler: animalId fehlt",
                    Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        // eventId aus dem Intent holen
        eventId = getIntent().getStringExtra("eventId");
        if (eventId == null || eventId.isEmpty()) {
            Toast.makeText(this,
                    "Fehler: eventId fehlt",
                    Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        // Aktuelle Event-Daten aus Firestore laden
        loadEvent();

        // Klick auf "Speichern"
        save_event.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateEvent();
            }
        });
    }

    /**
     * Lädt das Event-Dokument aus Firestore
     * und setzt die Werte in die Eingabefelder
     */
    private void loadEvent() {
        db.collection("animals")
                .document(animalId)
                .collection("events")
                .document(eventId)
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot document) {

                        // Prüfen, ob das Event existiert
                        if (document.exists()) {

                            // Felder aus Firestore in die EditTexts setzen
                            name.setText(document.getString("name"));
                            info.setText(document.getString("info"));
                            date.setText(document.getString("date"));

                        } else {
                            Toast.makeText(EditEventActivity.this,
                                    "Event nicht gefunden",
                                    Toast.LENGTH_LONG).show();
                            finish();
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(Exception e) {
                        Toast.makeText(EditEventActivity.this,
                                "Fehler beim Laden: " + e.getMessage(),
                                Toast.LENGTH_LONG).show();
                    }
                });
    }

    /**
     * Validiert die Eingaben und aktualisiert das Event
     * im Firestore-Dokument
     */
    private void updateEvent() {

        // Texte aus den Eingabefeldern lesen
        String nameText = name.getText().toString().trim();
        String infoText = info.getText().toString().trim();
        String dateText = date.getText().toString().trim();

        // Eingabevalidierung
        if (nameText.isEmpty()) {
            name.setError("Bitte Namen eingeben");
            return;
        }
        if (infoText.isEmpty()) {
            info.setError("Bitte Info eingeben");
            return;
        }
        if (dateText.isEmpty()) {
            date.setError("Bitte Datum eingeben");
            return;
        }

        // Event-Daten vorbereiten
        Map<String, Object> event = new HashMap<>();
        event.put("name", nameText);
        event.put("info", infoText);
        event.put("date", dateText);

        // Event in Firestore aktualisieren
        db.collection("animals")
                .document(animalId)
                .collection("events")
                .document(eventId)
                .update(event)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Toast.makeText(EditEventActivity.this,
                                "Event aktualisiert!",
                                Toast.LENGTH_SHORT).show();
                        finish(); // zurück zur vorherigen Activity
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(Exception e) {
                        Log.w(TAG, "Error updating document", e);
                        Toast.makeText(EditEventActivity.this,
                                "Fehler beim Speichern: " + e.getMessage(),
                                Toast.LENGTH_LONG).show();
                    }
                });
    }
}
