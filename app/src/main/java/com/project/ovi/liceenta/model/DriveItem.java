package com.project.ovi.liceenta.model;

import com.google.api.client.util.DateTime;
import com.project.ovi.liceenta.util.GoogleTypesUtil;
import com.project.ovi.liceenta.util.ProjectConstants;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

/**
 * Created by Ovi on 27/07/16.
 */
public class DriveItem implements Serializable{

    private String id;

    private String name;

    private String mimeType;

    private DateTime creationDate;

    private Long iconId;

    private boolean isBookmarked = false;

    public DriveItem(String id, String name, String mimeType, DateTime createdTime, Map<String, String> properties){
        this.id = id;
        this.name = name;
        this.mimeType = mimeType;
        this.creationDate = createdTime;
        this.iconId = GoogleTypesUtil.getIconId(mimeType);
        if(properties != null && properties.get(ProjectConstants.IS_BOOKMARKED) != null) {
            this.isBookmarked = properties.get(ProjectConstants.IS_BOOKMARKED).equals("1");
        }
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

    public Integer getIconId() {
        return iconId.intValue();
    }

    public void setIconId(long iconId) {
        this.iconId = iconId;
    }

    public boolean isBookmarked() {
        return isBookmarked;
    }

    public void setIsBookmarked(boolean isBookmarked) {
        this.isBookmarked = isBookmarked;
    }

    public String getInfo(){
        DateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy hh:mm");
        Date date = new Date(creationDate.getValue());
        return dateFormat.format(date);
    }

}
