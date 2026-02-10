package hsa.de.feature_events;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import hsa.de.R;

/**
 * RecyclerView-Adapter für die Anzeige von Events eines Tieres
 * Jedes Listenelement zeigt den Event-Namen und das Datum an
 */
public class EventAdapter extends RecyclerView.Adapter<EventAdapter.EventViewHolder> {

    /**
     * Callback-Interface für Klicks auf ein Event
     * Wird von der aufrufenden Activity implementiert
     */
    public interface OnEventClickListener {
        void onEventClick(String animalId, Event event);
    }
    private final List<Event> events;
    private final String animalId;
    private final OnEventClickListener listener;

    /**
     * Konstruktor des Adapters.
     *
     * @param animalId  ID des zugehörigen Tieres
     * @param events    Liste der Events, die angezeigt werden sollen
     * @param listener  Callback für Klicks auf Listenelemente
     */
    public EventAdapter(String animalId, List<Event> events, OnEventClickListener listener) {
        this.animalId = animalId;
        this.events = events;
        this.listener = listener;
    }

    /**
     * Erstellt einen neuen ViewHolder und lädt das Layout
     * für ein einzelnes Event-Listenelement
     */
    @NonNull
    @Override
    public EventViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_event, parent, false);
        return new EventViewHolder(v);
    }

    /**
     * Bindet die Event-Daten an die Views eines Listeneintrags
     */
    @Override
    public void onBindViewHolder(@NonNull EventViewHolder holder, int position) {
        final Event event = events.get(position);

        // Event-Daten setzen
        holder.name.setText(event.name);
        holder.date.setText(event.date);

        // Klick auf ein Event-Listenelement
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.onEventClick(animalId, event);
                }
            }
        });
    }

    //Gibt die Anzahl der Events zurück
    @Override
    public int getItemCount() {
        return events.size();
    }

    /**
     * ViewHolder für ein einzelnes Event-Listenelement
     * Hält Referenzen auf die Views, um teure findViewById-Aufrufe zu vermeiden
     */
    static class EventViewHolder extends RecyclerView.ViewHolder {

        TextView name;
        TextView date;

        EventViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.event_name);
            date = itemView.findViewById(R.id.event_date);
        }
    }
}
