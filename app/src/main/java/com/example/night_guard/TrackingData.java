package com.example.night_guard;

import android.util.Log;

import androidx.annotation.NonNull;

import com.example.night_guard.Models.ActiveDeviceCodePackage;
import com.example.night_guard.Models.DayData;
import com.example.night_guard.Models.DayStatus;
import com.example.night_guard.Models.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.time.LocalDate;
import java.util.ArrayList;

public class TrackingData {

    public static ArrayList<DayData> wetVsDryDays = new ArrayList<>();

    public static void addWetDayToFirestore(){
        FirestoreDatabaseHelper firestoreDatabaseHelper = new FirestoreDatabaseHelper();
        firestoreDatabaseHelper.usersCollections.document(FirebaseAuth.getInstance().getCurrentUser().getEmail()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){
                    FirestoreDatabaseHelper.currentUser = task.getResult().toObject(User.class);
                    User user = FirestoreDatabaseHelper.currentUser;
                    firestoreDatabaseHelper.activeDeviceCodesCollection.document(user.getDeviceCode()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if(task.isSuccessful()){
                                ActiveDeviceCodePackage activeDeviceCodePackage = task.getResult().toObject(ActiveDeviceCodePackage.class);
                                LocalDate today = LocalDate.now();
                                DayData todayData = new DayData(today.getDayOfMonth(), today.getMonthValue(), today.getYear(), DayStatus.WET);
                                boolean isAddDay = true;
                                for(int i = 0;i <activeDeviceCodePackage.getWetDays().size(); i++){
                                    if(todayData.isEqual(activeDeviceCodePackage.getWetDays().get(i))){
                                        isAddDay = false;
                                        break;
                                    }
                                }
                                if(isAddDay){
                                    activeDeviceCodePackage.getWetDays().add(todayData);
                                    firestoreDatabaseHelper.activeDeviceCodesCollection.document(user.getDeviceCode()).set(activeDeviceCodePackage);
                                }
                            }
                        }
                    });

                }
            }
        });
    }


    public static void  calculateWetVsDryDays(){
        wetVsDryDays = new ArrayList<>();
        FirestoreDatabaseHelper firestoreDatabaseHelper = new FirestoreDatabaseHelper();

        firestoreDatabaseHelper.usersCollections.document(FirebaseAuth.getInstance().getCurrentUser().getEmail()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){
                    FirestoreDatabaseHelper.currentUser = task.getResult().toObject(User.class);
                    User user = FirestoreDatabaseHelper.currentUser;
                    firestoreDatabaseHelper.activeDeviceCodesCollection.document(user.getDeviceCode()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if(task.isSuccessful()){
                                ActiveDeviceCodePackage activeDeviceCodePackage = task.getResult().toObject(ActiveDeviceCodePackage.class);
                                DayData begin = activeDeviceCodePackage.getPatientSignedUpDate();
                                DayData today = new DayData(LocalDate.now().getDayOfMonth(), LocalDate.now().getMonthValue(), LocalDate.now().getYear(), null);
                                if(!begin.isEqual(today)){
                                    for(int year = begin.getYear(); year<= today.getYear(); year++){
                                        for(int month = begin.getMonth(); month <= today.getMonth(); month++){
                                            for (int day = begin.getDay(); day < today.getDay();day++){
                                                DayData dayData = new DayData(day,month,year,null);
                                                for(DayData wetDay :activeDeviceCodePackage.getWetDays()){
                                                    if(wetDay.isEqual(dayData)){
                                                        dayData.setStatus(DayStatus.WET);
                                                        break;
                                                    }
                                                }
                                                if(dayData.getStatus() == null){
                                                    dayData.setStatus(DayStatus.DRY);
                                                }
                                                wetVsDryDays.add(dayData);
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    });
                }
            }
        });

    }
}
