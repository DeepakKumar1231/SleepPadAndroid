package com.szip.smartdream.Util;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.text.TextUtils;
import android.util.Log;
import android.widget.EditText;
import android.widget.Toast;

public class mutils {
    public static void mLog(String message) {
        String TAG = "SANJAY";
        Log.i(TAG, "mLog: ------------------------------");
        Log.i(TAG, "mLog: " + message);
        Log.i(TAG, "mLog: ------------------------------");

    }

    public static Boolean isValidText(String text, EditText editText) {
        if (TextUtils.isEmpty(text)) {
            editText.requestFocus();
            editText.setError("Mandatory");
            return false;
        }
        return true;
    }

    public static void mToast(Context context, String message) {
        mLog("Toast Log :: --->" + message);
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public static Boolean checkALlPermissions(Activity context) {

        if (context.checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_DENIED
                || context.checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED
                || context.checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED) {
            context.requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE}, 100);

            return false;
        }
        return true;

    }


}
