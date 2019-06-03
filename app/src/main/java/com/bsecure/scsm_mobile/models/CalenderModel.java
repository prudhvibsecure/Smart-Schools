package com.bsecure.scsm_mobile.models;

public class CalenderModel {

    String occassion, fromdate, todate;

    public CalenderModel(String occassion, String from)
    {
        this.occassion = occassion;
        this.fromdate = from;
    }

    public CalenderModel()
    {

    }

    public String getOccassion() {
        return occassion;
    }

    public void setOccassion(String occassion) {
        this.occassion = occassion;
    }

    public String getFromdate() {
        return fromdate;
    }

    public void setFromdate(String fromdate) {
        this.fromdate = fromdate;
    }

    public String getTodate() {
        return todate;
    }

    public void setTodate(String todate) {
        this.todate = todate;
    }
}
