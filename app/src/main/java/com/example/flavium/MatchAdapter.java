package com.example.flavium;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.flavium.databinding.ItemMatchBinding;

import java.util.ArrayList;
import java.util.List;

public class MatchAdapter extends RecyclerView.Adapter<MatchAdapter.VH> {

    public interface OnMatchClickListener {
        void onClick(Match match);
    }

    private final OnMatchClickListener onOpen;
    private final OnMatchClickListener onScore;
    private List<Match> items = new ArrayList<>();

    public MatchAdapter(OnMatchClickListener onOpen, OnMatchClickListener onScore) {
        this.onOpen = onOpen;
        this.onScore = onScore;
    }

    public static class VH extends RecyclerView.ViewHolder {
        final ItemMatchBinding b;

        public VH(ItemMatchBinding b) {
            super(b.getRoot());
            this.b = b;
        }
    }

    public void submit(List<Match> list) {
        this.items = list != null ? list : new ArrayList<>();
        notifyDataSetChanged();
    }

    public void refresh() {
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemMatchBinding binding = ItemMatchBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false);
        return new VH(binding);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    @Override
    public void onBindViewHolder(@NonNull VH h, int position) {
        Match m = items.get(position);
        ItemMatchBinding b = h.b;

        b.tvSport.setText(Config.emoji(m.getSport()) + "  " + m.getSport());
        UiUtils.styleStatus(b.tvStatus, m.getStatus());
        b.tvTeamA.setText(DataUtils.teamLabel(m.getTeamA()));
        b.tvTeamB.setText(DataUtils.teamLabel(m.getTeamB()));

        boolean showScore = !"UPCOMING".equals(m.getStatus());
        b.tvScoreA.setVisibility(showScore ? View.VISIBLE : View.GONE);
        b.tvScoreB.setVisibility(showScore ? View.VISIBLE : View.GONE);
        b.tvScoreA.setText(DataUtils.scoreText(m, "A"));
        b.tvScoreB.setText(DataUtils.scoreText(m, "B"));

        b.tvInfo.setText("📅 " + DataUtils.whenText(m) + "\n📍 " + m.getVenue());

        if ("COMPLETED".equals(m.getStatus())) {
            b.tvResult.setVisibility(View.VISIBLE);
            if (m.getWinner().isEmpty()) {
                b.tvResult.setText("Match tied");
            } else {
                b.tvResult.setText("🏆 " + DataUtils.teamLabel(m.getWinner()) + " won");
            }
        } else {
            b.tvResult.setVisibility(View.GONE);
        }

        boolean showBtn = Repo.isAdmin() && !"COMPLETED".equals(m.getStatus());
        b.btnScore.setVisibility(showBtn ? View.VISIBLE : View.GONE);
        b.btnScore.setText("LIVE".equals(m.getStatus()) ? "Open scoring" : "Start / score match");
        b.btnScore.setOnClickListener(v -> {
            if (onScore != null) onScore.onClick(m);
        });

        b.getRoot().setOnClickListener(v -> {
            if (onOpen != null) onOpen.onClick(m);
        });
    }
}
