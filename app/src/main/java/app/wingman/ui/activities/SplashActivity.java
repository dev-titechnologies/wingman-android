package app.wingman.ui.activities;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
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
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.quickblox.auth.QBAuth;
import com.quickblox.auth.model.QBSession;
import com.quickblox.core.Consts;
import com.quickblox.core.QBEntityCallbackImpl;

import com.quickblox.core.exception.BaseServiceException;
import com.quickblox.core.exception.QBResponseException;
import com.quickblox.core.request.QBPagedRequestBuilder;
import com.quickblox.users.QBUsers;
import com.quickblox.users.model.QBUser;

import org.json.JSONArray;
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
import app.wingman.pushnotifications.PlayServicesHelper;
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


        new getallData().execute();
        if(getFinelOcationPermissionRequest()){
            if(getLOcationPermissionRequest()){

                GetMyLocation obj = new GetMyLocation(SplashActivity.this);
                obj.getMyLocation();
            }else
                ApplicationSingleton.ShowWarningAlert(getApplicationContext(),"Sorry please turn location permission in your device for wingman");

        }else
            ApplicationSingleton.ShowWarningAlert(getApplicationContext(),"Sorry please turn location permission in your device for wingman");


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
     @param page-pagecount
     */
    public  void retrieveAllUsersFromPage(final int page){



        final QBPagedRequestBuilder pagedRequestBuilder = new QBPagedRequestBuilder();
        pagedRequestBuilder.setPage(page);
        pagedRequestBuilder.setPerPage(100);
        Toast.makeText(getApplicationContext(), "retrieving users", Toast.LENGTH_LONG).show();


        // You have successfully created the session
        //
        // Now you can use QuickBlox API!

        QBUsers.getUsers(pagedRequestBuilder, new QBEntityCallbackImpl<ArrayList<QBUser>>() {

            int userNumber = 1;

            @Override
            public void onSuccess(ArrayList<QBUser> users, Bundle params) {



                int size = users.size();
                for (int i = 0; i < size; i++) {

                    if (phones.contains(users.get(i).getPhone())) {
                        modelclass obj = new modelclass();
                        obj.setUserName(users.get(i).getFullName().toLowerCase().trim());
                        obj.setUserEmail(users.get(i).getEmail());
                        obj.setUserId(users.get(i).getId().toString());
                        if(users.get(i).getCustomData()==null)
                            obj.setUserCustomData("{\"user_info\":\"hii I am using Wingman\",\"profile_pic\":\"\"}");
                        else
                            obj.setUserCustomData(users.get(i).getCustomData());
                        obj.setUserPhone(users.get(i).getPhone());
                        userList.add(obj);

                    }
                }


                userNumber = users.size() + 1;
                int totalEntries = params.getInt(Consts.TOTAL_ENTRIES);
                System.out.println("total user"+userNumber+ " : "+"total- "+totalEntries);

                if (userNumber < totalEntries) {

                    retrieveAllUsersFromPage(page + 1);
                } else{

                    if (userList.size() > 0)
                        obj.bulkInsertUserData(userList);
                  ///

                    final QBUser user = new QBUser();


                    user.setEmail(PreferencesUtils.getData("username", SplashActivity.this));
                    user.setPassword(PreferencesUtils.getData("password", SplashActivity.this));
                    ChatService.initIfNeed(SplashActivity.this);

                    ChatService.getInstance().login(user, new QBEntityCallbackImpl() {

                        @Override
                        public void onSuccess() {
                            // Go to Dialogs screen
                            //

                            Intent intent = new Intent(SplashActivity.this, DialogsActivity.class);
                            startActivity(intent);

                            finish();
                            Log.e("login success", "login success");

                        }

                        @Override
                        public void onError(List errors) {
                            AlertDialog.Builder dialog = new AlertDialog.Builder(SplashActivity.this);
                            dialog.setMessage("chat login errors: " + errors.toString()).create().show();

                        }
                    });


                    ///





//                    Intent in=new Intent(SplashActivity.this,DialogsActivity.class);
//                    startActivity(in);
                }
            }

            @Override
            public void onError(List<String> errors) {

                ApplicationSingleton.ShowFailedAlert(getApplicationContext(),"Sorry some error occurred on loading the app"+errors.toString());

            }
        });





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


        Toast.makeText(getApplicationContext(),"contacts loaded",Toast.LENGTH_LONG).show();
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

                    Intent in =new Intent(SplashActivity.this,LoginActivity.class);
                    startActivity(in);

                }
                else
                {
                    ApplicationSingleton.ShowWarningAlert(SplashActivity.this,"Please enable location settings for this app");
                }

            } else {
Log.e("NOT LOGGED IN Conditino","else part");
                new  QbSignin(PreferencesUtils.getData("password", SplashActivity.this),PreferencesUtils.getData("username", SplashActivity.this)).execute();
            }

        }
    }



    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }


    public  class QbSignin extends AsyncTask<String,String,Boolean> {


        String email,password;
        String message;
        public QbSignin(String pwd,String emaill) {


            password=pwd;
            email=emaill;



        }
        @Override
        protected Boolean doInBackground(String... strings) {


            final QBUser inputUser = new QBUser();




            inputUser.setEmail(email);
            inputUser.setPassword(password);


            try {
                QBUser user;

                QBAuth.createSession();

                String password = inputUser.getPassword();
                user = QBUsers.signIn(inputUser);

                String token = QBAuth.getBaseService().getToken();

                user.setPassword(password);

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
                obj.put("user_id",user.getId());
                System.out.println("passing"+obj.toString());
                String result= Connecttoget.callJsonWithparams(Urls.USER_UPDATE, obj.toString());

                if(new JSONObject(result).getString("status").equals("1"))

                    return true;
                else
                    message=new JSONObject(result).getString("message");
            }catch(QBResponseException e) {
                e.printStackTrace();
            }catch(BaseServiceException e) {
                e.printStackTrace();
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

                    retrieveAllUsersFromPage(1);
                } else {
                    ApplicationSingleton.ShowWarningAlert(SplashActivity.this, "Please enable location settings for this app");
                }
            }else{

                ApplicationSingleton.ShowFailedAlert(SplashActivity.this,message);
            }
        }
    }
    public  class getallData extends AsyncTask<String, Integer, String> {

        @Override
        protected String doInBackground(String... params) {
            String gettag= Connecttoget.callJson(Urls.GET_TAGS);
            try {
                JSONObject alltagarray = new JSONObject(gettag);
                if(alltagarray.getInt("status")==1){
                    JSONArray tags = alltagarray.getJSONArray("data");
                    Log.e("get tag ",tags.toString());
                    PreferencesUtils.saveData("ALLTAGS", tags.toString(), getApplicationContext());}
                else{
                    Toast.makeText(getApplicationContext(),"ERROR FROM SERVER.!!",2000).show();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }



            return null;
        }


    }
}