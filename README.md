# 🏆 Flavium - Sports Scoring & Live Standings App

Flavium is a native Android sports management application built in **Java** and powered by **Firebase Firestore**. It provides real-time match scoring, live scoreboards, match scheduling, and automatically updated standings for multi-sport college tournaments.

---

## 📱 Features

- **Dual Mode Access**:
  - **Viewer Mode**: Follow live scores, match schedules, detailed scorecards, and branch/year standings without needing an account.
  - **Scorer Mode**: Authorized scorers can log in to schedule fixtures, start/end matches, record live scores, and undo scoring inputs.
- **Multi-Sport Support**:
  - 🏏 **Cricket**: Tracks runs, wickets, and overs with dynamic scoreboard formatting (`45/2 (4.1 ov)`).
  - ⚽ **Football**: Tracks goals.
  - 🏀 **Basketball**: Supports Free Throws (+1), 2-Pointers (+2), and 3-Pointers (+3).
  - 🏸 **Badminton** & 🏐 **Volleyball**: Tracks rally points.
- **Real-Time Synchronisation**: Uses Firebase Firestore snapshot listeners to broadcast score updates instantly across all connected phones.
- **Points & Leaderboard**:
  - Automatic calculation: **2 points** for a win, **1 point** for a tie.
  - Dynamic filtering by **Sport**, **Branch** (CE, CS, IT, AIML, MBATech), and **Year** (1st, 2nd, 3rd year).
  - Medals (🥇, 🥈, 🥉) highlight top-ranked teams.
- **Modern UI**: Built with Material Design 3 components, View Binding, custom status/navigation bar inset padding, and responsive layouts.

---

## 🛠 Tech Stack

- **Language**: Java 11
- **UI Framework**: Android ViewBinding, Material Components, RecyclerView, ConstraintLayout
- **Backend & Auth**: Firebase Firestore (Realtime DB), Firebase Authentication
- **Minimum SDK**: API 24 (Android 7.0)
- **Target SDK**: API 36 (Android 16)

---

## 🏗 Architecture & Project Structure

```
com.example.flavium
├── Config.java           # Sports, teams, emojis, and tagline definitions
├── DataUtils.java        # Helper functions for scores, dates, team labels & toasts
├── Match.java            # Firestore data model for matches
├── PointsRow.java        # Firestore data model for leaderboard standings
├── Repo.java             # Firestore repository with snapshot listeners and transactions
├── UiUtils.java          # Window insets, toolbar wrappers & scoreboard rendering
├── MatchAdapter.java     # RecyclerView adapter for match lists
├── SportAdapter.java     # RecyclerView adapter for sport category cards
├── PointsAdapter.java    # RecyclerView adapter for leaderboard standings
└── Activities
    ├── MainActivity.java      # Welcome screen (Role choice)
    ├── DashboardActivity.java # Main home dashboard
    ├── LoginActivity.java     # Scorer login screen
    ├── MatchListActivity.java # Filtered matches list
    ├── PointsActivity.java    # Leaderboard & points table
    ├── ScheduleActivity.java  # Match scheduling screen
    ├── ScorecardActivity.java # Viewer live scorecard
    └── ScoringActivity.java   # Scorer live controls
```

---

## 🚀 Getting Started

### Prerequisites
- Android Studio Ladybug or newer
- JDK 11
- Firebase account with Firestore & Auth enabled

### Setup Instructions
1. **Clone the Repository**:
   ```bash
   git clone https://github.com/Tanay1533/Flavium-Sports-scoring-app.git
   cd Flavium-Sports-scoring-app
   ```
2. **Add `google-services.json`**:
   - Create a project on [Firebase Console](https://console.firebase.google.com/).
   - Add an Android app with package name `com.example.flavium`.
   - Download `google-services.json` and place it inside the `app/` directory.
3. **Open & Build**:
   - Open the project in Android Studio.
   - Sync Gradle project files and run on an emulator or physical device.

---

## 📜 License

This project is open source and available under the [MIT License](LICENSE).
