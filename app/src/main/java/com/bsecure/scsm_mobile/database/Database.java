package com.bsecure.scsm_mobile.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class Database extends SQLiteOpenHelper {

    private final static String APP_DATABASE_NAME = "schools_data_mob.db";
    private final static int APP_DATABASE_VERSION = 1;


    public final String CREATE_TABLE_Teachers = "CREATE TABLE Teachers(teacher_id integer primary key autoincrement, school_id TEXT, teacher_name TEXT, phone_number TEXT, class_teacher TEXT,class_id TEXT,status TEXT);";
    public final String CREATE_Teacher_classes = "CREATE TABLE Teacher_classes(teacher_classes_id integer primary key, teacher_id TEXT, class_name TEXT, section TEXT, subjects TEXT,class_id TEXT);";
    public final String CREATE_Students = "CREATE TABLE Students(student_id integer primary key,roll_no TEXT,student_name TEXT,parent_phone_number TEXT,class_id TEXT,status TEXT,extra_subjects TEXT,condition TEXT,examinations_id TEXT,marks_obtained TEXT,teacher_id TEXT,subject TEXT, section TEXT, class_name TEXT, school_id TEXT);";
    public final String CREATE_Attendance = "CREATE TABLE Attendance(attendance_id integer,class_id TEXT,student_ids TEXT,attendance_date TEXT,attendance TEXT,teacher_id TEXT,roll_no_ids TEXT,student_name TEXT,condition TEXT,status TEXT,student_id TEXT,roll_no TEXT,attendance_con TEXT,aDate TEXT);";
    public final String CREATE_Message = "CREATE TABLE Message(message_id TEXT,teacher_id TEXT,message TEXT,message_date TEXT, message_time TEXT, message_status TEXT, no_reply TEXT,sender_name TEXT,user_me TEXT,class_id TEXT,school_id TEXT,attendance_date TEXT,forward TEXT,tutor_id TEXT,forward_status TEXT,notifyType TEXT,student_id TEXT,offlineTag TEXT);";
    public final String CREATE_Transport = "CREATE TABLE Transport(transport_id integer primary key, phone_number TEXT, transport_name TEXT, school_id TEXT, status TEXT,name TEXT,student_id TEXT,created_by TEXT);";
    public final String CREATE_Staff = "CREATE TABLE Staff(staff_id integer primary key,name TEXT,designation TEXT,phone_number TEXT,school_id TEXT,status TEXT);";
    public final String CREATE_TUTORS = "CREATE TABLE TUTORS(tutor_id TEXT primary key,tutor_name TEXT,student_id TEXT,phone_number TEXT,school_id TEXT,tutor_status TEXT);";
    public final String CREATE_MARKS = "CREATE TABLE MARKS(marks_id integer primary key,examinations_id TEXT,marks_obtained TEXT,teacher_id TEXT,class_id TEXT,subject TEXT,student_name TEXT,student_id integer,roll_no TEXT,ro_list TEXT,marks_list TEXT);";
    public final String CREATE_SYLLABUS = "CREATE TABLE SYLLABUS(syllabus_id TEXT primary key,lesson TEXT,description TEXT,subject TEXT,class_id TEXT);";//FOREIGN KEY (student_id) REFERENCES Students (student_id)
    public final String CREATE_PARENT = "CREATE TABLE PARENT_MSG(message_id TEXT,message TEXT,message_date TEXT,message_time TEXT,user_me TEXT,sender_name TEXT,attendance_date TEXT,class_id TEXT);";//FOREIGN KEY (student_id) REFERENCES Students (student_id)
    public final String CREATE_PSTUDENTS = "CREATE TABLE PSTUDENTS(id integer primary key,tutor_id TEXT,student_id TEXT,student_name TEXT, class TEXT, section TEXT);";
    public final String CREATE_SYNCDATA = "CREATE TABLE SYNC(id integer primary key,action_v TEXT,data TEXT);";

    public Database(Context context) {
        super(context, APP_DATABASE_NAME, null, APP_DATABASE_VERSION);
    }

    public Database(Context context, String APP_DATABASE_NAME,
                    int APP_DATABASE_VERSION) {
        super(context, APP_DATABASE_NAME, null, APP_DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_Teachers);
        db.execSQL(CREATE_Teacher_classes);
        db.execSQL(CREATE_Students);
        db.execSQL(CREATE_Attendance);
        db.execSQL(CREATE_Message);
        db.execSQL(CREATE_Transport);
        db.execSQL(CREATE_Staff);
        db.execSQL(CREATE_TUTORS);
        db.execSQL(CREATE_MARKS);
        db.execSQL(CREATE_SYLLABUS);
        db.execSQL(CREATE_PARENT);
        db.execSQL(CREATE_PSTUDENTS);
        db.execSQL(CREATE_SYNCDATA);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onCreate(db);
//        if (newVersion > oldVersion) {
//            //db.execSQL(CREATE_TABLE_MSGS_SENT_1);
//        }
//        if (newVersion == oldVersion) {
//            onCreate(db);
//        }

    }


}
