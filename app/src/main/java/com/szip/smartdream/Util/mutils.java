package com.szip.smartdream.Util;

import static android.support.v4.app.ActivityCompat.requestPermissions;
import static android.support.v4.content.PermissionChecker.PERMISSION_GRANTED;
import static android.support.v4.content.PermissionChecker.checkSelfPermission;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.util.Log;
import android.widget.EditText;
import android.widget.Toast;

import com.szip.smartdream.Controller.MainActivity;
import com.szip.smartdream.Controller.WelcomeActivity;

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

//    public static Boolean checkALlPermissions(){
//
//        if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_DENIED
//                || checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED
//                || checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED) {
//            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
//                    Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE} , );
//
//                return false;
//            }
//            return true;
//
//    }


}
