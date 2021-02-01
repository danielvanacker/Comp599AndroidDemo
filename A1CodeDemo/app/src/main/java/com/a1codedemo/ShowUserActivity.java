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

        // Some wiring to get the email and name sent in.
        Intent intent = getIntent();
        String email = intent.getStringExtra(SignInActivity.EMAIL_EXTRA);
        String name = intent.getStringExtra(SignInActivity.NAME_EXTRA);

        TextView emailText = findViewById(R.id.textUserEmail);
        TextView nameText = findViewById(R.id.textUserName);

        emailText.setText(email);
        nameText.setText(name);

        // Create another GoogleSignInClient to sign out.
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        // Add listeners for the buttons.
        findViewById(R.id.sign_out_button).setOnClickListener(this::onClick);
        findViewById(R.id.delete_account_button).setOnClickListener(this::onClick);
    }

    public void onClick(View v) {
        switch(v.getId()) {
            case R.id.sign_out_button:
                signOut();
                break;
            case R.id.delete_account_button:
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