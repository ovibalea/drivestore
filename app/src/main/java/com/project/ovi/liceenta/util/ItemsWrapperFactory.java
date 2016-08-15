package com.project.ovi.liceenta.util;

import android.util.Log;

import com.google.api.services.drive.Drive;
import com.google.api.services.drive.model.File;
import com.google.api.services.drive.model.FileList;
import com.project.ovi.liceenta.model.DriveFile;
import com.project.ovi.liceenta.model.DriveFolder;
import com.project.ovi.liceenta.model.DriveItem;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Ovi on 15/08/16.
 */
public class ItemsWrapperFactory {

    private static final String TAG = "ItemsWrapperFactory";

    public ItemsWrapperFactory() {
    }

    public static ArrayList<DriveItem> createDriveItemsArray(FileList itemsList, Drive dService) throws IOException {
        ArrayList<DriveItem> driveItems = new ArrayList<>();

        List<File> files = itemsList.getFiles();
        if (files != null) {
            for (File file : files) {
                DriveItem item;
                if(file.getMimeType().equals(ProjectConstants.MIMETYPE_FOLDER)) {
                    FileList children = dService.files().list().setQ("'"+file.getId()+"' in parents").execute();
                    item = new DriveFolder(file, children.getFiles().size());
                } else {
                    item = new DriveFile(file);
                }
                driveItems.add(item);
                Log.i(TAG, file.toString() + file.getFullFileExtension());
            }
        }

        return driveItems;
    }
}
