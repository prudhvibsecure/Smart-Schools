package com.bsecure.scsm_mobile.models;

public class MarksModel {


    String marks_id, examinations_id, marks_obtained, teacher_id, class_id, subject, total_marks, marks, rank,grade,percengate;

    public String getPercengate() {
        return percengate;
    }

    public void setPercengate(String percengate) {
        this.percengate = percengate;
    }

    public String getGrade() {
        return grade;
    }

    public void setGrade(String grade) {
        this.grade = grade;
    }

    public String getTotal_marks() {
        return total_marks;
    }

    public void setTotal_marks(String total_marks) {
        this.total_marks = total_marks;
    }

    public String getMarks() {
        return marks;
    }

    public void setMarks(String marks) {
        this.marks = marks;
    }

    public String getRank() {
        return rank;
    }

    public void setRank(String rank) {
        this.rank = rank;
    }

    public String getMarks_id() {
        return marks_id;
    }

    public void setMarks_id(String marks_id) {
        this.marks_id = marks_id;
    }

    public String getExaminations_id() {
        return examinations_id;
    }

    public void setExaminations_id(String examinations_id) {
        this.examinations_id = examinations_id;
    }

    public String getMarks_obtained() {
        return marks_obtained;
    }

    public void setMarks_obtained(String marks_obtained) {
        this.marks_obtained = marks_obtained;
    }

    public String getTeacher_id() {
        return teacher_id;
    }

    public void setTeacher_id(String teacher_id) {
        this.teacher_id = teacher_id;
    }

    public String getClass_id() {
        return class_id;
    }

    public void setClass_id(String class_id) {
        this.class_id = class_id;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    @Override
    public String toString() {
        return super.toString();
    }
}
