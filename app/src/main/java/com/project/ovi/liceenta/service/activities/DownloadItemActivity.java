package com.project.ovi.liceenta.service.activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.google.api.client.googleapis.extensions.android.gms.auth.GooglePlayServicesAvailabilityIOException;
import com.google.api.client.googleapis.extensions.android.gms.auth.UserRecoverableAuthIOException;
import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpResponse;
import com.google.api.services.drive.Drive;
import com.project.ovi.liceenta.R;
import com.project.ovi.liceenta.model.DriveFile;
import com.project.ovi.liceenta.service.BaseActivity;
import com.project.ovi.liceenta.service.DriveServiceManager;
import com.project.ovi.liceenta.util.ProjectConstants;

import net.rdrei.android.dirchooser.DirectoryChooserConfig;
import net.rdrei.android.dirchooser.DirectoryChooserFragment;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by Ovi on 12/08/16.
 */
public class DownloadItemActivity extends BaseActivity implements DirectoryChooserFragment.OnFragmentInteractionListener {

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

    private void setButtonsActions(){
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
        final DirectoryChooserConfig config = DirectoryChooserConfig.builder()
                .newDirectoryName("DialogSample")
                .build();
        choseFolderDialog = DirectoryChooserFragment.newInstance(config);
        choseFolderDialog.show(getFragmentManager(), null);
    }

    @Override
    public void onSelectDirectory(String path) {
        new DownloadItemTask(DownloadItemActivity.this, path, driveFile).execute();
    }

    @Override
    public void onCancelChooser() {
        choseFolderDialog.dismiss();
        finish();
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

            com.google.api.services.drive.model.File dFile = mService.files().get(driveFile.getId()).setFields("id, name, fullFileExtension, trashed, createdTime, size, mimeType, webContentLink").execute();



            java.io.File file = null;

            if (dFile.getWebContentLink() != null && dFile.getWebContentLink().length() >0 ) {


                try {

                    String downloadUrl = "https://www.googleapis.com/drive/v3/files/" + dFile.getId() + "?alt=media";
                    Log.i("URL:", downloadUrl);
                    HttpRequest request = mService.getRequestFactory()
                            .buildGetRequest(new GenericUrl(downloadUrl));
                    HttpResponse resp = request.execute();

                    InputStream iStream = resp.getContent();
                    try {
                        file = new java.io.File(destinationPath, driveFile.getName());
                        Log.i("DEST PATH", file.getAbsolutePath());
                        file.createNewFile();
                        showToast("Downloading: " + driveFile.getName() + " to " + destinationPath);
                        storeFile(file, iStream);
                    } finally {
                        iStream.close();
                    }

                } catch (Exception e) {

                    showToast("Downloading Error: " + e.getMessage());
                    e.printStackTrace();
                }


            }
//            } else {
//

//                ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
//                InputStream inputStream =  mService.files().export(driveFile.getId(), dFile.getMimeType()).executeAsInputStream();
//
//                file = new java.io.File(destinationPath, driveFile.getName());
//                file.createNewFile();
//
//                storeFile(file, inputStream);
//            }

//            downloadGFileToJFolder(mService, );

            return file != null;
        }

        private void storeFile(java.io.File file, InputStream iStream)
        {
            try
            {
                final OutputStream oStream = new FileOutputStream(file);
                try
                {
                    try
                    {
                        final byte[] buffer = new byte[1024];
                        int read;
                        while ((read = iStream.read(buffer)) != -1)
                        {
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