package com.project.ovi.liceenta.service.activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;

import com.google.api.client.googleapis.extensions.android.gms.auth.GooglePlayServicesAvailabilityIOException;
import com.google.api.client.googleapis.extensions.android.gms.auth.UserRecoverableAuthIOException;
import com.google.api.services.drive.model.File;
import com.project.ovi.liceenta.service.BaseActivity;
import com.project.ovi.liceenta.service.DriveServiceManager;
import com.project.ovi.liceenta.util.ProjectConstants;

import java.io.IOException;

/**
 * Created by Ovi on 15/08/16.
 */
public class BookmarkItemActivity extends BaseActivity {

    private String itemId;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        itemId = getIntent().getStringExtra(ProjectConstants.ITEM_ID_TAG);
        new BookmarkItemTask(BookmarkItemActivity.this, itemId).execute();
    }

    /**
     * An asynchronous task that handles the Drive API call.
     * Placing the API calls in their own task ensures the UI stays responsive.
     */
    private class BookmarkItemTask extends AsyncTask<Void, Void, Boolean> {

        private com.google.api.services.drive.Drive mService = null;
        private Exception mLastError = null;
        private ProgressDialog mProgress;
        private String itemId;

        public BookmarkItemTask(Context context, String id) {
            mProgress = new ProgressDialog(context);
            mProgress.setMessage("Bookmark item...");
            itemId = id;

            mService = DriveServiceManager.getInstance().getService();
        }

        /**
         * Background task to call Drive API.
         * @param params no parameters needed for this task.
         */
        @Override
        protected Boolean doInBackground(Void... params) {
            try {
                return bookmarkItem();
            } catch (Exception e) {
                mLastError = e;
                cancel(true);
                return null;
            }
        }

        private boolean bookmarkItem() throws IOException {

            File item = mService.files().get(itemId).setFields("starred").execute();
            File newFile = new File();
            boolean isStared = item.getStarred();
            newFile.setStarred(!isStared);
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
                showToast("File bookmarked successfully!");
            } else {
                showToast("File could not be bookmarked!");
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
