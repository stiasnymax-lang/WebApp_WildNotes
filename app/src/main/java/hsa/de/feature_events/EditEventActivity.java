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

public class EditEventActivity extends AppCompatActivity {

    private static final String TAG = "EditEventActivity";

    private FirebaseFirestore db;
    private EditText title;
    private EditText description;
    private EditText date;
    private Button save_event;

    private String eventId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_event);

        db = FirebaseFirestore.getInstance();

        title = findViewById(R.id.title);
        description = findViewById(R.id.description);
        date = findViewById(R.id.date);
        save_event = findViewById(R.id.button);

        eventId = getIntent().getStringExtra("eventId");

        if (eventId == null) {
            Toast.makeText(this, "Kein Event zum Bearbeiten gefunden", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        loadEvent();

        save_event.setText("Änderungen speichern");

        save_event.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateEvent();
            }
        });
    }

    // 🔽 Event laden
    private void loadEvent() {
        db.collection("events")
                .document(eventId)
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot document) {
                        if (document.exists()) {
                            title.setText(document.getString("title"));
                            description.setText(document.getString("description"));
                            date.setText(document.getString("date"));
                        } else {
                            Toast.makeText(EditEventActivity.this,
                                    "Event nicht gefunden", Toast.LENGTH_LONG).show();
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

    // 🔼 Event aktualisieren
    private void updateEvent() {
        String titleText = title.getText().toString().trim();
        String descriptionText = description.getText().toString().trim();
        String dateText = date.getText().toString().trim();

        if (titleText.isEmpty()) {
            title.setError("Bitte Titel eingeben");
            return;
        }
        if (descriptionText.isEmpty()) {
            description.setError("Bitte Beschreibung eingeben");
            return;
        }
        if (dateText.isEmpty()) {
            date.setError("Bitte Datum eingeben");
            return;
        }

        Map<String, Object> event = new HashMap<>();
        event.put("title", titleText);
        event.put("description", descriptionText);
        event.put("date", dateText);

        db.collection("events")
                .document(eventId)
                .update(event)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Toast.makeText(EditEventActivity.this,
                                "Event aktualisiert!", Toast.LENGTH_SHORT).show();
                        finish();
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
