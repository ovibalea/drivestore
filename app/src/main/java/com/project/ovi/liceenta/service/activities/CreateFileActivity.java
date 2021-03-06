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
import com.google.api.client.http.ByteArrayContent;
import com.google.api.services.drive.model.File;
import com.project.ovi.liceenta.R;
import com.project.ovi.liceenta.service.BaseActivity;
import com.project.ovi.liceenta.service.DriveServiceManager;
import com.project.ovi.liceenta.util.ProjectConstants;

import java.io.IOException;
import java.util.Arrays;

/**
 * Created by Ovi on 21/07/16.
 */
public class CreateFileActivity extends BaseActivity {

    private Button cancelBtn;

    private Button saveBtn;

    private EditText contentEditor;

    private EditText titleEditor;

    private String folderId;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.file_editor_layout);

        setEditors();
        setButtonsActions();

        folderId = getIntent().getStringExtra(ProjectConstants.PARENT_FOLDER_ID_TAG);
    }

    private void setEditors(){
        contentEditor = (EditText) findViewById(R.id.editTextContent);
        titleEditor = (EditText) findViewById(R.id.editTextTitle);
    }

    private void setButtonsActions(){
        cancelBtn = (Button) findViewById(R.id.cancelBtn);
        saveBtn = (Button) findViewById(R.id.saveBtn);

        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finalizeActionWithResult(false);
            }
        });

        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new CreateFileTask(CreateFileActivity.this).execute();
            }
        });
    }

    private void finalizeActionWithResult(boolean result) {
        Intent intent = new Intent();
        intent.putExtra(ProjectConstants.IS_ITEM_PROCESSED,result);
        setResult(ProjectConstants.REQUEST_PROCESS_ITEM, intent);
        finish();
    }

    @Override
    public void onBackPressed() {
        finalizeActionWithResult(false);
    }

    /**
     * An asynchronous task that handles the Drive API call.
     * Placing the API calls in their own task ensures the UI stays responsive.
     */
    private class CreateFileTask extends AsyncTask<Void, Void, Boolean> {

        private com.google.api.services.drive.Drive mService = null;
        private Exception mLastError = null;
        private ProgressDialog mProgress;

        public CreateFileTask(Context context) {
            mProgress = new ProgressDialog(context);
            mProgress.setMessage("Creating file...");

            mService = DriveServiceManager.getInstance().getService();
        }

        /**
         * Background task to call Drive API.
         * @param params no parameters needed for this task.
         */
        @Override
        protected Boolean doInBackground(Void... params) {
            try {
                return createFileOnFolder();
            } catch (Exception e) {
                mLastError = e;
                cancel(true);
                return null;
            }
        }

        private boolean createFileOnFolder() throws IOException {

            String fileName = getFileName();
            String fileContent = contentEditor.getText().toString();

            File body = new File();
            body.setName(fileName);
            body.setMimeType("application/vnd.google-apps.file");
            body.setParents(Arrays.asList(folderId));

            ByteArrayContent content = ByteArrayContent.fromString(
                    "text/plain", fileContent);

            File file = mService.files().create(body, content)
                    .execute();

            return file != null;
        }

        private String getFileName(){
            String name = titleEditor.getText().toString();
            String fileName = (name != null && !name.isEmpty()) ? name : "New file";
            return fileName;
        }


        @Override
        protected void onPreExecute() {
            mProgress.show();
        }

        @Override
        protected void onPostExecute(Boolean output) {
            mProgress.dismiss();
            if (output == true) {
                showToast("File created successfully!");
            } else {
                showToast("File could not be created!");
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
