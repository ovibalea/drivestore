package com.project.ovi.liceenta.model;

import com.google.android.gms.drive.Metadata;
import com.google.api.services.drive.model.*;

import java.io.Serializable;

/**
 * Created by Ovi on 27/07/16.
 */
public class DriveItem implements Serializable{

    private String title;

    private String originalName;

    private boolean isFolder;

    public DriveItem(com.google.api.services.drive.model.File item){

        this.title = item.getName();
        this.originalName = item.getName();

        this.isFolder = item.getFileExtension() == null;
    }

    public String getTitle() {
        return this.title;
    }

    public String getName() {
        return  this.originalName;
    }

    public boolean isFolder() {
        return this.isFolder;
    }


}
