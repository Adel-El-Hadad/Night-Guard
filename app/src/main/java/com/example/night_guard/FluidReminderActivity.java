package com.example.night_guard;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.util.Calendar;

public class FluidReminderActivity extends AppCompatActivity {

    private Calendar calendar;
    private AlarmManager alarmManager;
    private PendingIntent pendingIntent;
    Button btn_fluid_reminder_save, btn_fluid_reminder_back;
    Switch switch_fluid_reminder;

    boolean isSwitchChecked = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fluid_reminder);

        // Finding by View
        btn_fluid_reminder_back = findViewById(R.id.btn_fluid_reminder_back);
        btn_fluid_reminder_save = findViewById(R.id.btn_fluid_reminder_save);
        switch_fluid_reminder = findViewById(R.id.switch_fluid_reminder);

        createNotificationChannel();

        // Reading From Database
        isSwitchChecked = FirestoreDatabaseHelper.currentUser.isFluidReminder();
        switch_fluid_reminder.setChecked(isSwitchChecked);

        // Listeners
        switch_fluid_reminder.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                isSwitchChecked = isChecked;
            }
        });


        btn_fluid_reminder_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isSwitchChecked) {
                    setAlarm();
                } else {
                    cancelAlarm();
                }
                FirestoreDatabaseHelper firestoreDatabaseHelper = new FirestoreDatabaseHelper();
                firestoreDatabaseHelper.updateFluidReminderFirestore(isSwitchChecked);
                finish();
            }
        });

        btn_fluid_reminder_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void setAlarm() {
        calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());


        alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

        Intent intent = new Intent(this, FluidReminderReceiver.class);
        pendingIntent = PendingIntent.getBroadcast(this, 1, intent, PendingIntent.FLAG_IMMUTABLE);

        alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),30*1000, pendingIntent);

        Toast.makeText(this, "Fluid Reminder Set Successfully", Toast.LENGTH_SHORT).show();
    }

    private void cancelAlarm() {
        Intent intent = new Intent(this, FluidReminderReceiver.class);
        pendingIntent = PendingIntent.getBroadcast(this, 1, intent, PendingIntent.FLAG_IMMUTABLE);

        if (alarmManager == null) {
            alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        }

        alarmManager.cancel(pendingIntent);
        Toast.makeText(this, "Fluid Reminder Cancelled", Toast.LENGTH_SHORT).show();
    }


    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Fluid Reminder";
            String description = "Time to drink water...!!";
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = new NotificationChannel("Fluid Reminder", name, importance);
            channel.setDescription(description);

            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }
}