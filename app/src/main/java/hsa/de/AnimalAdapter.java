package hsa.de;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Locale;

public class AnimalAdapter extends RecyclerView.Adapter<AnimalAdapter.AnimalViewHolder> {

    public interface OnAnimalClickListener {
        void onAnimalClick(Animal animal);
    }

    private final Context context;
    private final OnAnimalClickListener listener;

    private final ArrayList<Animal> visibleList = new ArrayList<>();
    private final ArrayList<Animal> fullList = new ArrayList<>();

    public AnimalAdapter(Context context, ArrayList<Animal> initial, OnAnimalClickListener listener) {
        this.context = context;
        this.listener = listener;
        setData(initial);
    }

    public void setData(ArrayList<Animal> data) {
        fullList.clear();
        if (data != null) fullList.addAll(data);

        visibleList.clear();
        if (data != null) visibleList.addAll(data);

        notifyDataSetChanged();
    }

    public void filter(String query) {
        String q = (query == null) ? "" : query.trim().toLowerCase(Locale.getDefault());

        visibleList.clear();

        if (q.length() == 0) {
            visibleList.addAll(fullList);
        } else {
            for (int i = 0; i < fullList.size(); i++) {
                Animal a = fullList.get(i);

                String name = (a.getName() == null) ? "" : a.getName().toLowerCase(Locale.getDefault());
                String info = (a.getInfo() == null) ? "" : a.getInfo().toLowerCase(Locale.getDefault());
                String enclosure = String.valueOf(a.getEnclosureNr());

                if (name.contains(q) || info.contains(q) || enclosure.contains(q)) {
                    visibleList.add(a);
                }
            }
        }

        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public AnimalViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.row_animal, parent, false);
        return new AnimalViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull AnimalViewHolder holder, int position) {
        final Animal a = visibleList.get(position);

        holder.tvName.setText(a.getName() != null ? a.getName() : "");
        holder.tvInfo.setText(a.getInfo() != null ? a.getInfo() : "");
        holder.tvEnclosure.setText("Gehege: " + a.getEnclosureNr());

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
        return visibleList.size();
    }

    static class AnimalViewHolder extends RecyclerView.ViewHolder {

        TextView tvName;
        TextView tvInfo;
        TextView tvEnclosure;

        AnimalViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.row_animal_name);
            tvInfo = itemView.findViewById(R.id.row_animal_info);
            tvEnclosure = itemView.findViewById(R.id.row_animal_enclosure);
        }
    }
}
