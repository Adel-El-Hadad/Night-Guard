package com.example.night_guard;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class TrackingFragment extends Fragment {

    Button btn_wet_vs_dry_days, btn_consecutive_dry_days;


    public TrackingFragment() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        TrackingData.calculateWetVsDryDays();
        FirestoreDatabaseHelper.getcurrentActiveDeviceCodePackage();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view  = inflater.inflate(R.layout.fragment_tracking, container, false);

        btn_wet_vs_dry_days = view.findViewById(R.id.btn_wet_vs_dry_days);
        btn_consecutive_dry_days = view.findViewById(R.id.btn_consecutive_dry_days);

        btn_wet_vs_dry_days.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToWetVsDryActivity();
            }
        });

        btn_consecutive_dry_days.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToConsecutiveDryDaysActivity();
            }
        });
        return view;
    }

    void goToWetVsDryActivity(){
        Intent intent = new Intent(getActivity(), WetVsDryActivity.class);
        startActivity(intent);
    }

    void goToConsecutiveDryDaysActivity(){
        Intent intent = new Intent(getActivity(), ConsecutiveDryDaysActivity.class);
        startActivity(intent);
    }
}
