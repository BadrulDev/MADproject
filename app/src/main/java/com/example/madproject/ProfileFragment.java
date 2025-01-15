package com.example.madproject;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class ProfileFragment extends Fragment {

    private TextView emailTextView;
    private SharedPreferences sharedPreferences;
    private DatabaseHelper databaseHelper;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        emailTextView = view.findViewById(R.id.profile_email);
        Button changeEmailButton = view.findViewById(R.id.change_email_button);
        Button changePasswordButton = view.findViewById(R.id.change_password_button);
        Button logoutButton = view.findViewById(R.id.logout_button);

        // Initialize SharedPreferences and DatabaseHelper
        sharedPreferences = requireContext().getSharedPreferences("UserPrefs", requireContext().MODE_PRIVATE);
        databaseHelper = new DatabaseHelper(getContext());

        // Retrieve the logged-in email from SharedPreferences
        String loggedInEmail = sharedPreferences.getString("logged_in_email", "user@example.com");
        if (loggedInEmail.equals("user@example.com")) {
            Toast.makeText(getContext(), "Email not found in SharedPreferences!", Toast.LENGTH_SHORT).show();
        } else {
            emailTextView.setText(loggedInEmail); // Display the logged-in email
        }

        // Set up button listeners
        changeEmailButton.setOnClickListener(v -> showChangeEmailDialog());
        changePasswordButton.setOnClickListener(v -> showChangePasswordDialog());
        logoutButton.setOnClickListener(v -> showLogoutConfirmationDialog());

        return view;
    }

    private void showChangeEmailDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        View dialogView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_change_email, null);
        builder.setView(dialogView);

        TextView currentEmailTextView = dialogView.findViewById(R.id.current_email);
        currentEmailTextView.setText(emailTextView.getText().toString());
        TextView newEmailEditText = dialogView.findViewById(R.id.new_email);

        builder.setTitle("Change Email")
                .setPositiveButton("Save", (dialog, which) -> {
                    String newEmail = newEmailEditText.getText().toString();
                    if (newEmail.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(newEmail).matches()) {
                        Toast.makeText(getContext(), "Invalid email address!", Toast.LENGTH_SHORT).show();
                    } else {
                        // Update email in database
                        String currentEmail = emailTextView.getText().toString();
                        boolean isUpdated = databaseHelper.updateUserEmail(currentEmail, newEmail);

                        if (isUpdated) {
                            // Update email in SharedPreferences
                            sharedPreferences.edit().putString("logged_in_email", newEmail).apply();
                            emailTextView.setText(newEmail);
                            Toast.makeText(getContext(), "Email updated successfully!", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(getContext(), "Failed to update email!", Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .setNegativeButton("Cancel", null)
                .create()
                .show();
    }

    private void showChangePasswordDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        View dialogView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_change_password, null);
        builder.setView(dialogView);

        TextView currentPasswordEditText = dialogView.findViewById(R.id.current_password);
        TextView newPasswordEditText = dialogView.findViewById(R.id.new_password);
        TextView confirmPasswordEditText = dialogView.findViewById(R.id.confirm_password);

        builder.setTitle("Change Password")
                .setPositiveButton("Save", (dialog, which) -> {
                    String currentPassword = currentPasswordEditText.getText().toString();
                    String newPassword = newPasswordEditText.getText().toString();
                    String confirmPassword = confirmPasswordEditText.getText().toString();

                    // Validate inputs
                    if (newPassword.isEmpty() || newPassword.length() < 6) {
                        Toast.makeText(getContext(), "Password must be at least 6 characters!", Toast.LENGTH_SHORT).show();
                    } else if (!newPassword.equals(confirmPassword)) {
                        Toast.makeText(getContext(), "Passwords do not match!", Toast.LENGTH_SHORT).show();
                    } else {
                        // Update password in database
                        String currentEmail = emailTextView.getText().toString();
                        boolean isUpdated = databaseHelper.updateUserPassword(currentEmail, newPassword);

                        if (isUpdated) {
                            Toast.makeText(getContext(), "Password updated successfully!", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(getContext(), "Failed to update password!", Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .setNegativeButton("Cancel", null)
                .create()
                .show();
    }

    private void showLogoutConfirmationDialog() {
        new AlertDialog.Builder(getContext())
                .setTitle("Logout")
                .setMessage("Are you sure you want to log out?")
                .setPositiveButton("Logout", (dialog, which) -> {
                    // Clear the logged-in email
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.remove("logged_in_email");
                    editor.apply();

                    // Navigate to MainActivity
                    Intent intent = new Intent(getContext(), MainActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);

                    Toast.makeText(getContext(), "Logged out successfully!", Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("Cancel", null)
                .create()
                .show();
    }
}
