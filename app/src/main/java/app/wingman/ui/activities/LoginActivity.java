package app.wingman.ui.activities;

import android.Manifest;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.LoaderManager.LoaderCallbacks;

import android.content.Context;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.provider.ContactsContract;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.provider.SyncStateContract;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;


import com.facebook.LoggingBehavior;
import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.Settings;
import com.facebook.UiLifecycleHelper;
import com.facebook.model.GraphUser;
import com.novell.sasl.client.DigestMD5SaslClient;
import com.quickblox.auth.QBAuth;
import com.quickblox.auth.model.QBSession;

import com.quickblox.content.QBContent;
import com.quickblox.content.model.QBFile;

import com.quickblox.core.Consts;
import com.quickblox.core.QBEntityCallbackImpl;
import com.quickblox.core.exception.BaseServiceException;
import com.quickblox.core.exception.QBResponseException;
import com.quickblox.core.helper.StringifyArrayList;


import com.quickblox.core.request.QBPagedRequestBuilder;
import com.quickblox.users.QBUsers;
import com.quickblox.users.model.QBUser;
import com.quickblox.users.result.QBUserResult;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import app.wingman.ApplicationSingleton;
import app.wingman.R;
import app.wingman.core.ChatService;
import app.wingman.database.CommentsDataSource;
import app.wingman.interfaces.ContactsQuery;
import app.wingman.models.modelclass;
import app.wingman.networks.Connecttoget;
import app.wingman.settings.Urls;
import app.wingman.utils.PreferencesUtils;

import static android.Manifest.permission.CAMERA;
import static android.Manifest.permission.READ_CONTACTS;

/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends BaseActivity implements LoaderCallbacks<Cursor>,View.OnClickListener {

    /**
     * Id to identity READ_CONTACTS permission request.
     */
    private static final int REQUEST_READ_CONTACTS = 0;
    private static final int REQUEST_READ_EXTERNAL_STORAGE= 1;
    private static final int REQUEST_WRITE_EXTERNAL_STORAGE= 2;

    /**
     * A dummy authentication store containing known user names and passwords.
     * TODO: remove after connecting to a real authentication system.
     */
    private static final String[] DUMMY_CREDENTIALS = new String[]{
            "foo@example.com:hello", "bar@example.com:world"
    };
    /**
     * Keep track of the login task to ensure we can cancel it if requested.
     */
    private UserLoginTask mAuthTask = null;

    // UI references.
    private AutoCompleteTextView mEmailView;
    private EditText mPasswordView;
    private EditText mUserName;
    private EditText mPhone;

    private View mProgressView;
    private View mLoginFormView;
    Button mEmailSignInButton;
    Button mEmailSignUpButton;
    Button fblogin;
    RadioGroup gendergrp;
    RadioButton mfemale,mmale;

    // for fb

    private Uri fileUri;
    private String SmediaPath;
    private Session.StatusCallback statusCallback = new SessionStatusCallback();
    private UiLifecycleHelper uiHelper;
    public boolean fbclicked=false;

    // for image picking
    private static final int CAMERA_REQUEST = 1888;
    private static final int CHOICE_AVATAR_FROM_CAMERA_CROP=1000;
    private static int CHOICE_AVATAR_FROM_GALLERY = 1;
    public static String cameraFileName;
    AlertDialog alert;
    LinearLayout alertLayout;
    ImageView mUserImage;
    TextView mUserDescription;
    LinearLayout mUserTopLayout;

    int count=0;
    QBUser crntUser;
    CommentsDataSource obj;
    ArrayList<modelclass> userList = new ArrayList<modelclass>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login2);
        // Set up the login form.
        mEmailView = (AutoCompleteTextView) findViewById(R.id.email);


        mPasswordView = (EditText) findViewById(R.id.password);
        mUserName = (EditText) findViewById(R.id.editext_name);
        gendergrp = (RadioGroup) findViewById(R.id.gendergrp);
        mfemale = (RadioButton) findViewById(R.id.femaleradio);
        mmale = (RadioButton) findViewById(R.id.maleradio);
        mPhone = (EditText) findViewById(R.id.editetxt_phone);
        fblogin=(Button)findViewById(R.id.fbsigninbtn);
        alertLayout=(LinearLayout)findViewById(R.id.user_layout);
        mUserTopLayout=(LinearLayout)findViewById(R.id.user_toplayout);
        fblogin.setOnClickListener(this);
        mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == R.id.login || id == EditorInfo.IME_NULL) {
                    if (mEmailSignInButton.getText().toString().equals(getResources().getString(R.string.action_sign_in)))

                        attemptLogin();

                    else {
                        if (count == 0) {
                            alertLayout.setVisibility(View.VISIBLE);
                            mUserTopLayout.setVisibility(View.GONE);
                            count++;
                        } else {
                            if (cameraFileName.length() > 0) {
                                new CheckEmail().execute();
                            } else {
                                Toast.makeText(LoginActivity.this, "Please select a profile pic", Toast.LENGTH_LONG).show();
                            }
                        }

                }

                }
                return false;
            }
        });


         mEmailSignInButton = (Button) findViewById(R.id.email_sign_in_button);
        mEmailSignUpButton = (Button) findViewById(R.id.email_sign_up_button);
        mUserImage=(ImageView)findViewById(R.id.person_image);
        mUserDescription=(TextView)findViewById(R.id.person_description);

        count=0;
        mEmailSignInButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {


                        if (((Button) view).getText().toString().equals(getResources().getString(R.string.action_sign_in)))
                            attemptLogin();

                        else {
                            if (count == 0) {
                                alertLayout.setVisibility(View.VISIBLE);
                                mUserTopLayout.setVisibility(View.GONE);
                                count++;
                            } else {
                                if (cameraFileName.length() > 0) {
                                    new CheckEmail().execute();
                                } else {
                                    Toast.makeText(LoginActivity.this, "Please select a profile pic", Toast.LENGTH_LONG).show();
                                }
                            }


                        }



            }
        });
        gendergrp.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {

                if(radioGroup.getId()==R.id.femaleradio){

                    PreferencesUtils.saveData("gender","female",LoginActivity.this);
                }else if(radioGroup.getId()==R.id.maleradio){
                    PreferencesUtils.saveData("gender","male",LoginActivity.this);
                }
            }
        });

        mLoginFormView  = findViewById(R.id.login_form);
        mProgressView   = findViewById(R.id.login_progress);
        Settings.addLoggingBehavior(LoggingBehavior.INCLUDE_ACCESS_TOKENS);
        uiHelper = new UiLifecycleHelper(LoginActivity.this, statusCallback);


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == CAMERA_REQUEST || requestCode == CHOICE_AVATAR_FROM_GALLERY || requestCode==CHOICE_AVATAR_FROM_CAMERA_CROP) {

                Bitmap avatar = getBitmapFromData(data);

                if(requestCode==CAMERA_REQUEST){

                    if (data != null) {

                        String[] projection = { MediaStore.Images.Media.DATA };
                        Cursor cursor = managedQuery(
                                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                                projection, null, null, null);
                        int column_index_data = cursor
                                .getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                        cursor.moveToLast();

                        cameraFileName = cursor.getString(column_index_data);

                        goToCrop();
                    }
                }
                    else if(requestCode==CHOICE_AVATAR_FROM_GALLERY){

                        handleGalleryResult(data);
                    goToCrop();
                    }else if(requestCode==CHOICE_AVATAR_FROM_CAMERA_CROP){


                    Bitmap cropped_bitmap=getBitmapFromData(data);
                    mUserImage.setImageBitmap(cropped_bitmap);
                    Uri tempUri = getImageUri(getApplicationContext(), cropped_bitmap);

                    // CALL THIS METHOD TO GET THE ACTUAL PATH
                    cameraFileName=(getRealPathFromURI(tempUri));

                    System.out.println("pathh"+cameraFileName);



                }

                // this bitmap is the finish image

            }else{
                Session.getActiveSession().onActivityResult(LoginActivity.this, requestCode, resultCode, data);
                uiHelper.onActivityResult(requestCode, resultCode, data);
            }
        }
        super.onActivityResult(requestCode, resultCode, data);


    }
    public Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }

    public String getRealPathFromURI(Uri uri) {
        Cursor cursor = getContentResolver().query(uri, null, null, null, null);
        cursor.moveToFirst();
        int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
        return cursor.getString(idx);
    }
    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
        super.onSaveInstanceState(outState, outPersistentState);
        Session session = Session.getActiveSession();
        Session.saveSession(session, outState);
    }

    /**
     * GO TO CROP INTENT
     *
     */

    public void goToCrop(){

        if (!writeStorageRequest()) {
            return;
        } if (!readStorageRequest()) {
            return;
        }

        Intent intent = new Intent("com.android.camera.action.CROP");
        Uri uri = Uri.fromFile(new File(cameraFileName));
        intent.setDataAndType(uri, "image/*");
        startActivityForResult(getCropIntent(intent), CHOICE_AVATAR_FROM_CAMERA_CROP);
    }
    public void Onsignup(View view){

        mUserName.setVisibility(View.VISIBLE);
        mPhone.setVisibility(View.VISIBLE);
        gendergrp.setVisibility(View.VISIBLE);
        mEmailSignUpButton.setVisibility(View.GONE);
        mEmailSignInButton.setText(getResources().getString(R.string.btntxt_sign_up));

    }

    /*
    method called from upload image place holder click
     */
    public void showGallery(View view){


        final CharSequence[] items = {"Pick Image", "Gallery", "Camera"};


        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Select");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int item) {
                // Do something with the selection

                if (item == 1) {
                    ChooseFromGallery();
                } else if (item == 2)
                    ChooseFromCamera();
                alert.dismiss();
            }
        });
         alert = builder.create();
        alert.show();

    }

    private void populateAutoComplete() {
        if (!mayRequestContacts()) {
            return;
        }

        getLoaderManager().initLoader(0, null, this);
    }

    private boolean mayRequestContacts() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return true;
        }
        if (checkSelfPermission(READ_CONTACTS) == PackageManager.PERMISSION_GRANTED) {
            return true;
        }
        if (shouldShowRequestPermissionRationale(READ_CONTACTS)) {
            Snackbar.make(mEmailView, R.string.permission_rationale, Snackbar.LENGTH_INDEFINITE)
                    .setAction(android.R.string.ok, new OnClickListener() {
                        @Override
                        @TargetApi(Build.VERSION_CODES.M)
                        public void onClick(View v) {
                            requestPermissions(new String[]{READ_CONTACTS}, REQUEST_READ_CONTACTS);
                        }
                    });
        } else {
            requestPermissions(new String[]{READ_CONTACTS}, REQUEST_READ_CONTACTS);
        }
        return false;
    }





    /**
    method for new user signup
     */



    public  class QbSignup extends AsyncTask<String,String,QBUser>{

        String username,password,email,phone;
        Context context;
        String imgurl="";


        public QbSignup(String usernam,String pwd,String emaill,String phones,Context cntxt) {

            username=usernam;
            password=pwd;
            email=emaill;
            phone=phones;
            context=cntxt;


        }
        @Override
        protected QBUser doInBackground(String... strings) {




                    final QBUser newUser = new QBUser(username, password);

                    newUser.setEmail(email);
                    newUser.setFullName(username);
                    newUser.setPhone(phone);
                     newUser.setOldPassword(password);

                    StringifyArrayList<String> tags = new StringifyArrayList<String>();
                    tags.add("android");

                    newUser.setTags(tags);

                    try {

                        QBUser user;
                        QBAuth.createSession();


                        user = QBUsers.signUpSignInTask(newUser);
                        if (null != cameraFileName) {
                            QBFile qbFile = QBContent.uploadFileTask(new File(cameraFileName), true, (String) null);
                            System.out.println("uploaded" + qbFile.getPublicUrl());
                           imgurl=qbFile.getPublicUrl();

                        }





                        String token = QBAuth.getBaseService().getToken();

                        ApplicationSingleton.saveChatUser(user, token, context);




                    }catch(QBResponseException e) {
                        e.printStackTrace();
                    }catch(BaseServiceException e) {
                        e.printStackTrace();
                    }




            return newUser;
        }

        @Override
        protected void onPostExecute(QBUser s) {
            super.onPostExecute(s);
            if(imgurl.length()>1)
                new UpdateUser(s,imgurl,password).execute();

        }
    }
    public void attemptSignup(){

        boolean cancel = false;
        View focusView = null;

        // Store values at the time of the login attempt.
        final String email = mEmailView.getText().toString();
        final String password = mPasswordView.getText().toString();
        String phone = mPhone.getText().toString();
        String username = mUserName.getText().toString();

        // Check for a valid password, if the user entered one.
        if (!TextUtils.isEmpty(password) && !isPasswordValid(password)) {
            mPasswordView.setError(getString(R.string.error_invalid_password));
            focusView = mPasswordView;
            cancel = true;
        }else if(password.length()<8){

            mPasswordView.setError(getString(R.string.error_invalid_password));
            focusView = mPasswordView;
            cancel = true;
        }

        // Check for a valid email address.
        if (TextUtils.isEmpty(email)) {
            mEmailView.setError(getString(R.string.error_field_required));
            focusView = mEmailView;
            cancel = true;
        } else if (!isEmailValid(email)) {
            mEmailView.setError(getString(R.string.error_invalid_email));
            focusView = mEmailView;
            cancel = true;
        }
        if (TextUtils.isEmpty(phone)) {
            mPhone.setError(getString(R.string.error_field_required));
            focusView = mPhone;
            cancel = true;
        }
        if (TextUtils.isEmpty(username)) {
            mUserName.setError(getString(R.string.error_field_required));
            focusView = mUserName;
            cancel = true;
        }
        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            showProgress(true);
        }
            new QbSignup(username,password,email,phone,LoginActivity.this).execute();
    }

    /**
     * user signin

     */


    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    private void attemptLogin() {
        if (mAuthTask != null) {
            return;
        }

        // Reset errors.
        mEmailView.setError(null);
        mPasswordView.setError(null);

        // Store values at the time of the login attempt.
        final String email = mEmailView.getText().toString();
        final String password = mPasswordView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid password, if the user entered one.
        if (!TextUtils.isEmpty(password) && !isPasswordValid(password)) {
            mPasswordView.setError(getString(R.string.error_invalid_password));
            focusView = mPasswordView;
            cancel = true;
        }

        // Check for a valid email address.
        if (TextUtils.isEmpty(email)) {
            mEmailView.setError(getString(R.string.error_field_required));
            focusView = mEmailView;
            cancel = true;
        } else if (!isEmailValid(email)) {
            mEmailView.setError(getString(R.string.error_invalid_email));
            focusView = mEmailView;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            showProgress(true);
            final QBUser user = new QBUser();


            user.setEmail(email);
            user.setPassword(password);
            ChatService.initIfNeed(LoginActivity.this);

            ChatService.getInstance().login(user, new QBEntityCallbackImpl() {

                @Override
                public void onSuccess() {


                    saveUser( email, password, Integer.toString(user.getId()));
                    new QbSignin(password,email).execute();

                }

                @Override
                public void onError(List errors) {

                    ApplicationSingleton.ShowFailedAlert(LoginActivity.this,errors.toString());

                }
            });





//            mAuthTask = new UserLoginTask(email, password);
//            mAuthTask.execute((Void) null);
        }
    }
    public  class QbSignin extends AsyncTask<String,String,Boolean>{


        String email,password;
       String message;
        QBUser user;
        boolean resultflag=false;

        public QbSignin(String pwd,String emaill) {


            password=pwd;
            email=emaill;



        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }

        @Override
        protected Boolean doInBackground(String... strings) {



                    // Go to Dialogs screen
                    //

                            try {
                                JSONObject obj = new JSONObject();

                                obj.put("device_os", "android");
                                obj.put("gcm_reg_token", "android");
                                TelephonyManager tm = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
                                String countryCode = tm.getSimCountryIso();
                                if (countryCode.length() > 0)
                                    obj.put("country_code", countryCode);
                                else
                                    obj.put("country_code", LoginActivity.this.getResources().getConfiguration().locale.getCountry());
                                obj.put("device_id", android.provider.Settings.Secure.getString(LoginActivity.this.getContentResolver(),
                                        android.provider.Settings.Secure.ANDROID_ID));
                                obj.put("latitude", ApplicationSingleton.LOCATION_ARRAY[0]);
                                obj.put("longitude", ApplicationSingleton.LOCATION_ARRAY[1]);
                                obj.put("street", ApplicationSingleton.USER_STREET);
                                obj.put("user_id", PreferencesUtils.getData("userid", LoginActivity.this));
                                System.out.println("passing" + obj.toString());
                                String result = Connecttoget.callJsonWithparams(Urls.USER_UPDATE, obj.toString());

                                if (new JSONObject(result).getInt("status")==1)

                                    resultflag=true;
                                else
                                    message = new JSONObject(result).getString("message");


                            } catch (JSONException e) {
                                e.printStackTrace();
                            }catch(Exception e) {
                                e.printStackTrace();
                            }

                             if(resultflag)
                                     return true;

                            return false;
        }
        @Override
        protected void onPostExecute(Boolean s) {
            super.onPostExecute(s);
            if(s) {

                new RetrieveUsers().execute();

            }else{

                ApplicationSingleton.ShowFailedAlert(LoginActivity.this,message);
            }
        }
    }
/**
  update user with description and avatar image in quickblox along with signup update to TI server
 */
    public  class UpdateUser extends AsyncTask<String,String,Boolean>{

        QBUser user;
        String imgurl;
        String psswrd;
    String message;

        public UpdateUser(QBUser usr,String img,String pswd){

            user=usr;
            imgurl=img;
            psswrd=pswd;

        }

        @Override
        protected Boolean doInBackground(String... strings) {

            try {

                JSONObject obj=new JSONObject();
                obj.put("profile_pic",imgurl);
                obj.put("user_info", mUserDescription.getText().toString());
                user.setCustomData(obj.toString());

                QBUsers.updateUser(user);
                user.setPassword(psswrd);

                obj.put("name", user.getFullName());
                obj.put("email",user.getEmail());
                obj.put("password",MessageDigest.getInstance("MD5").digest(user.getPassword().getBytes()));
                obj.put("mobile_no",user.getPhone());
                obj.put("chat_id",user.getId());
                PreferencesUtils.saveData("user_id", user.getId().toString(), LoginActivity.this);
                obj.put("device_os","android");
                obj.put("gcm_reg_token","android");
                obj.put("country_code",LoginActivity.this.getResources().getConfiguration().locale.getCountry());
                obj.put("device_id", android.provider.Settings.Secure.getString(LoginActivity.this.getContentResolver(),
                        android.provider.Settings.Secure.ANDROID_ID));
                obj.put("latitude",ApplicationSingleton.LOCATION_ARRAY[0]);
                obj.put("longitude",ApplicationSingleton.LOCATION_ARRAY[1]);
                obj.put("street", ApplicationSingleton.USER_STREET);
                TelephonyManager tm = (TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE);
                String countryCode = tm.getSimCountryIso();
                if(countryCode.length()>0)
                    obj.put("country_code",countryCode);
                else
                    obj.put("country_code",LoginActivity.this.getResources().getConfiguration().locale.getCountry());


                String result=Connecttoget.callJsonWithparams(Urls.USER_SIGNUP, obj.toString());

                if (new JSONObject(result).getInt("status")==1)

                    return true;
                else
                    message=new JSONObject(result).getString("message");

            }catch(QBResponseException e){

                e.printStackTrace();
            }catch(JSONException e){

                e.printStackTrace();
            }catch(NoSuchAlgorithmException e){

                e.printStackTrace();
            }

            return false;
        }

        @Override
        protected void onPostExecute(Boolean s) {
            super.onPostExecute(s);
            if(s) {
                saveUser( user.getEmail(), psswrd,Integer.toString(user.getId()));
                new RetrieveUsers().execute();
            }else{

                ApplicationSingleton.ShowFailedAlert(LoginActivity.this,message);
            }
        }
    }

    /**
     * check email exists or not
     */

    public  class CheckEmail extends AsyncTask<String,String,Boolean>{


        @Override
        protected Boolean doInBackground(String... strings) {


            try{
                JSONObject obj=new JSONObject();
                obj.put("email",mEmailView.getText().toString());
            String result=Connecttoget.callJsonWithparams(Urls.USER_email_check, obj.toString());

                if (new JSONObject(result).getInt("status")==1){
                    if (new JSONObject(result).getString("new_user").equals("1"))

                        return true;
                }

        }catch(JSONException e){

            e.printStackTrace();
        }

        return false;
        }
        @Override
        protected void onPostExecute(Boolean s) {
            super.onPostExecute(s);
            if(s) {

                attemptSignup();
            }else{

                mEmailView.setError("emailid already exists");


            }
        }
    }
    /**
    saves loggedin user data
     * @param email
     * @param password
     */

    private void saveUser(String email,String password,String ID){

        PreferencesUtils.saveData("username", email, LoginActivity.this);
        PreferencesUtils.saveData("password", password, LoginActivity.this);
        PreferencesUtils.saveData("userid", ID, LoginActivity.this);
        PreferencesUtils.saveData("user logged","1",LoginActivity.this);

    }
    private boolean isEmailValid(String email) {
        //TODO: Replace this with your own logic
        return email.contains("@");
    }

    private boolean isPasswordValid(String password) {
        //TODO: Replace this with your own logic
        return password.length() > 4;
    }

    /**
     * Shows the progress UI and hides the login form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            mLoginFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        return new CursorLoader(this,
                // Retrieve data rows for the device user's 'profile' contact.
                Uri.withAppendedPath(ContactsContract.Profile.CONTENT_URI,
                        ContactsContract.Contacts.Data.CONTENT_DIRECTORY), ProfileQuery.PROJECTION,

                // Select only email addresses.
                ContactsContract.Contacts.Data.MIMETYPE +
                        " = ?", new String[]{ContactsContract.CommonDataKinds.Email
                .CONTENT_ITEM_TYPE},

                // Show primary email addresses first. Note that there won't be
                // a primary email address if the user hasn't specified one.
                ContactsContract.Contacts.Data.IS_PRIMARY + " DESC");
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
        List<String> emails = new ArrayList<>();
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            emails.add(cursor.getString(ProfileQuery.ADDRESS));
            cursor.moveToNext();
        }

        addEmailsToAutoComplete(emails);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {

    }

    private void addEmailsToAutoComplete(List<String> emailAddressCollection) {
        //Create adapter to tell the AutoCompleteTextView what to show in its dropdown list.
        ArrayAdapter<String> adapter =
                new ArrayAdapter<>(LoginActivity.this,
                        android.R.layout.simple_dropdown_item_1line, emailAddressCollection);

        mEmailView.setAdapter(adapter);
    }

    @Override
    public void onClick(View view) {
        if(view.getId()==R.id.fbsigninbtn){

          onClickLogin();
           
        }
    }


    private interface ProfileQuery {
        String[] PROJECTION = {
                ContactsContract.CommonDataKinds.Email.ADDRESS,
                ContactsContract.CommonDataKinds.Email.IS_PRIMARY,
        };

        int ADDRESS = 0;
        int IS_PRIMARY = 1;
    }

    /**
     * Represents an asynchronous login/registration task used to authenticate
     * the user.
     */
    public class UserLoginTask extends AsyncTask<Void, Void, Boolean> {

        private final String mEmail;
        private final String mPassword;

        UserLoginTask(String email, String password) {
            mEmail = email;
            mPassword = password;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            // TODO: attempt authentication against a network service.

            try {
                // Simulate network access.
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                return false;
            }

            for (String credential : DUMMY_CREDENTIALS) {
                String[] pieces = credential.split(":");
                if (pieces[0].equals(mEmail)) {
                    // Account exists, return true if the password matches.
                    return pieces[1].equals(mPassword);
                }
            }

            // TODO: register the new account here.
            return true;
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            mAuthTask = null;
            showProgress(false);

            if (success) {
                finish();
            } else {
                mPasswordView.setError(getString(R.string.error_incorrect_password));
                mPasswordView.requestFocus();
            }
        }

        @Override
        protected void onCancelled() {
            mAuthTask = null;
            showProgress(false);
        }
    }
    
    
    /**
    for facebook login
     */

    private void onClickLogin() {
        Log.d("", "-----> 1");
        //  try {

        showHashKey(LoginActivity.this);
        fbclicked=true;
        Session session = new Session(LoginActivity.this);
        //session.closeAndClearTokenInformation();

        //session = new Session(LoginActivity.this);

        Session.setActiveSession(session);

        if (!session.isOpened() && !session.isClosed()) {
            Log.e("", "-----> ConnectToFacebook if");
//                Session.OpenRequest openRequest = new Session.OpenRequest(this);
//
//    			openRequest.setLoginBehavior(SessionLoginBehavior.SUPPRESS_SSO);
//    			openRequest.setCallback(statusCallback);
//    			openRequest.setPermissions(Arrays.asList("email","user_birthday"));
            session.openForRead(new Session.OpenRequest(this)
                    .setCallback(statusCallback)
                    .setPermissions(Arrays.asList("email", "user_birthday")));


        } else {
            Log.e("", "-----> ConnectToFacebook else");
            Session.openActiveSession(LoginActivity.this, true, statusCallback);
        }
//        }catch (NullPointerException e){
//            Toast.makeText(LoginActivity.this,"catch "+e.getMessage(),Toast.LENGTH_LONG).show();
//         }
    }
    
    private class SessionStatusCallback implements Session.StatusCallback {
        @Override
        public void call(final Session session, SessionState state, Exception exception) {
            if (session.isOpened()) {
                Log.e("facebook session","6");
                Request.newMeRequest(session,
                        new Request.GraphUserCallback() {
                            @Override
                            public void onCompleted(GraphUser user, Response response) {
                                if (fbclicked) {

                                    if (((String) user.getProperty("email")).trim().length() > 0)

                                        mEmailView.setText((String) user.getProperty("email"));

                                    String name="";
                                    if (user.getFirstName().trim().length() > 0)
                                        name = user.getFirstName();

                                    if (user.getLastName().trim().length() > 0)
                                        name =name+" "+ user.getLastName();


                                    mUserName.setVisibility(View.VISIBLE);
                                    mUserName.setText(name);

                                    String gender=user.getProperty("gender").toString();



                                    fblogin.setVisibility(View.GONE);

//										Supportingcalls.getData(context,(String)user.getProperty("email"),user.getName(),
//												user.getBirthday(),
//												user.getProperty("gender").toString());


                                    //Toast.makeText(LoginActivity.this, "Sorry we are unable to retrieve the email address of this facebook account", Toast.LENGTH_SHORT).show();



                                } else {
                                    session.closeAndClearTokenInformation();
                                }
                            }
                        }).executeAsync();
            }
        }
    }
    public static void showHashKey(Context context) {
        try {
            PackageInfo info = context.getPackageManager().getPackageInfo(
                    "com.app.wingman", PackageManager.GET_SIGNATURES); //Your            package name here
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                System.out.println("KeyHash:"+ Base64.encodeToString(md.digest(), Base64.DEFAULT));
            }
        } catch (PackageManager.NameNotFoundException e) {
        } catch (NoSuchAlgorithmException e) {
        }
    }



    /**
     *   seek image storage permissions
     */

    private boolean readStorageRequest() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return true;
        }
        if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            return true;
        }
        if (shouldShowRequestPermissionRationale(Manifest.permission.READ_EXTERNAL_STORAGE)) {
            Snackbar.make(mEmailView, R.string.permission_rationale, Snackbar.LENGTH_INDEFINITE)
                    .setAction(android.R.string.ok, new OnClickListener() {
                        @Override
                        @TargetApi(Build.VERSION_CODES.M)
                        public void onClick(View v) {
                            requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_READ_EXTERNAL_STORAGE);
                        }
                    });
        } else {
            requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_READ_EXTERNAL_STORAGE);
        }
        return false;
    }
    private boolean writeStorageRequest() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return true;
        }
        if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            return true;
        }
        if (shouldShowRequestPermissionRationale(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            Snackbar.make(mEmailView, R.string.permission_rationale, Snackbar.LENGTH_INDEFINITE)
                    .setAction(android.R.string.ok, new OnClickListener() {
                        @Override
                        @TargetApi(Build.VERSION_CODES.M)
                        public void onClick(View v) {
                            requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_WRITE_EXTERNAL_STORAGE);
                        }
                    });
        } else {
            requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_WRITE_EXTERNAL_STORAGE);
        }
        return false;
    }

    /**
     *   get image for upload and crop
      */

    public void ChooseFromGallery(){

        if (!readStorageRequest()) {
            return;
        }
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("image/*");
        startActivityForResult(getCropIntent(intent), CHOICE_AVATAR_FROM_GALLERY);
    }

    private Intent getCropIntent(Intent intent) {


        intent.putExtra("crop", "true");
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        intent.putExtra("outputX", 320);
        intent.putExtra("outputY", 320);
        intent.putExtra("return-data", true);
        return intent;
    }
    public void ChooseFromCamera(){
        if (!writeStorageRequest()) {
            return;
        }

        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        startActivityForResult(intent, CAMERA_REQUEST);
    }
    /**
     * Use for decoding camera response data.
     *
     * @param data
     * @return
     */
    public static Bitmap getBitmapFromData(Intent data) {
        Bitmap photo = null;
        Uri photoUri = data.getData();
        if (photoUri != null) {
            photo = BitmapFactory.decodeFile(photoUri.getPath());
        }
        if (photo == null) {
            Bundle extra = data.getExtras();
            if (extra != null) {
                photo = (Bitmap) extra.get("data");
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                photo.compress(Bitmap.CompressFormat.JPEG, 100, stream);
            }
        }

        return photo;
    }


    private void handleGalleryResult(Intent data)
    {
        Uri selectedImage = data.getData();
        cameraFileName = getPath(selectedImage);
        if(cameraFileName==null)


            try {
                InputStream is = getContentResolver().openInputStream(selectedImage);

                cameraFileName = selectedImage.getPath();
            } catch (FileNotFoundException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

    }


    @SuppressLint("NewApi")
    private String getPath(Uri uri) {
        if( uri == null ) {
            return null;
        }

        String[] projection = { MediaStore.Images.Media.DATA };

        Cursor cursor;
        if(Build.VERSION.SDK_INT >19)
        {
            // Will return "image:x*"
            String wholeID = DocumentsContract.getDocumentId(uri);
            // Split at colon, use second item in the array
            String id = wholeID.split(":")[1];
            // where id is equal to
            String sel = MediaStore.Images.Media._ID + "=?";

            cursor = getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                    projection, sel, new String[]{ id }, null);
        }
        else
        {
            cursor = getContentResolver().query(uri, projection, null, null, null);
        }
        String path = null;
        try
        {
            int column_index = cursor
                    .getColumnIndex(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            path = cursor.getString(column_index).toString();
            cursor.close();
        }
        catch(NullPointerException e) {

        }
        return path;
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
                obj=new CommentsDataSource(LoginActivity.this);
                obj.open();
                if( new JSONObject(ApplicationSingleton.apiresult).getInt("status")==1) {
                    ApplicationSingleton.apiresultJSON = new JSONObject(ApplicationSingleton.apiresult).getJSONArray("userList");

                    int size = ApplicationSingleton.apiresultJSON.length();
                    for (int i = 0; i < size; i++) {

                        if (SplashActivity.phones.contains(ApplicationSingleton.apiresultJSON.getJSONObject(i).getString("mobile_no"))) {
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
                    System.out.println("adding users " + userList);
                    if (userList.size() > 0)
                        obj.bulkInsertUserData(userList,LoginActivity.this);

                    String USERID=PreferencesUtils.getData("userid", LoginActivity.this);
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

                        System.out.println("adding groups " + userList);
                        if (userList.size() > 0)
                            obj.bulkInsertGroupData(userList);



                        return true;

                    }else {

                        ApplicationSingleton.apiresultMessage = new JSONObject(ApplicationSingleton.apiresult).getString("message");
                    }

//                    if (mEmailSignInButton.getText().toString().equals(getResources().getString(R.string.action_sign_in))) {
//
//
//                        final QBUser user = new QBUser();
//
//
//                        user.setEmail(mEmailView.getText().toString());
//                        user.setPassword(mPasswordView.getText().toString());
//                        ChatService.initIfNeed(LoginActivity.this);
//
//                        ChatService.getInstance().login(user, new QBEntityCallbackImpl() {
//
//                            @Override
//                            public void onSuccess() {
//                                // Go to Dialogs screen
//                                //
//
//                                Intent intent = new Intent(LoginActivity.this, DialogsActivity.class);
//                                startActivity(intent);
//
//                                finish();
//                                Log.e("login success", "login success");
//
//                            }
//
//                            @Override
//                            public void onError(List errors) {
//                                AlertDialog.Builder dialog = new AlertDialog.Builder(LoginActivity.this);
//                                dialog.setMessage("chat login errors: " + errors.toString()).create().show();
//
//                            }
//                        });
//
//
//
//
//
//                    }
//                    else{
//                        Intent intent = new Intent(LoginActivity.this, PickContact.class);
//                        startActivity(intent);
//
//                    }

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

                PreferencesUtils.saveData("distance","km",LoginActivity.this);
                PreferencesUtils.saveData("distancevalue", "5",LoginActivity.this);// saving initial distance data as 5 km

                if (mEmailSignInButton.getText().toString().equals(getResources().getString(R.string.action_sign_in))) {
                    Intent in = new Intent(LoginActivity.this, DialogsActivity.class);
                    startActivity(in);
                }
                else{
                    Intent intent = new Intent(LoginActivity.this, PickContact.class);
                    startActivity(intent);
                }


            }else{
                ApplicationSingleton.ShowFailedAlert(getApplicationContext(),ApplicationSingleton.apiresultMessage);
            }
        }
    }







}

