package hsa.de;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
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

    private FirebaseFirestore db;

    private String animalId;

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

        // Adapter: Klick auf Event -> EventActivity öffnen
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

        loadEventsFirestore(animalId);
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
                    public void onFailure(Exception e) {
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
                    public void onFailure(Exception e) {
                        Toast.makeText(
                                AnimalActivity.this,
                                "Fehler beim Löschen: " + e.getMessage(),
                                Toast.LENGTH_LONG
                        ).show();
                    }
                });
    }
}
