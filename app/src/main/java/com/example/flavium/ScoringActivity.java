package com.example.flavium;

import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.flavium.databinding.ActivityScoringBinding;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.firestore.ListenerRegistration;

import java.util.ArrayList;
import java.util.List;

/** Scorer screen. Every tap is written to Firestore and appears instantly on viewers' phones. */
public class ScoringActivity extends AppCompatActivity {

    private static class Action {
        final String side;
        final long runs;
        final long wicket;
        final long ball;

        Action(String side, long runs, long wicket, long ball) {
            this.side = side;
            this.runs = runs;
            this.wicket = wicket;
            this.ball = ball;
        }
    }

    private ActivityScoringBinding b;
    private String id;
    private ListenerRegistration reg;
    private Match match;
    private final List<Action> history = new ArrayList<>();

    private String getSide() {
        return b.toggleSide.getCheckedButtonId() == R.id.btnSideB ? "B" : "A";
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        b = ActivityScoringBinding.inflate(getLayoutInflater());
        UiUtils.setContentWithBack(this, b.getRoot(), "Scoring");

        id = getIntent().getStringExtra("id");
        if (id == null) {
            finish();
            return;
        }

        b.toggleSide.check(R.id.btnSideA);

        // Cricket run buttons: 0 1 2 3 4 6
        long[] runsArray = new long[]{0L, 1L, 2L, 3L, 4L, 6L};
        for (int i = 0; i < runsArray.length; i++) {
            long r = runsArray[i];
            MaterialButton btn = new MaterialButton(
                this, null, com.google.android.material.R.attr.materialButtonOutlinedStyle
            );
            btn.setText(String.valueOf(r));
            btn.setTextSize(18f);
            btn.setMinWidth(0);
            btn.setMinimumWidth(0);
            btn.setOnClickListener(v -> score(r, 0, 1));

            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(0, UiUtils.dp(52), 1f);
            if (i < 5) lp.setMarginEnd(UiUtils.dp(6));
            b.llRuns.addView(btn, lp);
        }

        b.btnWicket.setOnClickListener(v -> score(0, 1, 1));
        b.btnExtra.setOnClickListener(v -> score(1, 0, 0)); // wide / no-ball: 1 run, 0 ball

        // Other sports
        b.btnP1.setOnClickListener(v -> score(1, 0, 0));
        b.btnP2.setOnClickListener(v -> score(2, 0, 0));
        b.btnP3.setOnClickListener(v -> score(3, 0, 0));

        b.btnUndo.setOnClickListener(v -> undo());

        b.btnStart.setOnClickListener(v -> {
            Repo.setStatus(id, "LIVE")
                .addOnFailureListener(e -> DataUtils.toast(this, "Not allowed. Are you logged in as a scorer?"));
        });

        b.btnEnd.setOnClickListener(v -> {
            new AlertDialog.Builder(this)
                .setTitle("End match?")
                .setMessage("This finalises the result and updates the points table.")
                .setPositiveButton("End match", (dialog, which) -> {
                    Repo.finish(id)
                        .addOnSuccessListener(unused -> DataUtils.toast(this, "Match ended, points updated"))
                        .addOnFailureListener(e -> DataUtils.toast(this, "Failed: " + e.getMessage()));
                })
                .setNegativeButton("Cancel", null)
                .show();
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        reg = Repo.listenMatch(id, m -> {
            match = m;
            if (m != null) render(m);
        });
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (reg != null) {
            reg.remove();
        }
    }

    private void score(long runs, long wicket, long ball) {
        if (match == null) return;
        if (!"LIVE".equals(match.getStatus())) {
            DataUtils.toast(this, "Start the match first");
            return;
        }
        Action a = new Action(getSide(), runs, wicket, ball);
        Repo.add(id, a.side, a.runs, a.wicket, a.ball)
            .addOnFailureListener(e -> DataUtils.toast(this, "Not allowed. Are you logged in as a scorer?"));
        history.add(a);
    }

    private void undo() {
        if (match == null) return;
        if (!"LIVE".equals(match.getStatus())) return;
        if (history.isEmpty()) {
            DataUtils.toast(this, "Nothing to undo");
            return;
        }
        Action last = history.remove(history.size() - 1);
        Repo.add(id, last.side, -last.runs, -last.wicket, -last.ball);
    }

    private void render(Match m) {
        UiUtils.renderScoreboard(b.scoreboard, m);
        b.btnSideA.setText(DataUtils.teamLabel(m.getTeamA()));
        b.btnSideB.setText(DataUtils.teamLabel(m.getTeamB()));

        b.btnStart.setVisibility("UPCOMING".equals(m.getStatus()) ? View.VISIBLE : View.GONE);
        b.llControls.setVisibility("LIVE".equals(m.getStatus()) ? View.VISIBLE : View.GONE);

        boolean cricket = "Cricket".equals(m.getSport());
        b.cardCricket.setVisibility(cricket ? View.VISIBLE : View.GONE);
        b.cardOther.setVisibility(cricket ? View.GONE : View.VISIBLE);

        boolean basketball = "Basketball".equals(m.getSport());
        if ("Football".equals(m.getSport())) {
            b.btnP1.setText("⚽  Goal   +1");
        } else if ("Basketball".equals(m.getSport())) {
            b.btnP1.setText("Free throw   +1");
        } else {
            b.btnP1.setText("Point   +1");
        }
        b.btnP2.setVisibility(basketball ? View.VISIBLE : View.GONE);
        b.btnP3.setVisibility(basketball ? View.VISIBLE : View.GONE);
    }
}
