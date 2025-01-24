package com.example.madproject;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class HomeFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        // Find the addItemButton
        View addItemButton = view.findViewById(R.id.addItemButton);

        // Set an OnClickListener to navigate to AddItemFragment
        addItemButton.setOnClickListener(v -> {
            requireActivity().getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, new AddItemFragment())
                    .addToBackStack(null) // Optional: Add to back stack for navigating back
                    .commit();
        });

        // Find the itemListButton
        View itemListButton = view.findViewById(R.id.itemListButton);

        // Set an OnClickListener to navigate to CategoryItemsFragment
        itemListButton.setOnClickListener(v -> {
            requireActivity().getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, new CategoryItemsFragment())
                    .addToBackStack(null) // Optional: Add to back stack for navigating back
                    .commit();
        });

        // Find the modifyItemButton
        View modifyItemButton = view.findViewById(R.id.modifyItemButton);

        // Set an OnClickListener to navigate to ModifyItemFragment
        modifyItemButton.setOnClickListener(v -> {
            requireActivity().getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, new ModifyItemFragment())
                    .addToBackStack(null) // Optional: Add to back stack for navigating back
                    .commit();
        });

        View reportButton = view.findViewById(R.id.reportButton);

        // Set an OnClickListener to navigate to ModifyItemFragment
        reportButton.setOnClickListener(v -> {
            requireActivity().getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, new GenerateReportFragment())
                    .addToBackStack(null) // Optional: Add to back stack for navigating back
                    .commit();
        });

        return view;
    }
}
