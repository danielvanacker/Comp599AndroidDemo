package com.a1codedemo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

public class ShowUserActivity extends AppCompatActivity {
    private GoogleSignInClient mGoogleSignInClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_user);
        displayUserInfo();

        // Create another GoogleSignInClient to sign out.
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        // Add listeners for the buttons.
        findViewById(R.id.buttonSignOut).setOnClickListener(this::onClick);
        findViewById(R.id.buttonDeleteAccount).setOnClickListener(this::onClick);
    }

    // Displays user email and name.
    private void displayUserInfo() {
        // Some wiring to get the email and name sent in.
        Intent intent = getIntent();
        String email = intent.getStringExtra(SignInActivity.EMAIL_EXTRA);
        String name = intent.getStringExtra(SignInActivity.NAME_EXTRA);

        TextView emailText = findViewById(R.id.textUserEmail);
        TextView nameText = findViewById(R.id.textUserName);

        emailText.setText(email);
        nameText.setText(name);
    }

    public void onClick(View v) {
        switch(v.getId()) {
            case R.id.buttonSignOut:
                signOut();
                break;
            case R.id.buttonDeleteAccount:
                deleteAccount();
                break;
        }
    }

    private void signOut() {
        mGoogleSignInClient.signOut()
                .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                    // This is the function that will be called once a user is logged out.
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        returnToSignInPage();
                    }
                });

    }

    private void deleteAccount() {
        mGoogleSignInClient.revokeAccess()
                .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        // TODO: some stuff on server
                        returnToSignInPage();
                    }
                });
    }

    private void returnToSignInPage() {
        Intent intent = new Intent(this, SignInActivity.class);
        startActivity(intent);
    }
}