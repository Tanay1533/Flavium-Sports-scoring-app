package com.example.flavium;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.flavium.databinding.ActivityDashboardBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.ListenerRegistration;

/** Home: live matches, sports and points table. Same screen for viewers and scorers. */
public class DashboardActivity extends AppCompatActivity {
    private ActivityDashboardBinding b;
    private MatchAdapter liveAdapter;
    private ListenerRegistration reg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        b = ActivityDashboardBinding.inflate(getLayoutInflater());
        setContentView(b.getRoot());
        UiUtils.padForStatusBar(b.header);
        UiUtils.padForNavBar(b.scroll);

        liveAdapter = new MatchAdapter(
            m -> startActivity(new Intent(this, ScorecardActivity.class).putExtra("id", m.getId())),
            m -> startActivity(new Intent(this, ScoringActivity.class).putExtra("id", m.getId()))
        );

        b.rvLive.setLayoutManager(new LinearLayoutManager(this));
        b.rvLive.setAdapter(liveAdapter);
        b.rvLive.setNestedScrollingEnabled(false);

        b.rvSports.setLayoutManager(new GridLayoutManager(this, 2));
        b.rvSports.setAdapter(new SportAdapter(sport -> {
            startActivity(new Intent(this, MatchListActivity.class).putExtra("sport", sport));
        }));
        b.rvSports.setNestedScrollingEnabled(false);

        b.cardPoints.setOnClickListener(v -> {
            startActivity(new Intent(this, PointsActivity.class));
        });

        b.btnAuth.setOnClickListener(v -> {
            if (Repo.isAdmin()) {
                FirebaseAuth.getInstance().signOut();
                startActivity(
                    new Intent(this, MainActivity.class)
                        .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK)
                );
                finish();
            } else {
                startActivity(new Intent(this, LoginActivity.class).putExtra("goDashboard", false));
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart()  ;
        renderRole();
        reg = Repo.listenLive(list -> {
            liveAdapter.submit(list);
            b.rvLive.setVisibility(list.isEmpty() ? View.GONE : View.VISIBLE);
            b.tvNoLive.setVisibility(list.isEmpty() ? View.VISIBLE : View.GONE);
        });
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (reg != null) {
            reg.remove();
        }
    }

    private void renderRole() {
        boolean scorer = Repo.isAdmin();
        b.tvRole.setText(scorer ? "SCORER" : "VIEWER");
        b.btnAuth.setText(scorer ? "Logout" : "Scorer login");
        b.tvSubtitle.setText(
            scorer ? "Schedule matches and score them live" : "Follow live scores and standings"
        );
        liveAdapter.refresh();
    }
}
