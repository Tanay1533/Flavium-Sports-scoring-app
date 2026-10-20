package com.example.flavium;

public class PointsRow {
    private String sport = "";
    private String team = "";
    private int played = 0;
    private int won = 0;
    private int lost = 0;
    private int tied = 0;
    private int points = 0;

    public PointsRow() {}

    public PointsRow(String team) {
        this.team = team;
    }

    public PointsRow(String sport, String team) {
        this.sport = sport;
        this.team = team;
    }

    public PointsRow(String sport, String team, int played, int won, int lost, int tied, int points) {
        this.sport = sport;
        this.team = team;
        this.played = played;
        this.won = won;
        this.lost = lost;
        this.tied = tied;
        this.points = points;
    }

    public PointsRow after(boolean win, boolean tie) {
        return new PointsRow(
            this.sport,
            this.team,
            this.played + 1,
            this.won + (win ? 1 : 0),
            this.lost + (!win && !tie ? 1 : 0),
            this.tied + (tie ? 1 : 0),
            this.points + (win ? 2 : (tie ? 1 : 0))
        );
    }

    public String getSport() { return sport; }
    public void setSport(String sport) { this.sport = sport; }

    public String getTeam() { return team; }
    public void setTeam(String team) { this.team = team; }

    public int getPlayed() { return played; }
    public void setPlayed(int played) { this.played = played; }

    public int getWon() { return won; }
    public void setWon(int won) { this.won = won; }

    public int getLost() { return lost; }
    public void setLost(int lost) { this.lost = lost; }

    public int getTied() { return tied; }
    public void setTied(int tied) { this.tied = tied; }

    public int getPoints() { return points; }
    public void setPoints(int points) { this.points = points; }
}
