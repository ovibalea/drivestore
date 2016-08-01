package com.project.ovi.liceenta.model;

import com.google.android.gms.drive.Metadata;

/**
 * Created by Ovi on 27/07/16.
 */
public class File extends DriveItem {

    private String extension;

    private Double size;

    public File(Metadata metadata) {
        super(metadata);
    }

   
}
