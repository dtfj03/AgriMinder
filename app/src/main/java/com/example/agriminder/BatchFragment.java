package com.example.agriminder;

import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.fragment.app.Fragment;

public class BatchFragment extends Fragment {

    private BatchDatabase dbHelper;
    private LinearLayout batchLayout;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_batch, container, false);

        // Initialize database helper
        dbHelper = new BatchDatabase(requireContext());

        // Find the layout to display batches
        batchLayout = view.findViewById(R.id.batchLayout);

        // Fetch and display batches
        displayBatches();

        // Handle Add Batch button
        Button addBatchButton = view.findViewById(R.id.addBatch);
        addBatchButton.setOnClickListener(v -> {
            AddBatchDialog dialog = new AddBatchDialog();
            dialog.setTargetFragment(BatchFragment.this, 0);
            dialog.show(getParentFragmentManager(), "AddBatchDialog");
        });

        return view;
    }

    // Function to display batches dynamically
    private void displayBatches() {
        batchLayout.removeAllViews();  // Clear previous views
        Cursor cursor = dbHelper.getAllBatches();

        if (cursor != null && cursor.moveToFirst()) {
            do {
                String batchName = cursor.getString(cursor.getColumnIndexOrThrow("Batch_Name"));
                String batchType = cursor.getString(cursor.getColumnIndexOrThrow("Batch_Type"));
                String batchDOB = cursor.getString(cursor.getColumnIndexOrThrow("Batch_DateofBirth"));

                // Inflate batch item view
                View batchView = LayoutInflater.from(getContext()).inflate(R.layout.batch_item, null);
                ((TextView) batchView.findViewById(R.id.batchName)).setText(batchName);
                ((TextView) batchView.findViewById(R.id.batchType)).setText(batchType);
                ((TextView) batchView.findViewById(R.id.batchDOB)).setText("Date of Birth: " + batchDOB);

                // Add the view to layout
                batchLayout.addView(batchView);
            } while (cursor.moveToNext());
        }

        if (cursor != null) {
            cursor.close();
        }
    }

    public void refreshBatchList() {
        displayBatches();  // Refresh the list when a new batch is added
    }
}
