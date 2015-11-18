package app.wingman.utils;

import android.content.Context;
import android.content.SharedPreferences;


import com.quickblox.users.model.QBUser;

import java.util.ArrayList;

import app.wingman.ApplicationSingleton;


public class PreferencesUtils {

    public static void saveData(String name, String value, Context context) {

        try {

            SharedPreferences settings = context
                    .getSharedPreferences(app.wingman.ApplicationSingleton.PREFS_NAME, 0);

            SharedPreferences.Editor editor = settings.edit();

            editor.putString(name, value);
            editor.commit();
        } catch (NullPointerException ignored) {

        }

    }

    public static String getData(String name, Context context) {

        try {


            SharedPreferences settings = context
                    .getSharedPreferences(app.wingman.ApplicationSingleton.PREFS_NAME, 0);

            	return settings.getString(name, "0");
        } catch (NullPointerException ignored) {
            return "";
        }

    }

    /*
    save userlist in preference
     */
public static void SaveUsers(String key,ArrayList<QBUser> users,ArrayList<String> phone,Context context){


    try {

        SharedPreferences settings = context
                .getSharedPreferences(app.wingman.ApplicationSingleton.PREFS_NAME, 0);

        SharedPreferences.Editor editor = settings.edit();

        editor.putString(key, users.toString());

        editor.putString("phone", phone.toString());
        editor.commit();
    } catch (NullPointerException ignored) {

    }
}
}
