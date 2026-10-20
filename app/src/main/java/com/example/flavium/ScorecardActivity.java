package com.example.flavium;

import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.example.flavium.databinding.ActivityScorecardBinding;
import com.google.firebase.firestore.ListenerRegistration;

/** Viewer screen: updates live while the scorer taps buttons. */
public class ScorecardActivity extends AppCompatActivity {
    private ActivityScorecardBinding b;
    private ListenerRegistration reg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        b = ActivityScorecardBinding.inflate(getLayoutInflater());
        UiUtils.setContentWithBack(this, b.getRoot(), "Live scorecard");
    }

    @Override
    protected void onStart() {
        super.onStart();
        String id = getIntent().getStringExtra("id");
        if (id == null) return;
        reg = Repo.listenMatch(id, m -> {
            if (m != null) show(m);
        });
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (reg != null) {
            reg.remove();
        }
    }

    private void show(Match m) {
        UiUtils.renderScoreboard(b.scoreboard, m);
        b.tvVenue.setText("📍  " + m.getVenue());
        b.tvDate.setText("📅  " + DataUtils.whenText(m));
        b.tvLiveNote.setVisibility("LIVE".equals(m.getStatus()) ? View.VISIBLE : View.GONE);
    }
}
