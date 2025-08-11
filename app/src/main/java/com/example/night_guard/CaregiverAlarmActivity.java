package com.example.night_guard;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

public class CaregiverAlarmActivity extends AppCompatActivity {

    Switch switch_caregiver_alarm ;
    EditText et_caregiver_alaram_minutes;
    Button btn_save_settings_caregiver_alarm;
    Button btn_back_settings_caregiver_alarm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_caregiver_alarm);


        // Find By Id
        switch_caregiver_alarm = findViewById(R.id.switch_caregiver_alarm);
        et_caregiver_alaram_minutes = findViewById(R.id.et_caregiver_alaram_minutes);
        btn_save_settings_caregiver_alarm = findViewById(R.id.btn_save_settings_caregiver_alarm);
        btn_back_settings_caregiver_alarm = findViewById(R.id.btn_back_settings_caregiver_alarm);

        setInitialValues();
        // Actions
        switch_caregiver_alarm.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                et_caregiver_alaram_minutes.setEnabled(isChecked);
            }
        });

        btn_save_settings_caregiver_alarm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveSettings();
            }
        });

        btn_back_settings_caregiver_alarm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                backSettings();
            }
        });


    }

    void saveSettings(){

        if(validateMinutesInput()){
            uploadSettingsOnDatabase();
            finish();
        }else{
            Toast.makeText(this, "Valid Minutes Input", Toast.LENGTH_LONG).show();
        }
    }

    void backSettings(){
        finish();
    }

    boolean validateMinutesInput(){
        boolean isValid = false;
        String minutesString = et_caregiver_alaram_minutes.getText().toString();
        if(minutesString.length()==1){
            isValid = true;
        }else {
            et_caregiver_alaram_minutes.setError("Minutes Should Be Value From 1 to 9 mins");
            isValid = false;
        }
        return  isValid;
    }


    void uploadSettingsOnDatabase(){
        RealTimeDatabaseHelper realTimeDatabaseHelper = new RealTimeDatabaseHelper();
        realTimeDatabaseHelper.setCaregiverNeeded(switch_caregiver_alarm.isChecked());
        realTimeDatabaseHelper.setTimerMillis(convertToMilliSeconds(et_caregiver_alaram_minutes.getText().toString()));
    }

    int convertToMilliSeconds(String minutesString){
        int minutes = Integer.valueOf(minutesString);
        int milliseconds = minutes * 60 * 1000;
        return milliseconds;
    }

    void setInitialValues(){

        RealTimeDatabaseHelper realTimeDatabaseHelper = new RealTimeDatabaseHelper();
        realTimeDatabaseHelper.CaregiverNeeded.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.getValue(boolean.class)!=null){
                    boolean bool = snapshot.getValue(boolean.class);
                    switch_caregiver_alarm.setChecked(bool);
                }else{
                    switch_caregiver_alarm.setChecked(false);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        realTimeDatabaseHelper.TimerMillis.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.getValue(int.class)!=null){
                    int integer = snapshot.getValue(int.class)/(60*1000);
                    et_caregiver_alaram_minutes.setText(String.valueOf(integer) );
                }else{
                    et_caregiver_alaram_minutes.setText("0");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }
}