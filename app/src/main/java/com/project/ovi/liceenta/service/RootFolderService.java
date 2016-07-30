package com.project.ovi.liceenta.service;

import com.google.api.services.drive.Drive;
import com.google.api.services.drive.model.File;
import com.google.api.services.drive.model.FileList;
import com.project.ovi.liceenta.model.DriveItem;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Ovi on 30/07/16.
 */
public class RootFolderService {


    public RootFolderService() {
    }

    public List<DriveItem> getRootFolderItems() throws IOException {

        List<DriveItem> resulItems = new ArrayList<DriveItem>();

        Drive drive = RestService.getDriveService();

        FileList items = drive.files().list().execute();
        List<File> listItems = items.getFiles();
        for(File file : listItems){
            resulItems.add(new DriveItem(file));
        }

        return  resulItems;
    }
}
