package hsa.de;

public class Animal {

    private String id;          // Firestore Document-ID (nicht in DB Feldern nötig)
    private String name;
    private String info;
    private int enclosureNr;

    // Firestore braucht leeren Konstruktor
    public Animal() { }

    public Animal(String name, String info, int enclosureNr) {
        this.name = name;
        this.info = info;
        this.enclosureNr = enclosureNr;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getName() { return name; }
    public String getInfo() { return info; }
    public int getEnclosureNr() { return enclosureNr; }

    public void setName(String name) { this.name = name; }
    public void setInfo(String info) { this.info = info; }
    public void setEnclosureNr(int enclosureNr) { this.enclosureNr = enclosureNr; }
}
