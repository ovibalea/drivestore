package com.project.ovi.liceenta;

import android.os.Bundle;

import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.drive.Drive;
import com.google.android.gms.drive.DriveFolder;
import com.google.android.gms.drive.MetadataChangeSet;

/**
 * Created by Ovi on 21/07/16.
 */
public class CreateEmptyFileActivity extends BaseManagerActivity {

    @Override
    public void onConnected(Bundle connectionHint) {
        super.onConnected(connectionHint);

        MetadataChangeSet changeSet = new MetadataChangeSet.Builder()
                .setTitle("New file")
                .setMimeType("text/plain")
                .setStarred(true).build();

        // Create an empty file on root folder.
        Drive.DriveApi.getRootFolder(getGoogleApiClient())
                .createFile(getGoogleApiClient(), changeSet, null /* DriveContents */)
                .setResultCallback(fileCallback);
    }

    final private ResultCallback<DriveFolder.DriveFileResult> fileCallback = new
            ResultCallback<DriveFolder.DriveFileResult>() {
                @Override
                public void onResult(DriveFolder.DriveFileResult result) {
                    if (!result.getStatus().isSuccess()) {
                        showMessage("Error while trying to create the file");
                        return;
                    }
                    finish();
                }
            };


}
