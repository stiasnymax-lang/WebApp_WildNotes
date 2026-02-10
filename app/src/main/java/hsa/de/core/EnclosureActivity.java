package hsa.de.core;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.navigation.NavigationBarView;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

import hsa.de.R;
import hsa.de.feature_animals.AddAnimalActivity;
import hsa.de.feature_animals.Animal;
import hsa.de.feature_animals.AnimalAdapter;
import hsa.de.feature_animals.EditAnimalActivity;

public class EnclosureActivity extends AppCompatActivity {

    private NavigationBarView bottomNavigation;
    private RecyclerView recyclerView;
    private TextView emptyView;

    private FirebaseFirestore db;
    private AnimalAdapter adapter;
    private List<Animal> animalList = new ArrayList<>();

    private int enclosureNr = -1; // Standardwert: nicht gesetzt

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enclosure);

        bottomNavigation = findViewById(R.id.bottomNavigation);
        bottomNavigation.setSelectedItemId(R.id.home);

        recyclerView = findViewById(R.id.recyclerViewAnimals);
        emptyView = findViewById(R.id.emptyView);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new AnimalAdapter(this, animalList, new AnimalAdapter.OnAnimalClickListener() {
            @Override
            public void onAnimalClick(Animal animal) {
                if (animal == null || animal.getId() == null || animal.getId().isEmpty()) {
                    return;
                }
                Intent intent = new Intent(EnclosureActivity.this, EditAnimalActivity.class);
                intent.putExtra("animalId", animal.getId());
                startActivity(intent);
            }
        });

        recyclerView.setAdapter(adapter);

        db = FirebaseFirestore.getInstance();


        if (getIntent().hasExtra("enclosureNr")) {
            enclosureNr = getIntent().getIntExtra("enclosureNr", -1);
        } else if (getIntent().hasExtra("enclosure")) {
            // optional: wenn MapActivity "enclosure" als String schickt
            try {
                enclosureNr = Integer.parseInt(getIntent().getStringExtra("enclosure"));
            } catch (Exception e) {
                enclosureNr = -1;
            }
        }

        if (enclosureNr == -1) {
            emptyView.setText("Kein Gehege ausgewählt");
            showEmpty(true);
        } else {
            setTitle("Gehege " + enclosureNr);
            loadAnimalsForEnclosure(enclosureNr);
        }

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
                        } else if (id == R.id.add) {
                            startActivity(new Intent(getApplicationContext(), AddAnimalActivity.class));
                            finish();
                            return true;
                        } else if (id == R.id.library) {
                            startActivity(new Intent(getApplicationContext(), LibraryActivity.class));
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

    private void loadAnimalsForEnclosure(int enclosureNr) {
        showEmpty(false); // vorbereiten
        animalList.clear();
        adapter.notifyDataSetChanged();

        db.collection("animals")
                .whereEqualTo("enclosureNr", enclosureNr)
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot snapshots) {
                        if (snapshots.isEmpty()) {
                            emptyView.setText("Keine Tiere in diesem Gehege.");
                            showEmpty(true);
                            return;
                        }

                        for (QueryDocumentSnapshot doc : snapshots) {
                            Animal a = new Animal();
                            a.setId(doc.getId());
                            a.setName(doc.getString("name"));
                            a.setInfo(doc.getString("info"));

                            // enclosureNr in Firestore ist vermutlich number (Long)
                            Long enc = doc.getLong("enclosureNr");
                            if (enc != null) {
                                a.setEnclosureNr(enc.intValue());
                            } else {
                                // falls als String gespeichert
                                try {
                                    a.setEnclosureNr(Integer.parseInt(doc.getString("enclosureNr")));
                                } catch (Exception ignored) {}
                            }

                            animalList.add(a);
                        }

                        adapter.setData(animalList);
                        showEmpty(false);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        emptyView.setText("Fehler beim Laden: " + e.getMessage());
                        showEmpty(true);
                    }
                });
    }

    private void showEmpty(boolean show) {
        if (show) {
            emptyView.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        } else {
            emptyView.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
        }
    }
}
