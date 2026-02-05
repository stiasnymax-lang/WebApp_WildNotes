package hsa.de;

public class Animal {

    private String id;          // Firestore Document-ID
    private String name;
    private String info;
    private int enclosureNr;

    public Animal() { }

    public Animal(String id, String name, String info, int enclosureNr) {
        this.id = id;
        this.name = name;
        this.info = info;
        this.enclosureNr = enclosureNr;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getInfo() { return info; }
    public void setInfo(String info) { this.info = info; }

    public int getEnclosureNr() { return enclosureNr; }
    public void setEnclosureNr(int enclosureNr) { this.enclosureNr = enclosureNr; }
}
