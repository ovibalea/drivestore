package com.project.ovi.liceenta.model;

import com.google.api.services.drive.model.File;

/**
 * Created by Ovi on 12/08/16.
 */
public class DriveFolder extends DriveItem {

    private int nrOfItems;

    public DriveFolder(File folder, int childrenNo) {
        super(folder.getId(), folder.getName(), folder.getMimeType(), folder.getCreatedTime());
        this.nrOfItems = childrenNo;
    }

    public int getNrOfItems() {
        return nrOfItems;
    }

    public void setNrOfItems(int nrOfItems) {
        this.nrOfItems = nrOfItems;
    }

    public String getInfo(){
        String info;
        if(this.nrOfItems > 1){
            info = this.nrOfItems + " Files";
        } else if(this.nrOfItems == 1) {
            info = "1 File";
        } else {
            info = "Empty";
        }
        return info + " - " + super.getInfo();
    }
}
