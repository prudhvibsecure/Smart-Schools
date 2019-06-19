package com.bsecure.scsm_mobile.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;

import com.bsecure.scsm_mobile.models.UserRepo;

import org.json.JSONArray;
import org.json.JSONObject;

import java.lang.reflect.Array;
import java.util.ArrayList;

/**
 * Created by Admin on 2018-12-05.
 */

public class DB_Tables {

    private Context context;

    private Database database = null;
    SQLiteDatabase db;

    public DB_Tables(Context context) {
        database = new Database(context);
    }

    public DB_Tables openDB() {
        try {
            db = database.getWritableDatabase();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return this;
    }

    public void close() {
        try {
            database.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public ArrayList<UserRepo> getRepMessage(String reply_id) {
        ArrayList<UserRepo> mArrayList = new ArrayList<UserRepo>();
        String message = null;
        String sender_name = null;
        String type = null;
        try {
            if (database != null) {

                String cursor_q = "select message,sender_name from Message where message_id='" + reply_id + "'";
                SQLiteDatabase db = database.getWritableDatabase();
                Cursor cursor = db
                        .rawQuery(cursor_q,
                                null);
                try {
                    if (null != cursor && !reply_id.equalsIgnoreCase("0") || !reply_id.isEmpty())
                        if (cursor.getCount() > 0) {
                            cursor.moveToFirst();
                            UserRepo repo = new UserRepo();
                            message = cursor.getString(cursor.getColumnIndex("message"));
                            sender_name = cursor.getString(cursor.getColumnIndex("sender_name"));
                            repo.setMessage(message);
                            repo.setName(sender_name);
                            mArrayList.add(repo);
                        }
                    cursor.close();
                    db.close();
                } finally {
                    db.close();
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return mArrayList;
    }

    public void addClassList(String teacher_id, String teacher_name, String phone_number, String class_teacher, String class_id, String status) {
        SQLiteDatabase db = null;
        try {
            long rawId;
            if (database != null) {
                db = database.getWritableDatabase();
                ContentValues cv = new ContentValues();
                cv.put("teacher_id", teacher_id);
                cv.put("teacher_name", teacher_name);
                cv.put("phone_number", phone_number);
                cv.put("class_teacher", class_teacher);
                cv.put("class_id", class_id);
                cv.put("status", status);
                db.insertWithOnConflict("Teachers", null, cv, SQLiteDatabase.CONFLICT_IGNORE);
                db.close();
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void addTeacherClassList(String teacher_classes_id, String class_id, String section, String class_name, String subjects) {
        SQLiteDatabase db = null;
        try {
            long rawId;
            if (database != null) {
                db = database.getWritableDatabase();
                ContentValues cv = new ContentValues();
                cv.put("teacher_classes_id", teacher_classes_id);
                cv.put("subjects", subjects);
                cv.put("section", section);
                cv.put("class_name", class_name);
                cv.put("class_id", class_id);
                db.insertWithOnConflict("Teacher_classes", null, cv, SQLiteDatabase.CONFLICT_IGNORE);
                db.close();
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    public void updateClassList(String teacher_classes_id, String class_id, String section, String class_name, String subjects) {
        SQLiteDatabase db = null;
        try {
            if (database != null) {
                db = database.getWritableDatabase();

                String iwhereClause = "class_id='" + class_id + "' and teacher_classes_id='" + teacher_classes_id + "'";
                ContentValues cv = new ContentValues();
                cv.put("teacher_classes_id", teacher_classes_id);
                cv.put("subjects", subjects);
                cv.put("section", section);
                cv.put("class_name", class_name);
                cv.put("class_id", class_id);
                db.update("Teacher_classes", cv, iwhereClause, null);
                db.close();
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void deleteClass(String class_id) {
        SQLiteDatabase db = null;
        try {
            long rawId;
            if (database != null) {
                String iwhereClause = "teacher_classes_id='" + class_id + "'";
                db = database.getWritableDatabase();
                db.delete("Teacher_classes", iwhereClause, null);
                db.close();
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void updateClassList(String ad_cname, String section, String tc_seb, String class_ids) {
        SQLiteDatabase db = null;
        try {
            if (database != null) {
                db = database.getWritableDatabase();

                String iwhereClause = "class_id='" + class_ids + "'";
                ContentValues cv = new ContentValues();
                cv.put("class_name", ad_cname);
                cv.put("section", section);
                cv.put("subjects", tc_seb);
                db.update("Classes", cv, iwhereClause, null);
                db.close();
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public String getClassId() {
        String Id = null;
        try {
            if (database != null) {

                String cursor_q = "select * from Classes";
                SQLiteDatabase db = database.getWritableDatabase();
                Cursor cursor = db
                        .rawQuery(cursor_q,
                                null);
                try {
                    if (null != cursor)
                        if (cursor.getCount() > 0) {
                            cursor.moveToLast();
                            Id = cursor.getString(cursor.getColumnIndex("class_id"));
                        }
                    cursor.close();
                    db.close();
                } finally {
                    db.close();
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return Id;
    }

    public String getclsList() {
        JSONObject jsonObject = new JSONObject();

        try {
            JSONArray array = new JSONArray();
            if (database != null) {
                SQLiteDatabase db = database.getWritableDatabase();

                String sql = "select * from Teacher_classes";
                Cursor cursor = db.rawQuery(sql,
                        null);
                if (cursor != null) {
                    while (cursor.moveToNext()) {

                        final JSONObject json = new JSONObject();
                        json.put("teacher_classes_id", cursor.getString(cursor.getColumnIndex("teacher_classes_id")));
                        json.put("section", cursor.getString(cursor.getColumnIndex("section")));
                        json.put("subjects", cursor.getString(cursor.getColumnIndex("subjects")));
                        json.put("class_id", cursor.getString(cursor.getColumnIndex("class_id")));
                        json.put("class_name", cursor.getString(cursor.getColumnIndex("class_name")));
                        array.put(json);
                    }

                    jsonObject.put("teacher_classes_details", array);
                    cursor.close();
                }
                db.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return jsonObject.toString();
    }

    public void addTutors(String tutor_id, String tutor_name, String phone_number, String student_id, String school_id, String tutor_status) {

        SQLiteDatabase db = null;
        try {
            long rawId;
            if (database != null) {
                db = database.getWritableDatabase();
                ContentValues cv = new ContentValues();
                cv.put("tutor_id", tutor_id);
                cv.put("tutor_name", tutor_name);
                cv.put("phone_number", phone_number);
                cv.put("student_id", student_id);
                cv.put("school_id", school_id);
                cv.put("tutor_status", tutor_status);
                db.insertWithOnConflict("TUTORS", null, cv, SQLiteDatabase.CONFLICT_IGNORE);
                db.close();
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public String getTutorsList() {

        JSONObject jsonObject = new JSONObject();

        try {
            JSONArray array = new JSONArray();
            if (database != null) {
                SQLiteDatabase db = database.getWritableDatabase();

                String sql = "select * from TUTORS";
                Cursor cursor = db.rawQuery(sql,
                        null);
                if (cursor != null) {
                    while (cursor.moveToNext()) {

                        final JSONObject json = new JSONObject();
                        json.put("tutor_id", cursor.getString(cursor.getColumnIndex("tutor_id")));
                        json.put("tutor_name", cursor.getString(cursor.getColumnIndex("tutor_name")));
                        json.put("phone_number", cursor.getString(cursor.getColumnIndex("phone_number")));
                        json.put("student_id", cursor.getString(cursor.getColumnIndex("student_id")));
                        json.put("school_id", cursor.getString(cursor.getColumnIndex("school_id")));
                        json.put("tutor_status", cursor.getString(cursor.getColumnIndex("tutor_status")));
                        array.put(json);
                    }

                    jsonObject.put("tutor_body", array);
                    cursor.close();
                }
                db.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return jsonObject.toString();
    }


    public String getTutorsList_Active() {

        JSONObject jsonObject = new JSONObject();

        try {
            JSONArray array = new JSONArray();
            if (database != null) {
                SQLiteDatabase db = database.getWritableDatabase();

                String sql = "select * from TUTORS where tutor_status='0'";
                Cursor cursor = db.rawQuery(sql,
                        null);
                if (cursor != null) {
                    while (cursor.moveToNext()) {

                        final JSONObject json = new JSONObject();
                        json.put("tutor_id", cursor.getString(cursor.getColumnIndex("tutor_id")));
                        json.put("tutor_name", cursor.getString(cursor.getColumnIndex("tutor_name")));
                        json.put("phone_number", cursor.getString(cursor.getColumnIndex("phone_number")));
                        json.put("student_id", cursor.getString(cursor.getColumnIndex("student_id")));
                        json.put("school_id", cursor.getString(cursor.getColumnIndex("school_id")));
                        json.put("tutor_status", cursor.getString(cursor.getColumnIndex("tutor_status")));
                        array.put(json);
                    }

                    jsonObject.put("tutor_body", array);
                    cursor.close();
                }
                db.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return jsonObject.toString();
    }

    ///

    public void tutorDeleteStudent(final String stu_id, String phone) {

        SQLiteDatabase db = null;
        try {
            long rawId;
            if (database != null) {
                String iwhereClause = "student_id='" + stu_id + "' and phone_number='" + phone + "'";
                db = database.getWritableDatabase();
                db.delete("TUTOR", iwhereClause, null);
                db.close();
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void parentDeleteStudent(final String stu_id) {

        SQLiteDatabase db = null;
        try {
            long rawId;
            if (database != null) {
                String iwhereClause = "student_id='" + stu_id + "'";
                db = database.getWritableDatabase();
                db.delete("Students", iwhereClause, null);
                db.close();
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void parentDeleteTutor(final String stu_id, String tut_id) {

        SQLiteDatabase db = null;
        try {
            long rawId;
            if (database != null) {
                String iwhereClause = "student_id='" + stu_id + "' and tutor_id='" + tut_id + "'";
                db = database.getWritableDatabase();
                db.delete("TUTORS", iwhereClause, null);
                db.close();
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    ///

    public void addStaff(String staff_id, String name, String phone_number, String designation, String school_id, String staus) {
        SQLiteDatabase db = null;
        try {
            long rawId;
            if (database != null) {
                db = database.getWritableDatabase();
                ContentValues cv = new ContentValues();
                cv.put("staff_id", staff_id);
                cv.put("name", name);
                cv.put("phone_number", phone_number);
                cv.put("designation", designation);
                cv.put("school_id", school_id);
                cv.put("status", staus);
                db.insertWithOnConflict("Staff", null, cv, SQLiteDatabase.CONFLICT_IGNORE);
                db.close();
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void updateStaff(String staff_id, String name, String phone_number, String designation, String school_id, String staus) {
        SQLiteDatabase db = null;
        try {
            long rawId;
            if (database != null) {
                String iwhereClause = "staff_id='" + staff_id + "'";
                db = database.getWritableDatabase();
                ContentValues cv = new ContentValues();
                cv.put("staff_id", staff_id);
                cv.put("name", name);
                cv.put("phone_number", phone_number);
                cv.put("designation", designation);
                cv.put("school_id", school_id);
                cv.put("status", staus);
                db.update("Staff", cv, iwhereClause, null);
                db.close();
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void updateStaffStatus(String staff_id, String school_id, String staus) {
        SQLiteDatabase db = null;
        try {
            long rawId;
            if (database != null) {
                String iwhereClause = "staff_id='" + staff_id + "'";
                db = database.getWritableDatabase();
                ContentValues cv = new ContentValues();
                cv.put("staff_id", staff_id);
                cv.put("school_id", school_id);
                cv.put("status", staus);
                db.update("Staff", cv, iwhereClause, null);
                db.close();
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void deleteStaff(String staff_id, String school_id, String staus) {
        SQLiteDatabase db = null;
        try {
            long rawId;
            if (database != null) {
                String iwhereClause = "staff_id='" + staff_id + "'";
                db = database.getWritableDatabase();
                db.delete("Staff", iwhereClause, null);
                db.close();
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public String getStaffeList() {

        JSONObject jsonObject = new JSONObject();

        try {
            JSONArray array = new JSONArray();
            if (database != null) {
                SQLiteDatabase db = database.getWritableDatabase();

                String sql = "select * from Staff";
                Cursor cursor = db.rawQuery(sql,
                        null);
                if (cursor != null) {
                    while (cursor.moveToNext()) {

                        final JSONObject json = new JSONObject();
                        json.put("staff_id", cursor.getString(cursor.getColumnIndex("staff_id")));
                        json.put("name", cursor.getString(cursor.getColumnIndex("name")));
                        json.put("phone_number", cursor.getString(cursor.getColumnIndex("phone_number")));
                        json.put("designation", cursor.getString(cursor.getColumnIndex("designation")));
                        json.put("school_id", cursor.getString(cursor.getColumnIndex("school_id")));
                        json.put("status", cursor.getString(cursor.getColumnIndex("status")));
                        array.put(json);
                    }

                    jsonObject.put("staff_body", array);
                    cursor.close();
                }
                db.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return jsonObject.toString();
    }

    public void addTransport(String transport_id, String transport_name, String phone_number, String school_id, String s, String st_id) {

        SQLiteDatabase db = null;
        try {
            long rawId;
            if (database != null) {
                db = database.getWritableDatabase();
                ContentValues cv = new ContentValues();
                cv.put("transport_id", transport_id);
                cv.put("transport_name", transport_name);
                cv.put("phone_number", phone_number);
                cv.put("school_id", school_id);
                cv.put("status", s);
                cv.put("student_id", st_id);
                db.insertWithOnConflict("Transport", null, cv, SQLiteDatabase.CONFLICT_IGNORE);
                db.close();
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public String getTransList() {

        JSONObject jsonObject = new JSONObject();

        try {
            JSONArray array = new JSONArray();
            if (database != null) {
                SQLiteDatabase db = database.getWritableDatabase();

                String sql = "select * from Transport";
                Cursor cursor = db.rawQuery(sql,
                        null);
                if (cursor != null) {
                    while (cursor.moveToNext()) {

                        final JSONObject json = new JSONObject();
                        json.put("transport_id", cursor.getString(cursor.getColumnIndex("transport_id")));
                        json.put("transport_name", cursor.getString(cursor.getColumnIndex("transport_name")));
                        json.put("phone_number", cursor.getString(cursor.getColumnIndex("phone_number")));
                        json.put("school_id", cursor.getString(cursor.getColumnIndex("school_id")));
                        json.put("status", cursor.getString(cursor.getColumnIndex("status")));
                        json.put("student_id", cursor.getString(cursor.getColumnIndex("student_id")));
                        array.put(json);
                    }

                    jsonObject.put("trans_body", array);
                    cursor.close();
                }
                db.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return jsonObject.toString();
    }


    public void addstudents(String student_id, String roll_no, String student_name, String status, String class_id, String section, String class_name) {
        SQLiteDatabase db = null;
        try {
            long rawId;
            if (database != null) {
                db = database.getWritableDatabase();
                ContentValues cv = new ContentValues();
                cv.put("student_id", student_id);
                cv.put("roll_no", roll_no);
                cv.put("student_name", student_name);
                cv.put("status", status);
                cv.put("condition", "0");
                cv.put("class_id", class_id);
                cv.put("section", section);
                cv.put("class_name", class_name);
                db.insertWithOnConflict("Students", null, cv, SQLiteDatabase.CONFLICT_IGNORE);
                db.close();
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void addTutorStudents(String id, String tutor_id, String student_id, String student_name, String stu_class, String section) {
        SQLiteDatabase db = null;
        try {
            long rawId;
            if (database != null) {
                db = database.getWritableDatabase();
                ContentValues cv = new ContentValues();
                cv.put("id", id);
                cv.put("tutor_id", tutor_id);
                cv.put("student_id", student_id);
                cv.put("student_name", student_name);
                cv.put("class", stu_class);
                cv.put("section", section);
                db.insertWithOnConflict("PSTUDENTS", null, cv, SQLiteDatabase.CONFLICT_IGNORE);
                db.close();
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    public void addstudentsAttandence(String student_id, String roll_no, String student_name, String status, String class_id, String date) {
        SQLiteDatabase db = null;
        try {
            long rawId;
            if (database != null) {
                db = database.getWritableDatabase();
                ContentValues cv = new ContentValues();
                cv.put("student_id", student_id);
                cv.put("roll_no", roll_no);
                cv.put("student_name", student_name);
                cv.put("status", status);
                cv.put("condition", "0");
                cv.put("class_id", class_id);
                cv.put("aDate", date);
                //db.insertWithOnConflict("Attendance", null, cv, SQLiteDatabase.CONFLICT_IGNORE);
                db.insert("Attendance", null, cv);
                db.close();
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public String getAttandeceClass(String class_id) {
        String Id = "";
        try {
            if (database != null) {

                String cursor_q = "select * from Attendance where class_id ='" + class_id + "'";

                SQLiteDatabase db = database.getWritableDatabase();
                Cursor cursor = db
                        .rawQuery(cursor_q,
                                null);
                try {
                    if (null != cursor)
                        if (cursor.getCount() > 0) {
                            cursor.moveToLast();
                            Id = cursor.getString(cursor.getColumnIndex("class_id"));
                        }
                    cursor.close();
                    db.close();
                } finally {
                    db.close();
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return Id;
    }


    public String getstudentsList_sub(String examinations_id) {
        JSONObject jsonObject = new JSONObject();

        try {
            JSONArray array = new JSONArray();
            if (database != null) {
                SQLiteDatabase db = database.getWritableDatabase();

                String sql = "select * from MARKS where examinations_id='" + examinations_id + "'";
                Cursor cursor = db.rawQuery(sql,
                        null);
                if (cursor != null) {
                    while (cursor.moveToNext()) {

                        final JSONObject json = new JSONObject();
                        json.put("student_id", cursor.getString(cursor.getColumnIndex("student_id")));
                        json.put("roll_no", cursor.getString(cursor.getColumnIndex("roll_no")));
                        json.put("student_name", cursor.getString(cursor.getColumnIndex("student_name")));
                        json.put("class_id", cursor.getString(cursor.getColumnIndex("class_id")));
//                        json.put("status", cursor.getString(cursor.getColumnIndex("status")));
//                        json.put("condition", cursor.getString(cursor.getColumnIndex("condition")));
                        json.put("marks_obtained", cursor.getString(cursor.getColumnIndex("marks_obtained")));
                        json.put("subject", cursor.getString(cursor.getColumnIndex("subject")));
                        array.put(json);
                    }

                    jsonObject.put("student_details", array);
                    cursor.close();
                }
                db.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return jsonObject.toString();
    }

    public String getstudentsList() {
        JSONObject jsonObject = new JSONObject();

        try {
            JSONArray array = new JSONArray();
            if (database != null) {
                SQLiteDatabase db = database.getWritableDatabase();

                String sql = "select * from Students";
                Cursor cursor = db.rawQuery(sql,
                        null);
                if (cursor != null) {
                    while (cursor.moveToNext()) {

                        final JSONObject json = new JSONObject();
                        json.put("student_id", cursor.getString(cursor.getColumnIndex("student_id")));
                        json.put("roll_no", cursor.getString(cursor.getColumnIndex("roll_no")));
                        json.put("student_name", cursor.getString(cursor.getColumnIndex("student_name")));
                        json.put("class_id", cursor.getString(cursor.getColumnIndex("class_id")));
                        json.put("status", cursor.getString(cursor.getColumnIndex("status")));
                        json.put("condition", cursor.getString(cursor.getColumnIndex("condition")));
                        json.put("marks_obtained", cursor.getString(cursor.getColumnIndex("marks_obtained")));
                        json.put("subject", cursor.getString(cursor.getColumnIndex("subject")));
                        json.put("section", cursor.getString(cursor.getColumnIndex("section")));
                        json.put("class_name", cursor.getString(cursor.getColumnIndex("class_name")));

                        array.put(json);
                    }

                    jsonObject.put("student_details", array);
                    cursor.close();
                }
                db.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return jsonObject.toString();
    }

   /* public String getstudentsList() {
        JSONObject jsonObject = new JSONObject();

        try {
            JSONArray array = new JSONArray();
            if (database != null) {
                SQLiteDatabase db = database.getWritableDatabase();

                String sql = "select * from PSTUDENTS";
                Cursor cursor = db.rawQuery(sql,
                        null);
                if (cursor != null) {
                    while (cursor.moveToNext()) {

                        final JSONObject json = new JSONObject();
                        json.put("student_id", cursor.getString(cursor.getColumnIndex("student_id")));
                        json.put("roll_no", cursor.getString(cursor.getColumnIndex("roll_no")));
                        json.put("student_name", cursor.getString(cursor.getColumnIndex("student_name")));
                        json.put("class_id", cursor.getString(cursor.getColumnIndex("class_id")));
                        json.put("status", cursor.getString(cursor.getColumnIndex("status")));
                        json.put("condition", cursor.getString(cursor.getColumnIndex("condition")));
                        json.put("marks_obtained", cursor.getString(cursor.getColumnIndex("marks_obtained")));
                        json.put("subject", cursor.getString(cursor.getColumnIndex("subject")));
                        array.put(json);
                    }

                    jsonObject.put("student_details", array);
                    cursor.close();
                }
                db.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return jsonObject.toString();
    }*/

    public void updateVV(String student_id, String chk, String class_id, String attdate) {
        SQLiteDatabase db = null;
        try {
            if (database != null) {
                db = database.getWritableDatabase();
                String iwhereClause = "student_id='" + student_id + "' and class_id='" + class_id + "' and aDate='" + attdate + "'";
                ContentValues cv = new ContentValues();
                cv.put("condition", chk);
                db.update("Attendance", cv, iwhereClause, null);
                db.close();
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    public void addAttendance(String attendance_id, String class_id, String student_ids, String s1, String teacher_id, String roll_no_ids) {
        SQLiteDatabase db = null;
        try {
            long rawId;
            if (database != null) {
                db = database.getWritableDatabase();
                ContentValues cv = new ContentValues();
                cv.put("attendance_id", attendance_id);
                cv.put("class_id", class_id);
                cv.put("student_ids", student_ids);
                cv.put("attendance_date", s1);
                cv.put("teacher_id", teacher_id);
                cv.put("roll_no_ids", roll_no_ids);
                db.insertWithOnConflict("Attendance", null, cv, SQLiteDatabase.CONFLICT_REPLACE);
                db.close();
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    public void addSyncAttendance(String attendance_id, String class_id, String student_ids, String s1, String teacher_id, String roll_no_ids, String adate) {
        SQLiteDatabase db = null;
        try {
            long rawId;
            if (database != null) {
                db = database.getWritableDatabase();
                ContentValues cv = new ContentValues();
                cv.put("attendance_id", attendance_id);
                cv.put("class_id", class_id);
                cv.put("student_ids", student_ids);
                cv.put("attendance_date", s1);
                cv.put("teacher_id", teacher_id);
                cv.put("roll_no_ids", roll_no_ids);
                //cv.put("aDate", adate);
                //  db.insert("Attendance", null, cv);
                db.insertWithOnConflict("Attendance", null, cv, SQLiteDatabase.CONFLICT_REPLACE);
                db.close();
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

    }


    public void updateAttendance(String attendance_date, String attendance_id, String class_id, String student_ids, String status, String roll_nos, String aDate) {
        try {
            SQLiteDatabase db = null;
            if (database != null) {
                db = database.getWritableDatabase();
                String iwhereClause = "student_id='" + student_ids + "' and class_id='" + class_id + "' and  aDate='" + attendance_date + "'";

                ContentValues cv = new ContentValues();
                //cv.put("attendance_id", attendance_id);
                // cv.put("student_ids", student_ids);
                // cv.put("attendance_date", attendance_date);
                cv.put("attendance_con", "Yes");
                cv.put("status", status);
                // cv.put("aDate", aDate);
                // cv.put("roll_no_ids", roll_nos);
                db.update("Attendance", cv, iwhereClause, null);
                db.close();
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    public void updateSyncAttendance(String attendance_date, String attendance_id, String class_id, String student_ids, String status, String roll_nos, String aDate) {
        try {
            SQLiteDatabase db = null;
            if (database != null) {
                db = database.getWritableDatabase();
                //String iwhereClause = "aDate='" + aDate + "' and class_id='" + class_id + "' and attendance_id='" + attendance_id + "'";
                String iwhereClause = "class_id='" + class_id + "' and attendance_id='" + attendance_id + "'";

                ContentValues cv = new ContentValues();
                //cv.put("attendance_id", attendance_id);
                cv.put("student_ids", student_ids);
                // cv.put("attendance_date", attendance_date);
                cv.put("attendance_con", "Yes");
                cv.put("aDate", aDate);
                cv.put("roll_no_ids", roll_nos);
                db.update("Attendance", cv, iwhereClause, null);
                db.close();
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

    }


    public String getAttandeceDate(String section, String class_id) {
        String Id = "";
        try {
            if (database != null) {

                String cursor_q = "select * from Attendance where class_id ='" + class_id + "' and attendance_con='Yes'";
                SQLiteDatabase db = database.getWritableDatabase();
                Cursor cursor = db
                        .rawQuery(cursor_q,
                                null);
                try {
                    if (null != cursor)
                        if (cursor.getCount() > 0) {
                            cursor.moveToLast();
                            Id = cursor.getString(cursor.getColumnIndex("attendance_date"));
                        }
                    cursor.close();
                    db.close();
                } finally {
                    db.close();
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return Id;
    }

    public String getSyncAttandeceDate(String attendance_id) {
        String Id = "";
        try {
            if (database != null) {

                String cursor_q = "select * from Attendance where attendance_id ='" + attendance_id + "'";

                SQLiteDatabase db = database.getWritableDatabase();
                Cursor cursor = db
                        .rawQuery(cursor_q,
                                null);
                try {
                    if (null != cursor)
                        if (cursor.getCount() > 0) {
                            cursor.moveToLast();
                            Id = cursor.getString(cursor.getColumnIndex("attendance_date"));
                        }
                    cursor.close();
                    db.close();
                } finally {
                    db.close();
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return Id;
    }


    public String getAttandenceList(String class_id, String attendDate) {
        JSONObject jsonObject = new JSONObject();

        try {
            JSONArray array = new JSONArray();
            if (database != null) {
                SQLiteDatabase db = database.getWritableDatabase();

                String sql = "select * from Attendance where class_id='" + class_id + "' and aDate='" + attendDate + "' and attendance_date is not null";
                Cursor cursor = db.rawQuery(sql,
                        null);
                if (cursor != null) {
                    while (cursor.moveToNext()) {

                        final JSONObject json = new JSONObject();
                        json.put("attendance_id", cursor.getString(cursor.getColumnIndex("attendance_id")));
                        json.put("class_id", cursor.getString(cursor.getColumnIndex("class_id")));
                        json.put("student_ids", cursor.getString(cursor.getColumnIndex("student_ids")));
                        json.put("attendance_date", cursor.getString(cursor.getColumnIndex("attendance_date")));
                        json.put("teacher_id", cursor.getString(cursor.getColumnIndex("teacher_id")));
                        json.put("roll_no_ids", cursor.getString(cursor.getColumnIndex("roll_no_ids")));
                        array.put(json);
                    }

                    jsonObject.put("attendance_details", array);
                    cursor.close();
                }
                db.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return jsonObject.toString();
    }


    public String getSyncAttandenceList(String class_id) {
        JSONObject jsonObject = new JSONObject();

        try {
            JSONArray array = new JSONArray();
            if (database != null) {
                SQLiteDatabase db = database.getWritableDatabase();

                String sql = "select * from Attendance where class_id='" + class_id + "' and attendance_date is not null";
                Cursor cursor = db.rawQuery(sql,
                        null);
                if (cursor != null) {
                    while (cursor.moveToNext()) {

                        final JSONObject json = new JSONObject();
                        json.put("attendance_id", cursor.getString(cursor.getColumnIndex("attendance_id")));
                        json.put("class_id", cursor.getString(cursor.getColumnIndex("class_id")));
                        json.put("student_ids", cursor.getString(cursor.getColumnIndex("student_ids")));
                        json.put("attendance_date", cursor.getString(cursor.getColumnIndex("attendance_date")));
                        json.put("teacher_id", cursor.getString(cursor.getColumnIndex("teacher_id")));
                        json.put("roll_no_ids", cursor.getString(cursor.getColumnIndex("roll_no_ids")));
                        array.put(json);
                    }

                    jsonObject.put("attendance_details", array);
                    cursor.close();
                }
                db.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return jsonObject.toString();
    }

    public void updateStudents1(String student_id, String status) {

        try {
            SQLiteDatabase db = null;
            if (database != null) {
                db = database.getWritableDatabase();
                String iwhereClause = "student_id='" + student_id + "'";
                ContentValues cv = new ContentValues();
                cv.put("status", status);
                db.update("Students", cv, iwhereClause, null);
                db.close();
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    public String getMarks(String exam_id, String class_id, String sub) {

        String marks = "";
        try {
            if (database != null) {

                // String cursor_q = "select * from Students where examinations_id='" + exam_id + "' and class_id='" + class_id + "' and subject='" + sub + "'";
                String cursor_q = "select * from MARKS where examinations_id='" + exam_id + "' and class_id='" + class_id + "' and subject='" + sub + "'";
                SQLiteDatabase db = database.getWritableDatabase();
                Cursor cursor = db
                        .rawQuery(cursor_q,
                                null);
                try {
                    if (null != cursor)
                        if (cursor.getCount() > 0) {
                            cursor.moveToFirst();
                            marks = cursor.getString(cursor.getColumnIndex("marks_obtained"));
                        }
                    cursor.close();
                    db.close();
                } finally {
                    db.close();
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return marks;
    }

    public String getMarks2(String exam_id, String class_id, String sub, String st_id) {

        String marks = "";
        try {
            if (database != null) {

                // String cursor_q = "select * from Students where examinations_id='" + exam_id + "' and class_id='" + class_id + "' and subject='" + sub + "'";
                String cursor_q = "select * from MARKS where examinations_id='" + exam_id + "' and class_id='" + class_id + "' and subject='" + sub + "' and student_id='" + st_id + "'";
                SQLiteDatabase db = database.getWritableDatabase();
                Cursor cursor = db
                        .rawQuery(cursor_q,
                                null);
                try {
                    if (null != cursor)
                        if (cursor.getCount() > 0) {
                            cursor.moveToFirst();
                            marks = cursor.getString(cursor.getColumnIndex("marks_obtained"));
                        }
                    cursor.close();
                    db.close();
                } finally {
                    db.close();
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return marks;
    }

    public String getStudentId(String marks) {

        String students_id = "";
        try {
            if (database != null) {

                String cursor_q = "select * from Students where marks_obtained='" + marks + "'";
                SQLiteDatabase db = database.getWritableDatabase();
                Cursor cursor = db
                        .rawQuery(cursor_q,
                                null);
                try {
                    if (null != cursor)
                        if (cursor.getCount() > 0) {
                            cursor.moveToFirst();
                            students_id = cursor.getString(cursor.getColumnIndex("student_id"));
                        }
                    cursor.close();
                    db.close();
                } finally {
                    db.close();
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return students_id;
    }

    public void addSyllabus(String sy_id, String newText, String desc, String subject, String class_id) {
        SQLiteDatabase db = null;
        try {
            long rawId;
            if (database != null) {
                db = database.getWritableDatabase();
                ContentValues cv = new ContentValues();
                cv.put("syllabus_id", sy_id);
                cv.put("lesson", newText);
                cv.put("description", desc);
                cv.put("subject", subject);
                cv.put("class_id", class_id);
                db.insertWithOnConflict("SYLLABUS", null, cv, SQLiteDatabase.CONFLICT_IGNORE);
                db.close();
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public String getSyllabusList(String subject, String class_id) {
        JSONObject jsonObject = new JSONObject();

        try {
            JSONArray array = new JSONArray();
            if (database != null) {
                SQLiteDatabase db = database.getWritableDatabase();

                String sql = "select * from SYLLABUS where subject='" + subject + "' and class_id='" + class_id + "'";
                Cursor cursor = db.rawQuery(sql,
                        null);
                if (cursor != null) {
                    while (cursor.moveToNext()) {

                        final JSONObject json = new JSONObject();
                        json.put("syllabus_id", cursor.getString(cursor.getColumnIndex("syllabus_id")));
                        json.put("lesson", cursor.getString(cursor.getColumnIndex("lesson")));
                        json.put("description", cursor.getString(cursor.getColumnIndex("description")));
                        json.put("subject", cursor.getString(cursor.getColumnIndex("subject")));
                        array.put(json);
                    }

                    jsonObject.put("syllabus_details", array);
                    cursor.close();
                }
                db.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return jsonObject.toString();
    }

    public void syllabusRemove(String syllabus_id) {
        try {
            if (database != null) {

                String iwhereClause = "syllabus_id='" + syllabus_id + "'";
                SQLiteDatabase db = database.getWritableDatabase();
                db.delete("SYLLABUS", iwhereClause, null);
                db.close();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void syllabusUpdateV(String syllabus_id, String newText, String desc, String subject, String class_id) {
        try {
            SQLiteDatabase db = null;
            if (database != null) {
                db = database.getWritableDatabase();
                String iwhereClause = "syllabus_id='" + syllabus_id + "' and class_id='" + class_id + "'";
                ContentValues cv = new ContentValues();
                cv.put("lesson", newText);
                cv.put("description", desc);
                cv.put("subject", subject);
                cv.put("class_id", class_id);
                db.update("SYLLABUS", cv, iwhereClause, null);
                db.close();
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public String getchatList_view(String class_id, String stu_id) {

        JSONObject jsonObject = new JSONObject();

        try {
            JSONArray array = new JSONArray();
            if (database != null) {
                SQLiteDatabase db = database.getWritableDatabase();

                String sql = "select * from Message where class_id='" + class_id + "' and student_id='" + stu_id + "'";
                Cursor cursor = db.rawQuery(sql,
                        null);
                if (cursor != null) {
                    while (cursor.moveToNext()) {

                        final JSONObject json = new JSONObject();
                        json.put("message_id", cursor.getString(cursor.getColumnIndex("message_id")));
                        json.put("message", cursor.getString(cursor.getColumnIndex("message")));
                        json.put("message_date", cursor.getString(cursor.getColumnIndex("message_date")));
                        json.put("message_status", cursor.getString(cursor.getColumnIndex("message_status")));
                        json.put("sender_name", cursor.getString(cursor.getColumnIndex("sender_name")));
                        json.put("no_reply", cursor.getString(cursor.getColumnIndex("no_reply")));
                        json.put("user_me", cursor.getString(cursor.getColumnIndex("user_me")));
                        json.put("forward", cursor.getString(cursor.getColumnIndex("forward")));
                        json.put("forward_status", cursor.getString(cursor.getColumnIndex("forward_status")));
                        json.put("notifyType", cursor.getString(cursor.getColumnIndex("notifyType")));
                        json.put("class_id", cursor.getString(cursor.getColumnIndex("class_id")));
                        array.put(json);
                    }

                    jsonObject.put("message_body", array);
                    cursor.close();
                }
                db.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return jsonObject.toString();
    }

    public String getchatList_view1(String class_id) {

        JSONObject jsonObject = new JSONObject();

        try {
            JSONArray array = new JSONArray();
            if (database != null) {
                SQLiteDatabase db = database.getWritableDatabase();

                String sql = "select * from Message where class_id='" + class_id + "'";
                Cursor cursor = db.rawQuery(sql,
                        null);
                if (cursor != null) {
                    while (cursor.moveToNext()) {

                        final JSONObject json = new JSONObject();
                        json.put("message_id", cursor.getString(cursor.getColumnIndex("message_id")));
                        json.put("message", cursor.getString(cursor.getColumnIndex("message")));
                        json.put("message_date", cursor.getString(cursor.getColumnIndex("message_date")));
                        json.put("message_status", cursor.getString(cursor.getColumnIndex("message_status")));
                        json.put("sender_name", cursor.getString(cursor.getColumnIndex("sender_name")));
                        json.put("no_reply", cursor.getString(cursor.getColumnIndex("no_reply")));
                        json.put("user_me", cursor.getString(cursor.getColumnIndex("user_me")));
                        json.put("forward", cursor.getString(cursor.getColumnIndex("forward")));
                        json.put("forward_status", cursor.getString(cursor.getColumnIndex("forward_status")));
                        json.put("notifyType", cursor.getString(cursor.getColumnIndex("notifyType")));
                        json.put("class_id", cursor.getString(cursor.getColumnIndex("class_id")));
                        array.put(json);
                    }

                    jsonObject.put("message_body", array);
                    cursor.close();
                }
                db.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return jsonObject.toString();
    }

    public String getchatList_view_tutors(String class_id, String student_id) {

        JSONObject jsonObject = new JSONObject();

        try {
            JSONArray array = new JSONArray();
            if (database != null) {
                SQLiteDatabase db = database.getWritableDatabase();

                //String sql = "select * from Message where class_id='" + class_id + "'";
                String sql = "select * from Message where class_id='" + class_id + "' and student_id='" + student_id + "'";
                Cursor cursor = db.rawQuery(sql,
                        null);
                if (cursor != null) {
                    while (cursor.moveToNext()) {

                        final JSONObject json = new JSONObject();
                        json.put("message_id", cursor.getString(cursor.getColumnIndex("message_id")));
                        json.put("message", cursor.getString(cursor.getColumnIndex("message")));
                        json.put("message_date", cursor.getString(cursor.getColumnIndex("message_date")));
                        json.put("message_status", cursor.getString(cursor.getColumnIndex("message_status")));
                        json.put("sender_name", cursor.getString(cursor.getColumnIndex("sender_name")));
                        json.put("no_reply", cursor.getString(cursor.getColumnIndex("no_reply")));
                        json.put("user_me", cursor.getString(cursor.getColumnIndex("user_me")));
                        json.put("forward", cursor.getString(cursor.getColumnIndex("forward")));
                        json.put("student_id", cursor.getString(cursor.getColumnIndex("student_id")));
                        array.put(json);
                    }

                    jsonObject.put("message_body", array);
                    cursor.close();
                }
                db.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return jsonObject.toString();
    }

    public void messageData(String message, String msg_id, String mesg_date_time, String sender_member_id, String class_id, String school_id, String sme, String teacher_id, String student_id, String sendername, String attendance_date, String reply_id, String forwd_id, String forward_status, String nType) {
        SQLiteDatabase db = null;
        try {
            if (database != null) {
                db = database.getWritableDatabase();
                ContentValues cv = new ContentValues();
                cv.put("message_id", msg_id);
                cv.put("teacher_id", teacher_id);
                cv.put("message", message);
                cv.put("message_date", mesg_date_time);
                cv.put("message_time", mesg_date_time);
                cv.put("message_status", "0");
                cv.put("no_reply", reply_id);
                cv.put("user_me", sme);
                cv.put("sender_name", sendername);
                cv.put("attendance_date", attendance_date);
                cv.put("class_id", class_id);
                cv.put("forward", forwd_id);
                cv.put("forward_status", forward_status);
                cv.put("notifyType", nType);
                cv.put("student_id", student_id);
                db.insert("Message", null, cv);
                db.close();
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void messageDataOffline(String message, String msg_id, String mesg_date_time, String sender_member_id, String class_id, String school_id, String sme, String teacher_id, String student_id, String sendername, String attendance_date, String reply_id, String forwd_id, String forward_status, String nType, String offline, String displayname) {
        SQLiteDatabase db = null;
        try {
            if (database != null) {
                db = database.getWritableDatabase();
                ContentValues cv = new ContentValues();
                cv.put("message_id", msg_id);
                cv.put("teacher_id", teacher_id);
                cv.put("message", message);
                cv.put("message_date", mesg_date_time);
                cv.put("message_time", mesg_date_time);
                cv.put("message_status", "0");
                cv.put("no_reply", reply_id);
                cv.put("user_me", sme);
                cv.put("sender_name", sendername);
                cv.put("attendance_date", attendance_date);
                cv.put("class_id", class_id);
                cv.put("forward", forwd_id);
                cv.put("forward_status", forward_status);
                cv.put("notifyType", nType);
                cv.put("student_id", student_id);
                cv.put("offlineTag", offline);
                cv.put("displayname", displayname);
                db.insert("Message", null, cv);
                db.close();
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public String getProfileData(String number) {
        String students_id = "";
        try {
            if (database != null) {

                String cursor_q = "select * from Teachers where phone_number='" + number + "'";
                SQLiteDatabase db = database.getWritableDatabase();
                Cursor cursor = db
                        .rawQuery(cursor_q,
                                null);
                try {
                    if (null != cursor)
                        if (cursor.getCount() > 0) {
                            cursor.moveToFirst();
                            students_id = cursor.getString(cursor.getColumnIndex("teacher_name"));
                        }
                    cursor.close();
                    db.close();
                } finally {
                    db.close();
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return students_id;
    }

    public String getTransportList() {

        JSONObject jsonObject = new JSONObject();

        try {
            JSONArray array = new JSONArray();
            if (database != null) {
                SQLiteDatabase db = database.getWritableDatabase();

                String sql = "select * from Transport";
                Cursor cursor = db.rawQuery(sql,
                        null);
                if (cursor != null) {
                    while (cursor.moveToNext()) {

                        final JSONObject json = new JSONObject();

                        json.put("transport_id", cursor.getString(cursor.getColumnIndex("transport_id")));
                        json.put("transport_name", cursor.getString(cursor.getColumnIndex("transport_name")));
                        json.put("phone_number", cursor.getString(cursor.getColumnIndex("phone_number")));
                        json.put("status", cursor.getString(cursor.getColumnIndex("status")));
                        json.put("school_id", cursor.getString(cursor.getColumnIndex("school_id")));
                        json.put("created_by", cursor.getString(cursor.getColumnIndex("created_by")));
                        json.put("student_id", cursor.getString(cursor.getColumnIndex("student_id")));
                        // json.put("name", cursor.getString(cursor.getColumnIndex("name")));
                        array.put(json);
                    }

                    jsonObject.put("transport_body", array);
                    cursor.close();
                }
                db.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return jsonObject.toString();
    }

    public String getTrasportId() {
        String Id = null;
        try {
            if (database != null) {

                String cursor_q = "select * from Transport";
                SQLiteDatabase db = database.getWritableDatabase();
                Cursor cursor = db
                        .rawQuery(cursor_q,
                                null);
                try {
                    if (null != cursor)
                        if (cursor.getCount() > 0) {
                            cursor.moveToLast();
                            Id = String.valueOf(cursor.getInt(cursor.getColumnIndex("transport_id")));
                        }
                    cursor.close();
                    db.close();
                } finally {
                    db.close();
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return Id;
    }

    public void deleteTransport(String tsh_id) {
        try {
            if (database != null) {

                String iwhereClause = "transport_id='" + tsh_id + "'";
                SQLiteDatabase db = database.getWritableDatabase();
                db.delete("Transport", iwhereClause, null);
                db.close();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void addTransportList(String ad_xname, String s_id, String number, String trans_id, String status, String created) {

        SQLiteDatabase db = null;
        try {
            long rawId;
            if (database != null) {
                db = database.getWritableDatabase();
                ContentValues cv = new ContentValues();
                cv.put("transport_id", trans_id);
                cv.put("transport_name", ad_xname);
                cv.put("school_id", s_id);
                cv.put("status", status);
                cv.put("created_by", created);
                cv.put("phone_number", number);
                db.insertWithOnConflict("Transport", null, cv, SQLiteDatabase.CONFLICT_IGNORE);
                // db.insert("Transport", null, cv);
                db.close();
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void updateTransportList(String transport_id, String ad_xname, String number, String school_id) {

        SQLiteDatabase db = null;
        try {
            if (database != null) {
                db = database.getWritableDatabase();
                String iwhereClause = "transport_id='" + transport_id + "'";
                ContentValues cv = new ContentValues();
                cv.put("transport_name", ad_xname);
                cv.put("school_id", school_id);
                cv.put("status", "0");
                cv.put("phone_number", number);
                db.update("Transport", cv, iwhereClause, null);
                db.close();
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void update_transport_status(String transport_id, String t_status) {

        SQLiteDatabase db = null;
        try {
            if (database != null) {
                db = database.getWritableDatabase();
                String iwhereClause = "transport_id='" + transport_id + "'";
                ContentValues cv = new ContentValues();
                cv.put("status", t_status);
                db.update("Transport", cv, iwhereClause, null);
                db.close();
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void addTutorsList(String ad_xname, String s_id, String number, String st_id, String tras_id, String status) {

        SQLiteDatabase db = null;
        try {
            long rawId;
            if (database != null) {
                db = database.getWritableDatabase();
                ContentValues cv = new ContentValues();
                cv.put("tutor_id", tras_id);
                cv.put("tutor_name", ad_xname);
                cv.put("student_id", st_id);
                cv.put("phone_number", number);
                cv.put("school_id", s_id);
                cv.put("tutor_status", status);
                db.insert("TUTORS", null, cv);
                db.close();
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public String getTutorId(String id) {
        String tutor_id = "";
        try {
            if (database != null) {

                String cursor_q = "select * from TUTORS where tutor_id='" + id + "'";
                SQLiteDatabase db = database.getWritableDatabase();
                Cursor cursor = db
                        .rawQuery(cursor_q,
                                null);
                try {
                    if (null != cursor)
                        if (cursor.getCount() > 0) {
                            cursor.moveToFirst();
                            tutor_id = cursor.getString(cursor.getColumnIndex("tutor_id"));
                        }
                    cursor.close();
                    db.close();
                } finally {
                    db.close();
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return tutor_id;
    }

    public void updateTutorsList(String tutor_id, String ad_xname, String school_id, String number, String st_id) {

        SQLiteDatabase db = null;
        try {
            if (database != null) {
                db = database.getWritableDatabase();
                String iwhereClause = "tutor_id='" + tutor_id + "'";
                ContentValues cv = new ContentValues();
                cv.put("tutor_name", ad_xname);
                cv.put("phone_number", number);
                cv.put("school_id", school_id);
                cv.put("tutor_status", "0");
                cv.put("student_id", st_id);
                db.update("TUTORS", cv, iwhereClause, null);
                db.close();
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void updateTutorStudents(String tutor_id, String st_id) {

        SQLiteDatabase db = null;
        try {
            if (database != null) {
                db = database.getWritableDatabase();
                String iwhereClause = "tutor_id='" + tutor_id + "'";
                ContentValues cv = new ContentValues();
                cv.put("student_id", st_id);
                db.update("TUTORS", cv, iwhereClause, null);
                db.close();
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void updateTutorDelete(String tutor_id, String st_id) {

        SQLiteDatabase db = null;
        try {
            if (database != null) {
                db = database.getWritableDatabase();
                String iwhereClause = "tutor_id='" + tutor_id + "'";
                ContentValues cv = new ContentValues();
                cv.put("student_id", st_id);
                db.update("TUTORS", cv, iwhereClause, null);
                db.close();
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void update_tutors_status(String tutor_id, String t_status) {

        SQLiteDatabase db = null;
        try {
            if (database != null) {
                db = database.getWritableDatabase();
                String iwhereClause = "tutor_id='" + tutor_id + "'";
                ContentValues cv = new ContentValues();
                cv.put("tutor_status", t_status);
                db.update("TUTORS", cv, iwhereClause, null);
                db.close();
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void deleteTutor(String tsh_id) {
        try {
            if (database != null) {

                String iwhereClause = "tutor_id='" + tsh_id + "'";
                SQLiteDatabase db = database.getWritableDatabase();
                db.delete("TUTORS", iwhereClause, null);
                db.close();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void deleteMessage(String msg_id ) {
        try {
            if (database != null) {

                String iwhereClause = "message_id='" + msg_id + "'";
                SQLiteDatabase db = database.getWritableDatabase();
                db.delete("Message", iwhereClause, null);
                db.close();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void updateReply(String reply_id) {
        SQLiteDatabase db = null;
        try {
            if (database != null) {
                db = database.getWritableDatabase();
                String iwhereClause = "message_id='" + reply_id + "'";
                ContentValues cv = new ContentValues();
                cv.put("no_reply", reply_id);
                db.update("Message", cv, iwhereClause, null);
                db.close();
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void updatemsgStatus(String msgId) {
        SQLiteDatabase db = null;
        try {
            if (database != null) {
                db = database.getWritableDatabase();
                String iwhereClause = "message_id='" + msgId + "'";
                ContentValues cv = new ContentValues();
                cv.put("message_status", "1");
                db.update("Message", cv, iwhereClause, null);
                db.close();
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public String getTeacher_Id(String student_id) {

        String teacher_id = null;

        return teacher_id;
    }

    public void updateMessageId(String msgId, String msg_date) {
        SQLiteDatabase db = null;
        try {
            if (database != null) {
                db = database.getWritableDatabase();
                String iwhereClause = "message_date='" + msg_date + "'";
                ContentValues cv = new ContentValues();
                cv.put("message_id", msgId);

                db.update("Message", cv, iwhereClause, null);
                db.close();
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public String getcheckCondition(String id) {
        String Id = null;
        try {
            if (database != null) {

                String cursor_q = "select * from Students where student_id='" + id + "'";
                SQLiteDatabase db = database.getWritableDatabase();
                Cursor cursor = db
                        .rawQuery(cursor_q,
                                null);
                try {
                    if (null != cursor)
                        if (cursor.getCount() > 0) {
                            cursor.moveToLast();
                            Id = cursor.getString(cursor.getColumnIndex("condition"));
                        }
                    cursor.close();
                    db.close();
                } finally {
                    db.close();
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return Id;
    }

    public void insertMarks(String student_id, String exam_id, String makes_comma, String teacher_id, String class_id, String subject, String student_name, String roll_no) {

        try {
            SQLiteDatabase db = null;
            long rawId;
            if (database != null) {
                db = database.getWritableDatabase();
                ContentValues cv = new ContentValues();
//                cv.put("marks_id", System.currentTimeMillis());
                cv.put("student_id", student_id);
                cv.put("student_name", student_name);
                cv.put("class_id", class_id);
                cv.put("examinations_id", exam_id);
                cv.put("marks_obtained", makes_comma);
                cv.put("teacher_id", teacher_id);
                cv.put("teacher_id", teacher_id);
                cv.put("subject", subject);
                cv.put("roll_no", roll_no);
                db.insert("MARKS", null, cv);
                // db.insert("MARKS", null, cv);
                db.close();
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
//        try {
//            if (database != null) {
//                db = database.getWritableDatabase();
//                String iwhereClause = "student_id='" + student_id + "'";
//                ContentValues cv = new ContentValues();
//                cv.put("examinations_id", exam_id);
//                cv.put("marks_obtained", makes_comma);
//                cv.put("teacher_id", teacher_id);
//                cv.put("subject", subject);
//                db.update("MARKS", cv, iwhereClause, null);
//                db.close();
//            }
//
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }
    }

    public String getAttandecClassId(String id, String mDate) {
        String Id = null;
        try {
            if (database != null) {

                String cursor_q = "select * from Attendance where class_id='" + id + "' and attendance_con='" + "Yes' and aDate='" + mDate + "'";
                SQLiteDatabase db = database.getWritableDatabase();
                Cursor cursor = db
                        .rawQuery(cursor_q,
                                null);
                try {
                    if (null != cursor)
                        if (cursor.getCount() > 0) {
                            cursor.moveToFirst();
                            Id = cursor.getString(cursor.getColumnIndex("attendance_con"));
                        }
                    cursor.close();
                    db.close();
                } finally {
                    db.close();
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return Id;
    }

    public void addstudentsMarks(String student_id, String roll_no, String student_name, String status, String class_id, String subject, String ex_d) {
        SQLiteDatabase db = null;
        try {
            long rawId;
            if (database != null) {
                db = database.getWritableDatabase();
                ContentValues cv = new ContentValues();
//                cv.put("marks_id", System.currentTimeMillis());
                cv.put("student_id", student_id);
                cv.put("roll_no", roll_no);
                cv.put("student_name", student_name);
                cv.put("class_id", class_id);
                cv.put("subject", subject);
                db.insert("MARKS", null, cv);
                // db.insert("MARKS", null, cv);
                db.close();
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public String getstudentsListMarks(String subject, String ex_id) {
        JSONObject jsonObject = new JSONObject();

        try {
            JSONArray array = new JSONArray();
            if (database != null) {
                SQLiteDatabase db = database.getWritableDatabase();

                String sql = "select * from MARKS where subject='" + subject + "' and examinations_id='" + ex_id + "'";
                Cursor cursor = db.rawQuery(sql,
                        null);
                if (cursor != null) {
                    while (cursor.moveToNext()) {

                        final JSONObject json = new JSONObject();
                        json.put("student_id", cursor.getString(cursor.getColumnIndex("student_id")));
                        json.put("roll_no", cursor.getString(cursor.getColumnIndex("roll_no")));
                        json.put("student_name", cursor.getString(cursor.getColumnIndex("student_name")));
                        json.put("class_id", cursor.getString(cursor.getColumnIndex("class_id")));
                        json.put("examinations_id", cursor.getString(cursor.getColumnIndex("examinations_id")));
                        json.put("marks_obtained", cursor.getString(cursor.getColumnIndex("marks_obtained")));
                        json.put("subject", cursor.getString(cursor.getColumnIndex("subject")));
                        array.put(json);
                    }

                    jsonObject.put("student_details", array);
                    cursor.close();
                }
                db.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return jsonObject.toString();
    }

    public void updateStudents11(String student_id, String exam_id, String makes_comma, String teacher_id, String class_id, String subject) {
        SQLiteDatabase db = null;
        try {
            if (database != null) {
                db = database.getWritableDatabase();
                String iwhereClause = "student_id='" + student_id + "' and subject='" + subject + "' and examinations_id='" + exam_id + "'";
                ContentValues cv = new ContentValues();
//                cv.put("examinations_id", exam_id);
                cv.put("marks_obtained", makes_comma);
//                cv.put("teacher_id", teacher_id);
//                cv.put("subject", subject);
                db.update("MARKS", cv, iwhereClause, null);
                db.close();
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    public ArrayList<String> listMarks(String subject, String exam_id) {
        ArrayList<String> myArrayList = new ArrayList<String>();
        try {
            if (database != null) {

                String cursor_q = "select * from MARKS where examinations_id='" + exam_id + "' and subject='" + subject + "'";
                SQLiteDatabase db = database.getWritableDatabase();
                Cursor cursor = db
                        .rawQuery(cursor_q,
                                null);
                try {
                    if (cursor != null) {
                        while (cursor.moveToNext()) {
                            String aa_Id = cursor.getString(cursor.getColumnIndex("marks_obtained"));
                            myArrayList.add(aa_Id);
                        }
                    }
                    cursor.close();
                    db.close();
                } finally {
                    db.close();
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return myArrayList;
    }

    public ArrayList<String> listRollNo(String subject, String exam_id) {
        ArrayList<String> myArrayList = new ArrayList<String>();
        try {
            if (database != null) {

                String cursor_q = "select * from MARKS where examinations_id='" + exam_id + "' and subject='" + subject + "'";
                SQLiteDatabase db = database.getWritableDatabase();
                Cursor cursor = db
                        .rawQuery(cursor_q,
                                null);

                try {
                    if (cursor != null) {
                        while (cursor.moveToNext()) {
                            String aa_Id = cursor.getString(cursor.getColumnIndex("roll_no"));
                            myArrayList.add(aa_Id);
                        }
                    }
                    cursor.close();
                    db.close();
                } finally {
                    db.close();
                }

            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return myArrayList;
    }

    public String getstudentsList_Attend(String class_id, String date) {
        JSONObject jsonObject = new JSONObject();

        try {
            JSONArray array = new JSONArray();
            if (database != null) {
                SQLiteDatabase db = database.getWritableDatabase();

                String sql = "select * from Attendance where class_id='" + class_id + "' and  aDate='" + date + "' and student_name is not null";
                Cursor cursor = db.rawQuery(sql,
                        null);
                if (cursor != null) {
                    while (cursor.moveToNext()) {

                        final JSONObject json = new JSONObject();
                        json.put("student_id", cursor.getString(cursor.getColumnIndex("student_id")));
                        json.put("roll_no", cursor.getString(cursor.getColumnIndex("roll_no")));
                        json.put("student_name", cursor.getString(cursor.getColumnIndex("student_name")));
                        json.put("class_id", cursor.getString(cursor.getColumnIndex("class_id")));
                        json.put("status", cursor.getString(cursor.getColumnIndex("status")));
                        json.put("condition", cursor.getString(cursor.getColumnIndex("condition")));
                        array.put(json);
                    }

                    jsonObject.put("student_details", array);
                    cursor.close();
                }
                db.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return jsonObject.toString();
    }

    public String getcheckCondition_Att(String id, String class_id, String attdate) {
        String Id = null;
        try {
            if (database != null) {

                String cursor_q = "select * from Attendance where student_id='" + id + "' and class_id='" + class_id + "' and aDate='" + attdate + "'";
                SQLiteDatabase db = database.getWritableDatabase();
                Cursor cursor = db
                        .rawQuery(cursor_q,
                                null);
                try {
                    if (null != cursor)
                        if (cursor.getCount() > 0) {
                            cursor.moveToFirst();
                            Id = cursor.getString(cursor.getColumnIndex("condition"));
                        }
                    cursor.close();
                    db.close();
                } finally {
                    db.close();
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return Id;
    }

    public String getcheckCondition_roll(String id, String class_id, String att_date) {
        String Id = null;
        try {
            if (database != null) {

                String cursor_q = "select * from Attendance where roll_no='" + id + "' and class_id='" + class_id + "' and aDate='" + att_date + "'";
                SQLiteDatabase db = database.getWritableDatabase();
                Cursor cursor = db
                        .rawQuery(cursor_q,
                                null);
                try {
                    if (null != cursor)
                        if (cursor.getCount() > 0) {
                            cursor.moveToFirst();
                            Id = cursor.getString(cursor.getColumnIndex("condition"));
                        }
                    cursor.close();
                    db.close();
                } finally {
                    db.close();
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return Id;
    }

    public void messageFromParents(String messsages, String message_id, String message_timestamp, Object o, String class_id, String school_id, String user_type, Object o1, String student_id, String student_name, String attendance_date, String s1, String s2, String yes, String m_type) {
        SQLiteDatabase db = null;
        try {
            if (database != null) {
                db = database.getWritableDatabase();
                ContentValues cv = new ContentValues();
                cv.put("message_id", message_id);
                cv.put("message", messsages);
                cv.put("message_date", message_timestamp);
                cv.put("message_time", message_timestamp);
                cv.put("user_me", user_type);
                cv.put("sender_name", student_name);
                cv.put("attendance_date", attendance_date);
                cv.put("class_id", class_id);
                db.insert("PARENT_MSG", null, cv);
                db.close();
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public String getchatList_replys(String class_id) {

        JSONObject jsonObject = new JSONObject();

        try {
            JSONArray array = new JSONArray();
            if (database != null) {
                SQLiteDatabase db = database.getWritableDatabase();

                String sql = "select * from PARENT_MSG where class_id='" + class_id + "'";
                Cursor cursor = db.rawQuery(sql,
                        null);
                if (cursor != null) {
                    while (cursor.moveToNext()) {

                        final JSONObject json = new JSONObject();
                        json.put("message_id", cursor.getString(cursor.getColumnIndex("message_id")));
                        json.put("message", cursor.getString(cursor.getColumnIndex("message")));
                        json.put("message_date", cursor.getString(cursor.getColumnIndex("message_date")));
                        json.put("sender_name", cursor.getString(cursor.getColumnIndex("sender_name")));
                        json.put("user_me", cursor.getString(cursor.getColumnIndex("user_me")));
                        array.put(json);
                    }

                    jsonObject.put("message_body", array);
                    cursor.close();
                }
                db.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return jsonObject.toString();
    }

    public String getStudentIds(String class_id) {

        String students_id = "";
        try {
            if (database != null) {

                String cursor_q = "select * from Attendance where class_id='" + class_id + "'";
                SQLiteDatabase db = database.getWritableDatabase();
                Cursor cursor = db
                        .rawQuery(cursor_q,
                                null);
                try {
                    if (null != cursor)
                        if (cursor.getCount() > 0) {
                            cursor.moveToFirst();
                            students_id = cursor.getString(cursor.getColumnIndex("student_id"));
                        }
                    cursor.close();
                    db.close();
                } finally {
                    db.close();
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return students_id;
    }

    public String getTutorStudents(String tutor_id) {
        String student_id = "";
        try {
            if (database != null) {

                String cursor_q = "select * from TUTORS where tutor_id='" + tutor_id + "'";
                SQLiteDatabase db = database.getWritableDatabase();
                Cursor cursor = db
                        .rawQuery(cursor_q,
                                null);
                try {
                    if (null != cursor)
                        if (cursor.getCount() > 0) {
                            cursor.moveToFirst();
                            student_id = cursor.getString(cursor.getColumnIndex("student_id"));
                        }
                    cursor.close();
                    db.close();
                } finally {
                    db.close();
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return student_id;
    }

    public String getSyncStudents(String attendDate, String class_id) {
        String Id = "";
        try {
            if (database != null) {

                String cursor_q = "select * from Attendance where class_id ='" + class_id + "' and aDate='" + attendDate + "'";

                SQLiteDatabase db = database.getWritableDatabase();
                Cursor cursor = db
                        .rawQuery(cursor_q,
                                null);
                try {
                    if (null != cursor)
                        if (cursor.getCount() > 0) {
                            cursor.moveToLast();
                            Id = cursor.getString(cursor.getColumnIndex("aDate"));
                        }
                    cursor.close();
                    db.close();
                } finally {
                    db.close();
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return Id;
    }

    public String getofflineData(String flag) {
        JSONObject jsonObject = new JSONObject();

        try {
            JSONArray array = new JSONArray();
            if (database != null) {
                SQLiteDatabase db = database.getWritableDatabase();

                String sql = "select * from Message where offlineTag='" + flag + "'";
                Cursor cursor = db.rawQuery(sql,
                        null);
                if (cursor != null) {
                    while (cursor.moveToNext()) {

                        final JSONObject json = new JSONObject();
                        //json.put("message_id", cursor.getString(cursor.getColumnIndex("message_id")));
                        json.put("message", cursor.getString(cursor.getColumnIndex("message")));
                        json.put("message_date", cursor.getString(cursor.getColumnIndex("message_date")));
                        json.put("message_status", cursor.getString(cursor.getColumnIndex("message_status")));
                        json.put("sender_name", cursor.getString(cursor.getColumnIndex("sender_name")));
                        json.put("no_reply", cursor.getString(cursor.getColumnIndex("no_reply")));
                        json.put("user_me", cursor.getString(cursor.getColumnIndex("user_me")));
                        json.put("forward", cursor.getString(cursor.getColumnIndex("forward")));
                        json.put("forward_status", cursor.getString(cursor.getColumnIndex("forward_status")));
                        json.put("displayname", cursor.getString(cursor.getColumnIndex("displayname")));
                        json.put("class_id", cursor.getString(cursor.getColumnIndex("class_id")));
                        json.put("student_id", cursor.getString(cursor.getColumnIndex("student_id")));
                        array.put(json);
                    }

                    jsonObject.put("message_body", array);
                    cursor.close();
                }
                db.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return jsonObject.toString();
    }

    public void messageDataFlagUpdate(String message_id) {

        SQLiteDatabase db = null;
        try {
            if (database != null) {
                db = database.getWritableDatabase();
                String iwhereClause = "message_date='" + message_id + "'";
                ContentValues cv = new ContentValues();
                cv.put("offlineTag", "0");
                db.update("Message", cv, iwhereClause, null);
                db.close();
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void syncMarksData(String type, String data) {
        SQLiteDatabase db = null;
        try {
            if (database != null) {
                db = database.getWritableDatabase();
                ContentValues cv = new ContentValues();
                cv.put("action_v", type);
                cv.put("data", data);
                db.insert("SYNC", null, cv);
                db.close();
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public String getActionData(String action_type) {
        String data = "";
        String id = "";
        try {
            if (database != null) {

                String cursor_q = "select * from SYNC where action_v ='" + action_type + "'";

                SQLiteDatabase db = database.getWritableDatabase();
                Cursor cursor = db
                        .rawQuery(cursor_q,
                                null);
                try {
                    if (null != cursor)
                        if (cursor.getCount() > 0) {
                            cursor.moveToFirst();
                            data = cursor.getString(cursor.getColumnIndex("data"));
                           // id = cursor.getString(cursor.getColumnIndex("id"));
                        }
                    cursor.close();
                    db.close();
                } finally {
                    db.close();
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return data;
    } public String getSyncId(String action_type) {
        String id = "";
        try {
            if (database != null) {

                String cursor_q = "select * from SYNC where action_v ='" + action_type + "'";

                SQLiteDatabase db = database.getWritableDatabase();
                Cursor cursor = db
                        .rawQuery(cursor_q,
                                null);
                try {
                    if (null != cursor)
                        if (cursor.getCount() > 0) {
                            cursor.moveToFirst();
                            id = cursor.getString(cursor.getColumnIndex("id"));
                        }
                    cursor.close();
                    db.close();
                } finally {
                    db.close();
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return id;
    }
    public void deleteSyncItems(String _id) {
        SQLiteDatabase db = null;
        try {
            long rawId;
            if (database != null) {
                String iwhereClause = "id='" + _id + "'";
                db = database.getWritableDatabase();
                db.delete("SYNC", iwhereClause, null);
                db.close();
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
