package com.project.ovi.liceenta.service.activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;

import com.google.api.client.googleapis.extensions.android.gms.auth.GooglePlayServicesAvailabilityIOException;
import com.google.api.client.googleapis.extensions.android.gms.auth.UserRecoverableAuthIOException;
import com.google.api.services.drive.model.File;
import com.google.api.services.drive.model.FileList;
import com.project.ovi.liceenta.R;
import com.project.ovi.liceenta.model.ItemAttribute;
import com.project.ovi.liceenta.service.BaseActivity;
import com.project.ovi.liceenta.service.DriveServiceManager;
import com.project.ovi.liceenta.util.ProjectConstants;
import com.project.ovi.liceenta.view.attributes.AttributesViewAdapter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by Ovi on 23/08/16.
 */
public class ShowItemInfoActivity extends BaseActivity {

    private String itemId;
    private RecyclerView infoRecyclerView;
    private AttributesViewAdapter attributesViewAdapter;
    private Button closeButton;
    private Button addAttrButton;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        itemId = getIntent().getStringExtra(ProjectConstants.ITEM_ID_TAG);

        setContentView(R.layout.content_item_info);
        setButtonListeners();
        showItemAttributes();
    }

    private void showItemAttributes(){
        new ItemAttributesTask(ShowItemInfoActivity.this, itemId).execute();
    }

    private void setButtonListeners() {
        closeButton = (Button) findViewById(R.id.closeAttrViewBtn);
        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        addAttrButton = (Button) findViewById(R.id.addAttrBtn);
        addAttrButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent addAttrIntent = new Intent(ShowItemInfoActivity.this, AddCustomAttributeActivity.class);
                addAttrIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                addAttrIntent.putExtra(ProjectConstants.ITEM_ID_TAG, itemId);
                startActivityForResult(addAttrIntent, ProjectConstants.REQUEST_PROCESS_ITEM);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case ProjectConstants.REQUEST_PROCESS_ITEM:
                boolean isItemProcessed = data.getBooleanExtra(ProjectConstants.IS_ITEM_PROCESSED, true);
                if (isItemProcessed) {
                    showItemAttributes();
                }
                break;
        }
    }

    @Override
    public void onBackPressed() {
        finish();
    }

    /**
     * An asynchronous task that handles the Drive API call.
     * Placing the API calls in their own task ensures the UI stays responsive.
     */
    private class ItemAttributesTask extends AsyncTask<Void, Void, ArrayList<ItemAttribute>> {

        private com.google.api.services.drive.Drive mService = null;
        private Exception mLastError = null;
        private ProgressDialog mProgress;
        private String itemId;

        public ItemAttributesTask(Context context, String itemId) {
            mProgress = new ProgressDialog(context);
            mProgress.setMessage("Prepare item attributes...");
            mService = DriveServiceManager.getInstance().getService();
            this.itemId = itemId;
        }

        /**
         * Background task to call Drive API.
         *
         * @param params no parameters needed for this task.
         */
        @Override
        protected ArrayList<ItemAttribute> doInBackground(Void... params) {
            try {
                return processItemAttributes();
            } catch (Exception e) {
                mLastError = e;
                cancel(true);
                return null;
            }
        }

        private ArrayList<ItemAttribute> processItemAttributes() throws IOException {

            File item = mService.files().get(itemId).setFields(ProjectConstants.ITEM_ATTRIBUTES).execute();


            ArrayList<ItemAttribute> attributes = getAttributesList(item);

            return attributes;
        }

        private ArrayList<ItemAttribute> getAttributesList(File item) {
            ArrayList<ItemAttribute> attributes = new ArrayList<>();
            if (item.getId() != null) {
                attributes.add(new ItemAttribute("ID", item.getId()));
            }
            if (item.getName() != null) {
                attributes.add(new ItemAttribute("Name", item.getName()));
            }
            if (item.getDescription() != null) {
                attributes.add(new ItemAttribute("Description", item.getDescription()));
            }
            if (item.getKind() != null) {
                attributes.add(new ItemAttribute("ItemType", item.getKind()));
            }
            if (item.getMimeType() != null) {
                attributes.add(new ItemAttribute("MimeType", item.getMimeType()));
            }
            if (item.getCreatedTime() != null) {
                attributes.add(new ItemAttribute("CreationDate", item.getCreatedTime()));
            }
            if (item.getModifiedTime() != null) {
                attributes.add(new ItemAttribute("ModifiedDate", item.getModifiedTime()));
            }
            if (item.getModifiedByMeTime() != null) {
                attributes.add(new ItemAttribute("LastModifiedByMe", item.getModifiedByMeTime()));
            }
            if (item.getViewedByMeTime() != null) {
                attributes.add(new ItemAttribute("LastViewedByMe", item.getViewedByMeTime()));
            }
            if (item.getLastModifyingUser() != null) {
                attributes.add(new ItemAttribute("LastModifyingUser", item.getLastModifyingUser()));
            }
            if (item.getOwners() != null) {
                attributes.add(new ItemAttribute("Owners", item.getOwners()));
            }
            if (item.getParents() != null) {
                attributes.add(new ItemAttribute("Links", getLinks(item.getParents()).toArray(new String[]{})));
            }
            if (item.getShared() != null) {
                attributes.add(new ItemAttribute("IsShared", item.getShared()));
            }
            if (item.getSize() != null) {
                attributes.add(new ItemAttribute("Size", item.getSize()));
            }
            if (item.getVersion() != null) {
                attributes.add(new ItemAttribute("Version", item.getVersion()));
            }
            if (item.getProperties() != null) {
                Map<String, String> properties = item.getProperties();
                for (String name : properties.keySet()) {
                    attributes.add(new ItemAttribute(name, properties.get(name)));
                }
            }
            return attributes;
        }

        public List<String> getLinks(List<String> parents) {
            List<String> parentNames = new ArrayList<>();
            String pageToken = null;
            try {
                do {
                    FileList result = mService.files().list()
                            .setFields("nextPageToken, files(id, name)")
                            .setPageToken(pageToken)
                            .execute();
                    for (File file : result.getFiles()) {
                        if (parents.contains(file.getId())) {
                            parentNames.add(file.getName());
                        }
                    }
                    pageToken = result.getNextPageToken();
                } while (pageToken != null);
            } catch (IOException ex) {
                showToast(ex.getMessage());
            }
            return parentNames;
        }

        @Override
        protected void onPreExecute() {
            mProgress.show();
        }

        @Override
        protected void onPostExecute(ArrayList<ItemAttribute> output) {
            mProgress.dismiss();
            if (output.size() > 0) {
                infoRecyclerView = (RecyclerView) findViewById(R.id.recyclerviewshowinfo);
                attributesViewAdapter = new AttributesViewAdapter(output);
                infoRecyclerView.setAdapter(attributesViewAdapter);
                infoRecyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
            } else {
                showToast("Error occurs while prepare item attributes!");
                finish();
            }
        }

        @Override
        protected void onCancelled() {
            mProgress.dismiss();
            if (mLastError != null) {
                if (mLastError instanceof GooglePlayServicesAvailabilityIOException) {
                    showGooglePlayServicesAvailabilityErrorDialog(
                            ((GooglePlayServicesAvailabilityIOException) mLastError)
                                    .getConnectionStatusCode());
                } else if (mLastError instanceof UserRecoverableAuthIOException) {
                    startActivityForResult(
                            ((UserRecoverableAuthIOException) mLastError).getIntent(),
                            ProjectConstants.REQUEST_AUTHORIZATION);
                } else {
                    showToast("The following error occurred:\n"
                            + mLastError.getMessage());
                }
            } else {
                showToast("Request cancelled.");
            }
        }
    }

}
