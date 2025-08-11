package com.example.night_guard;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.example.night_guard.Models.DayData;
import com.example.night_guard.Models.DayStatus;

import java.util.ArrayList;
import java.util.Collections;

public class WetVsDryActivity extends AppCompatActivity {

    TextView tv_startedSince,tv_totalWetDays, tv_totalDryDays;
    Button btn_wetVsDryClose;

    ListView lv_wetVsDry;

    ArrayList<String> wetVsDryStrings;
    ArrayAdapter<String> wetVsDryAdapter;
    int totalDryDays = 0;
    int totalWetDays = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wet_vs_dry);

        getWetVsDryStrings();
        wetVsDryAdapter = new ArrayAdapter<>(this, androidx.appcompat.R.layout.support_simple_spinner_dropdown_item, wetVsDryStrings);
        // Find Views By Id
        tv_startedSince = findViewById(R.id.tv_startedSince);
        tv_totalWetDays = findViewById(R.id.tv_totalWetDays);
        tv_totalDryDays = findViewById(R.id.tv_totalDryDays);
        btn_wetVsDryClose = findViewById(R.id.btn_wetVsDryClose);
        lv_wetVsDry = findViewById(R.id.lv_wetVsDry);

        tv_startedSince.setText("Started Since: "+FirestoreDatabaseHelper.currentActiveDeviceCodePackage.getPatientSignedUpDate().dayDataToString());
        tv_totalWetDays.setText("Total Wet Days: " + totalWetDays);
        tv_totalDryDays.setText("Total Dry Days: " + totalDryDays);
        lv_wetVsDry.setAdapter(wetVsDryAdapter);

        btn_wetVsDryClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }

    void getWetVsDryStrings(){
        wetVsDryStrings = new ArrayList<>();
        for(DayData dayData: TrackingData.wetVsDryDays){
            if(dayData.getStatus()== DayStatus.DRY){
                totalDryDays++;
            } else if (dayData.getStatus() == DayStatus.WET) {
                totalWetDays++;
            }
            wetVsDryStrings.add(dayData.dayDataToString());
        }
        Collections.reverse(wetVsDryStrings);
    }
}