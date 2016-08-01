/**
 * Copyright 2013 Google Inc. All Rights Reserved.
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 * <p/>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p/>
 * Unless required by applicable law or agreed to in writing, software distributed under the
 * License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 * express or implied. See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.project.ovi.liceenta;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.drive.Drive;
import com.google.android.gms.drive.DriveApi.MetadataBufferResult;
import com.google.android.gms.drive.Metadata;
import com.google.android.gms.drive.query.Query;
import com.google.android.gms.drive.widget.DataBufferAdapter;
import com.project.ovi.liceenta.model.DriveItem;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * An activity illustrates how to list file results and infinitely
 * populate the results list view with data if there are more results.
 */
public class ListOjectsFromRootActivity extends BaseManagerActivity {

    public static final String DRIVE_ITEMS_RESULT_TAG = "driveItems";

//    private RecyclerView recyclerView;
    private ArrayList<DriveItem> driveItems;


    private String mNextPageToken;
    private boolean mHasMore;


    @Override
    protected void onCreate(Bundle b) {
        super.onCreate(b);
//        setContentView(R.layout.content_main);

//        recyclerView = (RecyclerView) findViewById(R.id.recyclerview);
    }

    /**
     * Handles the Drive service connection initialization
     * and inits the first listing request.
     */
    @Override
    public void onConnected(Bundle connectionHint) {
        super.onConnected(connectionHint);
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
    private final ResultCallback<MetadataBufferResult> metadataBufferCallback = new
            ResultCallback<MetadataBufferResult>() {
                @Override
                public void onResult(MetadataBufferResult result) {
                    if (!result.getStatus().isSuccess()) {
                        showMessage("Problem while retrieving files");
                        return;
                    }
                    driveItems = new ArrayList<DriveItem>();
                    Iterator<Metadata> resultsIterator = result.getMetadataBuffer().iterator();
                    while (resultsIterator.hasNext()){
                        Metadata item = resultsIterator.next();
                        driveItems.add(new DriveItem(item));
                    }

//                    DriveItemsViewAdapter driveItemsViewAdapter = new DriveItemsViewAdapter(driveItems);
//                    recyclerView.setAdapter(driveItemsViewAdapter);
//                    recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));

//                    finish();

                }
            };


    @Override
    public void finish(){
        Intent returnIntent = new Intent();
        returnIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        returnIntent.putExtra(DRIVE_ITEMS_RESULT_TAG, driveItems);
        setResult(RESULT_OK, returnIntent);
        super.finish();
    }
}