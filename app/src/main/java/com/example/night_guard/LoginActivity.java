package com.example.night_guard;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.night_guard.Models.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;

public class LoginActivity extends AppCompatActivity {
    FirebaseAuth firebaseAuth;
    FirestoreDatabaseHelper firestoreDatabaseHelper;
    FirebaseUser currentUser;
    ProgressBar login_progressBar;
    TextInputEditText et_login_email, et_login_password;
    Button btn_login;
    TextView tv_goToSignup;

    NetworkCheckListener networkCheckListener = new NetworkCheckListener();

    @Override
    public void onStart() {
        super.onStart();
        //Check Internet Access
        IntentFilter intentFilter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(networkCheckListener, intentFilter);
        // Check if user is signed in (non-null) and update UI accordingly.
        firebaseAuth = FirebaseAuth.getInstance();
        currentUser = firebaseAuth.getCurrentUser();
        firestoreDatabaseHelper = new FirestoreDatabaseHelper();

        if(currentUser != null){
            gotoMainActivity();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Find Views By Id
        et_login_email = findViewById(R.id.et_login_email);
        et_login_password = findViewById(R.id.et_login_password);
        login_progressBar = findViewById(R.id.login_progressBar);
        btn_login = findViewById(R.id.btn_login);
        tv_goToSignup = findViewById(R.id.tv_goToSignup);

        tv_goToSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gotoSignUpPage();
            }
        });

        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(validateLoginInputs()){
                    loadingOn();
                    String email = et_login_email.getText().toString();
                    String password = et_login_password.getText().toString();
                    firebaseAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isSuccessful()){
                                loadingOff();
                                gotoMainActivity();
                                firestoreDatabaseHelper.usersCollections.document(email).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                        if(task.isSuccessful()){
                                            User user = task.getResult().toObject(User.class);
                                            user.subscribeToFCMTopic();
                                        }
                                    }
                                });

                            }else{
                                loadingOff();
                                Toast.makeText(LoginActivity.this, "Login Failed", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
        });
    }

    @Override
    protected void onStop() {
        super.onStop();
        unregisterReceiver(networkCheckListener);
    }

    boolean validateLoginInputs(){
        boolean inputsValidated = true;

        if(String.valueOf(et_login_email.getText()).length()==0){
            et_login_email.setError("Email should not be empty.");
            inputsValidated = false;
        }

        if(String.valueOf(et_login_password.getText()).length() ==0){
            et_login_password.setError("Password should not be empty.");
            inputsValidated = false;
        }

        if(!inputsValidated){
            loadingOff();
        }
        return inputsValidated;
    }

    void gotoMainActivity(){
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(intent);
        finish();
    }

    void gotoSignUpPage(){
        Intent intent = new Intent(getApplicationContext(), SignupActivity.class);
        startActivity(intent);
        finish();
    }

    void loadingOn(){
        login_progressBar.setVisibility(View.VISIBLE);
        btn_login.setVisibility(View.GONE);
    }

    void loadingOff(){
        login_progressBar.setVisibility(View.GONE);
        btn_login.setVisibility(View.VISIBLE);
    }
}