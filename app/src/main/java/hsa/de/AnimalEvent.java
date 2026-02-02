package hsa.de;

public class AnimalEvent {

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
