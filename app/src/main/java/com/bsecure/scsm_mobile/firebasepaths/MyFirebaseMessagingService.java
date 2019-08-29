package com.bsecure.scsm_mobile.firebasepaths;

import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.text.Html;
import android.util.Log;
import android.widget.Toast;

import com.bsecure.scsm_mobile.database.DB_Tables;
import com.bsecure.scsm_mobile.modules.ParentActivity;
import com.bsecure.scsm_mobile.modules.StaffView;
import com.bsecure.scsm_mobile.modules.TeacherView;
import com.bsecure.scsm_mobile.modules.TransportView;
import com.bsecure.scsm_mobile.modules.TutorsView;
import com.bsecure.scsm_mobile.utils.SharedValues;
import com.bsecure.scsm_mobile.utils.Utils;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

/**
 * Created by Admin on 2018-09-27.
 */

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private static final String TAG = "MyFirebaseMsgService";
    Intent intent, intent_pending;
    String secureTage, repId_type2, f_status, fwd;
    long count = 0;
    String m_type, status_code, student_name = "SCM";

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        if (remoteMessage.getData().size() > 0) {
            Log.e(TAG, "Data Payload: " + remoteMessage.getData().toString());
            try {
//                JSONObject json= new JSONObject(remoteMessage.getData().toString());
//                myvales=String.valueOf(json);
                Map<String, String> params = remoteMessage.getData();
                Object object = new JSONObject(params);
                // setBadge(getApplicationContext(), badge);
                sendPushNotification(object);
            } catch (Exception e) {
                //sendPushNotification(object);
                Log.e(TAG, "Exception: " + e.getMessage());
            }
        }
    }

    private void sendPushNotification(Object json) {
        //optionally we can display the json into log
        // Log.e(TAG, "Notification JSON " + json.toString());
        DB_Tables db_tables = new DB_Tables(this);
        try {
            //getting the json data
            JSONObject data = new JSONObject(json.toString());
            String data_silent = data.optString("silent");
            if (data_silent.equalsIgnoreCase("true")) {

                String message_data = data.getString("message");
                JSONObject object = new JSONObject(message_data);
                String messsages = object.optString("msg");
                String title_msg = object.optString("msg_det");
                String arry_data[] = title_msg.split(",");
                m_type = arry_data[0];
                status_code = arry_data[1];
                if (m_type.equalsIgnoreCase("DTU")) {
                    //Delete Tutor Push Notification
                    String tutor_id = arry_data[2];
                    String school_id = arry_data[3];
                    db_tables.deleteTutor(tutor_id);
                } else if (m_type.equalsIgnoreCase("IATS")) {
                    //Active/Inactive Tutor Push Notification:
                    String tutor_id = arry_data[2];
                    String school_id = arry_data[3];
                    db_tables.update_tutors_status(tutor_id, "1");
                }else if (m_type.equalsIgnoreCase("ATS")) {
                    String transport_name = arry_data[1];
                    String transport_id = arry_data[2];
                    String school_id = arry_data[3];
                    String phone_number = arry_data[4];
                    String status = arry_data[5];
                    String student_id = arry_data[6];
                    db_tables.addTransportList(transport_name, school_id,phone_number, transport_id, status, "1");
                    Intent refresh = new Intent("com.trans.refresh");
                    sendBroadcast(refresh);
                }
                else if (m_type.equalsIgnoreCase("ETS")) {
                    //Edit Transport Name Push Notification
                    String transport_name = arry_data[2];
                    String transport_id = arry_data[3];
                    String number = arry_data[4];
                    String school_id = arry_data[5];
                    db_tables.updateTransportList(transport_id, transport_name, number, school_id);

                } else if (m_type.equalsIgnoreCase("FM")) {
                    //Forward Push Notification
                    String message_id = arry_data[2];
                    String message_timestamp = arry_data[3];
                    String student_id = arry_data[4];
                    db_tables.messageData(messsages, message_id, message_timestamp, null, null, SharedValues.getValue(this, "school_id"), "1", null, student_id, student_name, null, "0", "1", "Yes", m_type);
                } else if (m_type.equalsIgnoreCase("DTS")) {
                    String transport_id = arry_data[2];
                    String school_id = arry_data[3];
                    db_tables.deleteTransport(transport_id);
                    Intent refresh = new Intent("com.trans.refresh");
                    sendBroadcast(refresh);

                } else if (m_type.equalsIgnoreCase("GC")) {
                    String transport_id = arry_data[2];
                    String student_id = arry_data[3];
                    String school_id = arry_data[4];
                    Intent my_maps = new Intent();
                    my_maps.setAction("com.scm.gps");
                    my_maps.putExtra("trans_id", transport_id);
                    my_maps.putExtra("student_id", student_id);
                    my_maps.putExtra("school_id", school_id);
                    sendBroadcast(my_maps);
                    //  View Transport Push Notification
                } else if (m_type.equalsIgnoreCase("EOH")) {
                    String name = arry_data[2];
                    String designation = arry_data[3];
                    String staff_id = arry_data[4];
                    String school_id = arry_data[5];
                    db_tables.updateStaff(staff_id, name, "", designation, school_id, "0");
                } else if (m_type.equalsIgnoreCase("DOH")) {
                    String staff_id = arry_data[2];
                    String school_id = arry_data[3];
                    db_tables.deleteStaff(staff_id, school_id, "1");
                } else if (m_type.equalsIgnoreCase("IAOH")) {
                    String staff_id = arry_data[2];
                    String school_id = arry_data[3];
                    db_tables.updateStaffStatus(staff_id, school_id, "1");
                } else if (m_type.equalsIgnoreCase("TS")) {
                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                        @Override
                        public void run() {
                            getredAlert("Your Access Has Been Deactivated - Please Contact Administrator");
                        }
                    });
                    sendBD();
                }  else if (m_type.equalsIgnoreCase("SFS")) {
                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                        @Override
                        public void run() {
                            getredAlert("Your Access Has Been Deactivated - Please Contact Administrator");
                        }
                    });
                    sendBD();
                }else if (m_type.equalsIgnoreCase("SS")) {
                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                        @Override
                        public void run() {
                            getredAlert("Your Access Has Been Deactivated - Please Contact Administrator");
                        }
                    });
                    sendBD();
                } else if (m_type.equalsIgnoreCase("STS")) {

                    String student_id = arry_data[1];
                    String status = arry_data[2];
                    db_tables.updateStudents1(student_id, status);
                } else if (m_type.equalsIgnoreCase("DTC")) {
                    String class_id = arry_data[1];
                    db_tables.deleteClass(class_id);
                    Intent refresh = new Intent("com.teacher.add");
                    sendBroadcast(refresh);

                }
                else if(m_type.equalsIgnoreCase("DELS"))
                {
                    String Student_id = arry_data[1];
                    Intent list = new Intent();
                    list.putExtra("student_id", Student_id);
                    list.setAction("com.parent.refresh");
                    sendBroadcast(list);
                }
                else if(m_type.equalsIgnoreCase("DELM")) {

                    String mid = arry_data[1];
                    db_tables.deleteMessage(mid);
                    Intent list = new Intent();
                    list.setAction("com.chat.app.DELIVER");
                    sendBroadcast(list);

                }
                else if (m_type.equalsIgnoreCase("ATC")) {
                    String teacher_classes_id = arry_data[1];
                    String class_id = arry_data[2];
                    String class_name = arry_data[3];
                    String section = arry_data[4];
                    String subjects = arry_data[5];
                    db_tables.addTeacherClassList(teacher_classes_id, class_id, section, class_name, subjects);
                    Intent refresh = new Intent("com.teacher.add");
                    sendBroadcast(refresh);
                } else if (m_type.equalsIgnoreCase("ETC")) {
                    String teacher_classes_id = arry_data[1];
                    String class_id = arry_data[2];
                    String class_name = arry_data[3];
                    String section = arry_data[4];
                    String subjects = arry_data[5];
                    db_tables.updateClassList(teacher_classes_id, class_id, section, class_name, subjects);
                    Intent refresh = new Intent("com.teacher.add");
                    sendBroadcast(refresh);
                } else if (m_type.equalsIgnoreCase("TDTU")) {
//                    String student_id = arry_data[1];
//                    String phone = arry_data[2];
//                    db_tables.tutorDeleteStudent(student_id, phone);
                    String student_id = arry_data[1];
                    String tutor_id = arry_data[2];
                    //db_tables.parentDeleteTutor(student_id, tutor_id);
                    Intent refresh = new Intent("com.parenttutor.refresh");
                    refresh.putExtra("student_id", student_id);
                    refresh.putExtra("tutor_id", tutor_id);
                    sendBroadcast(refresh);

                } else if (m_type.equalsIgnoreCase("DTUS")) {
                    
                    Intent refresh = new Intent("com.tutor.refresh");
                    sendBroadcast(refresh);
                }
                else if(m_type.equalsIgnoreCase("ATU"))
                {
                    Intent refresh = new Intent("com.tutor.refresh");
                    sendBroadcast(refresh);

                }

            } else {
                String imageUrl = data.optString("image");
                String message_data = data.getString("message");
                JSONObject object = new JSONObject(message_data);
                String messsages = object.optString("msg");
                String title_msg = object.optString("msg_det");
                String arry_data[] = title_msg.split(",");
                m_type = arry_data[0];

                if (m_type.equalsIgnoreCase("SAM")){

                    String message_id = arry_data[1];
                    String message_date = arry_data[2];
                    String student_id = arry_data[3];
                    String class_id = arry_data[4];

                    //db_tables.messageData(messsages, message_id, message_date, null, class_id, SharedValues.getValue(this, "school_id"), "1", null, student_id, student_name, null, "0", "0", "No", m_type);
                    //write code and


                    intent_pending = new Intent();
                    intent_pending.putExtra("message_id", message_id);
                    intent_pending.putExtra("student_id", student_id);
                    intent_pending.putExtra("message", messsages);
                    intent_pending.setAction("com.scs.app.dashboard");
                    sendBroadcast(intent_pending);
                    //sendBD2();

                }
                if (m_type.equalsIgnoreCase("MAR")) {
                    //MAR*message_id*message_timestamp*student_id*student_name*attendance_date*class_id
                    String message_id = arry_data[1];
                    String message_timestamp = arry_data[2];
                    String student_id = arry_data[3];
                    student_name = arry_data[4];
                    String attendance_date = arry_data[5];
                    String class_id = arry_data[6];

                    db_tables.messageData(messsages, message_id, message_timestamp, null, class_id, SharedValues.getValue(this, "school_id"), "1", null, student_id, student_name, attendance_date, "0", "0", "Yes", m_type);
                }
                if (m_type.equalsIgnoreCase("AR")) {
                    // AR*message_id*message*message_timestamp*student_name
                    String message_id = arry_data[1];
                    String message_timestamp = arry_data[2];
                    student_name = arry_data[3];
                    String student_id = arry_data[4];
                    String class_id = arry_data[5];
                    String attendance_date = arry_data[6];
                    db_tables.messageFromParents(messsages, message_id, message_timestamp, null, class_id, SharedValues.getValue(this, "school_id"), "1", null, student_id, student_name, attendance_date, "0", "0", "Yes", m_type);
                }
                if (m_type.equalsIgnoreCase("TL")) {
                    String trans_id = arry_data[1];
                    String student_id = arry_data[2];
                    String school_id = arry_data[3];
                    String lat = arry_data[4];
                    String lang = arry_data[5];

                    Intent my_maps = new Intent();
                    my_maps.setAction("com.scm.mapsview");
                    my_maps.putExtra("lat", lat);
                    my_maps.putExtra("lang", lang);
                    my_maps.putExtra("trans_id", trans_id);
                    my_maps.putExtra("student_id", student_id);
                    sendBroadcast(my_maps);

                }
                if (m_type.equalsIgnoreCase("SSM")) {
                    //[SSN,message_id,message_date,student_id,class_id]
                    String msg_id = arry_data[1];
                    String date_time = arry_data[2];
                    String student_id = arry_data[3];
                    student_name = arry_data[4];
                    String cls_id = arry_data[5];
                    db_tables.messageData(messsages, msg_id, date_time, null, cls_id, SharedValues.getValue(this, "school_id"), "1", null, student_id, student_name, null, "0", "0", "Yes", m_type);
                }
                if (m_type.equalsIgnoreCase("EAP")) {

                    String msg_id = arry_data[1];
                    String date_time = arry_data[2];
                    student_name = arry_data[3];
                    String cls_id = arry_data[4];
                    String stu_id = arry_data[5];
                    String class_name = arry_data[6];
                    String section = arry_data[7];
                    String lDate = getDate(Long.parseLong(date_time));
                    messsages = "<Html>Dear Parent,<br/>Sorry for the inconvenience caused. <b>" + student_name + "</b> ( class " + class_name + " - " + section + " ) is present on " + lDate + " Please ignore earlier message<br/><br/> Thank you.</Html>";
                    db_tables.messageData(messsages, msg_id, String.valueOf(System.currentTimeMillis()), null, cls_id, SharedValues.getValue(this, "school_id"), "1", null, stu_id, "Class Teacher", date_time, "0", "0", "No", m_type);
                }
                if(m_type.equalsIgnoreCase("DELS"))
                {
                    String Student_id = arry_data[1];
                    Intent list = new Intent();
                    list.putExtra("student_id", Student_id);
                    list.setAction("com.parent.refresh");
                    sendBroadcast(list);
                }
                if (m_type.equalsIgnoreCase("EAA")) {

                    String msg_id = arry_data[1];
                    String date_time = arry_data[2];
                    student_name = arry_data[3];
                    String cls_id = arry_data[4];
                    String stu_id = arry_data[5];
                    String class_name = arry_data[6];
                    String section = arry_data[7];
                    String lDate = getDate(Long.parseLong(date_time));
                    messsages = "<Html>Dear Parent,<br/><b>" + student_name + "</b> ( class " + class_name + " - " + section + " ) is absent on " + lDate + " Please Call us if not absent or provide us reason of being absent <br/><br/> Thank you.</Html>";
                    db_tables.messageData(messsages, msg_id, String.valueOf(System.currentTimeMillis()), null, cls_id, SharedValues.getValue(this, "school_id"), "1", null, stu_id, "Class Teacher", date_time, "0", "0", "No", m_type);
                }
                if (m_type.equalsIgnoreCase("AA")) {

                    String msg_id = arry_data[1];
                    String date_time = arry_data[2];
                    student_name = arry_data[3];
                    String cls_id = arry_data[4];
                    String stu_id = arry_data[5];
                    String class_name = arry_data[6];
                    String section = arry_data[7];
                    String lDate = getDate(Long.parseLong(date_time));
                    messsages = "<Html>Dear Parent,<br/> <b>" + student_name + "</b> ( class " + class_name + " - " + section + " ) is absent on " + lDate + " Please Call us if not absent or provide us reason of being absent <br/><br/>  Thank you.</Html>";
                    db_tables.messageData(messsages, msg_id, date_time, null, cls_id, SharedValues.getValue(this, "school_id"), "1", null, stu_id, "Class Teacher ", date_time, "0", "0", "No", m_type);
                }

                if (m_type.equalsIgnoreCase("AM")) {
                    String msg_id = arry_data[1];
                    String date_time = arry_data[2];
                    String student_id = arry_data[3];
                    student_name = arry_data[4];
                    String class_name = arry_data[5];
                    String section = arry_data[6];
                    String marks = arry_data[7];
                    String total_marks = arry_data[8];
                    String subject = arry_data[9];
                    String examination_name = arry_data[10];
                    String exam_date = arry_data[11];
                    String class_id = arry_data[12];
                    String lDate = getDate(Long.parseLong(exam_date));
                    if (marks.equalsIgnoreCase("AB")) {
                        messsages = "<Html>Dear Parent,<br/>" + student_name + "(" + class_name + " - " + section + ") was absent for " + examination_name + " - " + subject + " conducted on " + lDate + "<br/><br/>  Thank you.</Html>";
                    } else {
                        messsages = "<Html>Dear Parent,<br/>" + student_name + "</b> ( class " + class_name + " - " + section + " ) obtained  " + marks + "/" + total_marks + "in " + subject + " for " + examination_name + " conducted on " + lDate + "<br/><br/>  Thank you.</Html>";

                    }
                    db_tables.messageData(messsages, msg_id, date_time, null, class_id, SharedValues.getValue(this, "school_id"), "1", null, student_id, "Class Teacher", date_time, "0", "0", "No", m_type);
                }
                if (m_type.equalsIgnoreCase("EM")) {
                    String msg_id = arry_data[1];
                    String date_time = arry_data[2];
                    String student_id = arry_data[3];
                    student_name = arry_data[4];
                    String class_name = arry_data[5];
                    String section = arry_data[6];
                    String marks = arry_data[7];
                    String total_marks = arry_data[8];
                    String subject = arry_data[9];
                    String examination_name = arry_data[10];
                    String exam_date = arry_data[11];
                    String class_id = arry_data[12];
                    String lDate = getDate(Long.parseLong(exam_date));
                    if (marks.equalsIgnoreCase("AB")) {
                        messsages = "<Html>Dear Parent,<br/>" + "Marks Modified - " + student_name + "( " + class_name + " - " + section + " ) was absent for " + examination_name + " - " + subject + " conducted on " + lDate + "<br/><br/>  Thank you.</Html>";
                    } else {
                        messsages = "<Html>Dear Parent,<br/>Marks Modified - " + student_name + "( " + class_name + " - " + section + ") obtained  " + marks + " / " + total_marks + "  in " + subject + " for " + examination_name + " conducted on " + lDate + "<br/><br/>  Thank you.</Html>";
                    }
                    db_tables.messageData(messsages, msg_id, date_time, null, class_id, SharedValues.getValue(this, "school_id"), "1", null, student_id, "Class Teacher", date_time, "0", "0", "No", m_type);
                }
                if (m_type.equalsIgnoreCase("FM")) {

                    String msg_id = arry_data[1];
                    String date_time = arry_data[2];
//                    student_name= arry_data[3];

                    String stu_id = arry_data[3];
                    String cls_id = arry_data[4];
                    String tut_id = arry_data[5];
//                    String class_name = arry_data[6];
//                    String section = arry_data[7];u


//                    messsages = "<Html>Dear Parent,<br/> Sorry for the inconvenience caused. <b>" + student_name + "</b> class"+class_name+"("+section+") is present on -" + Utils.getDate(Long.parseLong(date_time)) + "<br/><br/>  Thank you.</Html>";
                    db_tables.messageData(messsages, msg_id, date_time, null, cls_id, SharedValues.getValue(this, "school_id"), "1", null, stu_id, null, date_time, "0", "1", "No", m_type);
                }
                if (m_type.equalsIgnoreCase("SCU")) {
                    String msg_id = arry_data[1];
                    String date_time = arry_data[2];
//                    student_name= arry_data[3];

                    String stu_id = arry_data[3];
                    String teacher_name = arry_data[4];
                    String cls_id = arry_data[5];
//                    String section = arry_data[7];
//                    messsages = "<Html>Dear Parent,<br/> Sorry for the inconvenience caused. <b>" + student_name + "</b> class"+class_name+"("+section+") is present on -" + Utils.getDate(Long.parseLong(date_time)) + "<br/><br/>  Thank you.</Html>";
                    db_tables.messageData(messsages, msg_id, date_time, null, cls_id, SharedValues.getValue(this, "school_id"), "1", null, stu_id, teacher_name, date_time, "0", "0", "No", m_type);
                    db_tables.updatemsgStatus(msg_id);
                }
                if(m_type.equalsIgnoreCase("CEN")) {
                    String class_id="";
                    String event_name = arry_data[1];
                    String description = arry_data[2];
                    String edate = arry_data[3];
                    String stu_id = arry_data[4];
                    String event_date=getDate(Long.parseLong(edate)*1000);
                    if (arry_data.length>5) {
                        class_id = arry_data[5];
                    }
                    String message="<Html>Event Reminder on <b>" + event_date + ":" + event_name + " </b> <br/> " + description + "</Html>";
                    db_tables.messageData(message, "", String.valueOf(System.currentTimeMillis()), null, class_id, SharedValues.getValue(this, "school_id"), "1", null, stu_id, "Admin", String.valueOf(System.currentTimeMillis()), "0", "0", "No", m_type);
                }
                if(m_type.equalsIgnoreCase("BM"))
                {
                    String message = arry_data[1];
                    String student_id = arry_data[2];
                    String class_id = arry_data[3];
                    String sender = "School Admin";
                    db_tables.messageData(message,String.valueOf(System.currentTimeMillis()),String.valueOf(System.currentTimeMillis()), null, class_id, null, "1", null, student_id, sender,null, "0","0", "No", m_type );
                }
                if(m_type.equalsIgnoreCase("BMC"))
                {
                    String attachment_name = arry_data[1];
                    String student_id = arry_data[2];
                    String class_id = arry_data[3];
                    db_tables.messageData(attachment_name,String.valueOf(System.currentTimeMillis()),String.valueOf(System.currentTimeMillis()), null, class_id, null, "1", null, student_id, "School Admin",null, "0","0", "No", m_type );
                }

                MyNotificationManager mNotificationManager = new MyNotificationManager(getApplicationContext());
                String memb_id = SharedValues.getValue(this, "member_id");
                if (memb_id.equalsIgnoreCase("1")) {
                    //Teacher
                    intent_pending = new Intent(this, TeacherView.class);
                } else if (memb_id.equalsIgnoreCase("2")) {
                    // Parent
                    intent_pending = new Intent(this, ParentActivity.class);
                } else if (memb_id.equalsIgnoreCase("3")) {
                    // Staff
                    intent_pending = new Intent(this, StaffView.class);
                } else if (memb_id.equalsIgnoreCase("4")) {
                    // Tutor  startPages(TutorsView.class);
                    intent_pending = new Intent(this, TutorsView.class);
                } else {
                    intent_pending = new Intent(this, TransportView.class);
                    // Transport
                }
                //if there is no image
                if (imageUrl.equals("null") || imageUrl.isEmpty()) {
                    mNotificationManager.showSmallNotification(student_name, String.valueOf(Html.fromHtml(messsages)), intent_pending,m_type);
                    myBroadcaster(message_data);
                } else {
                    mNotificationManager.showBigNotification(student_name, String.valueOf(Html.fromHtml(messsages)), imageUrl, intent_pending);
                    myBroadcaster(message_data);

                }
            }
        } catch (JSONException e) {
            Log.e(TAG, "Json Exception: " + e.getMessage());
        } catch (Exception e) {
            Log.e(TAG, "Exception: " + e.getMessage());
        }

    }

    private void myBroadcaster(String message_data) {
        Intent intent = new Intent();
        intent.setAction("com.acc.app.BROADCAST_NOTIFICATION");
        intent.putExtra("NotificationData", "1");
        sendBroadcast(intent);
    }

    private String getDate(long timeStamp) {

        try {
            SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
            Date netDate = (new Date(timeStamp));
            return sdf.format(netDate);
        } catch (Exception ex) {
            return "date";
        }
    }

    private void sendBD() {
        Intent intent = new Intent();
        intent.setAction("com.scs.app.SESSION");
        sendBroadcast(intent);
    } private void sendBD2() {
        Intent intent = new Intent();
        intent.setAction("com.scs.app.dashboard");
        sendBroadcast(intent);
    }

    private void getredAlert(String text) {
        Toast.makeText(this, text, Toast.LENGTH_LONG).show();
    }
}
