package com.bsecure.scsm_mobile.backservice;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.view.Gravity;
import android.widget.Toast;

import com.bsecure.scsm_mobile.callbacks.HttpHandler;
import com.bsecure.scsm_mobile.callbacks.IDownloadCallback;
import com.bsecure.scsm_mobile.common.NetworkInfoAPI;
import com.bsecure.scsm_mobile.common.Paths;
import com.bsecure.scsm_mobile.database.DB_Tables;
import com.bsecure.scsm_mobile.https.FileUploader;
import com.bsecure.scsm_mobile.https.HTTPostJson;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Hashtable;

public class AttachmentsScheduler extends Service implements HttpHandler, IDownloadCallback {

    private Messenger mMessenger = new Messenger(new IncomingHandler());

    private Messenger client = null;

    public static final int MSG_REGISTER_CLIENT = 1;

    public static final int MSG_UNREGISTER_CLIENT = 2;

    public static final int DWL_START = 3;

    public static final int DWL_PROGS = 4;

    public static final int DWL_CANCEL = 5;

    public static final int DWL_FAILED = 6;

    public static final int DWL_COMPLT = 7;

    public static final int DWL_INFO = 8;

    public static final int MSG_INFO_CLIENT = 9;

    public static final int DWL_STOP = -2;

    private FileUploader task = null;

    private DB_Tables table = null;

    private NetworkInfoAPI networkInfoAPI = null;

    private JSONObject jsonObject = null;

    private JSONArray attachments = new JSONArray();

    private int attachCounter = -1;

    private String fialddata;
    private String updatevalue, mailcount, mPosition, mComId;
    private Hashtable<Integer, JSONObject> requestData = new Hashtable<>();

    @Override
    public void onCreate() {
        super.onCreate();
        networkInfoAPI = new NetworkInfoAPI();
        networkInfoAPI.initialize(this);
    }

    public boolean isOnline(Context context) {
        ConnectivityManager connMgr = (ConnectivityManager)
                context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        return (networkInfo != null && networkInfo.isConnected());
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        try {
            if (intent != null) {
                if (intent.getAction().equals("com.scm.action.addcompose")) {

                    if (isOnline(this)) {

                        startUpload(intent.getIntExtra("requestId", -1), intent.getStringExtra("messagedata"));
                        fialddata = intent.getStringExtra("messagedata");
                    } else {
                        showToast("Sending mail delivery faild");

                        fialddata = intent.getStringExtra("messagedata");
                        jsonObject = new JSONObject(fialddata);
                        table = getDataBaseObject();
                        //table.insertOutboxData(1, jsonObject.toString());
                        //startUpload(intent.getIntExtra("requestId", -1), intent.getStringExtra("messagedata"));
                    }

                } else if (intent.getAction().equals("com.mail.sendsafe.action.removecontent")) {
                    removeDownloadingContent(intent.getIntExtra("requestId", -1));
                } else if (intent.getAction().equals("com.mail.sendsafe.action.net.connected")) {
                    checkDBNStartUploading();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return START_STICKY; // run until explicitly stopped.
    }

    @Override
    public IBinder onBind(Intent intent) {

        return mMessenger.getBinder();
    }

    @Override
    public void onDestroy() {

        super.onDestroy();
        mMessenger = null;
        client = null;

        if (networkInfoAPI != null)
            networkInfoAPI.onDestroy();
        networkInfoAPI = null;

        if (task != null /*&& task.getStatus() == Status.RUNNING*/) {
            task.cancelRequest();
            task = null;
        }
    }

    private void stopIt() {

        if (client == null)
            stopSelf();

        if (client != null) {
            sendMessageToUI(DWL_STOP, 0, 0, null);
            stopSelf();
            return;
        }

        sendMessageToUI(DWL_STOP, 0, 0, null);

    }

    @Override
    public void onStateChange(int what, int arg1, int arg2, Object obj, int requestId) {

        try {

            switch (what) {

                case -1: // failed

                    /*View fview = attachmentLayout.findViewById(requestId);
                    Item fitem = (Item) fview.getTag();
                    removeAttachment(requestId);*/

                    break;

                case 1: // progressBar
                    break;

                case 0: // success

//                    if(attachCounter > 0) {
//
//                        attachCounter = attachCounter--;
//                        startAttachmentUploading(requestId,  attachments.getJSONObject(attachCounter1));
//                        return;
//
//                    }
                    if (attachCounter > 0) {
                        attachCounter = --attachCounter;
                        startAttachmentUploading(requestId, attachments.getJSONObject(attachCounter));
                        return;
                    }
                    if (attachments == null) {
                        jsonObject = new JSONObject(fialddata);
//                        table = getDataBaseObject();
//                        table.insertOutboxData(1, jsonObject.toString());
                    }
                    sendMailBody(requestId);

                    break;

                default:
                    break;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    class IncomingHandler extends Handler { // Handler of incoming messages from

        // clients.
        @Override
        public void handleMessage(Message msg) {

            switch (msg.what) {

                case MSG_REGISTER_CLIENT:
                    client = msg.replyTo;

                    if (jsonObject != null) {
                        int count = 0;

                        //sendMessageToUI(DownloadService.DWL_START, currentCount, totalCount, jsonObject.optString("title"));
                        //sendMessageToUI(DownloadService.DWL_START, 1, count, currentItem.getAttribute("TITLE"));
                    } else {
                        //sendMessageToUI(DownloadService.DWL_START, 1, 1, "");
                    }


                    break;

                case MSG_UNREGISTER_CLIENT:
                    client = null;
                    break;

                case MSG_INFO_CLIENT:

                    int count = msg.arg1;

                    //totalCount = count + (currentCount - 1);
                    //sendMessageToUI(DownloadService.DWL_INFO, currentCount, totalCount, "");

                    break;

                default:
                    super.handleMessage(msg);
            }
        }

    }

    private void sendMessageToUI(int state, int value, int value1, Object object) {

        try {
            if (client == null) {
                return;
            }

            client.send(Message.obtain(null, state, value, value1, object));
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void showToast(int value) {

        Toast.makeText(this, value, Toast.LENGTH_SHORT).show();
    }

    public void showToast(String value) {

        Toast toast = Toast.makeText(this, value, Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.BOTTOM, 0, 0);
        toast.show();

        toast.show();

    }

    private DB_Tables getDataBaseObject() {

        if (table == null)
            table = new DB_Tables(this);

        return table;
    }

    private void removeDownloadingContent(int requestId) {

        /*FileUploader httptask = requestItems.get(requestId);
        if (httptask != null) {
            httptask.cancelRequest();
            requestItems.remove(requestId);
            httptask = null;
        }*/
    }

    private void startAttachmentUploading(int requestId, JSONObject attahmentObject) {
        String url = Paths.base + "teacher/upload_photo";

        FileUploader uploader = new FileUploader(this, this);
        uploader.setFileName(attahmentObject.optString("displayname"), attahmentObject.optString("attachname"));
        uploader.userRequest("", requestId, url, attahmentObject.optString("filepath"));
        sendMessageToUI(DWL_START, requestId, 0, null);
    }


    private void sendMailBody(int requestId) {

        attachCounter = -1;
        attachments = null;
        HTTPostJson post = new HTTPostJson(this, this, jsonObject.toString(), requestId);
        post.setContentType("application/json");
        post.execute(jsonObject.optString("sendurl"), "");

    }

    private void checkDBNStartUploading() {

        if (requestData.size() > 1) {
            return;
        }

        table = getDataBaseObject();

        // JSONObject nextObj = table.getOutboxMail(/*AppPreferences.getInstance(this).getFromStore("userid")*/);
//
//        if (nextObj != null) {
//            startUpload(nextObj.optInt("messageid"), nextObj.optString("messagedata"));
//            return;
//        }

        if (requestData.size() == 0) {
            this.stopSelf();
        }

    }

    private void startUpload(int requestId, String data) {

        try {

            if (requestData.size() > 1) {
                return;
            }

            jsonObject = new JSONObject(data);

            requestData.put(requestId, jsonObject);

            if (isOnline(this)) {
                if (jsonObject.has("attachments")) {
                    attachments = jsonObject.getJSONArray("attachments");
                    attachCounter = attachments.length();

                    //if (attachments.length() > 0) {
                    if (attachCounter > 0) {
                        attachCounter = --attachCounter;
                        startAttachmentUploading(requestId, attachments.getJSONObject(attachCounter));
                        return;
                    }
                }
            } else {
                jsonObject = new JSONObject(data);
                table = getDataBaseObject();
                // table.insertOutboxData(1, jsonObject.toString());
            }

            //table = getDataBaseObject();

            sendMailBody(requestId);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onResponse(Object results, int requestId) {

        if (requestId == -1) {
            return;
        }

        try {

            if (results != null) {

                JSONObject object = new JSONObject(results.toString());

                if (object.optString("statuscode").equalsIgnoreCase("200")) {
                    getDataBaseObject().messageDataFlagUpdate(object.optString("message_date"));
                }

                requestData.remove(requestId);

                table = getDataBaseObject();

                sendMessageToUI(DWL_COMPLT, requestId, 0, jsonObject);

            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onFailure(String errorData, int requestId) {

        requestData.remove(requestId);

        table = getDataBaseObject();

        //table.updateDownloadStatus(String.valueOf(requestId), "F");

        /*if (errorData.equalsIgnoreCase(getString(R.string.yhctd))) {
            sendMessageToUI(DWL_CANCEL, requestId, 0, errorData);
            showToast(getString(R.string.yhctd));
            table.updateErrMsgOnFail(String.valueOf(requestId), getString(R.string.yhctd));
            return;
        }

        table.updateErrMsgOnFail(String.valueOf(requestId),getString(R.string.dfptal));*/

        sendMessageToUI(DWL_FAILED, requestId, 0, errorData);
        showToast(errorData);

    }

    /* @Override
     public void onProgressChange(int requestId, Long... values) {
         sendMessageToUI(DWL_PROGS, requestId, 0, values);
     }*/
    public boolean isNetworkAvailable() {

        ConnectivityManager manager = (ConnectivityManager) getApplicationContext()
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        if (manager == null) {
            return false;
        }

        NetworkInfo net = manager.getActiveNetworkInfo();

        return net != null && net.isConnected();

    }
}
