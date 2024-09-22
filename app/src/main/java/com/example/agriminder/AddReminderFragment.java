package com.example.agriminder;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

public class AddReminderFragment extends Fragment {

    private EditText titleInput, timeInput, dayInput, locationInput;
    private ReminderDatabase dbHelper;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_add_reminder, container, false);

        // Initialize the database helper
        dbHelper = new ReminderDatabase(getContext());

        // Update the IDs to match the ones in fragment_add_reminder.xml
        titleInput = view.findViewById(R.id.addReminderTitle);
        timeInput = view.findViewById(R.id.addReminderTime);
        dayInput = view.findViewById(R.id.addReminderDay);
        locationInput = view.findViewById(R.id.addReminderLocation);
        Button addReminderButton = view.findViewById(R.id.addReminder);

        // Set an onClickListener for the "Add Reminder" button
        addReminderButton.setOnClickListener(v -> addReminder());

        return view;
    }

    private void addReminder() {
        // Get user input from EditText fields
        String title = titleInput.getText().toString();
        String time = timeInput.getText().toString();
        String day = dayInput.getText().toString();
        String location = locationInput.getText().toString();

        // Validate that all fields are filled
        if (title.isEmpty() || time.isEmpty() || day.isEmpty() || location.isEmpty()) {
            Toast.makeText(getContext(), "Please fill out all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        // Insert the reminder into the database
        boolean isInserted = dbHelper.insertData(title, time, day, location);

        if (isInserted) {
            Toast.makeText(getContext(), "Reminder added successfully", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(getContext(), "Error adding reminder", Toast.LENGTH_SHORT).show();
        }
    }
}
