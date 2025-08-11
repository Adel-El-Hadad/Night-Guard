package com.example.night_guard.Models;

import com.google.firebase.messaging.FirebaseMessaging;

public class User {

    private String firstName;
    private String lastName;
    private String email;
    private String deviceCode;
    private AccountType userAccountType;

    private boolean isUDSReminder = false;
    private boolean isFluidReminder = false;

    private int udsHours;
    private int udsMins;

    public User( String firstName, String lastName, String email, String deviceCode, AccountType userAccountType) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.deviceCode = deviceCode;
        this.userAccountType = userAccountType;
        this.isUDSReminder = false;
        this.isFluidReminder = false;
        this.udsMins = 0;
        this.udsHours = 0;
    }

    public User() {
    }


    // Custom Functions
    public static AccountType assignAccountType(boolean isPatient, boolean isCaregiver){
        if(isPatient && !isCaregiver){
            return AccountType.PATIENT;
        } else if (isCaregiver && !isPatient) {
            return AccountType.CAREGIVER;
        }else {
            return null;
        }
    }


    // Setters and Getters
    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getDeviceCode() {
        return deviceCode;
    }

    public void setDeviceCode(String deviceCode) {
        this.deviceCode = deviceCode;
    }

    public AccountType getUserAccountType() {
        return userAccountType;
    }

    public void setUserAccountType(AccountType userAccountType) {
        this.userAccountType = userAccountType;
    }

    public boolean isUDSReminder() {
        return isUDSReminder;
    }

    public void setUDSReminder(boolean UDSReminder) {
        isUDSReminder = UDSReminder;
    }

    public boolean isFluidReminder() {
        return isFluidReminder;
    }

    public void setFluidReminder(boolean fluidReminder) {
        isFluidReminder = fluidReminder;
    }

    public int getUdsHours() {
        return udsHours;
    }

    public void setUdsHours(int udsHours) {
        this.udsHours = udsHours;
    }

    public int getUdsMins() {
        return udsMins;
    }

    public void setUdsMins(int udsMins) {
        this.udsMins = udsMins;
    }

    // Other Functions
   public void subscribeToFCMTopic(){
        if(this.getUserAccountType()==AccountType.PATIENT){
            FirebaseMessaging.getInstance().subscribeToTopic("Urine_Detected_Topic_DC_"+this.deviceCode);
        } else if (this.getUserAccountType() == AccountType.CAREGIVER) {
            FirebaseMessaging.getInstance().subscribeToTopic("Caregiver_Trigger_Topic_DC_"+this.deviceCode);
        }
    }

   public void  unsubscribeToAllTopics(){
       if(this.getUserAccountType()==AccountType.PATIENT){
           FirebaseMessaging.getInstance().unsubscribeFromTopic("Urine_Detected_Topic_DC_"+this.deviceCode);
       } else if (this.getUserAccountType() == AccountType.CAREGIVER) {
           FirebaseMessaging.getInstance().unsubscribeFromTopic("Caregiver_Trigger_Topic_DC_"+this.deviceCode);
       }
    }



}
