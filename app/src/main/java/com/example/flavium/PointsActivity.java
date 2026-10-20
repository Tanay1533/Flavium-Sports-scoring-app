package com.example.flavium;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.flavium.databinding.ActivityPointsBinding;
import com.google.firebase.firestore.ListenerRegistration;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PointsActivity extends AppCompatActivity {
    private ActivityPointsBinding b;
    private final PointsAdapter adapter = new PointsAdapter();
    private List<PointsRow> all = new ArrayList<>();
    private ListenerRegistration reg;

    private String sport = "Overall";
    private String branch = "All";
    private String year = "All";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        b = ActivityPointsBinding.inflate(getLayoutInflater());
        UiUtils.setContentWithBack(this, b.getRoot(), "Points table");

        b.rv.setLayoutManager(new LinearLayoutManager(this));
        b.rv.setAdapter(adapter);

        List<String> sportChips = new ArrayList<>();
        sportChips.add("Overall");
        sportChips.addAll(Config.sports);
        UiUtils.setupChipGroup(b.chipSport, sportChips, 0, item -> {
            sport = item;
            refresh();
        });

        List<String> branchChips = new ArrayList<>();
        branchChips.add("All");
        branchChips.addAll(Config.branches);
        UiUtils.setupChipGroup(b.chipBranch, branchChips, 0, item -> {
            branch = item;
            refresh();
        });

        UiUtils.setupChipGroup(b.chipYear, Arrays.asList("All", "1st year", "2nd year", "3rd year"), 0, item -> {
            year = item;
            refresh();
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        reg = Repo.listenPoints(list -> {
            all = list;
            refresh();
        });
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (reg != null) {
            reg.remove();
        }
    }

    private void refresh() {
        List<PointsRow> base;
        if ("Overall".equals(sport)) {
            Map<String, List<PointsRow>> grouped = new HashMap<>();
            for (PointsRow r : all) {
                if (!grouped.containsKey(r.getTeam())) {
                    grouped.put(r.getTeam(), new ArrayList<>());
                }
                grouped.get(r.getTeam()).add(r);
            }
            base = new ArrayList<>();
            for (Map.Entry<String, List<PointsRow>> entry : grouped.entrySet()) {
                String team = entry.getKey();
                List<PointsRow> list = entry.getValue();
                int played = 0, won = 0, lost = 0, tied = 0, points = 0;
                for (PointsRow pr : list) {
                    played += pr.getPlayed();
                    won += pr.getWon();
                    lost += pr.getLost();
                    tied += pr.getTied();
                    points += pr.getPoints();
                }
                base.add(new PointsRow("", team, played, won, lost, tied, points));
            }
        } else {
            base = new ArrayList<>();
            for (PointsRow r : all) {
                if (sport.equals(r.getSport())) {
                    base.add(r);
                }
            }
        }

        String yearDigit;
        if ("1st year".equals(year)) yearDigit = "1";
        else if ("2nd year".equals(year)) yearDigit = "2";
        else if ("3rd year".equals(year)) yearDigit = "3";
        else yearDigit = null;

        List<PointsRow> rows = new ArrayList<>();
        for (String t : Config.teams) {
            PointsRow found = null;
            for (PointsRow pr : base) {
                if (t.equals(pr.getTeam())) {
                    found = pr;
                    break;
                }
            }
            PointsRow row = found != null ? found : new PointsRow(t);

            boolean matchBranch = "All".equals(branch) || row.getTeam().startsWith(branch + "-");
            boolean matchYear = yearDigit == null || row.getTeam().endsWith("-" + yearDigit);

            if (matchBranch && matchYear) {
                rows.add(row);
            }
        }

        Collections.sort(rows, (r1, r2) -> {
            if (r1.getPoints() != r2.getPoints()) {
                return Integer.compare(r2.getPoints(), r1.getPoints());
            }
            return Integer.compare(r2.getWon(), r1.getWon());
        });

        adapter.submit(rows);
    }
}
