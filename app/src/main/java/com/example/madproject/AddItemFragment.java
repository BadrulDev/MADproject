package com.example.madproject;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class AddItemFragment extends Fragment {

    private DatabaseHelper databaseHelper;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.add_item, container, false);

        databaseHelper = new DatabaseHelper(requireContext());

        // Initialize input fields
        EditText nameEditText = view.findViewById(R.id.edit_item_name);
        Spinner categorySpinner = view.findViewById(R.id.spinner_item_category);
        EditText quantityEditText = view.findViewById(R.id.edit_item_quantity);

        // Predefined categories
        String[] categories = {
                "Engine Component", "Transmission", "Electrical System", "Suspension and Steering",
                "Braking System", "Cooling System", "Fuel System", "Body and Exterior",
                "Interior Components", "Wheels and Tires", "Accessories", "Maintenance Supplies"
        };

        // Set up Spinner with categories
        ArrayAdapter<String> categoryAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, categories);
        categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        categorySpinner.setAdapter(categoryAdapter);

        // Set up the Add button
        view.findViewById(R.id.button_add_item).setOnClickListener(v -> {
            String name = nameEditText.getText().toString();
            String category = categorySpinner.getSelectedItem().toString();
            int quantity;

            if (TextUtils.isEmpty(name)) {
                Toast.makeText(requireContext(), "Please enter the item name", Toast.LENGTH_SHORT).show();
                return;
            }

            try {
                quantity = Integer.parseInt(quantityEditText.getText().toString());
            } catch (NumberFormatException e) {
                Toast.makeText(requireContext(), "Invalid quantity", Toast.LENGTH_SHORT).show();
                return;
            }

            // Insert item into the database
            if (databaseHelper.insertItem(name, category, quantity)) {
                Toast.makeText(requireContext(), "Item Added", Toast.LENGTH_SHORT).show();
                requireActivity().onBackPressed(); // Navigate back
            } else {
                Toast.makeText(requireContext(), "Error Adding Item", Toast.LENGTH_SHORT).show();
            }
        });

        return view;
    }
}
