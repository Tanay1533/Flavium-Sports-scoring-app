package com.example.flavium;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.EditorInfo;

import androidx.appcompat.app.AppCompatActivity;

import com.example.flavium.databinding.ActivityLoginBinding;
import com.google.firebase.FirebaseNetworkException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;

public class LoginActivity extends AppCompatActivity {
    private ActivityLoginBinding b;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        b = ActivityLoginBinding.inflate(getLayoutInflater());
        UiUtils.setContentWithBack(this, b.getRoot(), "Scorer login");

        b.btnLogin.setOnClickListener(v -> login());
        b.etPass.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                login();
                return true;
            }
            return false;
        });
    }

    private void login() {
        String email = b.etEmail.getText() != null ? b.etEmail.getText().toString().trim() : "";
        String pass = b.etPass.getText() != null ? b.etPass.getText().toString() : "";
        b.tvError.setVisibility(View.GONE);

        if (email.isEmpty() || pass.isEmpty()) {
            showError("Enter your email and password");
            return;
        }

        setLoading(true);
        FirebaseAuth auth = FirebaseAuth.getInstance();
        auth.signInWithEmailAndPassword(email, pass)
            .addOnSuccessListener(result -> {
                String uid = result.getUser() != null ? result.getUser().getUid() : null;
                if (uid == null) {
                    setLoading(false);
                    showError("Login failed. Please try again.");
                    return;
                }
                Repo.isAuthorisedScorer(uid, ok -> {
                    if (ok) {
                        onLoggedIn();
                    } else {
                        auth.signOut();
                        setLoading(false);
                        showError("This account is not authorised as a scorer. Ask the event admin to add you.");
                    }
                });
            })
            .addOnFailureListener(e -> {
                setLoading(false);
                String msg;
                if (e instanceof FirebaseNetworkException) {
                    msg = "No internet connection";
                } else if (e instanceof FirebaseAuthInvalidCredentialsException || e instanceof FirebaseAuthInvalidUserException) {
                    msg = "Incorrect email or password";
                } else {
                    msg = e.getMessage() != null ? e.getMessage() : "Login failed";
                }
                showError(msg);
            });
    }

    private void onLoggedIn() {
        if (getIntent().getBooleanExtra("goDashboard", true)) {
            startActivity(new Intent(this, DashboardActivity.class));
        }
        DataUtils.toast(this, "Welcome, scorer!");
        finish();
    }

    private void setLoading(boolean loading) {
        b.btnLogin.setEnabled(!loading);
        b.progress.setVisibility(loading ? View.VISIBLE : View.GONE);
    }

    private void showError(String msg) {
        b.tvError.setText(msg);
        b.tvError.setVisibility(View.VISIBLE);
    }
}
