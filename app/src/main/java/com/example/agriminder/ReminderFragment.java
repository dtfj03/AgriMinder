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
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

public class ReminderFragment extends Fragment {

    private ReminderDatabase dbHelper;
    private LinearLayout reminderLayout;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_reminder, container, false);

        // Initialize database helper
        dbHelper = new ReminderDatabase(getContext());

        // Find the LinearLayout and add reminder button
        reminderLayout = view.findViewById(R.id.reminderLayout);
        Button addReminderButton = view.findViewById(R.id.addReminder);

        // Set the click listener for the addReminder button
        addReminderButton.setOnClickListener(v -> {
            FragmentManager fragmentManager = getParentFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.fragment_container, new AddReminderFragment());
            fragmentTransaction.addToBackStack(null);
            fragmentTransaction.commit();
        });

        // Fetch and display reminders
        displayReminders();

        return view;
    }

    private void displayReminders() {
        // Fetch all reminders from the database
        Cursor cursor = dbHelper.getAllData();

        if (cursor != null && cursor.getCount() > 0) {
            reminderLayout.setVisibility(View.VISIBLE); // Ensure the layout is visible

            // Iterate through the reminders and display them
            while (cursor.moveToNext()) {
                int id = cursor.getInt(cursor.getColumnIndexOrThrow("_id"));
                String title = cursor.getString(cursor.getColumnIndexOrThrow(ReminderDatabase.COL_REMINDER_TITLE));
                String time = cursor.getString(cursor.getColumnIndexOrThrow(ReminderDatabase.COL_REMINDER_TIME));
                String day = cursor.getString(cursor.getColumnIndexOrThrow(ReminderDatabase.COL_REMINDER_DAY));
                String location = cursor.getString(cursor.getColumnIndexOrThrow(ReminderDatabase.COL_REMINDER_LOCATION));

                // Dynamically create TextViews to display each reminder's details
                TextView reminderTextView = new TextView(getContext());
                reminderTextView.setText("Title: " + title + "\nTime: " + time + "\nDay: " + day + "\nLocation: " + location);
                reminderTextView.setPadding(16, 16, 16, 16); // Adding padding for better readability
                reminderLayout.addView(reminderTextView);
            }
        } else {
            reminderLayout.setVisibility(View.VISIBLE); // Ensure the layout is visible
            TextView noDataTextView = new TextView(getContext());
            noDataTextView.setText("No reminders available.");
            noDataTextView.setPadding(16, 16, 16, 16); // Adding padding for better readability
            reminderLayout.addView(noDataTextView);
        }

        // Close the cursor to avoid memory leaks
        if (cursor != null) {
            cursor.close();
        }
    }
}
