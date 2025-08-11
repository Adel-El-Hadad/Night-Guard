package com.example.night_guard;

import androidx.annotation.NonNull;

import com.example.night_guard.Models.AccountType;
import com.example.night_guard.Models.ActiveDeviceCodePackage;
import com.example.night_guard.Models.DayData;
import com.example.night_guard.Models.Reward;
import com.example.night_guard.Models.RewardState;
import com.example.night_guard.Models.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.time.LocalDate;
import java.util.ArrayList;

public class FirestoreDatabaseHelper {


    FirebaseFirestore firebaseFirestore;
    CollectionReference usersCollections;
    CollectionReference activeDeviceCodesCollection;

    static User currentUser = new User();
    public static ActiveDeviceCodePackage currentActiveDeviceCodePackage;




    FirestoreDatabaseHelper(){
        firebaseFirestore = FirebaseFirestore.getInstance();
        usersCollections = firebaseFirestore.collection("Users");
        activeDeviceCodesCollection = firebaseFirestore.collection("ActiveDeviceCodes");
    }


    // Adding and Reading User Information
    public void addUserDataToFirestore(User user){
        usersCollections.document(user.getEmail()).set(user);
    }

    public void addActiveDeviceCodePackage(User user){
        activeDeviceCodesCollection.document(user.getDeviceCode()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if(task.isSuccessful()){
                        DocumentSnapshot documentSnapshot = task.getResult();
                        if(documentSnapshot.exists()){
                            ActiveDeviceCodePackage activeDeviceCodePackage = documentSnapshot.toObject(ActiveDeviceCodePackage.class);
                            if(user.getUserAccountType()==AccountType.PATIENT){
                                activeDeviceCodesCollection.document(user.getDeviceCode()).update("patientAssigned", true, "patientEmail", user
                                        .getEmail(), "patientSignedUpDate", new DayData(LocalDate.now().getDayOfMonth(), LocalDate.now().getMonthValue(), LocalDate.now().getYear(), null));
                            } else if (user.getUserAccountType()==AccountType.CAREGIVER) {
                                activeDeviceCodesCollection.document(user.getDeviceCode()).update("caregiverAssigned", true, "caregiverEmail", user
                                        .getEmail());
                            }
                        }else{
                            ActiveDeviceCodePackage activeDeviceCodePackage = new ActiveDeviceCodePackage(user.getEmail(), user.getUserAccountType(), user.getDeviceCode());
                            activeDeviceCodesCollection.document(user.getDeviceCode()).set(activeDeviceCodePackage);
                        }
                    }
            }
        });
    }

public static void getCurrentUserData(String email){
    FirebaseFirestore.getInstance().collection("Users").document(email).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
        @Override
        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
            if(task.isSuccessful()){
                FirestoreDatabaseHelper.currentUser = task.getResult().toObject(User.class);
            }
        }
    });
}

    public static void getcurrentActiveDeviceCodePackage(){


        FirebaseFirestore.getInstance().collection("Users").document(FirebaseAuth.getInstance().getCurrentUser().getEmail()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){
                    User user = task.getResult().toObject(User.class);
                    FirebaseFirestore.getInstance().collection("ActiveDeviceCodes").document(user.getDeviceCode()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if(task.isSuccessful()){
                                FirestoreDatabaseHelper.currentActiveDeviceCodePackage = task.getResult().toObject(ActiveDeviceCodePackage.class);
                            }
                        }
                    });
                }
            }
        });

    }

    public void addRewardToFirestore(String imageUrl, int levelNumber){
        activeDeviceCodesCollection.document(FirestoreDatabaseHelper.currentUser.getDeviceCode()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){
                    ActiveDeviceCodePackage activeDeviceCodePackage = task.getResult().toObject(ActiveDeviceCodePackage.class);
                    Reward reward = activeDeviceCodePackage.getRewards().get(levelNumber-1);
                    reward.setImageUrl(imageUrl);
                    reward.setRewardState(RewardState.LOCKED);
                    activeDeviceCodesCollection.document(FirestoreDatabaseHelper.currentUser.getDeviceCode()).set(activeDeviceCodePackage).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            FirestoreDatabaseHelper.getcurrentActiveDeviceCodePackage();
                        }
                    });
                }
            }
        });
    }
    public void updateFluidReminderFirestore(boolean isChecked){
        usersCollections.document(currentUser.getEmail()).update("fluidReminder", isChecked);
    }
    public void updateUDSReminderFirestore(boolean isChecked, int hours, int mins){
        if(isChecked){
            usersCollections.document(currentUser.getEmail()).update("udsreminder", isChecked);
            usersCollections.document(currentUser.getEmail()).update("udsHours", hours);
            usersCollections.document(currentUser.getEmail()).update("udsMins", mins);
        }else {
            usersCollections.document(currentUser.getEmail()).update("udsreminder", isChecked);
        }
    }
}


