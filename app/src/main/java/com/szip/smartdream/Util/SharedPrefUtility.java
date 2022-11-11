package com.szip.smartdream.Util;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class SharedPrefUtility {

    private static PreferenceManager preferenceManager;
    private SharedPreferences userSharedPref;
    private SharedPreferences.Editor editor;
    private String USER_SHARED_PREF = "sleep_pad_android_pref";

     public SharedPrefUtility(final Context context) {
        userSharedPref = context.getSharedPreferences(USER_SHARED_PREF, Context.MODE_PRIVATE);
        editor = userSharedPref.edit();
    }


    public void setStringData(String key, String data){
         editor.putString(key, data);
         editor.commit();
    }

    public String getStringData(String key, String defaultValue) {
         return userSharedPref.getString(key, defaultValue);
    }

}
