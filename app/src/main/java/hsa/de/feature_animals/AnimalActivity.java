package hsa.de.feature_animals;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

import hsa.de.core.HomeActivity;
import hsa.de.core.LibraryActivity;
import hsa.de.R;
import hsa.de.core.MapActivity;
import hsa.de.core.SettingsActivity;
import hsa.de.feature_events.AddEventActivity;
import hsa.de.feature_events.Event;
import hsa.de.feature_events.EventActivity;
import hsa.de.feature_events.EventAdapter;

/**
 * Activity zur Anzeige eines einzelnen Tieres.
 * Zeigt die Tierdetails (Name, Info, Gehege) sowie
 * eine Liste aller zugehörigen Events an.
 */
public class AnimalActivity extends AppCompatActivity {

    private RecyclerView recyclerView;

    private EventAdapter adapter;

    private final ArrayList<Event> events = new ArrayList<>();

    // Buttons für Aktionen am Tier
    private Button btDeleteAnimal;
    private Button btEditAnimal;
    private Button btAddEvents;

    // TextViews für Tierinformationen
    private TextView animal_name;
    private TextView animal_info;
    private TextView animal_enclosure;

    private FirebaseFirestore db;

    private String animalId;

    private BottomNavigationView bottomNavigation;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_animal);

        db = FirebaseFirestore.getInstance();

        animal_name = findViewById(R.id.animal_name);
        animal_info = findViewById(R.id.animal_info);
        animal_enclosure = findViewById(R.id.animal_enclosure);

        // animalId aus dem Intent holen
        // Wird benötigt, um das richtige Tier aus Firestore zu laden
        animalId = getIntent().getStringExtra("animalId");
        if (animalId == null || animalId.isEmpty()) {
            Toast.makeText(this,
                    "Fehler: animalId fehlt",
                    Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        recyclerView = findViewById(R.id.events_recycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // EventAdapter initialisieren
        adapter = new EventAdapter(animalId, events, new EventAdapter.OnEventClickListener() {
            @Override
            public void onEventClick(String aId, Event event) {
                // Öffnet die Detailansicht eines Events
                Intent intent = new Intent(AnimalActivity.this, EventActivity.class);
                intent.putExtra("animalId", aId);
                intent.putExtra("eventId", event.id);
                startActivity(intent);
            }
        });
        recyclerView.setAdapter(adapter);

        btDeleteAnimal = findViewById(R.id.delete_animal);
        btEditAnimal = findViewById(R.id.edit_animal);
        btAddEvents = findViewById(R.id.add_event);

        // Button: Tier löschen
        btDeleteAnimal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteAnimal(animalId);
            }
        });

        // Button: Tier bearbeiten
        btEditAnimal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AnimalActivity.this, EditAnimalActivity.class);
                intent.putExtra("animalId", animalId);
                startActivity(intent);
            }
        });

        // Button: Neues Event hinzufügen
        btAddEvents.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AnimalActivity.this, AddEventActivity.class);
                intent.putExtra("animalId", animalId);
                startActivity(intent);
            }
        });

        // Tierdaten (Name, Info, Gehege) aus Firestore laden
        loadAnimalDetails(animalId);

        // Events des Tieres laden
        loadEventsFirestore(animalId);

        // Bottom Navigation konfigurieren
        bottomNavigation = findViewById(R.id.bottomNavigation);
        bottomNavigation.setSelectedItemId(R.id.home);

        bottomNavigation.setOnItemSelectedListener(
                new NavigationBarView.OnItemSelectedListener() {
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
                }
        );
    }

    /**
     * Lädt die Tierdetails aus Firestore
     * und setzt die Werte in die TextViews
     */
    private void loadAnimalDetails(String animalId) {
        db.collection("animals")
                .document(animalId)
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot doc) {

                        // Prüfen, ob das Tier existiert
                        if (doc == null || !doc.exists()) {
                            Toast.makeText(AnimalActivity.this,
                                    "Tier nicht gefunden",
                                    Toast.LENGTH_LONG).show();
                            finish();
                            return;
                        }

                        // Felder aus Firestore lesen
                        String name = doc.getString("name");
                        String info = doc.getString("info");

                        // enclosureNr ist in Firestore als Number (Long) gespeichert
                        Long enclosureNrLong = doc.getLong("enclosureNr");

                        // Daten in die TextViews setzen
                        animal_name.setText(name != null ? name : "");
                        animal_info.setText(info != null ? info : "");

                        if (enclosureNrLong != null) {
                            animal_enclosure.setText("Gehege: " + enclosureNrLong);
                        } else {
                            animal_enclosure.setText("Gehege: ");
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(AnimalActivity.this,
                                "Fehler beim Laden des Tiers: " + e.getMessage(),
                                Toast.LENGTH_LONG).show();
                    }
                });
    }

    /**
     * Lädt alle Events des Tieres aus Firestore
     * und aktualisiert die RecyclerView
     */
    private void loadEventsFirestore(String animalId) {
        db.collection("animals")
                .document(animalId)
                .collection("events")
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot querySnapshot) {

                        // Alte Daten löschen
                        events.clear();

                        // Neue Events aus Firestore hinzufügen
                        for (QueryDocumentSnapshot doc : querySnapshot) {
                            Event e = doc.toObject(Event.class);
                            e.id = doc.getId();
                            events.add(e);
                        }

                        // RecyclerView aktualisieren
                        adapter.notifyDataSetChanged();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(AnimalActivity.this,
                                "Fehler beim Laden: " + e.getMessage(),
                                Toast.LENGTH_LONG).show();
                    }
                });
    }

    /**
     * Löscht das Tier-Dokument aus Firestore
     * Hinweis: Subcollections (z. B. events) werden dabei nicht automatisch gelöscht
     */
    private void deleteAnimal(String animalId) {
        db.collection("animals")
                .document(animalId)
                .delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(AnimalActivity.this,
                                "Animal gelöscht",
                                Toast.LENGTH_SHORT).show();
                        finish(); // zurück zur vorherigen Activity
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(AnimalActivity.this,
                                "Fehler beim Löschen: " + e.getMessage(),
                                Toast.LENGTH_LONG).show();
                    }
                });
    }

    /**
     * Wird aufgerufen, wenn die Activity wieder sichtbar wird
     * Lädt die Event-Liste neu, z. B. nach dem Hinzufügen oder Bearbeiten eines Events
     */
    @Override
    protected void onResume() {
        super.onResume();
        loadEventsFirestore(animalId);
    }
}
