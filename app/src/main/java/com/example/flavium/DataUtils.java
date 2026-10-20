package com.example.flavium;

import android.content.Context;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class DataUtils {

    public static String teamLabel(String team) {
        if (team == null) return "";
        int i = team.lastIndexOf('-');
        if (i > 0) {
            return team.substring(0, i) + " • Year " + team.substring(i + 1);
        }
        return team;
    }

    public static String mainScore(Match m, String side) {
        int s = side.equals("A") ? m.getScoreA() : m.getScoreB();
        int w = side.equals("A") ? m.getWicketsA() : m.getWicketsB();
        if ("Cricket".equals(m.getSport())) {
            return s + "/" + w;
        }
        return String.valueOf(s);
    }

    public static String subScore(Match m, String side) {
        if (!"Cricket".equals(m.getSport())) return "";
        int b = side.equals("A") ? m.getBallsA() : m.getBallsB();
        return (b / 6) + "." + (b % 6) + " overs";
    }

    public static String scoreText(Match m, String side) {
        int s = side.equals("A") ? m.getScoreA() : m.getScoreB();
        int w = side.equals("A") ? m.getWicketsA() : m.getWicketsB();
        int b = side.equals("A") ? m.getBallsA() : m.getBallsB();
        if ("Cricket".equals(m.getSport())) {
            return s + "/" + w + " (" + (b / 6) + "." + (b % 6) + " ov)";
        }
        return String.valueOf(s);
    }

    public static String whenText(Match m) {
        SimpleDateFormat sdf = new SimpleDateFormat("EEE, dd MMM yyyy • hh:mm a", Locale.getDefault());
        return sdf.format(new Date(m.getDateTime()));
    }

    public static void toast(Context context, String msg) {
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
    }
}
