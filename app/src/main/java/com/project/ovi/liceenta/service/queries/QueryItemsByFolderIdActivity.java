package com.project.ovi.liceenta.service.queries;

import android.os.Bundle;

import com.project.ovi.liceenta.service.BaseActivity;

/**
 * Created by Ovi on 05/08/16.
 */
public class QueryItemsByFolderIdActivity extends BaseActivity {

    public static final String FOLDER_ID = "folderId";


    private String folderId;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        launchProcessing();
    }

    public void launchProcessing() {

            folderId = getIntent().getStringExtra(FOLDER_ID);
            new RequestItemsTask(this, "'" + folderId + "' in parents and trashed != true", "modifiedTime desc").execute();
    }

}
