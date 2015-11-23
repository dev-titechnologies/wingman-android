package app.wingman;


import android.*;
import android.Manifest;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.Application;
import android.content.ContentUris;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.provider.ContactsContract;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.View;

import com.quickblox.core.QBSettings;
import com.quickblox.users.model.QBUser;

import app.wingman.utils.PreferencesUtils;


public class ApplicationSingleton extends Application {
    private static final String TAG = ApplicationSingleton.class.getSimpleName();

    public static final String APP_ID = "30921";
    public static final String AUTH_KEY = "r9f7kQ-QmhbgbVF";
    public static final String AUTH_SECRET = "sP5phuGFE2sseyx";
    public static int USER_ID_QUICKBLOX = 0;
    public static String USER_DESCRIPTION_QUICKBLOX = "My status";
    public static String USER_IMAGE_PATH = "";
    public static String USER_STREET = "";

    public static final String DOWNLOAD_IMAGE_PATH = "/sdcard/flashCropped/";
    public static final String DOWNLOAD_IMAGE_PATH_External = "/sdcard/flashCropped/";
//    public static final String QB_APP_ID = "29673";
//    public static final String QB_AUTH_KEY = "Tt2Gh-kBe6bXKUL";
//    public static final String QB_AUTH_SECRET = "O9gbvOgTCsUrCgF";


    public static final String PREFS_NAME = "WingManPref";

    private static ApplicationSingleton instance;

    public static double LOCATION_ARRAY[];

    private static final String[] PHOTO_ID_PROJECTION = new String[]{ContactsContract.Contacts.PHOTO_ID};

    private static final String[] PHOTO_BITMAP_PROJECTION = new String[]{ContactsContract.CommonDataKinds.Photo.PHOTO};

    public static ApplicationSingleton getInstance() {
        return instance;
    }

    public static void saveChatUser(QBUser user,String token,Context context) {

        PreferencesUtils.saveData("qbuser",user.toString(),context);
        PreferencesUtils.saveData("qbusertoken",token,context);


    }

    @Override
    public void onCreate() {
        super.onCreate();

        Log.d(TAG, "onCreate");

        instance = this;


        // Initialise QuickBlox SDK
        //
        QBSettings.getInstance().fastConfigInit(APP_ID, AUTH_KEY, AUTH_SECRET);

    }

    public int getAppVersion() {
        try {
            PackageInfo packageInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
            return packageInfo.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            // should never happen
            throw new RuntimeException("Could not get package name: " + e);
        }
    }


    public static Integer fetchThumbnailId(Context context, String photoid) {

        final Uri uri = Uri.withAppendedPath(ContactsContract.CommonDataKinds.Phone.CONTENT_FILTER_URI, Uri.encode(photoid));
        final Cursor cursor = context.getContentResolver().query(uri, PHOTO_ID_PROJECTION, null, null, ContactsContract.Contacts.DISPLAY_NAME + " ASC");

        try {
            Integer thumbnailId = null;
            if (cursor.moveToFirst()) {

                thumbnailId = cursor.getInt(cursor.getColumnIndex(ContactsContract.Contacts.PHOTO_ID));
            }
            return thumbnailId;
        } finally {

            cursor.close();
        }

    }

    public static Bitmap fetchThumbnail(Context context, final int thumbnailId) {

        final Uri uri = ContentUris.withAppendedId(ContactsContract.Data.CONTENT_URI, thumbnailId);
        final Cursor cursor = context.getContentResolver().query(uri, PHOTO_BITMAP_PROJECTION, null, null, null);

        try {
            Bitmap thumbnail = null;
            if (cursor.moveToFirst()) {
                final byte[] thumbnailBytes = cursor.getBlob(0);
                if (thumbnailBytes != null) {
                    thumbnail = BitmapFactory.decodeByteArray(thumbnailBytes, 0, thumbnailBytes.length);
                    System.out.println("scale" + thumbnail.getHeight() + " " + thumbnail.getWidth());
                }
            }
            return thumbnail;
        } finally {
            cursor.close();
        }
    }

    public static void ShowAlert(Context context, String message) {

        AlertDialog.Builder dialog = new AlertDialog.Builder(context);
        dialog.setMessage(message).create().show();

    }


}













