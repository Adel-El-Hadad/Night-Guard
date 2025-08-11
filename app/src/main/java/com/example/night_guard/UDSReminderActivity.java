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

public class UDSReminderActivity extends AppCompatActivity {

    private Calendar calendar;
    private AlarmManager alarmManager;
    private PendingIntent pendingIntent;
    Button btn_uds_reminder_select_time, btn_uds_reminder_save, btn_uds_reminder_back;
    TextView tv_uds_reminder_sleeping_time;
    Switch switch_uds_reminder;

    boolean istimeChosen = false;
    boolean isSwitchChecked = false;
    int savedHours, savedMins;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_udsreminder);

        // Finding by View
        btn_uds_reminder_back = findViewById(R.id.btn_uds_reminder_back);
        btn_uds_reminder_save = findViewById(R.id.btn_uds_reminder_save);
        btn_uds_reminder_select_time = findViewById(R.id.btn_uds_reminder_select_time);
        tv_uds_reminder_sleeping_time = findViewById(R.id.tv_uds_reminder_sleeping_time);
        switch_uds_reminder = findViewById(R.id.switch_uds_reminder);

        createNotificationChannel();

        // Retrieving From Database
        isSwitchChecked = FirestoreDatabaseHelper.currentUser.isUDSReminder();
        switch_uds_reminder.setChecked(isSwitchChecked);
        showReminderSettings(isSwitchChecked);
        savedHours = FirestoreDatabaseHelper.currentUser.getUdsHours();
        savedMins = FirestoreDatabaseHelper.currentUser.getUdsMins();
        if (savedHours > 12) {
            tv_uds_reminder_sleeping_time.setText(
                    String.format("%02d", (savedHours - 12)) + " : " +
                            String.format("%02d", savedMins) + " PM"
            );
        } else {
            tv_uds_reminder_sleeping_time.setText(
                    String.format("%02d", savedHours) + " : " +
                            String.format("%02d", savedMins) + " AM"
            );
        }

        // Listeners
        switch_uds_reminder.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                showReminderSettings(isChecked);
                isSwitchChecked = isChecked;
            }
        });

        btn_uds_reminder_select_time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showTimePicker();
            }
        });

        btn_uds_reminder_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isSwitchChecked) {
                    if(validateTimeSelected()){
                        setAlarm();
                        finish();
                    }
                } else {
                    cancelAlarm();
                    finish();
                }
                FirestoreDatabaseHelper firestoreDatabaseHelper = new FirestoreDatabaseHelper();
                firestoreDatabaseHelper.updateUDSReminderFirestore(isSwitchChecked,savedHours,savedMins);
            }
        });

        btn_uds_reminder_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void setAlarm() {
        alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

        Intent intent = new Intent(this, UDSReminderReceiver.class);
        pendingIntent = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_IMMUTABLE);

        alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),
                2*60*1000, pendingIntent);

        Toast.makeText(this, "UDS Reminder Set Successfully", Toast.LENGTH_SHORT).show();
    }

    private void cancelAlarm() {
        Intent intent = new Intent(this, UDSReminderReceiver.class);
        pendingIntent = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_IMMUTABLE);

        if (alarmManager == null) {
            alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        }

        alarmManager.cancel(pendingIntent);
        Toast.makeText(this, "UDS Reminder Cancelled", Toast.LENGTH_SHORT).show();
    }

    private void showTimePicker() {
        TimePickerDialog timePickerDialog = new TimePickerDialog(
                this,
                new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        savedHours = hourOfDay;
                        savedMins = minute;
                        if (hourOfDay > 12) {
                            tv_uds_reminder_sleeping_time.setText(
                                    String.format("%02d", (hourOfDay - 12)) + " : " +
                                            String.format("%02d", minute) + " PM"
                            );
                        } else {
                            tv_uds_reminder_sleeping_time.setText(
                                    String.format("%02d", hourOfDay) + " : " +
                                            String.format("%02d", minute) + " AM"
                            );
                        }

                        calendar = Calendar.getInstance();
                        calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                        calendar.set(Calendar.MINUTE, minute);
                        calendar.set(Calendar.SECOND, 0);
                        calendar.set(Calendar.MILLISECOND, 0);
                        istimeChosen = true;
                    }
                },
                12, 0, false // Set default time here (12:00 PM in this case)
        );

        timePickerDialog.setTitle("Select Reminder Time");
        timePickerDialog.show();
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "UDS Reminder";
            String description = "Please Wear Device When Sleeping";
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = new NotificationChannel("UDS Reminder", name, importance);
            channel.setDescription(description);

            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    private void showReminderSettings(boolean isVisible) {
        if (!isVisible) {
            tv_uds_reminder_sleeping_time.setVisibility(View.GONE);
            btn_uds_reminder_select_time.setVisibility(View.GONE);
        } else {
            tv_uds_reminder_sleeping_time.setVisibility(View.VISIBLE);
            btn_uds_reminder_select_time.setVisibility(View.VISIBLE);
        }
    }

    private boolean validateTimeSelected(){
        if(istimeChosen){
            return true;
        }else{
            Toast.makeText(getApplicationContext(), "Choose Your Sleeping Time", Toast.LENGTH_LONG).show();
            return false;
        }
    }
}
