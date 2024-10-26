package com.example.agriminder;

import android.app.TimePickerDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import java.util.Calendar;

public class AddReminderFragment extends DialogFragment {

    private EditText titleInput, dayInput, locationInput;
    private TextView timeInput; // Changed from EditText to TextView for TimePicker
    private ReminderDatabase dbHelper;
    private String selectedTime = ""; // To hold the selected time

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate the dialog layout for this fragment
        View view = inflater.inflate(R.layout.fragment_add_reminder, container, false);

        // Initialize the database helper
        dbHelper = new ReminderDatabase(getContext());

        // Bind the EditText and Button views
        titleInput = view.findViewById(R.id.addReminderTitle);
        timeInput = view.findViewById(R.id.addReminderTime); // TextView now
        dayInput = view.findViewById(R.id.addReminderDay);
        locationInput = view.findViewById(R.id.addReminderLocation);
        Button addReminderButton = view.findViewById(R.id.addReminder);

        // Set the TimePickerDialog on the timeInput TextView
        timeInput.setOnClickListener(v -> showTimePicker());

        // Set an onClickListener for the "Add Reminder" button
        addReminderButton.setOnClickListener(v -> addReminder());

        return view;
    }

    private void showTimePicker() {
        // Get the current time to show in the picker initially
        Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);

        // Create and show the TimePickerDialog
        TimePickerDialog timePickerDialog = new TimePickerDialog(getContext(), (TimePicker view, int hourOfDay, int selectedMinute) -> {
            // Format the time to a readable format and set it on the timeInput TextView
            selectedTime = String.format("%02d:%02d", hourOfDay, selectedMinute);
            timeInput.setText(selectedTime);
        }, hour, minute, true); // The last parameter (true) is for 24-hour format. Use false for 12-hour format.

        timePickerDialog.show();
    }

    private void addReminder() {
        // Get user input from EditText fields
        String title = titleInput.getText().toString();
        String time = selectedTime; // Get time from the selectedTime variable
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
            dismiss(); // Close the dialog once the reminder is added
        } else {
            Toast.makeText(getContext(), "Error adding reminder", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        // Set dialog size to match your preference (optional)
        if (getDialog() != null) {
            getDialog().getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        }
    }
}
