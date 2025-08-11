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
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.night_guard.Models.AccountType;
import com.example.night_guard.Models.ActiveDeviceCodePackage;
import com.example.night_guard.Models.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.ArrayList;

public class SignupActivity extends AppCompatActivity {

    // Elements Variables
    TextInputEditText et_signup_firstName, et_signup_lastName,
            et_signup_email, et_signup_password, et_signup_confirmPassword, et_signup_deviceCode;
    RadioButton radio_patient_account, radio_caregiver_account;

    Button btn_signup;
       ProgressBar signUp_progressBar;
    TextView tv_goToLogin;

    // Firebase variables
    FirebaseAuth firebaseAuth;
    FirebaseUser currentUser;
    CollectionReference ValidDeviceCodes = FirebaseFirestore.getInstance().collection("ValidDeviceCodes");
    FirestoreDatabaseHelper firestoreDatabaseHelper = new FirestoreDatabaseHelper();

    // Other Variables
    ArrayList<String> validDeviceCodesArray = new ArrayList<String>();
    ArrayList<ActiveDeviceCodePackage> activeDeviceCodePackageArrayList = new ArrayList<>();
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
        if(currentUser != null){
           gotoMainActivity();
        }
        retrieveDeviceCodeInputsFromFirestore();
        retrieveAllActiveDeviceCodePackages();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        // Find Views By Id
        et_signup_firstName = findViewById(R.id.et_signup_firstName);
        et_signup_lastName = findViewById(R.id.et_signup_lastName);
        et_signup_email = findViewById(R.id.et_signup_email);
        et_signup_password = findViewById(R.id.et_signup_password);
        et_signup_confirmPassword = findViewById(R.id.et_signup_confirmPassword);
        et_signup_deviceCode = findViewById(R.id.et_signup_deviceCode);
        radio_patient_account= findViewById(R.id.radio_patient_account);
        radio_caregiver_account = findViewById(R.id.radio_caregiver_account);

        btn_signup = findViewById(R.id.btn_signup);
        signUp_progressBar = findViewById(R.id. signup_progressBar);
        tv_goToLogin = findViewById(R.id.tv_goToLogin);


        btn_signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SignUp();
            }
        });

        tv_goToLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gotoLoginPage();
            }
        });


    }

    @Override
    protected void onStop() {
        super.onStop();
        unregisterReceiver(networkCheckListener);
    }

    void SignUp(){
        loadingOn();
        if(validateSignUpInputs()){
            String email = et_signup_email.getText().toString();
            String password = et_signup_password.getText().toString();
            firebaseAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if(task.isSuccessful()){
                        User user = creatingUserFromSignupInfo();
                        firestoreDatabaseHelper.addUserDataToFirestore(user);
                        firestoreDatabaseHelper.addActiveDeviceCodePackage(user);
                        loadingOff();
                        user.subscribeToFCMTopic();
                        gotoMainActivity();
                    }else{
                        loadingOff();
                        Toast.makeText(SignupActivity.this, "Sign Up Failed", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

    boolean validateSignUpInputs(){
        boolean inputsValidated = true;

        if(String.valueOf(et_signup_firstName.getText()).length()==0){
            et_signup_firstName.setError("First Name should not be empty");
            inputsValidated = false;
        }

        if(String.valueOf(et_signup_lastName.getText()).length()==0){
            et_signup_lastName.setError("Last Name should not be empty");
            inputsValidated = false;
        }

        if(String.valueOf(et_signup_email.getText()).length()==0){
            et_signup_email.setError("Email should not be empty");
            inputsValidated = false;
        }else{
            if(!String.valueOf(et_signup_email.getText()).contains("@")){
                et_signup_email.setError("Enter valid Email");
                inputsValidated = false;
            }
        }

        if(String.valueOf(et_signup_password.getText()).length()==0){
            et_signup_password.setError("Password should not be empty");
            inputsValidated = false;
        }

        if(String.valueOf(et_signup_confirmPassword.getText()).length()==0){
            et_signup_confirmPassword.setError("Confirm Password should not be empty");
            inputsValidated = false;
        }else{
            if(!validateConfirmPassword()){
                et_signup_confirmPassword.setError("Confirm Password is not matching Password");
                inputsValidated = false;
            }
        }
        if(String.valueOf(et_signup_deviceCode.getText()).length()==0){
            et_signup_deviceCode.setError("Device Code should not be empty");
            inputsValidated = false;
        }else{
            if(!validateDeviceCodeInput()){
                inputsValidated = false;
            }
        }

        if(!radio_caregiver_account.isChecked() && !radio_patient_account.isChecked()){
            Toast.makeText(getApplicationContext(), "Choose Account Type", Toast.LENGTH_SHORT).show();
            inputsValidated = false;
        }

        if(validatePositionNotTaken()){
            et_signup_deviceCode.setError("This Device Code Already Assigned A " + creatingUserFromSignupInfo().getUserAccountType());
            inputsValidated = false;
        }


        if(!inputsValidated){
            loadingOff();
        }
        return inputsValidated;
    }

    void retrieveDeviceCodeInputsFromFirestore(){

        ValidDeviceCodes.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()){
                    for (QueryDocumentSnapshot document : task.getResult()){
                        validDeviceCodesArray.add(document.getData().get("DC_Code").toString());
                    }
                }
            }
        });
    }

    boolean validateDeviceCodeInput(){
        boolean deviceCodeFound = false;

        for(String deviceCode: validDeviceCodesArray){
            if(et_signup_deviceCode.getText().toString().equals(deviceCode)){
                deviceCodeFound = true;
                break;
            }
        }
        if(!deviceCodeFound){
             et_signup_deviceCode.setError("Device Code is Not Valid");
        }
        return deviceCodeFound;
    }


    boolean validateConfirmPassword(){
        boolean passwordConfirmed = false;

        String password = et_signup_password.getText().toString();
        String confirmPassword = et_signup_confirmPassword.getText().toString();
        if(password.equals(confirmPassword)){
            passwordConfirmed = true;
        }

        return passwordConfirmed;
    }
    void gotoMainActivity(){
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(intent);
        finish();
    }

    void gotoLoginPage(){
        Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
        startActivity(intent);
        finish();
    }

    void loadingOn(){
        signUp_progressBar.setVisibility(View.VISIBLE);
        btn_signup.setVisibility(View.GONE);
    }

    void loadingOff(){
        signUp_progressBar.setVisibility(View.GONE);
        btn_signup.setVisibility(View.VISIBLE);
    }


    User creatingUserFromSignupInfo(){
        // Creating the User
        String firstName = et_signup_firstName.getText().toString();
        String lastName = et_signup_lastName.getText().toString();
        String email = et_signup_email.getText().toString();
        String deviceCode = et_signup_deviceCode.getText().toString();
        AccountType userAccountType = User.assignAccountType(radio_patient_account.isChecked(), radio_caregiver_account.isChecked());
        User user = new User(firstName,lastName,email,deviceCode,userAccountType);
        return user;
    }

    public void retrieveAllActiveDeviceCodePackages(){
        firestoreDatabaseHelper.activeDeviceCodesCollection.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()){
                    for(QueryDocumentSnapshot document : task.getResult()){
                        activeDeviceCodePackageArrayList.add(document.toObject(ActiveDeviceCodePackage.class));
                    }
                }
            }
        });
    }

    boolean validatePositionNotTaken(){
        User user = creatingUserFromSignupInfo();
        boolean isAssigned = false;
        for(ActiveDeviceCodePackage activeDeviceCodePackage: activeDeviceCodePackageArrayList){
            if(activeDeviceCodePackage.getDeviceCode().equals(user.getDeviceCode())){
                if(user.getUserAccountType() == AccountType.PATIENT){
                    isAssigned = activeDeviceCodePackage.isPatientAssigned();
                } else if (user.getUserAccountType() == AccountType.CAREGIVER) {
                    isAssigned = activeDeviceCodePackage.isCaregiverAssigned();
                }
            }else{
                isAssigned = false;
            }
        }
        return isAssigned;
    }

}