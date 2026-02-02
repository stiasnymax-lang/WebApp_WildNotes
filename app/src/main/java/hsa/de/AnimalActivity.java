package hsa.de;

import android.content.Intent;
import android.view.View;
import android.widget.Toast;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

public class AnimalActivity extends AppCompatActivity {

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    private RecyclerView recyclerView;
    private EventAdapter adapter;
    private ArrayList<AnimalEvent> events = new ArrayList<>();

    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_animal_detail);

        String animalId = getIntent().getStringExtra("animalId"); // musst du übergeben

        recyclerView = findViewById(R.id.events_recycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        adapter = new EventAdapter(events, event -> {
            Toast.makeText(this, "Klick: " + event.name, Toast.LENGTH_SHORT).show();
            // hier z.B. Detail Activity öffnen mit event.id
        });
        recyclerView.setAdapter(adapter);

        db = FirebaseFirestore.getInstance();

        loadEventsFirestore(animalId);
    }

    private void loadEventsFirestore(String animalId) {
        db.collection("animals")
                .document(animalId)
                .collection("events")
                // optional sortieren, wenn date als Timestamp gespeichert ist (besser)
                //.orderBy("date")
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    events.clear();
                    for (QueryDocumentSnapshot doc : querySnapshot) {
                        AnimalEvent e = doc.toObject(AnimalEvent.class);
                        e.id = doc.getId(); // ✅ eventId merken
                        events.add(e);
                    }
                    adapter.notifyDataSetChanged();
                })
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Fehler: " + e.getMessage(), Toast.LENGTH_LONG).show()
                );
    }

}
