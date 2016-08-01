package com.project.ovi.liceenta.model;

import com.google.android.gms.drive.Metadata;

import java.io.Serializable;

/**
 * Created by Ovi on 27/07/16.
 */
public class DriveItem implements Serializable{

    private String title;

    private String originalName;

    private boolean isFolder;

    public DriveItem(Metadata object){
        this.title = object.getTitle();
        this.originalName = object.getOriginalFilename();
        this.isFolder = object.isFolder();
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
