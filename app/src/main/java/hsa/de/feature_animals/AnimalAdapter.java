package hsa.de.feature_animals;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import hsa.de.R;

public class AnimalAdapter extends RecyclerView.Adapter<AnimalAdapter.ViewHolder> {

    public interface OnAnimalClickListener {
        void onAnimalClick(Animal animal);
    }

    private final OnAnimalClickListener listener;

    // Für Anzeige (gefilterte Liste)
    private final List<Animal> displayList = new ArrayList<>();

    // Master-Liste (alle Tiere, ungefiltert)
    private final List<Animal> fullList = new ArrayList<>();

    public AnimalAdapter(List<Animal> animals, OnAnimalClickListener listener) {
        this.listener = listener;

        if (animals != null) {
            fullList.addAll(animals);
            displayList.addAll(animals);
        }
    }

    @NonNull
    @Override
    public AnimalAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_animal, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull AnimalAdapter.ViewHolder holder, int position) {
        final Animal a = displayList.get(position);

        holder.name.setText(a.getName() != null ? a.getName() : "");
        holder.info.setText(a.getInfo() != null ? a.getInfo() : "");
        holder.enclosure.setText("Gehege: " + a.getEnclosureNr());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.onAnimalClick(a);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return displayList.size();
    }


    // Wird von LibraryActivity nach dem Firestore-Laden aufgerufen
    // Aktualisiert sowohl die Full-Liste als auch die Anzeige-Liste

    public void setData(List<Animal> newAnimals) {
        fullList.clear();
        displayList.clear();

        if (newAnimals != null) {
            fullList.addAll(newAnimals);
            displayList.addAll(newAnimals);
        }

        notifyDataSetChanged();
    }

    // Einfache Suche über Name, Info und Gehege-Nr.
    public void filter(String query) {
        String q = (query == null) ? "" : query.trim().toLowerCase(Locale.getDefault());

        displayList.clear();

        if (q.isEmpty()) {
            displayList.addAll(fullList);
        } else {
            for (Animal a : fullList) {
                String name = a.getName() != null ? a.getName().toLowerCase(Locale.getDefault()) : "";
                String info = a.getInfo() != null ? a.getInfo().toLowerCase(Locale.getDefault()) : "";
                String enc = String.valueOf(a.getEnclosureNr());

                if (name.contains(q) || info.contains(q) || enc.contains(q)) {
                    displayList.add(a);
                }
            }
        }

        notifyDataSetChanged();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView name, info, enclosure;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.item_name);
            info = itemView.findViewById(R.id.item_info);
            enclosure = itemView.findViewById(R.id.item_enclosure);
        }
    }
}
