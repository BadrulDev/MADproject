package com.example.madproject;

import android.app.AlertDialog;
import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.util.ArrayList;

public class InventoryFragment extends Fragment {

    private ArrayList<InventoryItem> inventoryList;
    private InventoryAdapter adapter;
    private DatabaseHelper databaseHelper;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_inventory, container, false);

        // Initialize DatabaseHelper
        databaseHelper = new DatabaseHelper(requireContext());

        // Initialize inventory list and load data from database
        inventoryList = new ArrayList<>();
        loadInventoryFromDatabase();

        // Setup ListView
        ListView inventoryListView = view.findViewById(R.id.inventory_list);
        adapter = new InventoryAdapter(requireContext(), inventoryList);
        inventoryListView.setAdapter(adapter);

        // Add new item functionality
        Button addItemButton = view.findViewById(R.id.add_item_button);
        addItemButton.setOnClickListener(v -> showAddItemDialog());

        // Edit and remove functionality
        inventoryListView.setOnItemClickListener((parent, itemView, position, id) -> showEditRemoveDialog(position));

        return view;
    }

    private void loadInventoryFromDatabase() {
        inventoryList.clear();
        Cursor cursor = databaseHelper.getAllItems();
        if (cursor != null && cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(cursor.getColumnIndex("id"));
                String name = cursor.getString(cursor.getColumnIndex("name"));
                String category = cursor.getString(cursor.getColumnIndex("category"));
                int quantity = cursor.getInt(cursor.getColumnIndex("quantity"));
                inventoryList.add(new InventoryItem(id, name, category, quantity));
            } while (cursor.moveToNext());
            cursor.close();
        }
    }

    private void showAddItemDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        View dialogView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_add_edit_item, null);
        builder.setView(dialogView);

        EditText nameEditText = dialogView.findViewById(R.id.edit_item_name);
        Spinner categorySpinner = dialogView.findViewById(R.id.spinner_item_category); // Use Spinner for category
        EditText quantityEditText = dialogView.findViewById(R.id.edit_item_quantity);

        // Predefined categories
        String[] categories = {
                "Engine Component", "Transmission", "Electrical System", "Suspension and Steering",
                "Braking System", "Cooling System", "Fuel System", "Body and Exterior",
                "Interior Components", "Wheels and Tires", "Accessories", "Maintenance Supplies"
        };

        // Set up Spinner with categories
        ArrayAdapter<String> categoryAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, categories);
        categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        categorySpinner.setAdapter(categoryAdapter);

        builder.setTitle("Add New Item")
                .setPositiveButton("Add", (dialog, which) -> {
                    String name = nameEditText.getText().toString();
                    String category = categorySpinner.getSelectedItem().toString(); // Get selected category
                    int quantity = Integer.parseInt(quantityEditText.getText().toString());

                    if (databaseHelper.insertItem(name, category, quantity)) {
                        loadInventoryFromDatabase();
                        adapter.notifyDataSetChanged();
                        Toast.makeText(getContext(), "Item Added", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getContext(), "Error Adding Item", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Cancel", null)
                .create()
                .show();
    }


    private void showEditRemoveDialog(int position) {
        InventoryItem item = inventoryList.get(position);

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        View dialogView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_add_edit_item, null);
        builder.setView(dialogView);

        EditText nameEditText = dialogView.findViewById(R.id.edit_item_name);
        Spinner categorySpinner = dialogView.findViewById(R.id.spinner_item_category); // Use Spinner for category
        EditText quantityEditText = dialogView.findViewById(R.id.edit_item_quantity);

        // Predefined categories
        String[] categories = {
                "Engine Component", "Transmission", "Electrical System", "Suspension and Steering",
                "Braking System", "Cooling System", "Fuel System", "Body and Exterior",
                "Interior Components", "Wheels and Tires", "Accessories", "Maintenance Supplies"
        };

        // Set up Spinner with categories
        ArrayAdapter<String> categoryAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, categories);
        categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        categorySpinner.setAdapter(categoryAdapter);

        // Populate fields with existing data
        nameEditText.setText(item.getName());
        quantityEditText.setText(String.valueOf(item.getQuantity()));

        // Set the correct category in the Spinner
        int categoryPosition = categoryAdapter.getPosition(item.getCategory());
        categorySpinner.setSelection(categoryPosition);

        builder.setTitle("Edit Item")
                .setPositiveButton("Save", (dialog, which) -> {
                    String newName = nameEditText.getText().toString();
                    String newCategory = categorySpinner.getSelectedItem().toString();
                    int newQuantity = Integer.parseInt(quantityEditText.getText().toString());

                    if (databaseHelper.updateItem(item.getId(), newName, newCategory, newQuantity)) {
                        loadInventoryFromDatabase();
                        adapter.notifyDataSetChanged();
                        Toast.makeText(getContext(), "Item Updated", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getContext(), "Error Updating Item", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNeutralButton("Remove", (dialog, which) -> {
                    if (databaseHelper.deleteItem(item.getId())) {
                        loadInventoryFromDatabase();
                        adapter.notifyDataSetChanged();
                        Toast.makeText(getContext(), "Item Removed", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getContext(), "Error Removing Item", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Cancel", null)
                .create()
                .show();
    }

}
