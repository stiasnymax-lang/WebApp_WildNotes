package hsa.de;

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

public class AnimalActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private EventAdapter adapter;
    private final ArrayList<AnimalEvent> events = new ArrayList<>();

    private Button btDeleteAnimal;
    private Button btEditAnimal;

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

        animalId = getIntent().getStringExtra("animalId");
        if (animalId == null || animalId.isEmpty()) {
            Toast.makeText(this, "Fehler: animalId fehlt", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        recyclerView = findViewById(R.id.events_recycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        animal_name = findViewById(R.id.animal_name);
        animal_info = findViewById(R.id.animal_info);
        animal_enclosure = findViewById(R.id.animal_enclosure);

        adapter = new EventAdapter(events, new EventAdapter.OnEventClickListener() {
            @Override
            public void onEventClick(AnimalEvent event) {
                Intent intent = new Intent(AnimalActivity.this, EventActivity.class);
                intent.putExtra("ANIMAL_ID", animalId);
                intent.putExtra("EVENT_ID", event.id);
                startActivity(intent);
            }
        });

        recyclerView.setAdapter(adapter);

        btDeleteAnimal = findViewById(R.id.delete_animal);
        btEditAnimal = findViewById(R.id.edit_animal);

        btDeleteAnimal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteAnimal(animalId);
            }
        });

        btEditAnimal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AnimalActivity.this, EditAnimalActivity.class);
                intent.putExtra("animalId", animalId);
                startActivity(intent);
            }
        });

        // ✅ NEU: Animal-Daten (Name/Info/Gehege) laden
        loadAnimalDetails(animalId);

        // Events laden
        loadEventsFirestore(animalId);

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
                        }
                        else if (id == R.id.home) {
                            startActivity(new Intent(getApplicationContext(), HomeActivity.class));
                            finish();
                            return true;

                        } else if (id == R.id.settings) {
                            startActivity(new Intent(getApplicationContext(), SettingsActivity.class));
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

    // ✅ NEU: Animal-Dokument laden und TextViews setzen
    private void loadAnimalDetails(String animalId) {
        db.collection("animals")
                .document(animalId)
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot doc) {
                        if (doc == null || !doc.exists()) {
                            Toast.makeText(AnimalActivity.this, "Tier nicht gefunden", Toast.LENGTH_LONG).show();
                            finish();
                            return;
                        }

                        String name = doc.getString("name");
                        String info = doc.getString("info");

                        // enclosureNr kann als Long kommen, wenn Firestore es so speichert
                        Long enclosureNrLong = doc.getLong("enclosureNr");

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

    private void loadEventsFirestore(String animalId) {
        db.collection("animals")
                .document(animalId)
                .collection("events")
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot querySnapshot) {
                        events.clear();
                        for (QueryDocumentSnapshot doc : querySnapshot) {
                            AnimalEvent e = doc.toObject(AnimalEvent.class);
                            e.id = doc.getId();
                            events.add(e);
                        }
                        adapter.notifyDataSetChanged();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(
                                AnimalActivity.this,
                                "Fehler beim Laden: " + e.getMessage(),
                                Toast.LENGTH_LONG
                        ).show();
                    }
                });
    }

    private void deleteAnimal(String animalId) {
        db.collection("animals")
                .document(animalId)
                .delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(
                                AnimalActivity.this,
                                "Animal gelöscht",
                                Toast.LENGTH_SHORT
                        ).show();
                        finish();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(
                                AnimalActivity.this,
                                "Fehler beim Löschen: " + e.getMessage(),
                                Toast.LENGTH_LONG
                        ).show();
                    }
                });
    }
}
