package com.example.flavium;

import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Repo {

    public interface OnMatchesChangeListener {
        void onChange(List<Match> matches);
    }

    public interface OnMatchChangeListener {
        void onChange(Match match);
    }

    public interface OnPointsChangeListener {
        void onChange(List<PointsRow> points);
    }

    public interface OnBooleanResultListener {
        void onResult(boolean result);
    }

    public static boolean isAdmin() {
        return FirebaseAuth.getInstance().getCurrentUser() != null;
    }

    private static FirebaseFirestore getDb() {
        return FirebaseFirestore.getInstance();
    }

    public static ListenerRegistration listenMatches(String sport, OnMatchesChangeListener listener) {
        return getDb().collection("matches")
                .whereEqualTo("sport", sport)
                .addSnapshotListener((snap, e) -> {
                    if (snap != null) {
                        listener.onChange(snap.toObjects(Match.class));
                    } else {
                        listener.onChange(Collections.emptyList());
                    }
                });
    }

    public static ListenerRegistration listenLive(OnMatchesChangeListener listener) {
        return getDb().collection("matches")
                .whereEqualTo("status", "LIVE")
                .addSnapshotListener((snap, e) -> {
                    if (snap != null) {
                        List<Match> list = snap.toObjects(Match.class);
                        Collections.sort(list, Comparator.comparingLong(Match::getDateTime));
                        listener.onChange(list);
                    } else {
                        listener.onChange(Collections.emptyList());
                    }
                });
    }

    public static ListenerRegistration listenMatch(String id, OnMatchChangeListener listener) {
        return getDb().collection("matches").document(id)
                .addSnapshotListener((snap, e) -> {
                    if (snap != null) {
                        listener.onChange(snap.toObject(Match.class));
                    } else {
                        listener.onChange(null);
                    }
                });
    }

    public static ListenerRegistration listenPoints(OnPointsChangeListener listener) {
        return getDb().collection("points")
                .addSnapshotListener((snap, e) -> {
                    if (snap != null) {
                        listener.onChange(snap.toObjects(PointsRow.class));
                    } else {
                        listener.onChange(Collections.emptyList());
                    }
                });
    }

    public static void isAuthorisedScorer(String uid, OnBooleanResultListener onResult) {
        getDb().collection("admins").document(uid).get()
                .addOnSuccessListener(snap -> onResult.onResult(snap.exists()))
                .addOnFailureListener(e -> onResult.onResult(false));
    }

    public static Task<DocumentReference> schedule(Match m) {
        return getDb().collection("matches").add(m);
    }

    public static Task<Void> setStatus(String id, String status) {
        return getDb().collection("matches").document(id).update("status", status);
    }

    public static Task<Void> add(String id, String side, long runs, long wicket, long ball) {
        Map<String, Object> updates = new HashMap<>();
        updates.put("score" + side, FieldValue.increment(runs));
        updates.put("wickets" + side, FieldValue.increment(wicket));
        updates.put("balls" + side, FieldValue.increment(ball));
        return getDb().collection("matches").document(id).update(updates);
    }

    public static Task<Void> finish(String id) {
        DocumentReference matchRef = getDb().collection("matches").document(id);
        return getDb().runTransaction(tx -> {
            Match m = tx.get(matchRef).toObject(Match.class);
            if (m != null && !"COMPLETED".equals(m.getStatus())) {
                DocumentReference refA = getDb().collection("points").document(m.getSport() + "_" + m.getTeamA());
                DocumentReference refB = getDb().collection("points").document(m.getSport() + "_" + m.getTeamB());

                PointsRow a = tx.get(refA).toObject(PointsRow.class);
                if (a == null) a = new PointsRow(m.getSport(), m.getTeamA());

                PointsRow b = tx.get(refB).toObject(PointsRow.class);
                if (b == null) b = new PointsRow(m.getSport(), m.getTeamB());

                String winner;
                if (m.getScoreA() > m.getScoreB()) {
                    winner = m.getTeamA();
                } else if (m.getScoreB() > m.getScoreA()) {
                    winner = m.getTeamB();
                } else {
                    winner = "";
                }

                tx.set(refA, a.after(winner.equals(m.getTeamA()), winner.isEmpty()));
                tx.set(refB, b.after(winner.equals(m.getTeamB()), winner.isEmpty()));

                Map<String, Object> updates = new HashMap<>();
                updates.put("status", "COMPLETED");
                updates.put("winner", winner);
                tx.update(matchRef, updates);
            }
            return null;
        });
    }
}
