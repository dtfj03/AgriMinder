package com.example.agriminder;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class ReminderBroadcastReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        String title = intent.getStringExtra("REMINDER_TITLE");
        String time = intent.getStringExtra("REMINDER_TIME");

        // Send a push notification
        NotificationHelper.sendNotification(context, "Reminder: " + title, "Time: " + time);
    }
}
