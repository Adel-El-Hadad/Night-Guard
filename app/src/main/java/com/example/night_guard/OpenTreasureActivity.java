package com.example.night_guard;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.night_guard.Models.ActiveDeviceCodePackage;
import com.example.night_guard.Models.RewardState;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;

public class OpenTreasureActivity extends AppCompatActivity {

    TextView tv_level_x_treasure,tv_pay_x_keys;
    Button btn_open_treasure_back, btn_open_treasure;
    int levelNumber = 0, keysPrice = 0;
    ProgressBar open_treasure_progress_bar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_open_treasure);
        tv_level_x_treasure = findViewById(R.id.tv_level_x_treasure);
        tv_pay_x_keys = findViewById(R.id.tv_pay_x_keys);
        btn_open_treasure = findViewById(R.id.btn_open_treasure);
        btn_open_treasure_back = findViewById(R.id.btn_open_treasure_back);
        open_treasure_progress_bar = findViewById(R.id.open_treasure_progress_bar);


        levelNumber = getIntent().getIntExtra("levelNumber", 0);
        if(levelNumber==1){
            keysPrice = 2;
        } else if (levelNumber ==2) {
            keysPrice = 5;
        } else if (levelNumber == 3) {
            keysPrice = 8;
        } else if (levelNumber == 4) {
            keysPrice = 11;
        }else if(levelNumber == 5){
            keysPrice = 14;
        }

        tv_level_x_treasure.setText("Level " + levelNumber + " Treasure");
        tv_pay_x_keys.setText("Pay " + keysPrice + " Keys");

        btn_open_treasure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openTreasure();
            }
        });

        btn_open_treasure_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    public void openTreasure(){
        open_treasure_progress_bar.setVisibility(View.VISIBLE);
        btn_open_treasure.setVisibility(View.GONE);
        int spentKeys = keysPrice;
        FirestoreDatabaseHelper firestoreDatabaseHelper = new FirestoreDatabaseHelper();
        firestoreDatabaseHelper.activeDeviceCodesCollection.document(FirestoreDatabaseHelper.currentUser.getDeviceCode()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){
                    ActiveDeviceCodePackage activeDeviceCodePackage = task.getResult().toObject(ActiveDeviceCodePackage.class);
                    activeDeviceCodePackage.setSpentKeys(activeDeviceCodePackage.getSpentKeys()+spentKeys);
                    activeDeviceCodePackage.getRewards().get(levelNumber-1).setRewardState(RewardState.REDEEMED);
                    firestoreDatabaseHelper.activeDeviceCodesCollection.document(FirestoreDatabaseHelper.currentUser.getDeviceCode()).set(activeDeviceCodePackage).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            open_treasure_progress_bar.setVisibility(View.GONE);
                            FirestoreDatabaseHelper.getcurrentActiveDeviceCodePackage();
                            Toast.makeText(getApplicationContext(), "Treasure Opened Successfully", Toast.LENGTH_LONG).show();
                        }
                    });
                }else{
                    btn_open_treasure.setVisibility(View.VISIBLE);
                }
            }
        });
    }
}