package com.a1codedemo;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONStringer;

import java.util.Objects;

public class SignInActivity extends AppCompatActivity {

    // Constants
    private final int RC_SIGN_IN = 99;
    private final String endpoint = "http://10.0.2.2:8080/verify";
    public static final String EMAIL_EXTRA = "com.a1codedemo.email";
    public static final String NAME_EXTRA = "com.a1codedemo.name";

    private GoogleSignInClient mGoogleSignInClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        // Configure sign-in to request the user's ID, email address, and basic profile.
        // More permissions can be requested.
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.server_client_id))
                .requestEmail()
                .build();

        // Build a GoogleSignInClient with the options specified by gso.
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        // Register a listener for the "Sign in with Google" button.
        findViewById(R.id.sign_in_button).setOnClickListener(this::onClick);

        // Set the dimensions of the sign-in button.
        SignInButton signInButton = findViewById(R.id.sign_in_button);
        signInButton.setSize(SignInButton.SIZE_STANDARD);
    }

    @Override
    protected void onStart() {
        super.onStart();
        // Check for existing Google Sign In account, if the user is already signed in
        // the GoogleSignInAccount will be non-null.
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
        updateUi(account, false);
    }

    // Updates the UI based on the status of signing in.
    private void updateUi(@Nullable GoogleSignInAccount account, boolean isInvalidAttempt) {
        if(Objects.isNull(account) && isInvalidAttempt) {
            // An error occurred somewhere along the way
            TextView errorMessage = findViewById(R.id.error_message);
            errorMessage.setText("Error logging in, please try again.");
        } else if(Objects.isNull(account)) {
            // Nothing happens here.
        } else {
            // Transition to the user info page. Include the user email and name.
            Intent intent = new Intent(this, ShowUserActivity.class);
            intent.putExtra(EMAIL_EXTRA, account.getEmail());
            intent.putExtra(NAME_EXTRA, account.getDisplayName());
            startActivity(intent);
        }
    }

    // The listener for the "Sign in with Google" button.
    public void onClick(View v) {
        switch(v.getId()) {
            case R.id.sign_in_button:
                signIn();
                break;
        }
    }

    // Google provides an Intent for signing in, we get it and use it.
    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN); // This will have a result.
    }

    // This is where we catch that result.
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInClient.getSignInIntent(...);
        if(requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }
    }

    // This is where we call our server to check if the user is new or not.
    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);
            String idToken = account.getIdToken();
            String json = String.format("{ \"token\": \"%s\"}", idToken);

            // Create request
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, endpoint, new JSONObject(json), new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    try {
                        String status = response.getString("status");
                        if("success".equals(status)) {
                            updateUi(account, true);
                        } else {
                            updateUi(null, true);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    // TODO: handle error
                    mGoogleSignInClient.signOut();
                    updateUi(null, true);
                }
            });

            // Send request
            SingletonRequestQueue.getInstance(this).addToRequestQueue(jsonObjectRequest);
        } catch (ApiException e) {
            Log.w("INVALID_LOGIN_REQUEST", e.getMessage());
            updateUi(null, true);
        } catch (JSONException e) {
            Log.w("INVALID_JSON", e.getMessage());
        } catch (Exception e) {
            Log.w("UNEXPECTED_EXCEPTION", e.getMessage());
        }
    }
}