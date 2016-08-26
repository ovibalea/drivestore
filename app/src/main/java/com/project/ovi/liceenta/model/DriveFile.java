package com.project.ovi.liceenta.model;

import com.google.api.services.drive.model.File;
import com.project.ovi.liceenta.util.GoogleTypesUtil;

/**
 * Created by Ovi on 12/08/16.
 */
public class DriveFile extends DriveItem {

    private String extension;

    private long size = 0;

    public DriveFile(File file) {
        super(file.getId(), file.getName(), file.getMimeType(), file.getCreatedTime(), file.getStarred(), file.getProperties());
        if(file.getSize() != null) {
            this.size = file.getSize();
        }
        if(file.getFullFileExtension() != null) {
            this.extension = file.getFullFileExtension();
        } else {
            this.extension = GoogleTypesUtil.getExtensionByGoogleMimeType(file.getMimeType());
        }
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }

    public String getExtension() {
        return extension;
    }

    public void setExtension(String extension) {
        this.extension = extension;
    }

    @Override
    public String getInfo(){
        if(size > 0){
            return size + " B - " + super.getInfo();
        }
        return super.getInfo();
    }
}
