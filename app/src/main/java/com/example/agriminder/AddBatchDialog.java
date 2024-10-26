package com.example.agriminder;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

public class AddBatchDialog extends DialogFragment {

    private EditText batchNameInput, batchTypeInput, batchDOBInput;
    private BatchDatabase dbHelper;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_add_batch, container, false);

        dbHelper = new BatchDatabase(requireActivity());

        // Initialize inputs and button
        batchNameInput = view.findViewById(R.id.batchNameInput);
        batchTypeInput = view.findViewById(R.id.batchTypeInput);
        batchDOBInput = view.findViewById(R.id.batchDOBInput);
        Button saveButton = view.findViewById(R.id.saveBatchButton);

        saveButton.setOnClickListener(v -> {
            String batchName = batchNameInput.getText().toString().trim();
            String batchType = batchTypeInput.getText().toString().trim();
            String batchDOB = batchDOBInput.getText().toString().trim();

            if (batchName.isEmpty() || batchType.isEmpty() || batchDOB.isEmpty()) {
                Toast.makeText(requireActivity(), "Please fill all fields", Toast.LENGTH_SHORT).show();
            } else {
                // Add batch to database
                dbHelper.addBatch(batchName, batchType, batchDOB);

                // Notify and dismiss dialog
                Toast.makeText(requireActivity(), "Batch added successfully", Toast.LENGTH_SHORT).show();
                dismiss();

                // Refresh the list in BatchFragment
                if (getTargetFragment() instanceof BatchFragment) {
                    ((BatchFragment) getTargetFragment()).refreshBatchList();
                }
            }
        });

        return view;
    }
}
