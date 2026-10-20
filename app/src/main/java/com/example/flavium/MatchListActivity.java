package com.example.flavium;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.flavium.databinding.ActivityMatchListBinding;
import com.google.firebase.firestore.ListenerRegistration;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class MatchListActivity extends AppCompatActivity {
    private ActivityMatchListBinding b;
    private String sport;
    private MatchAdapter adapter;
    private ListenerRegistration reg;
    private List<Match> all = new ArrayList<>();
    private String filter = "All";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        b = ActivityMatchListBinding.inflate(getLayoutInflater());
        sport = getIntent().getStringExtra("sport");
        if (sport == null) sport = "Cricket";
        UiUtils.setContentWithBack(this, b.getRoot(), Config.emoji(sport) + "  " + sport);

        adapter = new MatchAdapter(
            m -> startActivity(new Intent(this, ScorecardActivity.class).putExtra("id", m.getId())),
            m -> startActivity(new Intent(this, ScoringActivity.class).putExtra("id", m.getId()))
        );
        b.rv.setLayoutManager(new LinearLayoutManager(this));
        b.rv.setAdapter(adapter);

        UiUtils.setupChipGroup(b.chipFilter, Arrays.asList("All", "Live", "Upcoming", "Completed"), 0, item -> {
            filter = item;
            applyFilter();
        });

        b.fabSchedule.setOnClickListener(v -> {
            startActivity(new Intent(this, ScheduleActivity.class).putExtra("sport", sport));
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        b.fabSchedule.setVisibility(Repo.isAdmin() ? View.VISIBLE : View.GONE);
        reg = Repo.listenMatches(sport, list -> {
            all = new ArrayList<>(list);
            Collections.sort(all, (m1, m2) -> {
                int o1 = order(m1.getStatus());
                int o2 = order(m2.getStatus());
                if (o1 != o2) return Integer.compare(o1, o2);
                if ("COMPLETED".equals(m1.getStatus())) {
                    return Long.compare(m2.getDateTime(), m1.getDateTime());
                }
                return Long.compare(m1.getDateTime(), m2.getDateTime());
            });
            applyFilter();
        });
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (reg != null) {
            reg.remove();
        }
    }

    private int order(String status) {
        if ("LIVE".equals(status)) return 0;
        if ("UPCOMING".equals(status)) return 1;
        return 2;
    }

    private void applyFilter() {
        List<Match> filtered = new ArrayList<>();
        for (Match m : all) {
            if ("Live".equals(filter) && "LIVE".equals(m.getStatus())) {
                filtered.add(m);
            } else if ("Upcoming".equals(filter) && "UPCOMING".equals(m.getStatus())) {
                filtered.add(m);
            } else if ("Completed".equals(filter) && "COMPLETED".equals(m.getStatus())) {
                filtered.add(m);
            } else if ("All".equals(filter)) {
                filtered.add(m);
            }
        }
        adapter.submit(filtered);
        b.emptyState.setVisibility(filtered.isEmpty() ? View.VISIBLE : View.GONE);
        b.tvEmptyHint.setText(
            Repo.isAdmin() ? "Tap “Schedule match” to add one." : "Check back soon for fixtures."
        );
    }
}
