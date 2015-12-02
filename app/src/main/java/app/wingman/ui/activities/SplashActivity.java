package app.wingman.ui.activities;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.telephony.TelephonyManager;
import android.view.View;
import android.widget.Toast;

import com.quickblox.core.QBEntityCallbackImpl;
import com.quickblox.users.model.QBUser;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import app.wingman.ApplicationSingleton;
import app.wingman.R;
import app.wingman.core.ChatService;
import app.wingman.database.CommentsDataSource;
import app.wingman.interfaces.ContactsQuery;
import app.wingman.models.modelclass;
import app.wingman.networks.Connecttoget;
import app.wingman.settings.Urls;
import app.wingman.utils.GetMyLocation;
import app.wingman.utils.PreferencesUtils;

public class SplashActivity extends app.wingman.ui.activities.BaseActivity implements LoaderManager.LoaderCallbacks<Cursor> {


    private static final int REQUEST_ACCESS_COARSE_LOCATION= 0;
    private static final int REQUEST_ACCESS_FINE_LOCATION= 1;


    // for database
    public static ArrayList<String> phones = new ArrayList<String>();
    public static ArrayList<QBUser> userslist=new ArrayList<QBUser>();
    CommentsDataSource obj;
    ArrayList<modelclass> userList = new ArrayList<modelclass>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
         obj=new CommentsDataSource(SplashActivity.this);
        obj.open();
        if(getFinelOcationPermissionRequest()){
            if(getLOcationPermissionRequest()){

                GetMyLocation obj = new GetMyLocation(SplashActivity.this);
                obj.getMyLocation();
            }else
                ApplicationSingleton.ShowWarningAlert(SplashActivity.this,"Sorry please turn location permission in your device for wingman");

        }else
            ApplicationSingleton.ShowWarningAlert(SplashActivity.this,"Sorry please turn location permission in your device for wingman");


        getSupportLoaderManager().initLoader(1, null, SplashActivity.this);




    }
    /**
     * permission for location
     */
    private boolean getLOcationPermissionRequest() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return true;
        }
        if (checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED

                ) {
            return true;
        }
        if (shouldShowRequestPermissionRationale(android.Manifest.permission.ACCESS_COARSE_LOCATION)) {
            Snackbar.make(findViewById(R.id.splash_bg), R.string.permission_rationale, Snackbar.LENGTH_INDEFINITE)
                    .setAction(android.R.string.ok, new View.OnClickListener() {
                        @Override
                        @TargetApi(Build.VERSION_CODES.M)
                        public void onClick(View v) {
                            requestPermissions(new String[]{android.Manifest.permission.ACCESS_COARSE_LOCATION}, REQUEST_ACCESS_COARSE_LOCATION);
                        }
                    });
        } else {
            requestPermissions(new String[]{android.Manifest.permission.ACCESS_COARSE_LOCATION}, REQUEST_ACCESS_COARSE_LOCATION);
        }
        return false;
    }
    private boolean getFinelOcationPermissionRequest() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return true;
        }
        if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED

                ) {
            return true;
        }
        if (shouldShowRequestPermissionRationale(android.Manifest.permission.ACCESS_FINE_LOCATION)) {
            Snackbar.make(findViewById(R.id.splash_bg), R.string.permission_rationale, Snackbar.LENGTH_INDEFINITE)
                    .setAction(android.R.string.ok, new View.OnClickListener() {
                        @Override
                        @TargetApi(Build.VERSION_CODES.M)
                        public void onClick(View v) {
                            requestPermissions(new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_ACCESS_FINE_LOCATION);
                        }
                    });
        } else {
            requestPermissions(new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_ACCESS_FINE_LOCATION);
        }
        return false;
    }

    /**
    get all users
      */
    public class RetrieveUsers extends AsyncTask<String,String,Boolean>{



        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }

        @Override
        protected Boolean doInBackground(String... strings) {

            try {
                ApplicationSingleton.apiresult = Connecttoget.callJson(Urls.GET_USERS);

               if( new JSONObject(ApplicationSingleton.apiresult).getInt("status")==1) {
                   ApplicationSingleton.apiresultJSON = new JSONObject(ApplicationSingleton.apiresult).getJSONArray("userList");
                   int size = ApplicationSingleton.apiresultJSON.length();
                   for (int i = 0; i < size; i++) {

                       if (phones.contains(ApplicationSingleton.apiresultJSON.getJSONObject(i).getString("mobile_no"))) {
                           modelclass obj = new modelclass();
                           obj.setUserName(ApplicationSingleton.apiresultJSON.getJSONObject(i).getString("name"));
                           obj.setUserEmail(ApplicationSingleton.apiresultJSON.getJSONObject(i).getString("email"));
                           obj.setUserId(ApplicationSingleton.apiresultJSON.getJSONObject(i).getString("chat_id"));
                           obj.setGender(ApplicationSingleton.apiresultJSON.getJSONObject(i).getString("gender"));

                           JSONObject OBJ=new JSONObject();
                           OBJ.put("profile_pic",ApplicationSingleton.apiresultJSON.getJSONObject(i).getString("profile_pic"));
                           OBJ.put("latitude",ApplicationSingleton.apiresultJSON.getJSONObject(i).getString("latitude"));
                           OBJ.put("longitude",ApplicationSingleton.apiresultJSON.getJSONObject(i).getString("longitude"));
                           OBJ.put("user_info",ApplicationSingleton.apiresultJSON.getJSONObject(i).getString("user_info"));


                               obj.setUserCustomData(OBJ.toString());
                           obj.setUserPhone(ApplicationSingleton.apiresultJSON.getJSONObject(i).getString("mobile_no"));
                           userList.add(obj);

                       }
                   }

                   if (userList.size() > 0)
                       obj.bulkInsertUserData(userList,SplashActivity.this);

                   String USERID=PreferencesUtils.getData("userid", SplashActivity.this);
                   ApplicationSingleton.apiresult = Connecttoget.callJson(Urls.GET_GROUPS);

                   if(new JSONObject(ApplicationSingleton.apiresult).getInt("status")==1) {
                       ApplicationSingleton.apiresultJSON = new JSONObject(ApplicationSingleton.apiresult).getJSONArray("data");
                       size = ApplicationSingleton.apiresultJSON.length();
                       userList.clear();
                       for (int i = 0; i < size; i++) {


                           if ( ApplicationSingleton.apiresultJSON.getJSONObject(i).getJSONArray("userDetails").toString().contains("\"id\":\""+USERID+"\"")) {
                               modelclass obj = new modelclass();
                               obj.setGroupName(ApplicationSingleton.apiresultJSON.getJSONObject(i).getString("group_name"));
                               obj.setGroupTags(ApplicationSingleton.apiresultJSON.getJSONObject(i).getJSONArray("tagDetails").toString());
                               obj.setGroupid(ApplicationSingleton.apiresultJSON.getJSONObject(i).getString("group_qb_id"));
                               obj.setAdminId(ApplicationSingleton.apiresultJSON.getJSONObject(i).getString("admin_id"));
                               obj.setGroupUsers(ApplicationSingleton.apiresultJSON.getJSONObject(i).getJSONArray("userDetails").toString());

                               userList.add(obj);

                           }
                       }
                       if (userList.size() > 0)
                           obj.bulkInsertGroupData(userList);



                       return true;

                   }else{

                       ApplicationSingleton.apiresultMessage=new JSONObject(ApplicationSingleton.apiresult).getString("message");

                   }

               }else{

                   ApplicationSingleton.apiresultMessage=new JSONObject(ApplicationSingleton.apiresult).getString("message");
               }
            }catch(JSONException e){

                e.printStackTrace();
            }
            return false;
        }

        @Override
        protected void onPostExecute(Boolean s) {
            super.onPostExecute(s);
            if(s){



                    final QBUser user = new QBUser();


                    user.setEmail(PreferencesUtils.getData("username", SplashActivity.this));
                    user.setPassword(PreferencesUtils.getData("password", SplashActivity.this));
                    ChatService.initIfNeed(SplashActivity.this);

                    ChatService.getInstance().login(user, new QBEntityCallbackImpl() {

                        @Override
                        public void onSuccess() {

                            Intent in = new Intent(SplashActivity.this, DialogsActivity.class);
                            startActivity(in);

                        }

                        @Override
                        public void onError(List errors) {

                            ApplicationSingleton.ShowFailedAlert(SplashActivity.this, "Fetching Message list failed " + errors.toString());

                        }
                    });


            }else{
                ApplicationSingleton.ShowFailedAlert(SplashActivity.this,ApplicationSingleton.apiresultMessage);
            }
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        if (id == ContactsQuery.QUERY_ID)
        {
            Uri contentUri;



            contentUri = ContactsQuery.CONTENT_URI;


            return new CursorLoader(SplashActivity.this,
                    contentUri,
                    ContactsQuery.PROJECTION,
                    ContactsQuery.SELECTION,
                    null,
                    ContactsQuery.SORT_ORDER);
        }



        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {


        Toast.makeText(SplashActivity.this,"contacts loaded",Toast.LENGTH_LONG).show();
        if (loader.getId() == ContactsQuery.QUERY_ID) {


            data.moveToFirst();

            do {
                try {
                    phones.add(data.getString(4));
                } catch (Exception e) {
                    e.printStackTrace();
                }


            } while (data.moveToNext());


            if (PreferencesUtils.getData("user logged", this).equals("0")) {

                if(getLOcationPermissionRequest() && getFinelOcationPermissionRequest()){


                    Intent in = new Intent(SplashActivity.this, LoginActivity.class);
                    startActivity(in);


                }
                else
                {
                    ApplicationSingleton.ShowWarningAlert(SplashActivity.this,"Please enable location settings for this app");
                }

            } else {

                new UpdateUserDataToServer(PreferencesUtils.getData("password", SplashActivity.this),PreferencesUtils.getData("username", SplashActivity.this)).execute();
            }

        }
    }



    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }


    /**
     * user data will be updated to server from splash screen for location as well as device updates
     */

    public  class UpdateUserDataToServer extends AsyncTask<String,String,Boolean> {


        String email,password;
        String message;
        public UpdateUserDataToServer(String pwd, String emaill) {


            password=pwd;
            email=emaill;



        }
        @Override
        protected Boolean doInBackground(String... strings) {


            final QBUser inputUser = new QBUser();




            inputUser.setEmail(email);
            inputUser.setPassword(password);


            try {


                JSONObject obj=new JSONObject();

                obj.put("device_os","android");
                obj.put("gcm_reg_token","android");
                TelephonyManager tm = (TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE);
                String countryCode = tm.getSimCountryIso();
                if(countryCode.length()>0)
                    obj.put("country_code",countryCode);
                else
                    obj.put("country_code",SplashActivity.this.getResources().getConfiguration().locale.getCountry());
                obj.put("device_id", android.provider.Settings.Secure.getString(SplashActivity.this.getContentResolver(),
                        android.provider.Settings.Secure.ANDROID_ID));
                obj.put("latitude",ApplicationSingleton.LOCATION_ARRAY[0]);
                obj.put("longitude",ApplicationSingleton.LOCATION_ARRAY[1]);
                obj.put("street", ApplicationSingleton.USER_STREET);
                obj.put("user_id",PreferencesUtils.getData("userid", SplashActivity.this));
                System.out.println("passing"+obj.toString());
                ApplicationSingleton.apiresult= Connecttoget.callJsonWithparams(Urls.USER_UPDATE, obj.toString());

                if(new JSONObject(ApplicationSingleton.apiresult).getInt("status")==1)

                    return true;
                else
                    message=new JSONObject(ApplicationSingleton.apiresult).getString("message");
            }catch(JSONException e) {
                e.printStackTrace();
            }


            return false;
        }
        @Override
        protected void onPostExecute(Boolean s) {
            super.onPostExecute(s);
            if(s) {

                if (getLOcationPermissionRequest() && getFinelOcationPermissionRequest()) {

                    new RetrieveUsers().execute();
                } else {
                    ApplicationSingleton.ShowWarningAlert(SplashActivity.this, "Please enable location settings for this app");
                }
            }else{

                ApplicationSingleton.ShowFailedAlert(SplashActivity.this,message);
            }
        }
    }
}