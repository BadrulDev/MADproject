package com.example.madproject;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import com.google.android.gms.auth.api.signin.*;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private SharedPreferences sharedPreferences;
    private ArrayList<String> registeredUsernames;

    private static final int RC_SIGN_IN = 9001;
    private GoogleSignInClient mGoogleSignInClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);

        // Initialize UI components
        EditText usernameEditText = findViewById(R.id.usernameEditText);
        EditText passwordEditText = findViewById(R.id.passwordEditText);
        Button loginButton = findViewById(R.id.loginButton);
        Button registerButton = findViewById(R.id.registerButton);

        SignInButton googleLoginButton = findViewById(R.id.loginGoogle);

        // Load registered usernames
        registeredUsernames = loadRegisteredUsernames();

        // Configure Google Sign-In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        // Login button click
        loginButton.setOnClickListener(v -> {
            String enteredUsername = usernameEditText.getText().toString();
            String password = passwordEditText.getText().toString();

            if (!registeredUsernames.contains(enteredUsername)) {
                Toast.makeText(this, "Username not registered", Toast.LENGTH_SHORT).show();
                return;
            }

            // Retrieve saved password and email for the username
            String savedPassword = sharedPreferences.getString(enteredUsername + "_password", "");
            String savedEmail = sharedPreferences.getString(enteredUsername + "_email", "");

            if (password.equals(savedPassword)) {
                // Save logged-in email to SharedPreferences
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("logged_in_username", enteredUsername);
                editor.putString("logged_in_email", savedEmail); // Save the logged-in user's email
                editor.apply();

                // Navigate to HomePageActivity
                Intent intent = new Intent(MainActivity.this, HomePageActivity.class);
                startActivity(intent);
                finish();
            } else {
                Toast.makeText(this, "Invalid username or password", Toast.LENGTH_SHORT).show();
            }
        });


        // Register button click (Navigate to RegisterActivity)
        registerButton.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, RegisterActivity.class);
            startActivity(intent);
        });

        googleLoginButton.setOnClickListener(v -> signOutAndSignInWithGoogle());
    }

    private void signOutAndSignInWithGoogle() {
        mGoogleSignInClient.signOut().addOnCompleteListener(this, task -> {
            // Once sign out is complete, initiate sign in
            signInWithGoogle();
        });
    }

    private void signInWithGoogle() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInClient.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }
    }

    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);

            // Signed in successfully, show authenticated UI.
            String email = account.getEmail();
            String username = account.getDisplayName();

            // Save logged-in email to SharedPreferences
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("logged_in_email", email);
            editor.putString("logged_in_username", username);
            editor.apply();

            Toast.makeText(this, "Login As: " + email, Toast.LENGTH_SHORT).show();

            // Navigate to HomePageActivity
            Intent intent = new Intent(MainActivity.this, HomePageActivity.class);
            startActivity(intent);
            finish();
        } catch (ApiException e) {
            Toast.makeText(this, "Google Sign-In signInResult:failed code=" + e.getStatusCode(), Toast.LENGTH_SHORT).show();
        }
    }


    private ArrayList<String> loadRegisteredUsernames() {
        ArrayList<String> usernames = new ArrayList<>();
        String accountsJson = sharedPreferences.getString("registered_accounts", "[]");
        try {
            JSONArray jsonArray = new JSONArray(accountsJson);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject account = jsonArray.getJSONObject(i);
                usernames.add(account.getString("username"));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return usernames;
    }
}
