package hsa.de.feature_events;

import androidx.appcompat.app.AppCompatActivity;

public class AnimalEvent extends AppCompatActivity {

    // Firestore Document-ID (wird NICHT automatisch gemappt)
    public String id;

    // Felder aus Firestore
    public String name;
    public String info;
    public String date;

    // WICHTIG für Firestore
    public AnimalEvent() {
    }
}
