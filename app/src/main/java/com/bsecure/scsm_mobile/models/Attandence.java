package com.bsecure.scsm_mobile.models;

public class Attandence {

    String attendance_id ,class_id,student_ids,attendance_date,attendance,teacher_id,roll_no_ids;

    public String getRoll_no_ids() {
        return roll_no_ids;
    }

    public void setRoll_no_ids(String roll_no_ids) {
        this.roll_no_ids = roll_no_ids;
    }

    public String getAttendance_id() {
        return attendance_id;
    }

    public void setAttendance_id(String attendance_id) {
        this.attendance_id = attendance_id;
    }

    public String getClass_id() {
        return class_id;
    }

    public void setClass_id(String class_id) {
        this.class_id = class_id;
    }

    public String getStudent_ids() {
        return student_ids;
    }

    public void setStudent_ids(String student_ids) {
        this.student_ids = student_ids;
    }

    public String getAttendance_date() {
        return attendance_date;
    }

    public void setAttendance_date(String attendance_date) {
        this.attendance_date = attendance_date;
    }

    public String getAttendance() {
        return attendance;
    }

    public void setAttendance(String attendance) {
        this.attendance = attendance;
    }

    public String getTeacher_id() {
        return teacher_id;
    }

    public void setTeacher_id(String teacher_id) {
        this.teacher_id = teacher_id;
    }

    @Override
    public String toString() {
        return super.toString();
    }
}
