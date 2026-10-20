package com.example.flavium;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.flavium.databinding.ItemPointsBinding;

import java.util.ArrayList;
import java.util.List;

public class PointsAdapter extends RecyclerView.Adapter<PointsAdapter.VH> {

    private List<PointsRow> items = new ArrayList<>();

    public static class VH extends RecyclerView.ViewHolder {
        final ItemPointsBinding b;

        public VH(ItemPointsBinding b) {
            super(b.getRoot());
            this.b = b;
        }
    }

    public void submit(List<PointsRow> list) {
        this.items = list != null ? list : new ArrayList<>();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemPointsBinding binding = ItemPointsBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false);
        return new VH(binding);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    @Override
    public void onBindViewHolder(@NonNull VH h, int position) {
        PointsRow r = items.get(position);
        Context ctx = h.b.getRoot().getContext();
        int rank = position + 1;
        boolean medal = r.getPoints() > 0 && rank <= 3;

        if (medal) {
            String[] medals = new String[]{"🥇", "🥈", "🥉"};
            h.b.tvRank.setText(medals[rank - 1]);
        } else {
            h.b.tvRank.setText(String.valueOf(rank));
        }

        h.b.tvTeam.setText(DataUtils.teamLabel(r.getTeam()));
        h.b.tvP.setText(String.valueOf(r.getPlayed()));
        h.b.tvW.setText(String.valueOf(r.getWon()));
        h.b.tvL.setText(String.valueOf(r.getLost()));
        h.b.tvT.setText(String.valueOf(r.getTied()));
        h.b.tvPts.setText(String.valueOf(r.getPoints()));

        int bg;
        if (medal && rank == 1) {
            bg = R.color.gold_bg;
        } else if (medal && rank == 2) {
            bg = R.color.silver_bg;
        } else if (medal && rank == 3) {
            bg = R.color.bronze_bg;
        } else {
            bg = R.color.white;
        }
        h.b.getRoot().setBackgroundColor(ContextCompat.getColor(ctx, bg));
    }
}
