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

/**
 * Created by Ovi on 23/08/16.
 */
public class RenameActivity extends BaseActivity {

    private String itemId;

    private Button saveButton;

    private Button cancelButton;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.rename_item_dialog);

        setButtonsActions();

        itemId = getIntent().getStringExtra(ProjectConstants.ITEM_ID_TAG);
    }

    private void setButtonsActions() {
        saveButton = (Button) findViewById(R.id.renameItemBtn);
        cancelButton = (Button) findViewById(R.id.cancelRenameItemBtn);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String newName = getFolderName();
                if(newName != null && newName.length() > 0) {
                    new RenameItemTask(RenameActivity.this, itemId, newName).execute();
                } else {
                    finalizeActionWithResult(false);
                }
            }
        });

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finalizeActionWithResult(false);
            }
        });
    }

    private String getFolderName(){
        EditText nameEditText = (EditText) findViewById(R.id.editTextNewName);
        String newName = nameEditText.getText().toString();

        return newName;
    }

    @Override
    public void onBackPressed() {
        finalizeActionWithResult(false);
    }

    private void finalizeActionWithResult(boolean result) {
        Intent intent = new Intent();
        intent.putExtra(ProjectConstants.IS_ITEM_CREATED, result);
        setResult(ProjectConstants.REQUEST_CREATE_ITEM, intent);
        finish();
    }


    /**
     * An asynchronous task that handles the Drive API call.
     * Placing the API calls in their own task ensures the UI stays responsive.
     */
    private class RenameItemTask extends AsyncTask<Void, Void, Boolean> {

        private com.google.api.services.drive.Drive mService = null;
        private Exception mLastError = null;
        private ProgressDialog mProgress;
        private String itemId;
        private String newName;

        public RenameItemTask(Context context, String itemId, String newName) {
            mProgress = new ProgressDialog(context);
            mProgress.setMessage("Rename item...");
            mService = DriveServiceManager.getInstance().getService();
            this.itemId = itemId;
            this.newName = newName;
        }

        /**
         * Background task to call Drive API.
         * @param params no parameters needed for this task.
         */
        @Override
        protected Boolean doInBackground(Void... params) {
            try {
                return renameItem();
            } catch (Exception e) {
                mLastError = e;
                cancel(true);
                return null;
            }
        }

        private boolean renameItem() throws IOException {

            File newFile = new File();
            newFile.setName(newName);
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
                showToast("File renamed successfully!");
            } else {
                showToast("File could not be renamed!");
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
