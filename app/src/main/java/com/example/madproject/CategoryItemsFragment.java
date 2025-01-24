package com.example.madproject;

import android.annotation.SuppressLint;
import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class CategoryItemsFragment extends Fragment {

    private ExpandableListView expandableListView;
    private ExpandableListAdapter expandableListAdapter;
    private List<String> categoryList;
    private HashMap<String, List<InventoryItem>> categoryItemsMap;
    private DatabaseHelper databaseHelper;

    @SuppressLint("MissingInflatedId")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_category_items, container, false);

        // Initialize database helper
        databaseHelper = new DatabaseHelper(requireContext());

        // Initialize category list and map
        categoryList = new ArrayList<>();
        categoryItemsMap = new HashMap<>();

        // Load items grouped by category from the database
        loadItemsByCategory();

        // Setup ExpandableListView
        expandableListView = view.findViewById(R.id.expandable_list_view);
        expandableListAdapter = new CategoryExpandableListAdapter(requireContext(), categoryList, categoryItemsMap);
        expandableListView.setAdapter(expandableListAdapter);

        // Handle item clicks
        expandableListView.setOnChildClickListener((parent, v, groupPosition, childPosition, id) -> {
            InventoryItem selectedItem = categoryItemsMap.get(categoryList.get(groupPosition)).get(childPosition);
            Toast.makeText(getContext(), "Selected: " + selectedItem.getName(), Toast.LENGTH_SHORT).show();
            return true;
        });

        return view;
    }

    private void loadItemsByCategory() {
        Cursor cursor = databaseHelper.getAllItems();
        if (cursor != null && cursor.moveToFirst()) {
            do {
                @SuppressLint("Range") String category = cursor.getString(cursor.getColumnIndex("category"));
                @SuppressLint("Range") int id = cursor.getInt(cursor.getColumnIndex("id"));
                @SuppressLint("Range") String name = cursor.getString(cursor.getColumnIndex("name"));
                @SuppressLint("Range") int quantity = cursor.getInt(cursor.getColumnIndex("quantity"));

                InventoryItem item = new InventoryItem(id, name, category, quantity);

                if (!categoryList.contains(category)) {
                    categoryList.add(category);
                    categoryItemsMap.put(category, new ArrayList<>());
                }

                categoryItemsMap.get(category).add(item);
            } while (cursor.moveToNext());
            cursor.close();
        }
    }
}
