package com.project.ovi.liceenta.service.activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.google.api.client.googleapis.extensions.android.gms.auth.GooglePlayServicesAvailabilityIOException;
import com.google.api.client.googleapis.extensions.android.gms.auth.UserRecoverableAuthIOException;
import com.google.api.services.drive.Drive;
import com.project.ovi.liceenta.R;
import com.project.ovi.liceenta.service.BaseActivity;
import com.project.ovi.liceenta.service.DriveServiceManager;
import com.project.ovi.liceenta.util.ProjectConstants;

import java.io.IOException;

/**
 * Created by Ovi on 13/08/16.
 */
public class DeleteItemActivity extends BaseActivity {

    private Button yesButton;

    private Button noButton;

    private String itemId;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.delete_item_dialog);

        setButtonsActions();

        itemId = getIntent().getStringExtra(ProjectConstants.DELETE_ITEM_ID_TAG);

    }

    private void setButtonsActions(){
        yesButton = (Button) findViewById(R.id.yesDeleteItemBtn);
        noButton = (Button) findViewById(R.id.noDeleteItemBtn);
        yesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DeleteItemTask(DeleteItemActivity.this, itemId).execute();
            }
        });

        noButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    /**
     * An asynchronous task that handles the Drive API call.
     * Placing the API calls in their own task ensures the UI stays responsive.
     */
    private class DeleteItemTask extends AsyncTask<Void, Void, Boolean> {

        private Drive mService = null;
        private Exception mLastError = null;
        private ProgressDialog mProgress;
        private String itemId;

        public DeleteItemTask(Context context, String itemId) {
            mProgress = new ProgressDialog(context);
            mProgress.setMessage("Delete item...");
            this.itemId = itemId;
            mService = DriveServiceManager.getInstance().getService();
        }

        /**
         * Background task to call Drive API.
         * @param params no parameters needed for this task.
         */
        @Override
        protected Boolean doInBackground(Void... params) {
            try {
                return deleteItem();
            } catch (Exception e) {
                mLastError = e;
                cancel(true);
            }
            return false;
        }

        private boolean deleteItem() throws IOException {

            mService.files().delete(itemId).execute();
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
                showToast("Item deleted successfully!");
            } else {
                showToast("Item could not be deleted!");
            }

            Intent intent = new Intent();
            intent.putExtra(ProjectConstants.IS_ITEM_PROCESSED,output);
            setResult(ProjectConstants.REQUEST_PROCESS_ITEM, intent);
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
