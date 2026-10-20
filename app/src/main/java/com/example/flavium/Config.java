package com.example.flavium;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Config {
    public static final List<String> sports = Arrays.asList("Cricket", "Football", "Basketball", "Badminton", "Volleyball");
    public static final List<String> branches = Arrays.asList("CE", "CS", "IT", "AIML", "MBATech");
    public static final List<Integer> years = Arrays.asList(1, 2, 3);
    public static final List<String> teams = new ArrayList<>();

    private static final Map<String, String> emojis = new HashMap<>();
    private static final Map<String, String> taglines = new HashMap<>();

    static {
        for (String b : branches) {
            for (Integer y : years) {
                teams.add(b + "-" + y);
            }
        }

        emojis.put("Cricket", "🏏");
        emojis.put("Football", "⚽");
        emojis.put("Basketball", "🏀");
        emojis.put("Badminton", "🏸");
        emojis.put("Volleyball", "🏐");

        taglines.put("Cricket", "Runs • Wickets • Overs");
        taglines.put("Football", "Goals");
        taglines.put("Basketball", "Points");
        taglines.put("Badminton", "Rally points");
        taglines.put("Volleyball", "Rally points");
    }

    public static String emoji(String sport) {
        String val = emojis.get(sport);
        return val != null ? val : "🏅";
    }

    public static String tagline(String sport) {
        String val = taglines.get(sport);
        return val != null ? val : "";
    }
}
