package com.szip.smartdream.Controller;

import static com.szip.smartdream.MyApplication.FILE;
import static com.szip.smartdream.Util.mutils.checkALlPermissions;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;
import android.view.View;

import com.szip.smartdream.MyApplication;
import com.szip.smartdream.R;
import com.szip.smartdream.Service.BleService;
import com.szip.smartdream.Util.MathUitl;
import com.szip.smartdream.View.MyAlerDialog;

public class WelcomeActivity extends BaseActivity  {

    public static final int REQUEST_ID_MULTIPLE_PERMISSIONS = 1;
    /**
     * 延时线程
     */
    private Thread thread;
    private int time = 3;
    private int SleepEECode = 100;
    /**
     * 轻量级文件
     */
    private SharedPreferences sharedPreferencesp;

    private boolean isFirst;
    private MyApplication app;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_welcome);




        app = (MyApplication) getApplicationContext();
        if (sharedPreferencesp == null) {
            sharedPreferencesp = getSharedPreferences(FILE, MODE_PRIVATE);
        }

        isFirst = sharedPreferencesp.getBoolean("isFirst", true);
        app.setUserInfo(MathUitl.loadInfoData(sharedPreferencesp));

        if (isFirst) {
            MyAlerDialog.getSingle().showAlerDialogWithPrivacy(false
                    , flag -> {
                        if (flag) {
                            sharedPreferencesp.edit().putBoolean("isFirst", false).commit();
                            if (checkALlPermissions(WelcomeActivity.this)) {
                                init();
                            } else {
                                startActivity(
                                        new Intent(
                                                android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                                                Uri.fromParts("package", getPackageName(), null)
                                        )
                                );
                            }
                        } else {
                            finish();
                        }
                    }, this);
        } else {
            init();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onResume() {
        super.onResume();
        checkALlPermissions(this);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == SleepEECode) {
            int code = grantResults[0];
            int code1 = grantResults[1];
            int code2 = grantResults[2];
            if (code == PackageManager.PERMISSION_GRANTED && code1 == PackageManager.PERMISSION_GRANTED && code2 == PackageManager.PERMISSION_GRANTED) {
                //initData();
                //init();
            } else {
                // WelcomeActivity.this.finish();
                //When user Deny the Permissions User Send to the Settings And Permission Ui of Mobile

                startActivity(
                        new Intent(
                                android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                                Uri.fromParts("package", getPackageName(), null)
                        )
                );

            }
        }
    }


//    private void initData() {
//        thread = new Thread(this);
//        thread.start();
//    }
//
//    public void run() {
//        try {
//            while (time != 0) {
//                Thread.sleep(1000);
//                time = time - 1;
//            }
//            init();
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
//    }


    @Override
    public View onCreateView(String name, Context context, AttributeSet attrs) {
        return super.onCreateView(name, context, attrs);


    }

    private void init() {

        if (app.getStartState() == 0) {//已登录
            if (app.getUserInfo().getDeviceCode() != null) {//已绑定
                BleService.getInstance().setmMac(app.getUserInfo().getDeviceCode());
                BleService.getInstance().startConnectDevice();
                Intent guiIntent = new Intent();
                guiIntent.setClass(WelcomeActivity.this, MainActivity.class);
                startActivity(guiIntent);
                finish();
            } else {//未绑定
                Intent in = new Intent();
                in.setClass(WelcomeActivity.this, FindDeviceActivity.class);
                startActivity(in);
                finish();
            }
        } else if (app.getStartState() == 1) {//未登录
            Intent in = new Intent();
            in.setClass(WelcomeActivity.this, LoginActivity.class);
            startActivity(in);
            finish();
        } else {//登陆过期
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    showToast(getString(R.string.tokenTimeout));
                }
            });
            Intent in = new Intent();
            in.setClass(WelcomeActivity.this, LoginActivity.class);
            startActivity(in);
            finish();
        }
    }
}
