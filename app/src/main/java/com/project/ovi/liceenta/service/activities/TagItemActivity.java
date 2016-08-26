package com.project.ovi.liceenta.service.activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RadioGroup;

import com.google.api.client.googleapis.extensions.android.gms.auth.GooglePlayServicesAvailabilityIOException;
import com.google.api.client.googleapis.extensions.android.gms.auth.UserRecoverableAuthIOException;
import com.google.api.services.drive.model.File;
import com.project.ovi.liceenta.R;
import com.project.ovi.liceenta.service.BaseActivity;
import com.project.ovi.liceenta.service.DriveServiceManager;
import com.project.ovi.liceenta.util.ProjectConstants;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Ovi on 25/08/16.
 */
public class TagItemActivity extends BaseActivity {

    private String itemId;
    private Button selectButton;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        itemId = getIntent().getStringExtra(ProjectConstants.ITEM_ID_TAG);

        setContentView(R.layout.tag_item_dialog);
        setButtonListener();


    }

    private void setButtonListener(){
        selectButton = (Button) findViewById(R.id.setTagBtn);
        selectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RadioGroup radioGroup = (RadioGroup) findViewById(R.id.tagRadioGroup);
                int tagId = radioGroup.getCheckedRadioButtonId();
                String tagName = ProjectConstants.tagIdNameMapping.get(tagId);
                new TagItemTask(TagItemActivity.this, itemId, tagName).execute();
            }
        });
    }



    @Override
    public void onBackPressed() {
        finish();
    }

    /**
     * An asynchronous task that handles the Drive API call.
     * Placing the API calls in their own task ensures the UI stays responsive.
     */
    private class TagItemTask extends AsyncTask<Void, Void, Boolean> {

        private com.google.api.services.drive.Drive mService = null;
        private Exception mLastError = null;
        private ProgressDialog mProgress;
        private String itemId;
        private String tagName;

        public TagItemTask(Context context, String itemId, String tagName) {
            mProgress = new ProgressDialog(context);
            mProgress.setMessage("Tag item...");
            this.itemId = itemId;
            this.tagName = tagName;

            mService = DriveServiceManager.getInstance().getService();
        }

        /**
         * Background task to call Drive API.
         * @param params no parameters needed for this task.
         */
        @Override
        protected Boolean doInBackground(Void... params) {
            try {
                return bookmarkItem();
            } catch (Exception e) {
                mLastError = e;
                cancel(true);
                return null;
            }
        }

        private boolean bookmarkItem() throws IOException {

            File item = mService.files().get(itemId).setFields("properties").execute();
            Map<String, String> properties = item.getProperties();
            if(properties == null){
                properties = new HashMap<>();
            }

            if(!tagName.equals("noTag")){
                properties.put(ProjectConstants.ITEM_TAG, tagName);
            } else {
                if(properties.get(ProjectConstants.ITEM_TAG) != null){
                    properties.put(ProjectConstants.ITEM_TAG, "");
                }
            }

            File newFile = new File();
            newFile.setProperties(properties);
            mService.files().update(itemId, newFile).execute();

            return true;
        }

        @Override
        protected void onPreExecute() {
            mProgress.show();
        }

        @Override
        protected void onPostExecute(Boolean output) {
            mProgress.dismiss();
            if (output == true) {
                showToast("File tagged successfully!");
            } else {
                showToast("File could not be tagged!");
            }

            Intent intent = new Intent();
            intent.putExtra(ProjectConstants.IS_ITEM_CREATED,output);
            setResult(ProjectConstants.REQUEST_CREATE_ITEM, intent);
            finish();
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
