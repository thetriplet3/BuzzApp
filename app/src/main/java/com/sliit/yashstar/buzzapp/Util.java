package com.sliit.yashstar.buzzapp;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by Tharushan on 2018-03-17.
 */

public class Util {


    public static String GetCurrentDateTime() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        Date date = new Date();
        return dateFormat.format(date);
    }

    public static void SetCurretLocation(Activity activity, String lat, String lon) {
        SharedPreferences sharedPref =  activity.getSharedPreferences(activity.getString(R.string.str_saved_info), activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(activity.getString(R.string.str_current_loc_lat), lat);
        editor.putString(activity.getString(R.string.str_current_loc_lon), lon);
        editor.commit();
    }

    public static String GetSavedLatitude(Activity activity) {
        SharedPreferences sharedPref =  activity.getSharedPreferences(activity.getString(R.string.str_saved_info), activity.MODE_PRIVATE);
        return sharedPref.getString(activity.getString(R.string.str_current_loc_lat), "0");
    }

    public static String GetSavedLongitude(Activity activity) {
        SharedPreferences sharedPref =  activity.getSharedPreferences(activity.getString(R.string.str_saved_info), activity.MODE_PRIVATE);
        return sharedPref.getString(activity.getString(R.string.str_current_loc_lon), "0");
    }

    public static String GetSOSMessage(Activity activity) {
        SharedPreferences sharedPref =  activity.getSharedPreferences(activity.getString(R.string.str_saved_info), activity.MODE_PRIVATE);
        return sharedPref.getString(activity.getString(R.string.str_sos_message), activity.getString(R.string.settings_default_message));
    }
}
