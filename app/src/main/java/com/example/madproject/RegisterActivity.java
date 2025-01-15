package com.example.madproject;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class RegisterActivity extends AppCompatActivity {

    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);

        EditText usernameEditText = findViewById(R.id.usernameEditText);
        EditText passwordEditText = findViewById(R.id.passwordEditText);
        EditText confirmPasswordEditText = findViewById(R.id.confirmPasswordEditText);
        Button registerButton = findViewById(R.id.registerButton);

        registerButton.setOnClickListener(v -> {
            String username = usernameEditText.getText().toString();
            String password = passwordEditText.getText().toString();
            String confirmPassword = confirmPasswordEditText.getText().toString();

            // Check if fields are empty
            if (username.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
                Toast.makeText(RegisterActivity.this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            // Check if passwords match
            if (!password.equals(confirmPassword)) {
                Toast.makeText(RegisterActivity.this, "Passwords do not match", Toast.LENGTH_SHORT).show();
                return;
            }

            // Save account to SharedPreferences
            saveAccount(username, password);

            Toast.makeText(RegisterActivity.this, "Account Registered", Toast.LENGTH_SHORT).show();

            // Redirect to Login screen
            Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        });
    }

    private void saveAccount(String username, String password) {
        // Load existing accounts
        ArrayList<JSONObject> registeredAccounts = loadRegisteredAccounts();

        // Create new account object
        JSONObject newAccount = new JSONObject();
        try {
            newAccount.put("username", username);
            newAccount.put("password", password);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        // Add new account to the list
        registeredAccounts.add(newAccount);

        // Save the updated accounts list to SharedPreferences
        SharedPreferences.Editor editor = sharedPreferences.edit();

        // Convert list to JSON format
        JSONArray jsonArray = new JSONArray(registeredAccounts);
        editor.putString("registered_accounts", jsonArray.toString());
        editor.putString(username + "_password", password); // Save username and password
        editor.apply();
    }

    private ArrayList<JSONObject> loadRegisteredAccounts() {
        ArrayList<JSONObject> accounts = new ArrayList<>();
        String accountsJson = sharedPreferences.getString("registered_accounts", "[]");
        try {
            JSONArray jsonArray = new JSONArray(accountsJson);
            for (int i = 0; i < jsonArray.length(); i++) {
                accounts.add(jsonArray.getJSONObject(i));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return accounts;
    }
}
