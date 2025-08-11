package com.example.night_guard.Models;

import java.time.LocalDate;
import java.util.ArrayList;

public class ActiveDeviceCodePackage {

    private String caregiverEmail;
    private String patientEmail;
    private boolean isPatientAssigned;
    private boolean isCaregiverAssigned;
    private String deviceCode;
    private ArrayList<DayData> wetDays;

    private DayData patientSignedUpDate;

    private int spentKeys;

    public int getSpentKeys() {
        return spentKeys;
    }

    public void setSpentKeys(int spentKeys) {
        this.spentKeys = spentKeys;
    }

    public ArrayList<Reward> getRewards() {
        return rewards;
    }

    public void setRewards(ArrayList<Reward> rewards) {
        this.rewards = rewards;
    }

    private ArrayList<Reward> rewards;


    public ActiveDeviceCodePackage(String email, AccountType userAccountType, String deviceCode) {
        this.deviceCode = deviceCode;
        this.rewards = addRewards();
        this.wetDays = new ArrayList<>();
        this.spentKeys = 0;
        if(userAccountType == AccountType.PATIENT){
            this.patientEmail = email;
            this.isPatientAssigned = true;
            this.caregiverEmail = "";
            this.isCaregiverAssigned = false;
            this.patientSignedUpDate = new DayData(LocalDate.now().getDayOfMonth(), LocalDate.now().getMonthValue(), LocalDate.now().getYear(), null);
        }else if(userAccountType == AccountType.CAREGIVER){
            this.caregiverEmail = email;
            this.isCaregiverAssigned = true;
            this.patientEmail = "";
            this.isPatientAssigned = false;
            this.patientSignedUpDate = null;
        }
    }

    public ActiveDeviceCodePackage() {
    }

    // Getters and Setters

    public DayData getPatientSignedUpDate() {
        return patientSignedUpDate;
    }

    public ArrayList<Reward> addRewards(){
        ArrayList<Reward> rewardsList = new ArrayList<>();

        for(int i = 1; i<=5; i++){
            Reward reward = new Reward("",i);
            rewardsList.add(reward);
        }
        return rewardsList;
    }
    public void setPatientSignedUpDate(DayData patientSignedUpDate) {
        this.patientSignedUpDate = patientSignedUpDate;
    }

    public String getCaregiverEmail() {
        return caregiverEmail;
    }

    public void setCaregiverEmail(String caregiverEmail) {
        this.caregiverEmail = caregiverEmail;
    }

    public String getPatientEmail() {
        return patientEmail;
    }

    public void setPatientEmail(String patientEmail) {
        this.patientEmail = patientEmail;
    }

    public boolean isPatientAssigned() {
        return isPatientAssigned;
    }

    public void setPatientAssigned(boolean patientAssigned) {
        isPatientAssigned = patientAssigned;
    }

    public boolean isCaregiverAssigned() {
        return isCaregiverAssigned;
    }

    public void setCaregiverAssigned(boolean caregiverAssigned) {
        isCaregiverAssigned = caregiverAssigned;
    }
    public String getDeviceCode() {
        return deviceCode;
    }

    public void setDeviceCode(String deviceCode) {
        this.deviceCode = deviceCode;
    }

    public ArrayList<DayData> getWetDays() {
        return wetDays;
    }

    public void setWetDays(ArrayList<DayData> wetDays) {
        this.wetDays = wetDays;
    }



}
