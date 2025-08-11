package com.example.night_guard;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

public class ChangeRewardsActivity extends AppCompatActivity {

    Button btn_back_change_reward, btn_add_change_reward;
    ImageView Img_newPrize;
    EditText et_rewardLvlChange;
    Uri imageUri;

    //Firebase Variables
    StorageReference storageReference = FirebaseStorage.getInstance().getReference();
    ProgressBar change_rewards_progress_bar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_rewards);

        // Find Elements by ID
        btn_back_change_reward = findViewById(R.id.btn_back_change_reward);
        btn_add_change_reward = findViewById(R.id.btn_add_change_reward);
        Img_newPrize = findViewById(R.id.Img_newPrize);
        et_rewardLvlChange = findViewById(R.id.et_rewardLvlChange);
        change_rewards_progress_bar = findViewById(R.id.change_rewards_progress_bar);

        Img_newPrize.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent galleryIntent = new Intent();
                galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
                galleryIntent.setType("image/*");
                startActivityForResult(galleryIntent,85);
            }
        });

        btn_add_change_reward.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(validateLevelInput()){
                    uploadImageToStorage();
                }
            }
        });
        btn_back_change_reward.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==85 && resultCode ==RESULT_OK && data !=null){
            imageUri = data.getData();
            Img_newPrize.setImageURI(imageUri);
        }
    }


    public void uploadImageToStorage(){
        change_rewards_progress_bar.setVisibility(View.VISIBLE);
        btn_add_change_reward.setVisibility(View.INVISIBLE);
        StorageReference changedPrizeImageRef = storageReference.child(FirestoreDatabaseHelper.currentUser.getDeviceCode()+"/"+"Level"+Integer.valueOf(et_rewardLvlChange.getText().toString()));
        changedPrizeImageRef.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                changedPrizeImageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        FirestoreDatabaseHelper firestoreDatabaseHelper = new FirestoreDatabaseHelper();
                        firestoreDatabaseHelper.addRewardToFirestore(uri.toString(),Integer.valueOf(et_rewardLvlChange.getText().toString()));
                        Toast.makeText(getApplicationContext(),"Reward Changed successfully!", Toast.LENGTH_LONG).show();
                        change_rewards_progress_bar.setVisibility(View.GONE);
                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                change_rewards_progress_bar.setVisibility(View.GONE);
                btn_add_change_reward.setVisibility(View.VISIBLE);
                Toast.makeText(getApplicationContext(),"Reward Changed Failed!!!", Toast.LENGTH_LONG).show();
            }
        });
    }
    public boolean validateLevelInput(){
        boolean isValid = true;
        if(et_rewardLvlChange.getText().toString().length() == 0){
            et_rewardLvlChange.setError("Reward Level is Empty, Select Level");
            isValid = false;
        }
        else if(Integer.valueOf(et_rewardLvlChange.getText().toString())<1 || Integer.valueOf(et_rewardLvlChange.getText().toString())>5){
            et_rewardLvlChange.setError("Reward Level should be between 1 and 5");
            isValid = false;
        }

        if(imageUri == null){
            isValid = false;
            Toast.makeText(getApplicationContext(), "No Image Selected", Toast.LENGTH_LONG).show();
        }
        return  isValid;
    }
}