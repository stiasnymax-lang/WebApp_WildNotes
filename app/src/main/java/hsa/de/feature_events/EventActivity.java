package hsa.de.feature_events;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import hsa.de.core.MapActivity;
import hsa.de.feature_animals.AddAnimalActivity;
import hsa.de.core.HomeActivity;
import hsa.de.core.LibraryActivity;
import hsa.de.R;
import hsa.de.core.SettingsActivity;

/**
 * Activity zur Anzeige eines einzelnen Events
 * Das Event gehört zu einem Tier und wird aus
 * animals/{animalId}/events/{eventId} geladen
 * Von hier aus kann das Event bearbeitet oder gelöscht werden
 */
public class EventActivity extends AppCompatActivity {

    private static final String TAG = "EventActivity";

    private FirebaseFirestore db;

    private TextView tvName;
    private TextView tvInfo;
    private TextView tvDate;

    private Button btEditEvent;
    private Button btDeleteEvent;

    private NavigationBarView bottomNavigation;

    // Firestore-IDs
    private String animalId;
    private String eventId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event);

        db = FirebaseFirestore.getInstance();

        tvName = findViewById(R.id.event_name);
        tvInfo = findViewById(R.id.event_info);
        tvDate = findViewById(R.id.event_date);

        btEditEvent = findViewById(R.id.edit_event);
        btDeleteEvent = findViewById(R.id.delete_event);

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

        // Event-Daten laden
        loadEventById(animalId, eventId);

        // Klick auf "Event bearbeiten"
        btEditEvent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(EventActivity.this, EditEventActivity.class);
                intent.putExtra("animalId", animalId);
                intent.putExtra("eventId", eventId);
                startActivity(intent);
            }
        });

        // Klick auf "Event löschen"
        btDeleteEvent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteEvent(animalId, eventId);
            }
        });

        // Bottom Navigation konfigurieren
        bottomNavigation = findViewById(R.id.bottomNavigation);
        bottomNavigation.setSelectedItemId(R.id.home);

        bottomNavigation.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();

                if (id == R.id.settings) {
                    startActivity(new Intent(getApplicationContext(), SettingsActivity.class));
                    finish();
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
        });
    }

    /**
     * Wird aufgerufen, wenn die Activity wieder sichtbar wird
     * Lädt das Event neu, z. B. nach dem Bearbeiten
     */
    @Override
    protected void onResume() {
        super.onResume();
        if (animalId != null && eventId != null) {
            loadEventById(animalId, eventId);
        }
    }

    /**
     * Lädt ein einzelnes Event aus Firestore
     * und setzt die Daten in die TextViews
     */
    private void loadEventById(String animalId, String eventId) {
        db.collection("animals")
                .document(animalId)
                .collection("events")
                .document(eventId)
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot doc) {

                        // Prüfen, ob das Event existiert
                        if (!doc.exists()) {
                            Toast.makeText(EventActivity.this,
                                    "Event nicht gefunden",
                                    Toast.LENGTH_LONG).show();
                            finish();
                            return;
                        }

                        // Felder aus Firestore lesen
                        String name = doc.getString("name");
                        String info = doc.getString("info");
                        String date = doc.getString("date");

                        // Daten in die TextViews setzen
                        tvName.setText(name != null ? name : "");
                        tvInfo.setText(info != null ? info : "");
                        tvDate.setText(date != null ? date : "");

                        Log.d(TAG, "Loaded event: " + doc.getId());
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(Exception e) {
                        Log.w(TAG, "Error getting event", e);
                        Toast.makeText(EventActivity.this,
                                "Fehler beim Laden: " + e.getMessage(),
                                Toast.LENGTH_LONG).show();
                    }
                });
    }

    /**
     * Löscht das Event aus Firestore
     * (animals/{animalId}/events/{eventId})
     */
    private void deleteEvent(String animalId, String eventId) {
        db.collection("animals")
                .document(animalId)
                .collection("events")
                .document(eventId)
                .delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Toast.makeText(EventActivity.this,
                                "Event gelöscht",
                                Toast.LENGTH_SHORT).show();
                        finish(); // zurück zur vorherigen Activity
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(Exception e) {
                        Log.w(TAG, "Error deleting event", e);
                        Toast.makeText(EventActivity.this,
                                "Fehler beim Löschen: " + e.getMessage(),
                                Toast.LENGTH_LONG).show();
                    }
                });
    }
}
