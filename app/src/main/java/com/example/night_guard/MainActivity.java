package com.example.night_guard;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;

import com.example.night_guard.databinding.ActivityMainBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.messaging.FirebaseMessaging;

public class MainActivity extends AppCompatActivity {

    static boolean wetVsDryCalculated = false;
    ActivityMainBinding binding;
    FirebaseAuth firebaseAuth;
    FirebaseUser currentUser;
    NetworkCheckListener networkCheckListener = new NetworkCheckListener();
    boolean isNetworkCheckListenerRegistered = false;


    @Override
    protected void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        firebaseAuth = FirebaseAuth.getInstance();
        currentUser = firebaseAuth.getCurrentUser();
        if(currentUser == null){
            gotoAuthenticationPage();
        }else{
            //Check Internet Access
            IntentFilter intentFilter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
            registerReceiver(networkCheckListener, intentFilter);
            isNetworkCheckListenerRegistered = true;

            // Get User Data
            FirestoreDatabaseHelper.getCurrentUserData(firebaseAuth.getCurrentUser().getEmail());
            FirestoreDatabaseHelper.getcurrentActiveDeviceCodePackage();

            // Calculate Wet Vs Dry
            if(!wetVsDryCalculated){
            TrackingData.calculateWetVsDryDays();
            wetVsDryCalculated = true;
            }
        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        replaceFragment(new HomeFragment());
        binding.mainBottomNavView.setOnItemSelectedListener(item -> {

           if(item.getItemId() == R.id.nav_bar_home){
               replaceFragment(new HomeFragment());
           } else if (item.getItemId()==R.id.nav_bar_tracking) {
               replaceFragment(new TrackingFragment());
           } else if(item.getItemId() == R.id.nav_bar_settings){
               replaceFragment(new SettingsFragment());
           } else if (item.getItemId() ==R.id.nav_bar_rewards) {
               replaceFragment(new RewardsFragment());
           } else if(item.getItemId() ==R.id.nav_bar_profile){
               replaceFragment(new ProfileFragment());
           }
            return  true;
        });

    }

    @Override
    protected void onStop() {
        super.onStop();
        if(isNetworkCheckListenerRegistered){
            unregisterReceiver(networkCheckListener);
        }
    }

    void replaceFragment(Fragment fragment){
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.main_FrameLayout, fragment);
        fragmentTransaction.commit();
    }

    void gotoAuthenticationPage(){
        Intent intent = new Intent(getApplicationContext(), AuthenticationActivity.class);
        startActivity(intent);
        finish();
    }
}