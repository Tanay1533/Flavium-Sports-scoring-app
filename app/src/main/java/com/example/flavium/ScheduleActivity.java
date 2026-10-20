package com.example.flavium;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.widget.ArrayAdapter;

import androidx.appcompat.app.AppCompatActivity;

import com.example.flavium.databinding.ActivityScheduleBinding;

import java.util.Calendar;
import java.util.Locale;

public class ScheduleActivity extends AppCompatActivity {
    private ActivityScheduleBinding b;
    private final Calendar cal = Calendar.getInstance();
    private boolean dateSet = false;
    private boolean timeSet = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        b = ActivityScheduleBinding.inflate(getLayoutInflater());
        UiUtils.setContentWithBack(this, b.getRoot(), "Schedule match");

        String sportExtra = getIntent().getStringExtra("sport");
        final String sport = sportExtra != null ? sportExtra : "Cricket";
        b.tvSport.setText(Config.emoji(sport) + "  " + sport + " match");

        ArrayAdapter<String> teamAdapterA = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, Config.teams);
        ArrayAdapter<String> teamAdapterB = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, Config.teams);
        b.acA.setAdapter(teamAdapterA);
        b.acB.setAdapter(teamAdapterB);
        b.acA.setText(Config.teams.get(0), false);
        b.acB.setText(Config.teams.get(1), false);

        b.btnDate.setOnClickListener(v -> {
            new DatePickerDialog(
                this,
                (view, y, m, d) -> {
                    cal.set(Calendar.YEAR, y);
                    cal.set(Calendar.MONTH, m);
                    cal.set(Calendar.DAY_OF_MONTH, d);
                    dateSet = true;
                    b.btnDate.setText(String.format(Locale.getDefault(), "📅  %02d/%02d/%d", d, m + 1, y));
                },
                cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH)
            ).show();
        });

        b.btnTime.setOnClickListener(v -> {
            new TimePickerDialog(
                this,
                (view, h, min) -> {
                    cal.set(Calendar.HOUR_OF_DAY, h);
                    cal.set(Calendar.MINUTE, min);
                    timeSet = true;
                    b.btnTime.setText(String.format(Locale.getDefault(), "⏰  %02d:%02d", h, min));
                },
                cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE), false
            ).show();
        });

        b.btnSave.setOnClickListener(v -> {
            String a = b.acA.getText().toString().trim();
            String t = b.acB.getText().toString().trim();
            String venue = b.etVenue.getText() != null ? b.etVenue.getText().toString().trim() : "";

            if (a.equals(t)) {
                DataUtils.toast(this, "Pick two different teams");
            } else if (venue.isEmpty()) {
                DataUtils.toast(this, "Enter a venue");
            } else if (!dateSet || !timeSet) {
                DataUtils.toast(this, "Pick the date and time");
            } else {
                cal.set(Calendar.SECOND, 0);
                cal.set(Calendar.MILLISECOND, 0);
                Match m = new Match(sport, a, t, venue, cal.getTimeInMillis());
                Repo.schedule(m).addOnFailureListener(e -> DataUtils.toast(this, "Not allowed. Are you logged in as a scorer?"));
                DataUtils.toast(this, "Match scheduled");
                finish();
            }
        });
    }
}
