package com.example.flavium;

import com.google.firebase.firestore.DocumentId;

public class Match {
    @DocumentId
    private String id = "";
    private String sport = "";
    private String teamA = "";
    private String teamB = "";
    private String venue = "";
    private long dateTime = 0L;
    private String status = "UPCOMING";
    private int scoreA = 0;
    private int scoreB = 0;
    private int wicketsA = 0;
    private int wicketsB = 0;
    private int ballsA = 0;
    private int ballsB = 0;
    private String winner = "";

    public Match() {}

    public Match(String id, String sport, String teamA, String teamB, String venue, long dateTime,
                 String status, int scoreA, int scoreB, int wicketsA, int wicketsB,
                 int ballsA, int ballsB, String winner) {
        this.id = id;
        this.sport = sport;
        this.teamA = teamA;
        this.teamB = teamB;
        this.venue = venue;
        this.dateTime = dateTime;
        this.status = status;
        this.scoreA = scoreA;
        this.scoreB = scoreB;
        this.wicketsA = wicketsA;
        this.wicketsB = wicketsB;
        this.ballsA = ballsA;
        this.ballsB = ballsB;
        this.winner = winner;
    }

    public Match(String sport, String teamA, String teamB, String venue, long dateTime) {
        this.sport = sport;
        this.teamA = teamA;
        this.teamB = teamB;
        this.venue = venue;
        this.dateTime = dateTime;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getSport() { return sport; }
    public void setSport(String sport) { this.sport = sport; }

    public String getTeamA() { return teamA; }
    public void setTeamA(String teamA) { this.teamA = teamA; }

    public String getTeamB() { return teamB; }
    public void setTeamB(String teamB) { this.teamB = teamB; }

    public String getVenue() { return venue; }
    public void setVenue(String venue) { this.venue = venue; }

    public long getDateTime() { return dateTime; }
    public void setDateTime(long dateTime) { this.dateTime = dateTime; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public int getScoreA() { return scoreA; }
    public void setScoreA(int scoreA) { this.scoreA = scoreA; }

    public int getScoreB() { return scoreB; }
    public void setScoreB(int scoreB) { this.scoreB = scoreB; }

    public int getWicketsA() { return wicketsA; }
    public void setWicketsA(int wicketsA) { this.wicketsA = wicketsA; }

    public int getWicketsB() { return wicketsB; }
    public void setWicketsB(int wicketsB) { this.wicketsB = wicketsB; }

    public int getBallsA() { return ballsA; }
    public void setBallsA(int ballsA) { this.ballsA = ballsA; }

    public int getBallsB() { return ballsB; }
    public void setBallsB(int ballsB) { this.ballsB = ballsB; }

    public String getWinner() { return winner; }
    public void setWinner(String winner) { this.winner = winner; }
}
