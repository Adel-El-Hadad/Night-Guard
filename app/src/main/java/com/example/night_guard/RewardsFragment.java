package com.example.night_guard;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.example.night_guard.Models.AccountType;
import com.example.night_guard.Models.ActiveDeviceCodePackage;
import com.example.night_guard.Models.DayStatus;
import com.example.night_guard.Models.Reward;
import com.example.night_guard.Models.RewardState;
import com.example.night_guard.Models.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.squareup.picasso.Picasso;

public class RewardsFragment extends Fragment {
    Button btn_changeRewards;
    TextView tv_currentKeys;

    ImageView[] img_lvls = new ImageView[5];
    ImageView[] redeemed_lvls = new ImageView[5];
    TextView[] price_lvls = new TextView[5];
    TextView tv_clickOnPrize;
    ActiveDeviceCodePackage activeDeviceCodePackage;
    int rewardsKeys, consecutiveDryDays;

    public RewardsFragment() {

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activeDeviceCodePackage = FirestoreDatabaseHelper.currentActiveDeviceCodePackage;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_rewards, container, false);

        //find Elements By Id
        btn_changeRewards = view.findViewById(R.id.btn_changeRewards);
        tv_currentKeys = view.findViewById(R.id.tv_currentKeys);
        tv_clickOnPrize = view.findViewById(R.id.tv_clickOnPrize);
        img_lvls[0] = view.findViewById(R.id.img_lvl_1);
        img_lvls[1] = view.findViewById(R.id.img_lvl_2);
        img_lvls[2] = view.findViewById(R.id.img_lvl_3);
        img_lvls[3] = view.findViewById(R.id.img_lvl_4);
        img_lvls[4] = view.findViewById(R.id.img_lvl_5);
        price_lvls[0] = view.findViewById(R.id.tv_lvl1_price);
        price_lvls[1] = view.findViewById(R.id.tv_lvl2_price);
        price_lvls[2] = view.findViewById(R.id.tv_lvl3_price);
        price_lvls[3] = view.findViewById(R.id.tv_lvl4_price);
        price_lvls[4] = view.findViewById(R.id.tv_lvl5_price);
        redeemed_lvls[0] = view.findViewById(R.id.redeemed_lvl1);
        redeemed_lvls[1] = view.findViewById(R.id.redeemed_lvl2);
        redeemed_lvls[2] = view.findViewById(R.id.redeemed_lvl3);
        redeemed_lvls[3] = view.findViewById(R.id.redeemed_lvl4);
        redeemed_lvls[4] = view.findViewById(R.id.redeemed_lvl5);

        // Set Initial
        intializeKeysAndRewardsStatus();
        // Roles
        setBtn_changeRewardsVisibility();
        btn_changeRewards.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToChangeRewardsActivity();
            }
        });

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        activeDeviceCodePackage = FirestoreDatabaseHelper.currentActiveDeviceCodePackage;
        intializeKeysAndRewardsStatus();
    }

    public void intializeKeysAndRewardsStatus() {
        // Calculate Consecutive Dry Days
        consecutiveDryDays = 0;
        for (int i = TrackingData.wetVsDryDays.size() - 1; i >= 0; i--) {
            if (TrackingData.wetVsDryDays.get(i).getStatus() == DayStatus.DRY) {
                consecutiveDryDays++;
            } else {
                break;
            }
        }


        //  Calculate Rewards Keys
        FirestoreDatabaseHelper firestoreDatabaseHelper = new FirestoreDatabaseHelper();
        firestoreDatabaseHelper.activeDeviceCodesCollection.document(FirestoreDatabaseHelper.currentUser.getDeviceCode()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    ActiveDeviceCodePackage activeDeviceCodePackage1 = task.getResult().toObject(ActiveDeviceCodePackage.class);

                    if (consecutiveDryDays == 0) {
                        rewardsKeys = 0;
                        activeDeviceCodePackage1.setSpentKeys(0);
                        firestoreDatabaseHelper.activeDeviceCodesCollection.document(FirestoreDatabaseHelper.currentUser.getDeviceCode()).set(activeDeviceCodePackage1).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                FirestoreDatabaseHelper.getcurrentActiveDeviceCodePackage();
                            }
                        });
                    } else {
                        rewardsKeys = consecutiveDryDays - activeDeviceCodePackage1.getSpentKeys();
                    }
                }

                // ------- Add after calculation
                // set Current keys Text
                tv_currentKeys.setText(String.valueOf(rewardsKeys));
                // set unlocked rewards
                setUnlockedRewards(rewardsKeys);
                setInitialImagesForRewards();
                // Set Price Color
                setPrice_lvlsColor(rewardsKeys);
                // add onClickListeners
                addClickListenersToRewards();
            }
        });
    }

    public void setUnlockedRewards(int keys) {
        if (keys >= 2 && activeDeviceCodePackage.getRewards().get(0).getRewardState() == RewardState.LOCKED) {
            activeDeviceCodePackage.getRewards().get(0).setRewardState(RewardState.UNLOCKED);
        }
        if (keys >= 5 && activeDeviceCodePackage.getRewards().get(1).getRewardState() == RewardState.LOCKED) {
            activeDeviceCodePackage.getRewards().get(1).setRewardState(RewardState.UNLOCKED);
        }
        if (keys >= 8 && activeDeviceCodePackage.getRewards().get(2).getRewardState() == RewardState.LOCKED) {
            activeDeviceCodePackage.getRewards().get(2).setRewardState(RewardState.UNLOCKED);
        }
        if (keys >= 11 && activeDeviceCodePackage.getRewards().get(3).getRewardState() == RewardState.LOCKED) {
            activeDeviceCodePackage.getRewards().get(3).setRewardState(RewardState.UNLOCKED);
        }
        if (keys >= 14 && activeDeviceCodePackage.getRewards().get(4).getRewardState() == RewardState.LOCKED) {
            activeDeviceCodePackage.getRewards().get(4).setRewardState(RewardState.UNLOCKED);
        }
    }


    public void setPrice_lvlsColor(int keys) {
        for (int i = 0; i < activeDeviceCodePackage.getRewards().size(); i++) {
            Reward reward = activeDeviceCodePackage.getRewards().get(i);
            if(reward.getRewardState()== RewardState.NOT_ADDED || reward.getRewardState()== RewardState.LOCKED){
                price_lvls[i].setTextColor(Color.parseColor("#FFD22B2B"));
            } else if (reward.getRewardState()==RewardState.REDEEMED || reward.getRewardState()== RewardState.UNLOCKED) {
                price_lvls[i].setTextColor(Color.parseColor("#FF097969"));
            }
        }
    }

    public void setInitialImagesForRewards() {
        User user = FirestoreDatabaseHelper.currentUser;
        if (user.getUserAccountType() == AccountType.PATIENT) {
            for (int i = 0; i < activeDeviceCodePackage.getRewards().size(); i++) {
                Reward reward = activeDeviceCodePackage.getRewards().get(i);
                if (reward.getRewardState() == RewardState.NOT_ADDED) {
                    img_lvls[i].setImageResource(R.drawable.empty_treasure);
                    redeemed_lvls[i].setVisibility(View.INVISIBLE);
                } else if (reward.getRewardState() == RewardState.LOCKED) {
                    img_lvls[i].setImageResource(R.drawable.treasure);
                    redeemed_lvls[i].setVisibility(View.INVISIBLE);
                } else if (reward.getRewardState() == RewardState.UNLOCKED || reward.getRewardState() == RewardState.REDEEMED) {
                    Picasso.get().load(activeDeviceCodePackage.getRewards().get(i).getImageUrl()).into(img_lvls[i]);
                    if (reward.getRewardState() == RewardState.REDEEMED) {
                        redeemed_lvls[i].setVisibility(View.VISIBLE);
                    } else {
                        redeemed_lvls[i].setVisibility(View.INVISIBLE);
                    }
                }
            }
        } else {
            for (int i = 0; i < activeDeviceCodePackage.getRewards().size(); i++) {
                Reward reward = activeDeviceCodePackage.getRewards().get(i);
                if (reward.getRewardState() == RewardState.NOT_ADDED) {
                    img_lvls[i].setImageResource(R.drawable.empty_treasure);
                    redeemed_lvls[i].setVisibility(View.INVISIBLE);
                } else {
                    Picasso.get().load(activeDeviceCodePackage.getRewards().get(i).getImageUrl()).into(img_lvls[i]);
                    if (reward.getRewardState() == RewardState.REDEEMED) {
                        redeemed_lvls[i].setVisibility(View.VISIBLE);
                    }
                }
            }
        }
    }

    public void addClickListenersToRewards() {
        User user = FirestoreDatabaseHelper.currentUser;
        if (user.getUserAccountType() == AccountType.PATIENT) {
            for (int i = 0; i < img_lvls.length; i++) {
                Reward reward = activeDeviceCodePackage.getRewards().get(i);
                int finalI = i;
                img_lvls[i].setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (reward.getRewardState() == RewardState.UNLOCKED) {
                            goToOpenTreasureActivity(finalI + 1);
                        } else {
                            Toast.makeText(getContext(), "This Treasure is " + reward.getRewardState(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        }
    }

    public void setBtn_changeRewardsVisibility() {
        User user = FirestoreDatabaseHelper.currentUser;
        if (user.getUserAccountType() == AccountType.PATIENT) {
            btn_changeRewards.setVisibility(View.GONE);
        } else {
            tv_clickOnPrize.setVisibility(View.GONE);
        }
    }

    public void goToChangeRewardsActivity() {
        Intent intent = new Intent(getActivity(), ChangeRewardsActivity.class);
        startActivity(intent);
    }

    public void goToOpenTreasureActivity(int levelNumber) {
        Intent intent = new Intent(getActivity(), OpenTreasureActivity.class);
        intent.putExtra("levelNumber", levelNumber);
        startActivity(intent);
    }
}
