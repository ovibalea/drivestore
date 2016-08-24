package com.project.ovi.liceenta.service.activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

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
 * Created by Ovi on 24/08/16.
 */
public class AddCustomAttributeActivity extends BaseActivity {

    private String itemId;
    private Button cancelButton;
    private Button addButton;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_attribute_dialog);

        setButtonsListeners();

        itemId = getIntent().getStringExtra(ProjectConstants.ITEM_ID_TAG);
    }

    private void setButtonsListeners() {
        addButton = (Button) findViewById(R.id.addAttributeBtn);
        cancelButton = (Button) findViewById(R.id.cancelAddAttr);

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.putExtra(ProjectConstants.IS_ITEM_PROCESSED, false);
                setResult(ProjectConstants.REQUEST_PROCESS_ITEM, intent);
                finish();
            }
        });

        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText attrNameInput = (EditText) findViewById(R.id.attrNameInput);
                EditText attrValueInput = (EditText) findViewById(R.id.attrValueInput);
                String attrName = attrNameInput.getText().toString();
                String attrValue = attrValueInput.getText().toString();
                if (attrName == null || attrName.length() == 0 || attrValue == null || attrValue.length() == 0) {
                    showToast("Attribute name or value cannot be empty!");
                } else {
                    new AddCustomAttributeTask(AddCustomAttributeActivity.this, attrName, attrValue).execute();
                }
            }
        });

    }

    /**
     * An asynchronous task that handles the Drive API call.
     * Placing the API calls in their own task ensures the UI stays responsive.
     */
    private class AddCustomAttributeTask extends AsyncTask<Void, Void, Boolean> {

        private com.google.api.services.drive.Drive mService = null;
        private Exception mLastError = null;
        private ProgressDialog mProgress;
        private String attributeName;
        private String attributeValue;

        public AddCustomAttributeTask(Context context, String attributeName, String attributeValue) {
            mProgress = new ProgressDialog(context);
            mProgress.setMessage("Add custom attribute...");

            mService = DriveServiceManager.getInstance().getService();
            this.attributeName = attributeName;
            this.attributeValue = attributeValue;
        }

        /**
         * Background task to call Drive API.
         *
         * @param params no parameters needed for this task.
         */
        @Override
        protected Boolean doInBackground(Void... params) {
            try {
                return addCustomAttribute();
            } catch (Exception e) {
                mLastError = e;
                cancel(true);
                return null;
            }
        }

        private boolean addCustomAttribute() throws IOException {

            File item = mService.files().get(itemId).execute();
            File newFile = new File();
            Map<String, String> properties = item.getProperties();
            if (properties == null) {
                properties = new HashMap<>();
                properties.put(attributeName, attributeValue);
            } else {

                String existentAttr = item.getProperties().get(attributeName);
                if (existentAttr != null) {
                    attributeValue = existentAttr + ", " + attributeValue;
                }
                item.getProperties().put(attributeName, attributeValue);

            }
            newFile.setProperties(properties);
            mService.files().update(item.getId(), newFile).execute();

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
                showToast("Custom attribute added successfully!");
            }

            Intent intent = new Intent();
            intent.putExtra(ProjectConstants.IS_ITEM_PROCESSED, output);
            setResult(ProjectConstants.REQUEST_PROCESS_ITEM, intent);
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
                finish();
            } else {
                showToast("Request cancelled.");
            }
        }
    }

}
