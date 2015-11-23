package app.wingman.ui.activities;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.quickblox.core.QBEntityCallbackImpl;

import com.quickblox.users.model.QBUser;

import java.util.List;

import app.wingman.ApplicationSingleton;
import app.wingman.R;
import app.wingman.core.ChatService;
import app.wingman.utils.GetMyLocation;
import app.wingman.utils.PreferencesUtils;

public class SplashActivity extends Activity {


    private static final int REQUEST_ACCESS_COARSE_LOCATION= 0;
    private static final int REQUEST_ACCESS_FINE_LOCATION= 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);




        if (PreferencesUtils.getData("user logged", this).equals("0")) {

            if(getLOcationPermissionRequest() && getFinelOcationPermissionRequest()){

                GetMyLocation obj=new GetMyLocation(SplashActivity.this);
                obj.getMyLocation();
            }
            else
            {
                  ApplicationSingleton.ShowAlert(SplashActivity.this,"Please enable location settings for this app");
            }

        } else {

            final QBUser user = new QBUser();

            user.setEmail(PreferencesUtils.getData("username", SplashActivity.this));
            user.setPassword(PreferencesUtils.getData("password", SplashActivity.this));

            ChatService.initIfNeed(this);

            ChatService.getInstance().login(user, new QBEntityCallbackImpl() {

                @Override
                public void onSuccess() {
                    // Go to Dialogs screen
                    //
                    if(getLOcationPermissionRequest() && getFinelOcationPermissionRequest()){

                        GetMyLocation obj=new GetMyLocation(SplashActivity.this);
                        obj.getMyLocation();
                    }
                    else
                    {
                        ApplicationSingleton.ShowAlert(SplashActivity.this,"Please enable location settings for this app");
                    }



                }

                @Override
                public void onError(List errors) {

                    ApplicationSingleton.ShowAlert(SplashActivity.this,"chat login errors");


                }
            });

        }


        // Login to REST API
        //

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


}