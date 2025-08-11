package com.example.night_guard;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class AlarmScreenActivity extends AppCompatActivity {
    Button btn_stop_alarm;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm_screen);

        // Definitions
        btn_stop_alarm = findViewById(R.id.btn_stop_alarm);
        RealTimeDatabaseHelper realTimeDatabaseHelper = new RealTimeDatabaseHelper();

        btn_stop_alarm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent alarmBuzzerIntent = new Intent(AlarmScreenActivity.this, AlarmBuzzerService.class);
                stopService(alarmBuzzerIntent);
                realTimeDatabaseHelper.setCaregiverTrigger(false);
                realTimeDatabaseHelper.setUrineDetected(false);
                finish();
            }
        });
    }
}