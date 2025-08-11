package com.example.night_guard;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.example.night_guard.Models.User;
import com.google.firebase.auth.FirebaseAuth;

public class ProfileFragment extends Fragment {

    Button btn_signOut;
    TextView tv_profile_userEmail,tv_profile_userFirstName, tv_profile_userLastName, tv_profile_userAccountType, tv_profile_userDeviceCode;
    public ProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view  = inflater.inflate(R.layout.fragment_profile, container, false);
        // Inflate the layout for this fragment

        //Find Views By Id
        btn_signOut = view.findViewById(R.id.btn_signOut);
        tv_profile_userEmail = view.findViewById(R.id.tv_profile_userEmail);
        tv_profile_userFirstName = view.findViewById(R.id.tv_profile_userFirstName);
        tv_profile_userLastName = view.findViewById(R.id.tv_profile_userLastName);
        tv_profile_userAccountType = view.findViewById(R.id.tv_profile_userAccountType);
        tv_profile_userDeviceCode = view.findViewById(R.id.tv_profile_userDeviceCode);

        // setting user data
        tv_profile_userEmail.setText(FirestoreDatabaseHelper.currentUser.getEmail());
        tv_profile_userFirstName.setText(FirestoreDatabaseHelper.currentUser.getFirstName());
        tv_profile_userLastName.setText(FirestoreDatabaseHelper.currentUser.getLastName());
        tv_profile_userAccountType.setText(FirestoreDatabaseHelper.currentUser.getUserAccountType().toString());
        tv_profile_userDeviceCode.setText(FirestoreDatabaseHelper.currentUser.getDeviceCode());

        btn_signOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signOut();
            }
        });

        return view;
    }

    void signOut(){
        FirebaseAuth.getInstance().signOut();
        gotoAuthenticationPage();
        User user = FirestoreDatabaseHelper.currentUser;
        user.unsubscribeToAllTopics();
    }

    void gotoAuthenticationPage(){
        Intent intent = new Intent(getActivity().getApplicationContext(), AuthenticationActivity.class);
        startActivity(intent);
        getActivity().finish();
    }
}
