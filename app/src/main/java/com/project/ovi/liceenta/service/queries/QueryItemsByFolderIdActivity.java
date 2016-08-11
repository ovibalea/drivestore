package com.project.ovi.liceenta.service.queries;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;

import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.extensions.android.gms.auth.GooglePlayServicesAvailabilityIOException;
import com.google.api.client.googleapis.extensions.android.gms.auth.UserRecoverableAuthIOException;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.drive.model.File;
import com.google.api.services.drive.model.FileList;
import com.project.ovi.liceenta.view.DriveItemsViewAdapter;
import com.project.ovi.liceenta.MainActivity;
import com.project.ovi.liceenta.model.DriveItem;
import com.project.ovi.liceenta.service.BaseActivity;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Ovi on 05/08/16.
 */
public class QueryItemsByFolderIdActivity extends BaseActivity {

    private static String TAG = "SmsBackupActivity";

    public static final String FOLDER_ID = "folderId";

    public static final String VIEW_ADAPTER_ITEMS = "driveItemsViewAdapter";

    private DriveItemsViewAdapter driveItemsViewAdapter;

    private String folderId;

    private ProgressDialog mProgress;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public void launchProcessing() {
            mProgress = new ProgressDialog(this);
            mProgress.setMessage("Calling Drive API ...");

            folderId = getIntent().getStringExtra(FOLDER_ID);
            new RequestItemsTask(this).execute();
    }

    /**
     * An asynchronous task that handles the Drive API call.
     * Placing the API calls in their own task ensures the UI stays responsive.
     */
    private class RequestItemsTask extends AsyncTask<Void, Void, ArrayList<DriveItem>> {
        private com.google.api.services.drive.Drive mService = null;
        private Exception mLastError = null;
        private ArrayList<DriveItem> rootFolderItems;

        public RequestItemsTask(Context context) {

            mProgress = new ProgressDialog(context);
            mProgress.setMessage("Calling Drive API ...");

            HttpTransport transport = AndroidHttp.newCompatibleTransport();
            JsonFactory jsonFactory = JacksonFactory.getDefaultInstance();
            mService = new com.google.api.services.drive.Drive.Builder(
                    transport, jsonFactory, getCredential())
                    .setApplicationName("Drive API Android Quickstart")
                    .build();
        }

        /**
         * Background task to call Drive API.
         * @param params no parameters needed for this task.
         */
        @Override
        protected ArrayList<DriveItem> doInBackground(Void... params) {
            try {
                return getDataFromApi();
            } catch (Exception e) {
                mLastError = e;
                cancel(true);
                return null;
            }
        }

        /**
         * Fetch a list of up to 10 file names and IDs.
         * @return List of Strings describing files, or an empty list if no files
         *         found.
         * @throws IOException
         */
        private ArrayList<DriveItem> getDataFromApi() throws IOException {
            // Get a list of up to 10 files.
            List<String> fileInfo = new ArrayList<String>();
            rootFolderItems = new ArrayList<DriveItem>();
            FileList result = mService.files().list()
//                    .setPageSize(10)
                    .setFields("nextPageToken, files(id, name, fullFileExtension, trashed)")
                    .setQ("'" + folderId + "' in parents and trashed != true")
                    .execute();
            List<File> files = result.getFiles();
            if (files != null) {
                for (File file : files) {

                    rootFolderItems.add(new DriveItem(file));
                    Log.i("Main", file.toString() + file.getFullFileExtension());
                    fileInfo.add(String.format("%s %s %s \n",
                            file.getName(), file.getId(), file.getFullFileExtension()));
                }
            }
            if(rootFolderItems.size() == 0) {
                rootFolderItems.add(new DriveItem("No items!"));
            }
            return rootFolderItems;
        }


        @Override
        protected void onPreExecute() {
//            mOutputText.setText("");
            mProgress.show();

        }

        @Override
        protected void onPostExecute(ArrayList<DriveItem> output) {
            mProgress.hide();
            if (output == null || output.size() == 0) {
                showToast("No results returned.");
            } else {
                Intent intent=new Intent();
                intent.putExtra(VIEW_ADAPTER_ITEMS,output);
                setResult(1, intent);
                finish();
            }
        }

        @Override
        protected void onCancelled() {
            mProgress.hide();
            if (mLastError != null) {
                if (mLastError instanceof GooglePlayServicesAvailabilityIOException) {
                    showGooglePlayServicesAvailabilityErrorDialog(
                            ((GooglePlayServicesAvailabilityIOException) mLastError)
                                    .getConnectionStatusCode());
                } else if (mLastError instanceof UserRecoverableAuthIOException) {
                    startActivityForResult(
                            ((UserRecoverableAuthIOException) mLastError).getIntent(),
                            MainActivity.REQUEST_AUTHORIZATION);
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
