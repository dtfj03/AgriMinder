package com.example.agriminder;

import android.database.Cursor;
import android.graphics.Paint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.fragment.app.Fragment;

public class HomeFragment extends Fragment {

    private ReminderDatabase reminderDatabase;
    private LinearLayout homeReminderLayout;

    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        // Initialize database and layout
        reminderDatabase = new ReminderDatabase(getContext());
        homeReminderLayout = view.findViewById(R.id.home_reminder2);

        // Load reminders from database
        loadReminders();

        return view;
    }

    private void loadReminders() {
        // Clear existing views
        homeReminderLayout.removeAllViews();

        // Retrieve all reminders from database
        Cursor cursor = reminderDatabase.getAllData();
        if (cursor != null && cursor.moveToFirst()) {
            do {
                // Ensure columns are available
                int titleIndex = cursor.getColumnIndex(ReminderDatabase.COL_REMINDER_TITLE);
                int timeIndex = cursor.getColumnIndex(ReminderDatabase.COL_REMINDER_TIME);

                if (titleIndex == -1 || timeIndex == -1) continue; // Skip if columns are missing

                // Get reminder details from cursor
                String title = cursor.getString(titleIndex);
                String time = cursor.getString(timeIndex);

                // Inflate a new reminder item layout
                View reminderItemView = LayoutInflater.from(getContext()).inflate(R.layout.reminder_item, homeReminderLayout, false);

                // Set reminder title and time
                TextView reminderTitle = reminderItemView.findViewById(R.id.reminderTitleTextView);
                TextView reminderTime = reminderItemView.findViewById(R.id.reminderTimeTextView);
                reminderTitle.setText(title);
                reminderTime.setText(time);

                // Handle checkbox for marking as done
                CheckBox checkBox = reminderItemView.findViewById(R.id.reminderCheckBox);
                checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
                    if (isChecked) {
                        // Strike-through text to indicate completion
                        reminderTitle.setPaintFlags(reminderTitle.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                        reminderTime.setPaintFlags(reminderTime.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                    } else {
                        // Remove strike-through if unchecked
                        reminderTitle.setPaintFlags(reminderTitle.getPaintFlags() & ~Paint.STRIKE_THRU_TEXT_FLAG);
                        reminderTime.setPaintFlags(reminderTime.getPaintFlags() & ~Paint.STRIKE_THRU_TEXT_FLAG);
                    }
                });

                // Add the reminder item view to the layout
                homeReminderLayout.addView(reminderItemView);

            } while (cursor.moveToNext());

            cursor.close();
        }
    }
}
