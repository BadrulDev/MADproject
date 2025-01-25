package com.example.madproject;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.pdf.PdfDocument;
import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class GenerateReportFragment extends Fragment {

    private DatabaseHelper databaseHelper;
    private TextView reportTextView;

    @SuppressLint("MissingInflatedId")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_generate_report, container, false);

        // Initialize DatabaseHelper
        databaseHelper = new DatabaseHelper(requireContext());

        // Initialize UI elements
        reportTextView = view.findViewById(R.id.report_text_view);
        Button generateReportButton = view.findViewById(R.id.generate_report_button);
        Button generatePdfButton = view.findViewById(R.id.generate_pdf_button);

        // Generate report on button click
        generateReportButton.setOnClickListener(v -> generateReport());

        // Generate PDF on button click
        generatePdfButton.setOnClickListener(v -> generatePdf());

        return view;
    }

    @SuppressLint("SetTextI18n")
    private void generateReport() {
        Cursor cursor = databaseHelper.getAllItems();

        if (cursor == null || cursor.getCount() == 0) {
            Toast.makeText(requireContext(), "No items to display in the report.", Toast.LENGTH_SHORT).show();
            return;
        }

        StringBuilder reportBuilder = new StringBuilder();
        reportBuilder.append("Inventory Report:\n\n");
        reportBuilder.append(String.format("%-5s %-20s %-20s %s\n", "ID", "Name", "Category", "Quantity"));
        reportBuilder.append("------------------------------------------------------------\n");

        while (cursor.moveToNext()) {
            @SuppressLint("Range") int id = cursor.getInt(cursor.getColumnIndex("id"));
            @SuppressLint("Range") String name = cursor.getString(cursor.getColumnIndex("name"));
            @SuppressLint("Range") String category = cursor.getString(cursor.getColumnIndex("category"));
            @SuppressLint("Range") int quantity = cursor.getInt(cursor.getColumnIndex("quantity"));

            reportBuilder.append(String.format("%-5d %-20s %-20s %d\n", id, name, category, quantity));
        }

        cursor.close();

        // Display report in the TextView
        reportTextView.setText(reportBuilder.toString());
        Toast.makeText(requireContext(), "Report generated successfully!", Toast.LENGTH_SHORT).show();
    }

    private void generatePdf() {
        if (reportTextView.getText().toString().isEmpty()) {
            Toast.makeText(requireContext(), "Please generate the report first!", Toast.LENGTH_SHORT).show();
            return;
        }

        PdfDocument pdfDocument = new PdfDocument();
        Paint paint = new Paint();
        PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(595, 842, 1).create();
        PdfDocument.Page page = pdfDocument.startPage(pageInfo);
        Canvas canvas = page.getCanvas();
        canvas.drawText("Inventory Report", 50, 50, paint);

        String[] reportLines = reportTextView.getText().toString().split("\n");
        int y = 100;

        for (String line : reportLines) {
            canvas.drawText(line, 50, y, paint);
            y += 20;
        }

        pdfDocument.finishPage(page);

        // Use app-specific directory
        File directory = new File(requireContext().getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS), "Reports");
        if (!directory.exists()) {
            directory.mkdirs();
        }

        File file = new File(directory, "Inventory_Report.pdf");
        try {
            pdfDocument.writeTo(new FileOutputStream(file));
            Toast.makeText(requireContext(), "PDF saved at: " + file.getAbsolutePath(), Toast.LENGTH_LONG).show();
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(requireContext(), "Failed to save PDF: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        } finally {
            pdfDocument.close();
        }
    }

}
