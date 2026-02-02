package hsa.de;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

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

        animalId = getIntent().getStringExtra("animalId"); // muss beim Öffnen übergeben werden
        if (animalId == null || animalId.isEmpty()) {
            Toast.makeText(this, "Fehler: animalId fehlt", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        recyclerView = findViewById(R.id.events_recycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Adapter: Klick auf Event -> EventActivity öffnen
        adapter = new EventAdapter(events, event -> {
            // event.id wurde beim Laden gesetzt
            Intent intent = new Intent(AnimalActivity.this, EventActivity.class);
            intent.putExtra("ANIMAL_ID", animalId);
            intent.putExtra("EVENT_ID", event.id);
            startActivity(intent);
        });

        recyclerView.setAdapter(adapter);

        btDeleteAnimal = findViewById(R.id.delete_animal);
        btEditAnimal = findViewById(R.id.edit_animal);

        btDeleteAnimal.setOnClickListener(v -> deleteAnimal(animalId));

        btEditAnimal.setOnClickListener(v -> {
            Intent intent = new Intent(AnimalActivity.this, EditAnimalActivity.class);
            intent.putExtra("animalId", animalId);
            startActivity(intent);
        });

        loadEventsFirestore(animalId);
    }

    private void loadEventsFirestore(String animalId) {
        db.collection("animals")
                .document(animalId)
                .collection("events")
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    events.clear();
                    for (QueryDocumentSnapshot doc : querySnapshot) {
                        AnimalEvent e = doc.toObject(AnimalEvent.class);
                        e.id = doc.getId(); // ✅ Document-ID merken
                        events.add(e);
                    }
                    adapter.notifyDataSetChanged();
                })
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Fehler beim Laden: " + e.getMessage(), Toast.LENGTH_LONG).show()
                );
    }

    private void deleteAnimal(String animalId) {
        // Minimal: nur Animal-Dokument löschen.
        // Achtung: Subcollection "events" wird dadurch NICHT automatisch gelöscht.
        db.collection("animals")
                .document(animalId)
                .delete()
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(this, "Animal gelöscht", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Fehler beim Löschen: " + e.getMessage(), Toast.LENGTH_LONG).show()
                );
    }
}
