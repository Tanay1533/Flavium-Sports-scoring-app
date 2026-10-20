package com.example.flavium;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.example.flavium.databinding.ActivityMainBinding;

/** Welcome screen: choose Viewer or Scorer. */
public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // A scorer who is already signed in skips the welcome screen
        if (savedInstanceState == null && Repo.isAdmin()) {
            startActivity(new Intent(this, DashboardActivity.class));
            finish();
            return;
        }

        ActivityMainBinding b = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(b.getRoot());
        UiUtils.padForStatusBar(b.hero);
        UiUtils.padForNavBar(b.content);

        b.cardViewer.setOnClickListener(v -> {
            startActivity(new Intent(this, DashboardActivity.class));
        });

        b.cardScorer.setOnClickListener(v -> {
            if (Repo.isAdmin()) {
                startActivity(new Intent(this, DashboardActivity.class));
            } else {
                startActivity(new Intent(this, LoginActivity.class));
            }
        });
    }
}
