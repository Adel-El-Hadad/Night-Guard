package com.example.night_guard;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.night_guard.Models.DayStatus;

public class ConsecutiveDryDaysActivity extends AppCompatActivity {

    Button btn_consecutiveDryDaysClose;
    TextView tv_consecutiveDryDays;
    int consecutiveDryDays = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_consecutive_dry_days);

        btn_consecutiveDryDaysClose = findViewById(R.id.btn_consecutiveDryDaysClose);
        tv_consecutiveDryDays = findViewById(R.id.tv_consecutiveDryDays);

        calculateConsecutiveDryDays();

        tv_consecutiveDryDays.setText("Consecutive Dry Days: " + consecutiveDryDays);
        btn_consecutiveDryDaysClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    void calculateConsecutiveDryDays(){
        for (int i = TrackingData.wetVsDryDays.size()-1; i>=0; i--){
            if(TrackingData.wetVsDryDays.get(i).getStatus()== DayStatus.DRY){
                consecutiveDryDays++;
            }else{
                break;
            }
        }
    }
}