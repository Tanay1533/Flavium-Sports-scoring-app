package com.example.flavium;

import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.flavium.databinding.ViewScoreboardBinding;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;

import java.util.List;

public class UiUtils {

    public interface OnPickListener {
        void onPick(String item);
    }

    public static int dp(int value) {
        return (int) (value * Resources.getSystem().getDisplayMetrics().density);
    }

    public static void padForStatusBar(View view) {
        int base = view.getPaddingTop();
        ViewCompat.setOnApplyWindowInsetsListener(view, (v, insets) -> {
            Insets bars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(v.getPaddingLeft(), base + bars.top, v.getPaddingRight(), v.getPaddingBottom());
            return insets;
        });
    }

    public static void padForNavBar(View view) {
        int base = view.getPaddingBottom();
        ViewCompat.setOnApplyWindowInsetsListener(view, (v, insets) -> {
            Insets bars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(v.getPaddingLeft(), v.getPaddingTop(), v.getPaddingRight(), base + bars.bottom);
            return insets;
        });
    }

    public static void setContentWithBack(AppCompatActivity activity, View content, String title) {
        LinearLayout root = new LinearLayout(activity);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setBackgroundColor(ContextCompat.getColor(activity, R.color.bg));

        View header = activity.getLayoutInflater().inflate(R.layout.view_toolbar, root, false);
        MaterialToolbar bar = header.findViewById(R.id.toolbar);
        bar.setTitle(title);
        bar.setNavigationOnClickListener(v -> activity.finish());
        padForStatusBar(header);

        root.addView(header);
        root.addView(content, new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 0, 1f));
        padForNavBar(root);
        activity.setContentView(root);
    }

    public static void styleStatus(TextView textView, String status) {
        String label;
        int bg;
        int fg;
        if ("LIVE".equals(status)) {
            label = "● LIVE";
            bg = R.color.live;
            fg = R.color.white;
        } else if ("COMPLETED".equals(status)) {
            label = "FINAL";
            bg = R.color.chip_done_bg;
            fg = R.color.chip_done_fg;
        } else {
            label = "UPCOMING";
            bg = R.color.chip_up_bg;
            fg = R.color.chip_up_fg;
        }
        textView.setText(label);
        textView.setTextColor(ContextCompat.getColor(textView.getContext(), fg));
        textView.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(textView.getContext(), bg)));
    }

    public static void setupChipGroup(ChipGroup group, List<String> items, int selected, OnPickListener onPick) {
        group.removeAllViews();
        group.setSingleSelection(true);
        group.setSelectionRequired(true);
        for (int i = 0; i < items.size(); i++) {
            String label = items.get(i);
            Chip chip = (Chip) LayoutInflater.from(group.getContext()).inflate(R.layout.item_chip, group, false);
            chip.setId(View.generateViewId());
            chip.setText(label);
            group.addView(chip);
            if (i == selected) {
                group.check(chip.getId());
            }
        }
        group.setOnCheckedStateChangeListener((g, ids) -> {
            if (!ids.isEmpty()) {
                Chip chip = g.findViewById(ids.get(0));
                if (chip != null) {
                    onPick.onPick(chip.getText().toString());
                }
            }
        });
    }

    public static void renderScoreboard(ViewScoreboardBinding binding, Match m) {
        binding.tvSport.setText(Config.emoji(m.getSport()) + "  " + m.getSport());
        styleStatus(binding.tvStatus, m.getStatus());
        binding.tvTeamA.setText(DataUtils.teamLabel(m.getTeamA()));
        binding.tvTeamB.setText(DataUtils.teamLabel(m.getTeamB()));

        boolean started = !"UPCOMING".equals(m.getStatus());
        binding.tvScoreA.setText(started ? DataUtils.mainScore(m, "A") : "–");
        binding.tvScoreB.setText(started ? DataUtils.mainScore(m, "B") : "–");

        String subA = started ? DataUtils.subScore(m, "A") : "";
        String subB = started ? DataUtils.subScore(m, "B") : "";
        binding.tvSubA.setText(subA);
        binding.tvSubB.setText(subB);
        binding.tvSubA.setVisibility(subA.isEmpty() ? View.GONE : View.VISIBLE);
        binding.tvSubB.setVisibility(subB.isEmpty() ? View.GONE : View.VISIBLE);

        if ("LIVE".equals(m.getStatus())) {
            binding.tvResult.setText("Match in progress");
        } else if ("COMPLETED".equals(m.getStatus())) {
            if (m.getWinner().isEmpty()) {
                binding.tvResult.setText("Match tied");
            } else {
                binding.tvResult.setText("🏆 " + DataUtils.teamLabel(m.getWinner()) + " won");
            }
        } else {
            binding.tvResult.setText("Starts " + DataUtils.whenText(m));
        }
    }
}
