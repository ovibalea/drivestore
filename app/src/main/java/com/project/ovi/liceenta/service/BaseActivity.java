package com.project.ovi.liceenta.service;

import android.app.Activity;
import android.app.Dialog;
import android.widget.Toast;

import com.google.android.gms.common.GoogleApiAvailability;

/**
 * Created by Ovi on 02/08/16.
 */
public class BaseActivity extends Activity {

    public static final int REQUEST_AUTHORIZATION = 1001;

    public static final int REQUEST_GOOGLE_PLAY_SERVICES = 1002;

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

    public void showToast(final String toast) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getApplicationContext(), toast,
                        Toast.LENGTH_SHORT).show();
            }
        });
    }
}
