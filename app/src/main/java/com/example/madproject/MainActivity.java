package com.example.madproject;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private SharedPreferences sharedPreferences;
    private ArrayList<String> registeredUsernames;

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

        // Load registered usernames
        registeredUsernames = loadRegisteredUsernames();

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
