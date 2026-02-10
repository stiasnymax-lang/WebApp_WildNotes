package hsa.de.core;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

import hsa.de.R;
import hsa.de.feature_animals.AddAnimalActivity;
import hsa.de.feature_animals.Animal;
import hsa.de.feature_animals.AnimalActivity;
import hsa.de.feature_animals.AnimalAdapter;

/**
 * Activity für die Bibliothek/Übersicht aller Tiere
 * Zeigt alle Tiere aus Firestore in einer RecyclerView an
 * und bietet eine Suchleiste zum Filtern nach Name/Info/Gehege
 */
public class LibraryActivity extends AppCompatActivity {

    private FirebaseFirestore db;

    private RecyclerView recyclerView;
    private AnimalAdapter adapter;
    private EditText searchBar;

    private BottomNavigationView bottomNavigation;

    // Lokale Liste aller Tiere (wird aus Firestore gefüllt)
    private final ArrayList<Animal> animals = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_library);

        // Firestore initialisieren
        db = FirebaseFirestore.getInstance();

        /**
         * RecyclerView einrichten:
         * - LayoutManager (vertikale Liste)
         * - Adapter mit Klick-Listener
         */
        recyclerView = findViewById(R.id.recycler_animals);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        adapter = new AnimalAdapter(animals, new AnimalAdapter.OnAnimalClickListener() {
            @Override
            public void onAnimalClick(Animal animal) {

                // Sicherheitscheck: Tier muss eine gültige ID haben
                if (animal == null || animal.getId() == null || animal.getId().isEmpty()) {
                    Toast.makeText(LibraryActivity.this,
                            "Fehler: animalId fehlt",
                            Toast.LENGTH_LONG).show();
                    return;
                }

                // Detailansicht des Tieres öffnen
                Intent intent = new Intent(LibraryActivity.this, AnimalActivity.class);
                // AnimalActivity erwartet: getIntent().getStringExtra("animalId")
                intent.putExtra("animalId", animal.getId());
                startActivity(intent);
            }
        });

        recyclerView.setAdapter(adapter);

        //Suchleiste: Filtert die Liste während der Eingabe
        searchBar = findViewById(R.id.search_bar);

        searchBar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // Filterfunktion des Adapters aufrufen
                adapter.filter(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) { }
        });

        // Tiere beim Start laden
        loadAnimals();

        // Bottom Navigation konfigurieren: Navigation zwischen den Hauptbereichen der App
        bottomNavigation = findViewById(R.id.bottomNavigation);
        bottomNavigation.setSelectedItemId(R.id.library);

        bottomNavigation.setOnItemSelectedListener(
                new NavigationBarView.OnItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                        int id = item.getItemId();

                        // Aktuelle Seite (Library)
                        if (id == R.id.library) {
                            return true;

                        } else if (id == R.id.home) {
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

                        } else if (id == R.id.map) {
                            startActivity(new Intent(getApplicationContext(), MapActivity.class));
                            finish();
                            return true;
                        }
                        return false;
                    }
                }
        );
    }

    /**
     * Lädt alle Tiere aus der Firestore-Collection "animals"
     * und aktualisiert den RecyclerView-Adapter
     */
    private void loadAnimals() {
        db.collection("animals")
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot querySnapshot) {

                        // Alte Daten entfernen
                        animals.clear();

                        // Alle Dokumente in Animal-Objekte umwandeln
                        for (QueryDocumentSnapshot doc : querySnapshot) {
                            Animal a = doc.toObject(Animal.class);

                            // Document-ID separat setzen (wird für Detailansicht benötigt)
                            a.setId(doc.getId());
                            animals.add(a);
                        }

                        // Adapter aktualisieren (setzt fullList + displayList)
                        adapter.setData(animals);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(LibraryActivity.this,
                                "Fehler beim Laden: " + e.getMessage(),
                                Toast.LENGTH_LONG).show();
                    }
                });
    }
}
