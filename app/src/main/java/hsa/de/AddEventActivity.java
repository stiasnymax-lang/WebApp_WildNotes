package hsa.de;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class AddEventActivity extends AppCompatActivity {

    private static final String TAG = "AddEventActivity";

    private FirebaseFirestore db;

    private EditText Name;
    private EditText Info;
    private EditText Date;
    private Button create_event;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_event);

        db = FirebaseFirestore.getInstance();

        Name = findViewById(R.id.event_name);
        Info = findViewById(R.id.event_info);
        Date = findViewById(R.id.event_date);
        create_event = findViewById(R.id.button);

        create_event.setOnClickListener(v -> saveEventToFirestore());
    }

    private void saveEventToFirestore() {
        String name = Name.getText().toString().trim();
        String info = Info.getText().toString().trim();
        String date = Date.getText().toString().trim();

        // Validierung
        if (name.isEmpty()) {
            Name.setError("Bitte Namen eingeben");
            return;
        }
        if (info.isEmpty()) {
            Info.setError("Bitte Info eingeben");
            return;
        }
        if (date.isEmpty()) {
            Date.setError("Bitte Datum eingeben");
            return;
        }

        Map<String, Object> event = new HashMap<>();
        event.put("name", name);
        event.put("info", info);
        event.put("date", date);

        db.collection("events")
                .add(event)
                .addOnSuccessListener((DocumentReference documentReference) -> {
                    Log.d(TAG, "Event added with ID: " + documentReference.getId());
                    Toast.makeText(this, "Event gespeichert!", Toast.LENGTH_SHORT).show();

                    // Felder leeren
                    Name.setText("");
                    Info.setText("");
                    Date.setText("");
                })
                .addOnFailureListener(e -> {
                    Log.w(TAG, "Error adding event", e);
                    Toast.makeText(this, "Fehler beim Speichern: " + e.getMessage(), Toast.LENGTH_LONG).show();
                });
    }
}
