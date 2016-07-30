package com.project.ovi.liceenta.model;

import com.google.android.gms.drive.Metadata;
import com.google.api.services.drive.model.*;

/**
 * Created by Ovi on 27/07/16.
 */
public class Folder extends DriveItem {

    public Folder(com.google.api.services.drive.model.File metadata) {
        super(metadata);
    }


}
