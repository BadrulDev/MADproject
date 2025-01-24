package com.example.madproject;

import android.annotation.SuppressLint;
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

public class ModifyItemFragment extends Fragment {

    private ArrayList<InventoryItem> itemList;
    private InventoryAdapter adapter;
    private DatabaseHelper databaseHelper;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_modify_items, container, false);

        // Initialize DatabaseHelper
        databaseHelper = new DatabaseHelper(requireContext());

        // Initialize item list and load data from database
        itemList = new ArrayList<>();
        loadItemsByCategory("All"); // Default to all categories

        // Setup ListView
        @SuppressLint({"MissingInflatedId", "LocalSuppress"}) ListView itemListView = view.findViewById(R.id.item_list);
        adapter = new InventoryAdapter(requireContext(), itemList);
        itemListView.setAdapter(adapter);

        // Setup Spinner for categories
        @SuppressLint({"MissingInflatedId", "LocalSuppress"}) Spinner categorySpinner = view.findViewById(R.id.category_spinner);
        String[] categories = {
                "All", "Engine Component", "Transmission", "Electrical System", "Suspension and Steering",
                "Braking System", "Cooling System", "Fuel System", "Body and Exterior",
                "Interior Components", "Wheels and Tires", "Accessories", "Maintenance Supplies"
        };

        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, categories);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        categorySpinner.setAdapter(spinnerAdapter);

        categorySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedCategory = categories[position];
                loadItemsByCategory(selectedCategory);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        // Edit and remove functionality
        itemListView.setOnItemClickListener((parent, itemView, position, id) -> showEditRemoveDialog(position));

        return view;
    }

    private void loadItemsByCategory(String category) {
        itemList.clear();
        Cursor cursor;
        if (category.equals("All")) {
            cursor = databaseHelper.getAllItems();
        } else {
            cursor = databaseHelper.getItemsByCategory(category); // No error now
        }

        if (cursor != null && cursor.moveToFirst()) {
            do {
                @SuppressLint("Range") int id = cursor.getInt(cursor.getColumnIndex("id"));
                @SuppressLint("Range") String name = cursor.getString(cursor.getColumnIndex("name"));
                @SuppressLint("Range") String itemCategory = cursor.getString(cursor.getColumnIndex("category"));
                @SuppressLint("Range") int quantity = cursor.getInt(cursor.getColumnIndex("quantity"));
                itemList.add(new InventoryItem(id, name, itemCategory, quantity));
            } while (cursor.moveToNext());
            cursor.close();
        }
    }


    private void showEditRemoveDialog(int position) {
        InventoryItem item = itemList.get(position);

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        View dialogView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_add_edit_item, null);
        builder.setView(dialogView);

        EditText nameEditText = dialogView.findViewById(R.id.edit_item_name);
        Spinner categorySpinner = dialogView.findViewById(R.id.spinner_item_category);
        EditText quantityEditText = dialogView.findViewById(R.id.edit_item_quantity);

        // Predefined categories
        String[] categories = {
                "Engine Component", "Transmission", "Electrical System", "Suspension and Steering",
                "Braking System", "Cooling System", "Fuel System", "Body and Exterior",
                "Interior Components", "Wheels and Tires", "Accessories", "Maintenance Supplies"
        };

        ArrayAdapter<String> categoryAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, categories);
        categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        categorySpinner.setAdapter(categoryAdapter);

        // Populate fields with existing data
        nameEditText.setText(item.getName());
        quantityEditText.setText(String.valueOf(item.getQuantity()));

        int categoryPosition = categoryAdapter.getPosition(item.getCategory());
        categorySpinner.setSelection(categoryPosition);

        builder.setTitle("Edit Item")
                .setPositiveButton("Save", (dialog, which) -> {
                    String newName = nameEditText.getText().toString();
                    String newCategory = categorySpinner.getSelectedItem().toString();
                    int newQuantity = Integer.parseInt(quantityEditText.getText().toString());

                    if (databaseHelper.updateItem(item.getId(), newName, newCategory, newQuantity)) {
                        loadItemsByCategory(newCategory);
                        adapter.notifyDataSetChanged();
                        Toast.makeText(getContext(), "Item Updated", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getContext(), "Error Updating Item", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNeutralButton("Remove", (dialog, which) -> {
                    if (databaseHelper.deleteItem(item.getId())) {
                        loadItemsByCategory("All");
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
