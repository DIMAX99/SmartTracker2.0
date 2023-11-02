package com.example.smarttracker;

import android.annotation.SuppressLint;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

public class TaskReminderBroadcastReceiver extends BroadcastReceiver {
    @SuppressLint("MissingPermission")
    @Override
    public void onReceive(Context context, Intent intent) {
        String taskName = intent.getStringExtra("task_name");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel("task_reminder", "Task Reminder", NotificationManager.IMPORTANCE_DEFAULT);
            NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
        // Create and display a notification
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "task_reminder")
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentTitle("Task Reminder")
                .setContentText("Task: " + taskName + " is due now.")
                .setPriority(NotificationCompat.PRIORITY_HIGH);
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        notificationManager.notify(0, builder.build());
        Log.d("set alarm","taskmreminderworkdone");

    }
}
