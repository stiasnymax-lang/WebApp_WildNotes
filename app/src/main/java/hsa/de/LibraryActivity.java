package hsa.de;

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

public class LibraryActivity extends AppCompatActivity {

    private FirebaseFirestore db;

    private RecyclerView recyclerView;
    private AnimalAdapter adapter;
    private EditText searchBar;

    private BottomNavigationView bottomNavigation;

    private final ArrayList<Animal> animals = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_library);

        db = FirebaseFirestore.getInstance();

        recyclerView = findViewById(R.id.recycler_animals);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        searchBar = findViewById(R.id.search_bar);

        adapter = new AnimalAdapter(this, animals, new AnimalAdapter.OnAnimalClickListener() {
            @Override
            public void onAnimalClick(Animal animal) {
                if (animal == null || animal.getId() == null || animal.getId().isEmpty()) {
                    Toast.makeText(LibraryActivity.this, "Fehler: animalId fehlt", Toast.LENGTH_LONG).show();
                    return;
                }

                Intent intent = new Intent(LibraryActivity.this, AnimalActivity.class);
                // AnimalActivity erwartet: getIntent().getStringExtra("animalId")
                intent.putExtra("animalId", animal.getId());
                startActivity(intent);
            }
        });

        recyclerView.setAdapter(adapter);

        searchBar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                adapter.filter(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) { }
        });

        loadAnimals();

        bottomNavigation = findViewById(R.id.bottomNavigation);
        bottomNavigation.setSelectedItemId(R.id.library);

        bottomNavigation.setOnItemSelectedListener(
                new NavigationBarView.OnItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                        int id = item.getItemId();

                        if (id == R.id.settings) {
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

    private void loadAnimals() {
        db.collection("animals")
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot querySnapshot) {
                        animals.clear();

                        for (QueryDocumentSnapshot doc : querySnapshot) {
                            Animal a = doc.toObject(Animal.class);
                            a.setId(doc.getId()); // Document-ID merken
                            animals.add(a);
                        }

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
