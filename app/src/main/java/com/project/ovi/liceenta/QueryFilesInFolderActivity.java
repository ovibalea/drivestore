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

import android.app.LauncherActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;


import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.drive.Drive;
import com.google.android.gms.drive.DriveApi;
import com.google.android.gms.drive.DriveFolder;
import com.google.android.gms.drive.DriveId;
import com.google.android.gms.drive.Metadata;
import com.google.android.gms.drive.MetadataBuffer;
import com.google.android.gms.drive.query.Filters;
import com.google.android.gms.drive.query.Query;
import com.google.android.gms.drive.query.SearchableField;

import java.util.ArrayList;
import java.util.List;

/**
 * An activity that illustrates how to query files in a folder. For an example
 * of pagination and displaying results, please see {@link }.
 */
public class QueryFilesInFolderActivity extends BaseManagerActivity {

    private ListView mResultsListView;
//    private ResultsAdapter mResultsAdapter;

    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.activity_listfiles);
        mResultsListView = (ListView) findViewById(R.id.listViewResults);
//        mResultsAdapter = new ResultsAdapter(this);
//        mResultsListView.setAdapter(mResultsAdapter);
    }

    @Override
    public void onConnected(Bundle connectionHint) {
        super.onConnected(connectionHint);
        String driveId = Drive.DriveApi.getRootFolder(getGoogleApiClient()).getDriveId().toString().substring(8);
        Log.i("QueryFiles", driveId);
        DriveFolder root = Drive.DriveApi.getRootFolder(getGoogleApiClient());
        root.listChildren(getGoogleApiClient()).setResultCallback(resultCallB);
        queryFolder();

    }

    private void queryFolder() {
        Query query = new Query.Builder()
                .build();
        Drive.DriveApi.query(getGoogleApiClient(), query)
                .setResultCallback(metadataBufferCallback);
    }

    /**
     * Appends the retrieved results to the result buffer.
     */
    private final ResultCallback<DriveApi.MetadataBufferResult> metadataBufferCallback = new
            ResultCallback<DriveApi.MetadataBufferResult>() {
                @Override
                public void onResult(DriveApi.MetadataBufferResult result) {
                    if (!result.getStatus().isSuccess()) {
                        showMessage("Problem while retrieving files");
                        return;
                    }
//                    mResultsAdapter.append(result.getMetadataBuffer());

                }
            };

    final private ResultCallback<DriveApi.DriveIdResult> idCallback = new ResultCallback<DriveApi.DriveIdResult>() {
        @Override
        public void onResult(DriveApi.DriveIdResult result) {
            if (!result.getStatus().isSuccess()) {
                showMessage("Cannot find DriveId. Are you authorized to view this file?");
                return;
            }
            DriveId driveId = result.getDriveId();
            DriveFolder folder = driveId.asDriveFolder();
            Query query = new Query.Builder()
                    .addFilter(Filters.eq(SearchableField.MIME_TYPE, "text/plain"))
                    .build();
            folder.queryChildren(getGoogleApiClient(), query)
                    .setResultCallback(metadataCallback);
        }
    };


    final private ResultCallback<DriveApi.MetadataBufferResult> resultCallB = new ResultCallback < DriveApi.MetadataBufferResult > () {
        @Override
        public void onResult(DriveApi.MetadataBufferResult result) {
            if (!result.getStatus().isSuccess()) {
                Log.i("QueryFiles","Error in listing root folder");
                return;
            }

            MetadataBuffer metadataBuffer = result.getMetadataBuffer();
            Metadata filedata;
            List<DriveApi.MetadataBufferResult> resultList = new ArrayList<>();
            LauncherActivity.ListItem[] items = new LauncherActivity.ListItem[metadataBuffer.getCount()];
            Log.i("","Count :" + metadataBuffer.getCount());
            for (int i = 0; i < metadataBuffer.getCount(); i++) {
                Log.i("QueryResult", metadataBuffer.get(i).getOriginalFilename() + " file name");
            }

        }
    };

    final private ResultCallback<DriveApi.MetadataBufferResult> metadataCallback = new
            ResultCallback<DriveApi.MetadataBufferResult>() {
        @Override
        public void onResult(DriveApi.MetadataBufferResult result) {
            if (!result.getStatus().isSuccess()) {
                showMessage("Problem while retrieving files");
                return;
            }
//            mResultsAdapter.clear();
//            mResultsAdapter.append(result.getMetadataBuffer());
            showMessage("Successfully listed files.");
        }
    };
}
