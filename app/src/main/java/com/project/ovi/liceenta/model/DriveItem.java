package com.project.ovi.liceenta.model;

import com.google.api.services.drive.model.File;

import java.io.Serializable;

/**
 * Created by Ovi on 27/07/16.
 */
public class DriveItem implements Serializable{

    private String id;

    private String name;

    private boolean isFolder;

    public DriveItem(File object){
        this.id = object.getId();
        this.name = object.getName();
        this.isFolder = object.getFullFileExtension() == null;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isFolder() {
        return isFolder;
    }

    public void setIsFolder(boolean isFolder) {
        this.isFolder = isFolder;
    }
}
