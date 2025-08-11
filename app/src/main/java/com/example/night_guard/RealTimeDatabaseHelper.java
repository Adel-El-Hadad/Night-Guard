package com.example.night_guard;

import androidx.annotation.NonNull;

import com.example.night_guard.Models.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class RealTimeDatabaseHelper {

    FirebaseDatabase realTimeDatabase;
    DatabaseReference UrineDetected;
    DatabaseReference CaregiverTrigger;
    DatabaseReference TimerMillis;
    DatabaseReference CaregiverNeeded;




    RealTimeDatabaseHelper(){
        FirebaseFirestore.getInstance().collection("Users").document(FirebaseAuth.getInstance().getCurrentUser().getEmail()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){
                    FirestoreDatabaseHelper.currentUser = task.getResult().toObject(User.class);
                    User user = FirestoreDatabaseHelper.currentUser;
                    realTimeDatabase = FirebaseDatabase.getInstance();
                    UrineDetected = realTimeDatabase.getReference("/DC_"+user.getDeviceCode()+"/UrineDetected");
                    CaregiverTrigger = realTimeDatabase.getReference("/DC_"+user.getDeviceCode()+"/CaregiverTrigger");
                    TimerMillis = realTimeDatabase.getReference("/DC_"+user.getDeviceCode()+"/TimerMillis");
                    CaregiverNeeded = realTimeDatabase.getReference("/DC_"+user.getDeviceCode()+"/CaregiverNeeded");
                }
            }

        });
        if(FirestoreDatabaseHelper.currentUser.getDeviceCode() != null){
            User user = FirestoreDatabaseHelper.currentUser;
            realTimeDatabase = FirebaseDatabase.getInstance();
            UrineDetected = realTimeDatabase.getReference("/DC_"+user.getDeviceCode()+"/UrineDetected");
            CaregiverTrigger = realTimeDatabase.getReference("/DC_"+user.getDeviceCode()+"/CaregiverTrigger");
            TimerMillis = realTimeDatabase.getReference("/DC_"+user.getDeviceCode()+"/TimerMillis");
            CaregiverNeeded = realTimeDatabase.getReference("/DC_"+user.getDeviceCode()+"/CaregiverNeeded");
        }
    }
    public void setUrineDetected(boolean urineDetectedValue) {
        UrineDetected.setValue(urineDetectedValue);
    }
    public  void setCaregiverTrigger(boolean caregiverTrigger){
        CaregiverTrigger.setValue(caregiverTrigger);
    }
    public  void setCaregiverNeeded(boolean caregiverNeeded){
        CaregiverNeeded.setValue(caregiverNeeded);
    }
    public void setTimerMillis(int timerMillis){
        TimerMillis.setValue(timerMillis);
    }

}
