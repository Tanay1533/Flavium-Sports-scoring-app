package com.example.flavium;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.flavium.databinding.ItemSportBinding;

public class SportAdapter extends RecyclerView.Adapter<SportAdapter.VH> {

    public interface OnSportClickListener {
        void onClick(String sport);
    }

    private final OnSportClickListener onClick;

    public SportAdapter(OnSportClickListener onClick) {
        this.onClick = onClick;
    }

    public static class VH extends RecyclerView.ViewHolder {
        final ItemSportBinding b;

        public VH(ItemSportBinding b) {
            super(b.getRoot());
            this.b = b;
        }
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemSportBinding binding = ItemSportBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false);
        return new VH(binding);
    }

    @Override
    public int getItemCount() {
        return Config.sports.size();
    }

    @Override
    public void onBindViewHolder(@NonNull VH h, int position) {
        String sport = Config.sports.get(position);
        h.b.tvEmoji.setText(Config.emoji(sport));
        h.b.tvName.setText(sport);
        h.b.tvTag.setText(Config.tagline(sport));
        h.b.getRoot().setOnClickListener(v -> {
            if (onClick != null) onClick.onClick(sport);
        });
    }
}
