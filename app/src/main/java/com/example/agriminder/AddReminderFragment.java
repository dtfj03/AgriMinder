package com.example.agriminder;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
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
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class AddReminderFragment extends DialogFragment {

    private EditText titleInput, dayInput, locationInput;
    private TextView timeInput;
    private ReminderDatabase dbHelper;
    private String selectedTime = "";
    private Calendar reminderCalendar;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate the dialog layout for this fragment
        View view = inflater.inflate(R.layout.fragment_add_reminder, container, false);

        // Initialize the database helper
        dbHelper = new ReminderDatabase(getContext());

        // Bind the EditText and Button views
        titleInput = view.findViewById(R.id.addReminderTitle);
        timeInput = view.findViewById(R.id.addReminderTime);
        dayInput = view.findViewById(R.id.addReminderDay);
        locationInput = view.findViewById(R.id.addReminderLocation);
        Button addReminderButton = view.findViewById(R.id.addReminder);

        // Initialize reminderCalendar
        reminderCalendar = Calendar.getInstance();

        // Set the TimePickerDialog on the timeInput TextView
        timeInput.setOnClickListener(v -> showTimePicker());

        // Set an onClickListener for the "Add Reminder" button
        addReminderButton.setOnClickListener(v -> addReminder());

        return view;
    }

    private void showTimePicker() {
        // Get the current time to show in the picker initially
        int hour = reminderCalendar.get(Calendar.HOUR_OF_DAY);
        int minute = reminderCalendar.get(Calendar.MINUTE);

        // Create and show the TimePickerDialog
        TimePickerDialog timePickerDialog = new TimePickerDialog(getContext(), (TimePicker view, int hourOfDay, int selectedMinute) -> {
            // Update reminderCalendar with selected time
            reminderCalendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
            reminderCalendar.set(Calendar.MINUTE, selectedMinute);
            reminderCalendar.set(Calendar.SECOND, 0);

            // Format the time to a readable format and set it on the timeInput TextView
            selectedTime = String.format(Locale.getDefault(), "%02d:%02d", hourOfDay, selectedMinute);
            timeInput.setText(selectedTime);
        }, hour, minute, true);

        timePickerDialog.show();
    }

    private void addReminder() {
        // Get user input from fields
        String title = titleInput.getText().toString();
        String time = selectedTime; // Use the selected time
        String day = dayInput.getText().toString();
        String location = locationInput.getText().toString();

        // Validate input fields
        if (title.isEmpty() || time.isEmpty() || day.isEmpty() || location.isEmpty()) {
            Toast.makeText(getContext(), "Please fill out all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            // Insert reminder into the database
            boolean isInserted = dbHelper.insertData(title, time, day, location);

            if (isInserted) {
                Toast.makeText(getContext(), "Reminder added successfully", Toast.LENGTH_SHORT).show();
                dismiss(); // Close the dialog
            } else {
                Toast.makeText(getContext(), "Failed to add reminder", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            // Log and display error
            e.printStackTrace();
            Toast.makeText(getContext(), "An error occurred: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }


    @SuppressLint("ScheduleExactAlarm")
    private void scheduleNotification(String title, String time) {
        Context context = getContext();
        if (context == null) return;

        Intent intent = new Intent(context, ReminderBroadcastReceiver.class);
        intent.putExtra("REMINDER_TITLE", title);
        intent.putExtra("REMINDER_TIME", time);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                context, (int) System.currentTimeMillis(), intent, PendingIntent.FLAG_UPDATE_CURRENT);

        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        if (alarmManager != null) {
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, reminderCalendar.getTimeInMillis(), pendingIntent);
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
