package com.example.night_guard.Models;

import androidx.annotation.Nullable;

public class DayData {
    private int day;
    private int month;
    private int year;
    private DayStatus status;


    public DayData(int day, int month, int year, DayStatus status) {
        this.day = day;
        this.month = month;
        this.year = year;
        this.status = status;
    }

    public DayData() {
    }

    public int getDay() {
        return day;
    }

    public void setDay(int day) {
        this.day = day;
    }

    public int getMonth() {
        return month;
    }

    public void setMonth(int month) {
        this.month = month;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public DayStatus getStatus() {
        return status;
    }

    public void setStatus(DayStatus status) {
        this.status = status;
    }

    public boolean isEqual(DayData dayData){

        if(this.day == dayData.day && this.month == dayData.month && this.year == dayData.year ){
            return true;
        }else {
            return false;
        }

    }
    public String dayDataToString(){

        String string = this.day + "-"+ this.month+"-" + this.year;

        if(this.status !=null){
            string = string + "   " + this.status;
        }
        return string;
    }
}
