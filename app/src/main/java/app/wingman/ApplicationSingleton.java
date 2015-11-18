package app.wingman;

import android.app.Application;
import android.content.ContentUris;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.ContactsContract;
import android.util.Log;

import com.quickblox.core.QBSettings;


public class ApplicationSingleton extends Application {
    private static final String TAG = ApplicationSingleton.class.getSimpleName();

    public static final String APP_ID = "30921";
    public static final String AUTH_KEY = "r9f7kQ-QmhbgbVF";
    public static final String AUTH_SECRET = "sP5phuGFE2sseyx";

    public static final String DOWNLOAD_IMAGE_PATH="/sdcard/flashCropped/";
    public static final String DOWNLOAD_IMAGE_PATH_External="/sdcard/flashCropped/";
//    public static final String QB_APP_ID = "29673";
//    public static final String QB_AUTH_KEY = "Tt2Gh-kBe6bXKUL";
//    public static final String QB_AUTH_SECRET = "O9gbvOgTCsUrCgF";



    public static final String PREFS_NAME = "WingManPref";

    private static ApplicationSingleton instance;

    private static final String[] PHOTO_ID_PROJECTION = new String[] { ContactsContract.Contacts.PHOTO_ID };

    private static final String[] PHOTO_BITMAP_PROJECTION = new String[] { ContactsContract.CommonDataKinds.Photo.PHOTO };
    public static ApplicationSingleton getInstance() {
        return instance;
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



    public static Integer fetchThumbnailId(Context context,String photoid) {

        final Uri uri = Uri.withAppendedPath(ContactsContract.CommonDataKinds.Phone.CONTENT_FILTER_URI, Uri.encode(photoid));
        final Cursor cursor =context.getContentResolver().query(uri, PHOTO_ID_PROJECTION, null, null, ContactsContract.Contacts.DISPLAY_NAME + " ASC");

        try {
            Integer thumbnailId = null;
            if (cursor.moveToFirst()) {

                thumbnailId = cursor.getInt(cursor.getColumnIndex(ContactsContract.Contacts.PHOTO_ID));
            }
            return thumbnailId;
        }
        finally {

            cursor.close();
        }

    }

    public static Bitmap fetchThumbnail(Context context,final int thumbnailId) {

        final Uri uri = ContentUris.withAppendedId(ContactsContract.Data.CONTENT_URI, thumbnailId);
        final Cursor cursor = context.getContentResolver().query(uri, PHOTO_BITMAP_PROJECTION, null, null, null);

        try {
            Bitmap thumbnail = null;
            if (cursor.moveToFirst()) {
                final byte[] thumbnailBytes = cursor.getBlob(0);
                if (thumbnailBytes != null) {
                    thumbnail = BitmapFactory.decodeByteArray(thumbnailBytes, 0, thumbnailBytes.length);
                    System.out.println("scale"+thumbnail.getHeight()+" "+thumbnail.getWidth());
                }
            }
            return thumbnail;
        }
        finally {
            cursor.close();
        }

    }












}
