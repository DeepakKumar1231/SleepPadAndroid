package com.szip.smartdream.Util;

import android.content.Context;
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

}
