package com.project.ovi.liceenta;

import android.content.Context;
import android.content.IntentSender;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.drive.Drive;

/**
 * Created by Ovi on 30/07/16.
 */
public class DriveConnectionService implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {

    private static final String TAG = "DriveConnectionService";

    /**
     * Google API client.
     */
    private GoogleApiClient mGoogleApiClient;


    public DriveConnectionService(Context context) {
        this.mGoogleApiClient =  new GoogleApiClient.Builder(context)
                    .addApi(Drive.API)
                    .addScope(Drive.SCOPE_FILE)
                    .addScope(Drive.SCOPE_APPFOLDER)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .build();
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnected(Bundle bundle) {
        Log.i(TAG, "GoogleApiClient connected");
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.i(TAG, "GoogleApiClient connection suspended");
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.i(TAG, "GoogleApiClient connection failed: " + connectionResult.toString());
        if (!connectionResult.hasResolution()) {
////             show the localized error dialog.
//            GoogleApiAvailability.getInstance().getErrorDialog(this, connectionResult.getErrorCode(), 0).show();
//            Log.i(connectionResult.getErrorCode());
//            return;
        }
//        try {
////            result.startResolutionForResult(this, REQUEST_CODE_RESOLUTION);
////            connectionResult.startResolutionForResult(this, connectionResult);
//        } catch (IntentSender.SendIntentException e) {
//            Log.e(TAG, "Exception while starting resolution activity", e);
//        }
    }
}
