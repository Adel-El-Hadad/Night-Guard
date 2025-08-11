package com.example.night_guard;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationManagerCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class PushNotificationsService extends FirebaseMessagingService {

    @Override
    public void onMessageReceived(@NonNull RemoteMessage message) {
        super.onMessageReceived(message);

        // Set Alarm Sound Once Notification Received
        Intent alarmBuzzerIntent = new Intent(this, AlarmBuzzerService.class);
        startService(alarmBuzzerIntent);

        //  Get Data from notification
        String notificationTitle = message.getNotification().getTitle();
        String notificationBody = message.getNotification().getBody();
        String notifcationClickAction = message.getNotification().getClickAction();

        // Create a Channel
        final String CHANNEL_ID = "NIGHT-GUARD-NOTIFICATIONS";
        NotificationChannel nightGuardChannel = new NotificationChannel(CHANNEL_ID,
                "Night-Guard-Notifications", NotificationManager.IMPORTANCE_HIGH);

        getSystemService(NotificationManager.class).createNotificationChannel(nightGuardChannel);

        // Open Alarm Screen Activity When Clicked On Notification
        Intent alarmScreenIntent;
        if("ALARM_SCREEN_ACTIVITY".equals(notifcationClickAction)){
             alarmScreenIntent = new Intent(this, AlarmScreenActivity.class);
             alarmScreenIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        }else{
            alarmScreenIntent = new Intent(this,MainActivity.class);
            alarmScreenIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        }

        PendingIntent alarmScreenPendingIntent = PendingIntent.getActivity(this,0, alarmScreenIntent, PendingIntent.FLAG_ONE_SHOT|PendingIntent.FLAG_IMMUTABLE);

        // Customize Notification
        Notification.Builder nightGuardNotification = new Notification.Builder(this, CHANNEL_ID)
                .setContentTitle(notificationTitle)
                .setContentText(notificationBody)
                .setSmallIcon(R.drawable.baseline_add_alert_24)
                .setContentIntent(alarmScreenPendingIntent)
                .setAutoCancel(true);

        NotificationManagerCompat.from(this).notify(1,nightGuardNotification.build());

        // Add Wet Day
        TrackingData.addWetDayToFirestore();

    }
}
