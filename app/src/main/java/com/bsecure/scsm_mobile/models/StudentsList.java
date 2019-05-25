package com.bsecure.scsm_mobile.models;

public class StudentsList {
    String id, name, stu_class, section;

    public StudentsList(String id, String name, String  stu_class, String section) {
        this.id = id;
        this.name = name;
        this.stu_class = stu_class;
        this.section = section;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getStu_class() {
        return stu_class;
    }

    public void setStu_class(String stu_class) {
        this.stu_class = stu_class;
    }

    public String getSection() {
        return section;
    }

    public void setSection(String section) {
        this.section = section;
    }
}
