package com.sohaib.eventcheck.Receivers;

import android.annotation.SuppressLint;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.sohaib.eventcheck.AppMainScreen;
import com.sohaib.eventcheck.R;

public class Notifier extends android.content.BroadcastReceiver {

    private static final String CHANNEL_ID = "reminder_channel_id";



    @Override
    public void onReceive(Context context, Intent intent1) {

        int Notification_id = intent1.getIntExtra("rem_id",0); // A unique ID for the notification
        String title = intent1.getStringExtra("rem_title");

        createNotificationChannel(context);


        Intent intent = new Intent(context, AppMainScreen.class); // Replace YourActivity with the activity you want to open when the user taps the notification.
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        @SuppressLint("UnspecifiedImmutableFlag") PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.ring) // Replace ic_notification with your notification icon
                .setContentTitle("Reminder App")
                .setContentText(title)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        notificationManager.notify(Notification_id, builder.build());
    }

    private static void createNotificationChannel(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence channelName = "Reminder Channel";
            String channelDescription = "Channel for reminder notifications";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel notificationChannel = new NotificationChannel(CHANNEL_ID, channelName, importance);
            notificationChannel.setDescription(channelDescription);
            NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(notificationChannel);
        }
    }
}
