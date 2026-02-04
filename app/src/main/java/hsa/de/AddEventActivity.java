package hsa.de;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
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

        create_event.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveEventToFirestore();
            }
        });
    }

    private void saveEventToFirestore() {
        String nameText = Name.getText().toString().trim();
        String infoText = Info.getText().toString().trim();
        String dateText = Date.getText().toString().trim();

        if (nameText.isEmpty()) {
            Name.setError("Bitte Namen eingeben");
            return;
        }
        if (infoText.isEmpty()) {
            Info.setError("Bitte Info eingeben");
            return;
        }
        if (dateText.isEmpty()) {
            Date.setError("Bitte Datum eingeben");
            return;
        }

        Map<String, Object> event = new HashMap<>();
        event.put("name", nameText);
        event.put("info", infoText);
        event.put("date", dateText);

        db.collection("events")
                .add(event)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Log.d(TAG, "Event added with ID: " + documentReference.getId());
                        Toast.makeText(AddEventActivity.this,
                                "Event gespeichert!", Toast.LENGTH_SHORT).show();

                        Name.setText("");
                        Info.setText("");
                        Date.setText("");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(Exception e) {
                        Log.w(TAG, "Error adding event", e);
                        Toast.makeText(AddEventActivity.this,
                                "Fehler beim Speichern: " + e.getMessage(),
                                Toast.LENGTH_LONG).show();
                    }
                });
    }
}
