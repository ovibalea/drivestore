package com.project.ovi.liceenta.service.sms;

import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.TextUtils;
import android.util.Log;

import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.googleapis.extensions.android.gms.auth.GooglePlayServicesAvailabilityIOException;
import com.google.api.client.googleapis.extensions.android.gms.auth.UserRecoverableAuthIOException;
import com.google.api.client.http.ByteArrayContent;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.model.File;
import com.google.api.services.drive.model.FileList;
import com.project.ovi.liceenta.MainActivity;
import com.project.ovi.liceenta.model.DriveItem;
import com.project.ovi.liceenta.service.BaseActivity;
import com.project.ovi.liceenta.util.ProjectConstants;

import android.provider.ContactsContract;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Created by Ovi on 02/08/16.
 */
public class SmsBackupActivity extends BaseActivity {

    private static String TAG = "SmsBackupActivity";


    private static final String MIME_FOLDER = "application/vnd.google-apps.folder";
    private static final String FOLDER_NAME = "backup";

    static final int REQUEST_AUTHORIZATION_FOLDER = 2;

    ProgressDialog mProgress;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }


    @Override
    public void launchProcessing() {
//        this.mProgress = ProgressDialog.show(this, "Fancy App",
//                "Loading...Please wait...", true, false);
        new BackupRequestTask().execute();
    }

    /*
     *
	 */
    private JSONObject readSmsInbox() throws JSONException {
        Cursor cursor = getSmsInboxCursor();
        // StringBuilder messages = new StringBuilder();
        JSONArray messages = new JSONArray();

        String and = "";

        int k = 0;

        if (cursor != null) {
            final String[] columns = cursor.getColumnNames();
            while (cursor.moveToNext() && k < 3) {
                k++;
                // messages.append(and);
                JSONObject msg = new JSONObject();
                // long id = cursor.getLong(SmsQuery._ID);
                long contactId = cursor.getLong(SmsQuery.PERSON);
                String address = cursor.getString(SmsQuery.ADDRESS);
                // long threadId = cursor.getLong(SmsQuery.THREAD_ID);
                // final long date = cursor.getLong(SmsQuery.DATE);

                final Map<String, String> msgMap = new HashMap<String, String>(
                        columns.length);

                for (int i = 0; i < 2; i++) {
                    String value = cursor.getString(i);
                    msgMap.put(columns[i], cursor.getString(i));
                    /*
                     * messages.append(columns[i]); messages.append("=");
					 * messages.append(value); messages.append(";;");
					 */
                    msg.put(columns[i], value);

                }
                // and = "\n";

                /**
                 * Retrieve display name
                 *
                 */
                String displayName = address;

                if (contactId > 0) {
                    // Retrieve from Contacts with contact id
                    Cursor contactCursor = tryOpenContactsCursorById(contactId);
                    if (contactCursor != null) {
                        if (contactCursor.moveToFirst()) {
                            displayName = contactCursor
                                    .getString(RawContactsQuery.DISPLAY_NAME);
                        } else {
                            contactId = 0;
                        }
                        contactCursor.close();
                    }
                } else {
                    if (contactId <= 0) {
                        // Retrieve with address
                        Cursor contactCursor = tryOpenContactsCursorByAddress(address);
                        if (contactCursor != null) {
                            if (contactCursor.moveToFirst()) {
                                displayName = contactCursor
                                        .getString(ContactsQuery.DISPLAY_NAME);
                            }
                            contactCursor.close();
                        }
                    }
                }
				/*
				 * messages.append("displayName"); messages.append("=");
				 * messages.append(displayName); messages.append(";;");
				 * messages.append("||");
				 */
                msg.put("displayName", displayName);

                messages.put(msg);

            }
        }

        JSONObject fileBackupTest = new JSONObject();
        fileBackupTest.put("messages", messages);
        return fileBackupTest;
    }

    /**
     * Retrieve sms
     *
     * @return
     */
    private Cursor getSmsInboxCursor() {

        try {
            return getContentResolver().query(
                    TelephonyProviderConstants.Sms.CONTENT_URI,
                    SmsQuery.PROJECTION, SmsQuery.WHERE_INBOX, null,
                    TelephonyProviderConstants.Sms.DEFAULT_SORT_ORDER);
        } catch (Exception e) {
            Log.e(TAG,
                    "Error accessing conversations cursor in SMS/MMS provider",
                    e);
            return null;
        }
    }

    private Cursor tryOpenContactsCursorById(long contactId) {
        try {
            return getContentResolver().query(
                    ContactsContract.RawContacts.CONTENT_URI.buildUpon()
                            .appendPath(Long.toString(contactId)).build(),
                    RawContactsQuery.PROJECTION, null, null, null);

        } catch (Exception e) {
            Log.e(TAG, "Error accessing contacts provider", e);
            return null;
        }
    }

    private Cursor tryOpenContactsCursorByAddress(String phoneNumber) {
        try {
            return getContentResolver().query(
                    ContactsContract.PhoneLookup.CONTENT_FILTER_URI.buildUpon()
                            .appendPath(Uri.encode(phoneNumber)).build(),
                    ContactsQuery.PROJECTION, null, null, null);

        } catch (Exception e) {
            // Can be called by the content provider
            // java.lang.IllegalArgumentException: URI:
            // content://com.android.contacts/phone_lookup/
            Log.w(TAG, "Error looking up contact name", e);
            return null;
        }
    }

    private interface SmsQuery {
        String[] PROJECTION = {
                TelephonyProviderConstants.Sms._ID,
                TelephonyProviderConstants.Sms.ADDRESS,
                TelephonyProviderConstants.Sms.PERSON,
                TelephonyProviderConstants.Sms.THREAD_ID,
                TelephonyProviderConstants.Sms.BODY,
                TelephonyProviderConstants.Sms.DATE,
                TelephonyProviderConstants.Sms.TYPE,
                TelephonyProviderConstants.Sms.READ,
                TelephonyProviderConstants.Sms.DATE_SENT,
                TelephonyProviderConstants.Sms.ERROR_CODE,
                TelephonyProviderConstants.Sms.STATUS,
                TelephonyProviderConstants.Sms.SUBJECT,
                TelephonyProviderConstants.Sms.PROTOCOL,
                TelephonyProviderConstants.Sms.SERVICE_CENTER

        };

        int _ID = 0;
        int ADDRESS = 1;
        int PERSON = 2;
        int THREAD_ID = 3;
        int BODY = 4;
        int DATE = 5;
        int TYPE = 6;
        int READ = 7;
        int DATE_SENT = 8;
        int ERROR_CODE = 9;
        int STATUS = 10;
        int SUBJECT = 11;
        int PROTOCOL = 12;
        int SERVICE_CENTER = 13;

        String WHERE_INBOX = TelephonyProviderConstants.Sms.TYPE + " = "
                + TelephonyProviderConstants.Sms.MESSAGE_TYPE_INBOX;

    }

    private interface RawContactsQuery {
        String[] PROJECTION = {ContactsContract.RawContacts.DISPLAY_NAME_PRIMARY,};

        int DISPLAY_NAME = 0;
    }

    private interface ContactsQuery {
        String[] PROJECTION = {ContactsContract.Contacts.DISPLAY_NAME,};

        int DISPLAY_NAME = 0;
    }

    /**
     * internal create Folder
     *
     * @return
     * @throws UserRecoverableAuthIOException
     * @throws IOException
     */
    private File _createFolder(Drive mService) throws UserRecoverableAuthIOException,
            IOException {



        File folder = _isFolderExists(mService);
        if (folder != null)
            return folder;

        File body = new File();
        body.setName(FOLDER_NAME);
        body.setMimeType(MIME_FOLDER);

        File file = mService.files().create(body).execute();
        return file;

    }

    /**
     * Verify if folder exists
     *
     * @return
     * @throws UserRecoverableAuthIOException
     * @throws IOException
     */
    private File _isFolderExists(Drive mService) throws UserRecoverableAuthIOException,
            IOException {


        FileList files = mService.files().list()
                .setQ("name='" + FOLDER_NAME + "'").execute();


        if (files != null) {
            for (File file : files.getFiles()) {
                return file;
            }
        }
        return null;
    }


    /**
     * An asynchronous task that handles the Drive API call.
     * Placing the API calls in their own task ensures the UI stays responsive.
     */
    private class BackupRequestTask extends AsyncTask<Void, Void, Void> {
        private com.google.api.services.drive.Drive mService = null;
        private Exception mLastError = null;
        private List<DriveItem> rootFolderItems;

        public BackupRequestTask() {

            mProgress = new ProgressDialog(getApplicationContext());
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
         *
         * @param params no parameters needed for this task.
         */
        @Override
        protected Void doInBackground(Void... params) {
            try {
               getDataFromApi();
            } catch (Exception e) {
                mLastError = e;
                cancel(true);
            }
            return null;
        }

        /**
         * Fetch a list of up to 10 file names and IDs.
         *
         * @return List of Strings describing files, or an empty list if no files
         * found.
         * @throws IOException
         */
        private void getDataFromApi() throws IOException {
            // Get a list of up to 10 files.
            try {
                JSONObject messages = readSmsInbox();
                Log.i(TAG, messages.toString());

                // Create sms backup Folder

                if (mService == null)
                    getResultsFromApi();


                File folder = createBackupFolder(mService);

                // File's metadata.
                String timeStamp = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss",
                        Locale.ROOT).format(new Date());

                File body = new File();
                body.setName("Sms Backup " + timeStamp);
                body.setMimeType("text/plain");
                body.setDescription("Backup sms");
                body.setParents(Arrays.asList(folder.getId()));


                ByteArrayContent content = ByteArrayContent.fromString(
                        "text/plain", messages.toString());

                File file = mService.files().create(body, content)
                        .execute();

                if (file != null) {
                    showToast("Sms backup uploaded: " + file.getName());
                }

            } catch (UserRecoverableAuthIOException e) {
                Intent intent = e.getIntent();
                startActivityForResult(intent, REQUEST_AUTHORIZATION_FOLDER);
            } catch (IOException e) {
                Log.e("TAG", "Error in backup", e);
            } catch (JSONException e) {
                Log.e("TAG", "Error in backup", e);
            }
        }

        private File createBackupFolder(Drive mService) {

            File folder = null;
            try {
                folder = _createFolder(mService);

            } catch (UserRecoverableAuthIOException e) {
                Intent intent = e.getIntent();
                startActivityForResult(intent, REQUEST_AUTHORIZATION_FOLDER);
            } catch (IOException e) {
                Log.e(TAG, "Error in create folder" + e.getMessage() + e.getCause(), e);
            }

            return folder;
        }


        @Override
        protected void onPreExecute() {
//           showToast("Request launched.");
//            mProgress.show();
            finish();
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


}
