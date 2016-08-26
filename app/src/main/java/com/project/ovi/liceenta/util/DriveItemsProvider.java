package com.project.ovi.liceenta.util;

import android.util.Log;

import com.google.api.services.drive.Drive;
import com.google.api.services.drive.model.File;
import com.google.api.services.drive.model.FileList;
import com.project.ovi.liceenta.model.DriveFile;
import com.project.ovi.liceenta.model.DriveFolder;
import com.project.ovi.liceenta.model.DriveItem;
import com.project.ovi.liceenta.service.DriveServiceManager;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by Ovi on 15/08/16.
 */
public class DriveItemsProvider {

    private static final String TAG = "DriveItemsProvider";

    private static DriveItemsProvider instance = null;

    private Drive dService;

    public DriveItemsProvider() {
        this.dService = DriveServiceManager.getInstance().getService();
    }

    public static DriveItemsProvider getInstance() {
        if (instance == null) {
            instance = new DriveItemsProvider();
        }
        return instance;
    }


    public ArrayList<DriveItem> createDriveItemsArray(String queryCondition, String orderBy) throws IOException {
        ArrayList<DriveItem> driveItems = new ArrayList<>();

        String pageToken = null;
        try {
            do {
                FileList itemsList = getFileList(queryCondition, orderBy, pageToken);

                for (File file : itemsList.getFiles()) {
                    DriveItem item;
                    if (file.getMimeType().equals(ProjectConstants.MIMETYPE_FOLDER)) {
                        FileList children = dService.files().list().setQ("'" + file.getId() + "' in parents").execute();
                        item = new DriveFolder(file, children.getFiles().size());
                    } else {
                        item = new DriveFile(file);
                    }
                    driveItems.add(item);
                    Log.i(TAG, file.toString() + file.getFullFileExtension());
                }
                pageToken = itemsList.getNextPageToken();
            } while (pageToken != null);
        } catch (IOException ex) {
            Log.i(TAG, ex.getMessage());
        }

        return driveItems;
    }

    private FileList getFileList(String queryCondition, String orderBy, String pageToken) throws IOException {
        Drive.Files.List driveList = dService.files().list()
                .setFields("nextPageToken, files(" + ProjectConstants.ITEM_FIELDS + ")")
                .setPageToken(pageToken);

        if(queryCondition != null){
            driveList.setQ(queryCondition);
        }
        if(orderBy != null){
            driveList.setOrderBy(orderBy);
        }

        return driveList.execute();
    }
}
