package hsa.de.feature_events;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import hsa.de.feature_animals.AddAnimalActivity;
import hsa.de.core.HomeActivity;
import hsa.de.core.LibraryActivity;
import hsa.de.R;
import hsa.de.core.SettingsActivity;

public class EventActivity extends AppCompatActivity {

    private static final String TAG = "EventActivity";

    private FirebaseFirestore db;

    private TextView tvName;
    private TextView tvInfo;
    private TextView tvDate;

    private NavigationBarView bottomNavigation;

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
