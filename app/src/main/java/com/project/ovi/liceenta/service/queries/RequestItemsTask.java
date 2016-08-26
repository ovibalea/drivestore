package com.project.ovi.liceenta.service.queries;

/**
 * Created by Ovi on 25/08/16.
 */

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;

import com.google.api.client.googleapis.extensions.android.gms.auth.GooglePlayServicesAvailabilityIOException;
import com.google.api.client.googleapis.extensions.android.gms.auth.UserRecoverableAuthIOException;
import com.project.ovi.liceenta.model.DriveItem;
import com.project.ovi.liceenta.model.MessageItem;
import com.project.ovi.liceenta.service.BaseActivity;
import com.project.ovi.liceenta.util.DriveItemsProvider;
import com.project.ovi.liceenta.util.ProjectConstants;

import java.io.IOException;
import java.util.ArrayList;

/**
 * An asynchronous task that handles the Drive API call.
 * Placing the API calls in their own task ensures the UI stays responsive.
 */
public class RequestItemsTask extends AsyncTask<Void, Void, ArrayList<DriveItem>> {
    private BaseActivity activity;
    private Exception mLastError = null;
    private ProgressDialog mProgress;
    private String queryCondition;
    private String orderBy;

    public RequestItemsTask(BaseActivity activity, String queryCondition, String orderBy) {
        mProgress = new ProgressDialog(activity);
        mProgress.setMessage("Calling Drive API ...");
        this.activity = activity;
        this.queryCondition = queryCondition;
        this.orderBy = orderBy;
    }

    /**
     * Background task to call Drive API.
     *
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
     *
     * @return List of Strings describing files, or an empty list if no files
     * found.
     * @throws IOException
     */
    private ArrayList<DriveItem> getDataFromApi() throws IOException {
        // Get a list of up to 10 files.

        DriveItemsProvider itemsProvider = DriveItemsProvider.getInstance();
        ArrayList<DriveItem> folderItems = itemsProvider.createDriveItemsArray(queryCondition, orderBy);

        if (folderItems.size() == 0) {
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
            activity.showToast("No results returned.");
        } else {
            Intent intent = new Intent();
            intent.putExtra(ProjectConstants.VIEW_ADAPTER_ITEMS, output);
            activity.setResult(1, intent);
            activity.finish();
        }
    }

    @Override
    protected void onCancelled() {
        mProgress.hide();
        if (mLastError != null) {
            if (mLastError instanceof GooglePlayServicesAvailabilityIOException) {
                activity.showGooglePlayServicesAvailabilityErrorDialog(
                        ((GooglePlayServicesAvailabilityIOException) mLastError)
                                .getConnectionStatusCode());
            } else if (mLastError instanceof UserRecoverableAuthIOException) {
                activity.startActivityForResult(
                        ((UserRecoverableAuthIOException) mLastError).getIntent(),
                        ProjectConstants.REQUEST_AUTHORIZATION);
            } else {
                activity.showToast("The following error occurred:\n"
                        + mLastError.getMessage());
            }
        } else {
            activity.showToast("Request cancelled.");
        }
    }


}
