package com.project.ovi.liceenta.model;

import com.google.api.client.util.DateTime;
import com.google.api.services.drive.model.File;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Ovi on 27/07/16.
 */
public class DriveItem implements Serializable{

    private String id;

    private String name;

    private String mimeType;

    private DateTime creationDate;

    public DriveItem(String id, String name, String mimeType, DateTime createdTime){
        this.id = id;
        this.name = name;
        this.mimeType = mimeType;
        this.creationDate = createdTime;
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

    public String getMimeType() {
        return mimeType;
    }

    public void setMimeType(String mimeType) {
        this.mimeType = mimeType;
    }

    public String getInfo(){
        DateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy mm:ss");
        Date date = new Date(creationDate.getValue());
        return dateFormat.format(date);
    }

}
