package com.example.night_guard;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class AuthenticationActivity extends AppCompatActivity {
    Button btn_auth_login,btn_auth_signup;
    NetworkCheckListener networkCheckListener = new NetworkCheckListener();

    @Override
    protected void onStart() {
        super.onStart();
        //Check Internet Access
        IntentFilter intentFilter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(networkCheckListener, intentFilter);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_authentication);

        // Find Views By Id
        btn_auth_login = findViewById(R.id.btn_auth_login);
        btn_auth_signup = findViewById(R.id.btn_auth_signup);


        btn_auth_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gotoLoginPage();
            }
        });

        btn_auth_signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gotoSignupPage();
            }
        });

    }

    @Override
    protected void onStop() {
        super.onStop();
        unregisterReceiver(networkCheckListener);
    }

    void gotoLoginPage(){
        Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
        startActivity(intent);
        finish();
    }

    void gotoSignupPage(){
        Intent intent = new Intent(getApplicationContext(), SignupActivity.class);
        startActivity(intent);
        finish();
    }
}