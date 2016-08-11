package com.project.ovi.liceenta.service.create;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.extensions.android.gms.auth.GooglePlayServicesAvailabilityIOException;
import com.google.api.client.googleapis.extensions.android.gms.auth.UserRecoverableAuthIOException;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.model.File;
import com.project.ovi.liceenta.R;
import com.project.ovi.liceenta.service.BaseActivity;
import com.project.ovi.liceenta.util.ProjectConstants;

import java.io.IOException;
import java.util.Arrays;

/**
 * Created by Ovi on 11/08/16.
 */
public class CreateFolderActivity extends BaseActivity {


    private String parentFolderId;

    private Button createButton;

    private Button cancelButton;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.create_folder_dialog);

        createButton = (Button) findViewById(R.id.createFolderBtn);
        cancelButton = (Button) findViewById(R.id.cancelCreateFolderBtn);
        setButtonsActions();

    }

    private void setButtonsActions() {


        createButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String folderName = getFolderName();
                new CreateFolderTask(CreateFolderActivity.this, parentFolderId, folderName).execute();
            }
        });

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finalizeActionWithResult(false);
            }
        });
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


    private String getFolderName(){
        EditText nameEditText = (EditText) findViewById(R.id.editTextFolderTitle);
        String editFolderName = nameEditText.getText().toString();
        String folderName = (editFolderName != null && !editFolderName.isEmpty()) ? editFolderName : "New folder";
        return folderName;
    }

    @Override
    public void launchProcessing() {

        parentFolderId = getIntent().getStringExtra(ProjectConstants.PARENT_FOLDER_ID_TAG);

    }

    /**
     * An asynchronous task that handles the Drive API call.
     * Placing the API calls in their own task ensures the UI stays responsive.
     */
    private class CreateFolderTask extends AsyncTask<Void, Void, Boolean> {

        private ProgressDialog mProgress;

        private Drive mService = null;

        private Exception mLastError = null;

        private String parentFolderId;

        private String folderName;

        public CreateFolderTask(Context context, String parentFolderId, String folderName) {
            mProgress = new ProgressDialog(context);
            mProgress.setMessage("Creating folder...");

            this.parentFolderId = parentFolderId;
            this.folderName = folderName;

            HttpTransport transport = AndroidHttp.newCompatibleTransport();
            JsonFactory jsonFactory = JacksonFactory.getDefaultInstance();
            mService = new Drive.Builder(
                    transport, jsonFactory, getCredential())
                    .setApplicationName("Drive API Android Quickstart")
                    .build();
        }

        /**
         * Background task to call Drive API.
         * @param params no parameters needed for this task.
         */
        @Override
        protected Boolean doInBackground(Void... params) {
            try {
                return createFolderOnFolder();
            } catch (Exception e) {
                mLastError = e;
                cancel(true);
                return null;
            }
        }

        private boolean createFolderOnFolder() throws IOException {

            File folderObject = new File();
            folderObject.setName(folderName);
            folderObject.setMimeType(ProjectConstants.MIME_FOLDER);
            folderObject.setParents(Arrays.asList(parentFolderId));

            File folder = mService.files().create(folderObject)
                    .execute();

            return folder != null;
        }




        @Override
        protected void onPreExecute() {
            mProgress.show();
        }

        @Override
        protected void onPostExecute(Boolean output) {
            mProgress.dismiss();
            if (output == true) {
                showToast("Folder created successfully!");
            } else {
                showToast("Folder could not be created!");
            }

            finalizeActionWithResult(output);
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
