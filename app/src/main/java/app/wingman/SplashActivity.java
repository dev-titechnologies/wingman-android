package app.wingman;

import android.app.Activity;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceFragment;
import android.util.Log;


import com.quickblox.core.QBEntityCallbackImpl;
import com.quickblox.users.model.QBUser;

import java.util.List;

import app.wingman.core.ChatService;
import app.wingman.ui.activities.LoginActivity;

import app.wingman.ui.activities.DialogsActivity;
import app.wingman.utils.PreferencesUtils;

public class SplashActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        if(PreferencesUtils.getData("user logged",this).equals("0")){

            Intent in =new Intent(SplashActivity.this, LoginActivity.class);
            startActivity(in);
        }else{

            final QBUser user = new QBUser();




            user.setEmail(PreferencesUtils.getData("username",SplashActivity.this));
            user.setPassword(PreferencesUtils.getData("password",SplashActivity.this));

            ChatService.initIfNeed(this);

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

        }



        // Login to REST API
        //

    }
}