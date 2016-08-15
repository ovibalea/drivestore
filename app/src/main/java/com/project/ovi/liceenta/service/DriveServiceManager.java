package com.project.ovi.liceenta.service;

import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.drive.Drive;

/**
 * Created by Ovi on 15/08/16.
 */
public class DriveServiceManager {

    private static DriveServiceManager instance;

    private Drive mService;

    private DriveServiceManager(GoogleAccountCredential credential) {
        HttpTransport transport = AndroidHttp.newCompatibleTransport();
        JsonFactory jsonFactory = JacksonFactory.getDefaultInstance();
        mService = new com.google.api.services.drive.Drive.Builder(
                transport, jsonFactory, credential)
                .setApplicationName("Drive Explorer")
                .build();
    }

    public static DriveServiceManager init(GoogleAccountCredential credential){
        if(instance == null){
            instance = new DriveServiceManager(credential);
        }
        return instance;
    }

    public static DriveServiceManager getInstance(){
        return instance;
    }



    public Drive getService(){
        return mService;
    }
}
