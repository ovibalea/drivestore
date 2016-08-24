package com.project.ovi.liceenta.service.queries;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.Toast;

import com.google.android.gms.common.GoogleApiAvailability;
import com.google.api.client.googleapis.extensions.android.gms.auth.GooglePlayServicesAvailabilityIOException;
import com.google.api.client.googleapis.extensions.android.gms.auth.UserRecoverableAuthIOException;
import com.google.api.services.drive.model.FileList;
import com.project.ovi.liceenta.model.DriveItem;
import com.project.ovi.liceenta.model.MessageItem;
import com.project.ovi.liceenta.service.DriveServiceManager;
import com.project.ovi.liceenta.util.ItemsWrapperFactory;
import com.project.ovi.liceenta.util.ProjectConstants;
import com.project.ovi.liceenta.view.main.DriveItemsViewAdapter;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by Ovi on 05/08/16.
 */
public class QueryItemsByFolderIdActivity extends Activity {

    private static String TAG = "QueryItemsByFolder";

    public static final int REQUEST_GOOGLE_PLAY_SERVICES = 1002;

    public static final String FOLDER_ID = "folderId";

    public static final String VIEW_ADAPTER_ITEMS = "driveItemsViewAdapter";

    private DriveItemsViewAdapter driveItemsViewAdapter;

    private String folderId;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        launchProcessing();
    }

    public void launchProcessing() {

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
        private ProgressDialog mProgress;

        public RequestItemsTask(Context context) {

            mProgress = new ProgressDialog(context);
            mProgress.setMessage("Calling Drive API ...");

            mService = DriveServiceManager.getInstance().getService();
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
            FileList result = mService.files().list()
                    .setFields("nextPageToken, files(" + ProjectConstants.ITEM_FIELDS + ")")
                    .setQ("'" + folderId + "' in parents and trashed != true")
                    .execute();

            ArrayList<DriveItem> folderItems = ItemsWrapperFactory.createDriveItemsArray(result, mService);

            if(folderItems.size() == 0) {
                folderItems.add(new MessageItem());
            }
            return folderItems;
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

    public void showToast(final String toast) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getApplicationContext(), toast,
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * Display an error dialog showing that Google Play Services is missing
     * or out of date.
     * @param connectionStatusCode code describing the presence (or lack of)
     *     Google Play Services on this device.
     */
    public void showGooglePlayServicesAvailabilityErrorDialog(
            final int connectionStatusCode) {
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        Dialog dialog = apiAvailability.getErrorDialog(
                this,
                connectionStatusCode,
                REQUEST_GOOGLE_PLAY_SERVICES);
        dialog.show();
    }
}
