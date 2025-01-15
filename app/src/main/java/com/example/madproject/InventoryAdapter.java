package com.example.madproject;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.widget.ArrayAdapter;
import java.util.List;

public class InventoryAdapter extends ArrayAdapter<InventoryItem> {

    public InventoryAdapter(Context context, List<InventoryItem> inventoryList) {
        super(context, 0, inventoryList);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_inventory, parent, false);
        }

        InventoryItem currentItem = getItem(position);

        TextView nameTextView = convertView.findViewById(R.id.item_name);
        TextView categoryTextView = convertView.findViewById(R.id.item_category);
        TextView quantityTextView = convertView.findViewById(R.id.item_quantity);

        nameTextView.setText(currentItem.getName());
        categoryTextView.setText(currentItem.getCategory());
        quantityTextView.setText(String.valueOf(currentItem.getQuantity()));

        return convertView;
    }
}
