package hsa.de;

import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class EventActivity extends AppCompatActivity {

    private static final String TAG = "EventActivity";

    private FirebaseFirestore db;

    private TextView tvName;
    private TextView tvInfo;
    private TextView tvDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event);

        db = FirebaseFirestore.getInstance();

        tvName = findViewById(R.id.event_name);
        tvInfo = findViewById(R.id.event_info);
        tvDate = findViewById(R.id.event_date);

        String eventId = getIntent().getStringExtra("EVENT_ID");
        if (eventId == null || eventId.isEmpty()) {
            Toast.makeText(this, "Keine Event-ID übergeben", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        loadEventById(eventId);
    }

    private void loadEventById(String eventId) {
        db.collection("events")
                .document(eventId)
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful() && task.getResult() != null) {
                            DocumentSnapshot doc = task.getResult();

                            if (!doc.exists()) {
                                Toast.makeText(
                                        EventActivity.this,
                                        "Event nicht gefunden",
                                        Toast.LENGTH_LONG
                                ).show();
                                finish();
                                return;
                            }

                            String name = doc.getString("name");
                            String info = doc.getString("info");
                            String date = doc.getString("date");

                            tvName.setText(name != null ? name : "");
                            tvInfo.setText(info != null ? info : "");
                            tvDate.setText(date != null ? date : "");

                            Log.d(TAG, "Loaded event: " + doc.getId());

                        } else {
                            Log.w(TAG, "Error getting event", task.getException());
                            Toast.makeText(
                                    EventActivity.this,
                                    "Fehler beim Laden",
                                    Toast.LENGTH_LONG
                            ).show();
                        }
                    }
                });
    }
}
