package com.project.ovi.liceenta.service.activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;

import com.github.angads25.filepicker.controller.DialogSelectionListener;
import com.github.angads25.filepicker.model.DialogConfigs;
import com.github.angads25.filepicker.model.DialogProperties;
import com.github.angads25.filepicker.view.FilePickerDialog;
import com.google.api.client.googleapis.extensions.android.gms.auth.GooglePlayServicesAvailabilityIOException;
import com.google.api.client.googleapis.extensions.android.gms.auth.UserRecoverableAuthIOException;
import com.google.api.client.http.FileContent;
import com.google.api.services.drive.model.File;
import com.project.ovi.liceenta.service.BaseActivity;
import com.project.ovi.liceenta.service.DriveServiceManager;
import com.project.ovi.liceenta.util.ProjectConstants;

import java.io.IOException;

/**
 * Created by Ovi on 22/08/16.
 */
public class UploadFileActivity extends BaseActivity {

    private Uri uri;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        DialogProperties properties = getDialogProperties();
        FilePickerDialog filePickerDialog = new FilePickerDialog(UploadFileActivity.this, properties);
        setDialogListener(filePickerDialog);
        filePickerDialog.show();

    }

    private void setDialogListener(FilePickerDialog filePickerDialog) {
        filePickerDialog.setDialogSelectionListener(new DialogSelectionListener() {
            @Override
            public void onSelectedFilePaths(String[] files) {
                String path = files[0];
                new UploadFileTask(UploadFileActivity.this, path).execute();
            }
        });
    }

    @NonNull
    private DialogProperties getDialogProperties() {
        DialogProperties properties=new DialogProperties();
        properties.selection_mode = DialogConfigs.SINGLE_MODE;
        properties.selection_type = DialogConfigs.FILE_SELECT;
        properties.root = new java.io.File(DialogConfigs.DEFAULT_DIR);
        properties.extensions = null;
        return properties;
    }

    /**
     * An asynchronous task that handles the Drive API call.
     * Placing the API calls in their own task ensures the UI stays responsive.
     */
    private class UploadFileTask extends AsyncTask<Void, Void, String> {

        private com.google.api.services.drive.Drive mService = null;
        private Exception mLastError = null;
        private ProgressDialog mProgress;
        private String filePath;


        public UploadFileTask(Context context, String filePath) {
            mProgress = new ProgressDialog(context);
            mProgress.setMessage("Upload file...");

            this.filePath = filePath;

            mService = DriveServiceManager.getInstance().getService();


        }

        /**
         * Background task to call Drive API.
         *
         * @param params no parameters needed for this task.
         */
        @Override
        protected String doInBackground(Void... params) {
            try {
                return uploadFile();
            } catch (Exception e) {
                mLastError = e;
                cancel(true);
                return null;
            }
        }

        private String uploadFile() throws IOException {


            java.io.File file = new java.io.File(filePath);

            File fileMetadata = new File();
            fileMetadata.setName(file.getName());
            fileMetadata.setMimeType("application/vnd.google-apps.photo");

            FileContent fileContent = new FileContent("image/jpeg", file);

            File driveFile = mService.files().create(fileMetadata, fileContent).setFields("id").execute();

            return driveFile.getId();
        }

        @Override
        protected void onPreExecute() {
            mProgress.show();
        }

        @Override
        protected void onPostExecute(String output) {
            mProgress.dismiss();
            if (output != null && output.length() > 0) {
                showToast(output);
            }
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
