package com.project.ovi.liceenta.model;

import com.google.api.services.drive.model.File;

/**
 * Created by Ovi on 12/08/16.
 */
public class DriveFile extends DriveItem {

    private String extension;

    private long size;

    public DriveFile(File file) {
        super(file.getId(), file.getName(), file.getCreatedTime());
        this.size = file.getSize();
        this.extension = file.getFullFileExtension();
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
        return size + " B - " + super.getInfo();
    }
}
