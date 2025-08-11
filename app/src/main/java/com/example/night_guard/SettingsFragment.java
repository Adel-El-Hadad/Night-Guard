package com.example.night_guard;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;


public class SettingsFragment extends Fragment {

Button btn_caregiver_alarm, btn_uds_reminder,btn_fluids_reminder ;
    public SettingsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view  = inflater.inflate(R.layout.fragment_settings, container, false);
        // Inflate the layout for this fragment

        btn_caregiver_alarm = view.findViewById(R.id.btn_caregiver_alarm);
        btn_uds_reminder = view.findViewById(R.id.btn_uds_reminder);
        btn_fluids_reminder = view.findViewById(R.id.btn_fluids_reminder);
        btn_caregiver_alarm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToCaregiverAlarmActivity();
            }
        });

        btn_uds_reminder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToUDSReminderActivity();
            }
        });

        btn_fluids_reminder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToFluidsReminder();
            }
        });

        return view;
    }

    void goToCaregiverAlarmActivity(){
        Intent intent = new Intent(getActivity(), CaregiverAlarmActivity.class);
        startActivity(intent);
    }
    void goToUDSReminderActivity(){
        Intent intent = new Intent(getActivity(), UDSReminderActivity.class);
        startActivity(intent);
    }
    void goToFluidsReminder(){
        Intent intent = new Intent(getActivity(), FluidReminderActivity.class);
        startActivity(intent);
    }
}