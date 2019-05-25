package com.bsecure.scsm_mobile.models;

public class TutorsModel {

    String tutor_id,
            tutor_name,
            phone_number,
            student_id,
            school_id,
            tutor_status;

    public String getTutor_id() {
        return tutor_id;
    }

    public void setTutor_id(String tutor_id) {
        this.tutor_id = tutor_id;
    }

    public String getTutor_name() {
        return tutor_name;
    }

    public void setTutor_name(String tutor_name) {
        this.tutor_name = tutor_name;
    }

    public String getPhone_number() {
        return phone_number;
    }

    public void setPhone_number(String phone_number) {
        this.phone_number = phone_number;
    }

    public String getStudent_id() {
        return student_id;
    }

    public void setStudent_id(String student_id) {
        this.student_id = student_id;
    }

    public String getSchool_id() {
        return school_id;
    }

    public void setSchool_id(String school_id) {
        this.school_id = school_id;
    }

    public String getTutor_status() {
        return tutor_status;
    }

    public void setTutor_status(String tutor_status) {
        this.tutor_status = tutor_status;
    }

    @Override
    public String toString() {
        return super.toString();
    }
}
