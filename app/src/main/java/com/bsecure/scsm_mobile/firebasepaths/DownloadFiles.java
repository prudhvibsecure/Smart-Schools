package com.bsecure.scsm_mobile.firebasepaths;

import android.app.DownloadManager;
import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.Nullable;

/**
 * Created by Admin on 2018-12-13.
 */

public class DownloadFiles extends IntentService {

    private static final String DOWNLOAD_PATH = "my_Download_path";
    private static final String DESTINATION_PATH = "my_Destination_path";
    private static final String FILE_NAME = "my_file_name";
    public DownloadFiles() {
        super("DownloadFiles");
    }

    public static Intent getDownloadService(final Context callingClassContext, final String downloadPath, final String destinationPath, String f_name) {
        return new Intent(callingClassContext, DownloadFiles.class)
                .putExtra(DOWNLOAD_PATH, downloadPath)
                .putExtra(DESTINATION_PATH, destinationPath)
                .putExtra(FILE_NAME, f_name);
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        String downloadPath = intent.getStringExtra(DOWNLOAD_PATH);
        String destinationPath = intent.getStringExtra(DESTINATION_PATH);
        String f_name = intent.getStringExtra(FILE_NAME);
        startDownload(downloadPath, destinationPath, f_name);
    }

    private void startDownload(String downloadPath, String destinationPath, String f_name) {
        Uri uri = Uri.parse(downloadPath);
        DownloadManager.Request request = new DownloadManager.Request(uri);
        request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_MOBILE | DownloadManager.Request.NETWORK_WIFI);  // Tell on which network you want to download file.
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);  // This will show notification on top when downloading the file.
        request.setTitle(f_name); // Title for notification.
        request.setVisibleInDownloadsUi(true);
        request.setDestinationInExternalPublicDir(destinationPath, uri.getLastPathSegment());  // Storage directory path
        ((DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE)).enqueue(request); // This will start downloading

    }
}
