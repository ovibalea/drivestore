package com.project.ovi.liceenta.service.activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.Button;

import com.github.angads25.filepicker.controller.DialogSelectionListener;
import com.github.angads25.filepicker.model.DialogConfigs;
import com.github.angads25.filepicker.model.DialogProperties;
import com.github.angads25.filepicker.view.FilePickerDialog;
import com.google.api.client.googleapis.extensions.android.gms.auth.GooglePlayServicesAvailabilityIOException;
import com.google.api.client.googleapis.extensions.android.gms.auth.UserRecoverableAuthIOException;
import com.google.api.services.drive.Drive;
import com.project.ovi.liceenta.R;
import com.project.ovi.liceenta.model.DriveFile;
import com.project.ovi.liceenta.service.BaseActivity;
import com.project.ovi.liceenta.service.DriveServiceManager;
import com.project.ovi.liceenta.util.GoogleTypesUtil;
import com.project.ovi.liceenta.util.ProjectConstants;

import net.rdrei.android.dirchooser.DirectoryChooserFragment;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by Ovi on 12/08/16.
 */
public class DownloadItemActivity extends BaseActivity {

    private DirectoryChooserFragment choseFolderDialog;

    private DriveFile driveFile;

    private Button yesButton;

    private Button noButton;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.download_item_dialog);


        setButtonsActions();

        Intent requestIntent = getIntent();
        driveFile = (DriveFile) requestIntent.getSerializableExtra(ProjectConstants.DOWNLOAD_ITEM_ID_TAG);
    }

    private void setButtonsActions() {
        yesButton = (Button) findViewById(R.id.yesDownloadBtn);
        noButton = (Button) findViewById(R.id.noDownloadBtn);
        yesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showChooser();
            }
        });

        noButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void showChooser() {
        DialogProperties properties = getDialogProperties();
        FilePickerDialog filePickerDialog = new FilePickerDialog(DownloadItemActivity.this, properties);
        setDialogListener(filePickerDialog);
        filePickerDialog.show();
    }

    private void setDialogListener(FilePickerDialog filePickerDialog) {
        filePickerDialog.setDialogSelectionListener(new DialogSelectionListener() {
            @Override
            public void onSelectedFilePaths(String[] files) {
                String path = files[0];
                new DownloadItemTask(DownloadItemActivity.this, path, driveFile).execute();
            }
        });
    }

    @NonNull
    private DialogProperties getDialogProperties() {
        DialogProperties properties = new DialogProperties();
        properties.selection_mode = DialogConfigs.SINGLE_MODE;
        properties.selection_type = DialogConfigs.DIR_SELECT;
        properties.root = new java.io.File(DialogConfigs.DEFAULT_DIR);
        properties.extensions = null;
        return properties;
    }


    /**
     * An asynchronous task that handles the Drive API call.
     * Placing the API calls in their own task ensures the UI stays responsive.
     */
    private class DownloadItemTask extends AsyncTask<Void, Void, Boolean> {

        private Drive mService = null;
        private Exception mLastError = null;
        private ProgressDialog mProgress;
        private String destinationPath;
        private DriveFile driveFile;

        public DownloadItemTask(Context context, String destinationPath, DriveFile driveFile) {
            mProgress = new ProgressDialog(context);
            mProgress.setMessage("Downloading item...");
            this.destinationPath = destinationPath;
            this.driveFile = driveFile;

            mService = DriveServiceManager.getInstance().getService();
        }

        /**
         * Background task to call Drive API.
         *
         * @param params no parameters needed for this task.
         */
        @Override
        protected Boolean doInBackground(Void... params) {
            try {
                return downloadFile();
            } catch (Exception e) {
                mLastError = e;
                cancel(true);
                return null;
            }
        }

        private boolean downloadFile() throws IOException {


            java.io.File file = null;
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            InputStream inputStream = null;


            try {

                if (!GoogleTypesUtil.isGoogleType(driveFile.getMimeType())) {
                    mService.files().get(driveFile.getId()).executeMediaAndDownloadTo(outputStream);
                } else {
                    mService.files().export(driveFile.getId(), "application/pdf").executeMediaAndDownloadTo(outputStream);
                }

                inputStream = new ByteArrayInputStream(outputStream.toByteArray());

                file = new java.io.File(destinationPath, driveFile.getName() + "." + driveFile.getExtension());
                file.createNewFile();
                showToast("File " + driveFile.getName() + " was downloaded to " + destinationPath);
                storeFile(file, inputStream);

            } catch (Exception e) {

                showToast("File could not be downloaded.");
                e.printStackTrace();
            } finally {
                inputStream.close();
                outputStream.close();
            }

            return file != null;
        }

        private void storeFile(java.io.File file, InputStream iStream) {
            try {
                final OutputStream oStream = new FileOutputStream(file);
                try {
                    try {
                        final byte[] buffer = new byte[1024];
                        int read;
                        while ((read = iStream.read(buffer)) != -1) {
                            oStream.write(buffer, 0, read);
                        }
                        oStream.flush();
                    } finally {
                        oStream.close();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
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
