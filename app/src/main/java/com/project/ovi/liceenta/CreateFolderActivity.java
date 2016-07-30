/**
 * Copyright 2013 Google Inc. All Rights Reserved.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software distributed under the
 * License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 * express or implied. See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.project.ovi.liceenta;

import android.content.IntentSender;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.drive.Drive;
import com.google.android.gms.drive.DriveFolder.DriveFolderResult;
import com.google.android.gms.drive.MetadataChangeSet;

/**
 * An activity to illustrate how to create a new folder.
 */
public class CreateFolderActivity extends BaseManagerActivity {

    private static final String TAG = "Google Drive Activity";

    private static final  int REQUEST_CODE_OPENER = 2;

    @Override
    public void onConnected(Bundle connectionHint) {
        super.onConnected(connectionHint);

        MetadataChangeSet changeSet = new MetadataChangeSet.Builder()
                .setTitle("New folder").build();
        Drive.DriveApi.getRootFolder(getGoogleApiClient()).createFolder(
                getGoogleApiClient(), changeSet).setResultCallback(callback);
    }

    /**
     *  Open list of folder and file of the Google Drive
     */
    public void OpenFileFromGoogleDrive(){

        IntentSender intentSender = Drive.DriveApi
                .newOpenFileActivityBuilder()
                .setMimeType(new String[]{"text/plain", "text/html"})
                .build(getGoogleApiClient());
        try {
            startIntentSenderForResult(

                    intentSender, REQUEST_CODE_OPENER, null, 0, 0, 0);

        } catch (IntentSender.SendIntentException e) {

            Log.w(TAG, "Unable to send intent", e);
        }

    }

    final ResultCallback<DriveFolderResult> callback = new ResultCallback<DriveFolderResult>() {
        @Override
        public void onResult(DriveFolderResult result) {
            if (!result.getStatus().isSuccess()) {
                showMessage("Error while trying to create the folder");
                return;
            }
            finish();
        }
    };
}
