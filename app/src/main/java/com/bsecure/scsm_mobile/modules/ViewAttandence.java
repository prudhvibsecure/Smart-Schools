package com.bsecure.scsm_mobile.modules;

import java.io.Serializable;
import java.util.ArrayList;

public class ViewAttandence implements Serializable {
    String month, year, no_working_days, no_of_absents;
    ArrayList<String> absentdays;

    public String getMonth() {
        return month;
    }

    public ArrayList<String> getAbsentdays() {
        return absentdays;
    }

    public void setAbsentdays(ArrayList<String> absentdays) {
        this.absentdays = absentdays;
    }

    public void setMonth(String month) {
        this.month = month;
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public String getNo_working_days() {
        return no_working_days;
    }

    public void setNo_working_days(String no_working_days) {
        this.no_working_days = no_working_days;
    }

    public String getNo_of_absents() {
        return no_of_absents;
    }

    public void setNo_of_absents(String no_of_absents) {
        this.no_of_absents = no_of_absents;
    }
}
