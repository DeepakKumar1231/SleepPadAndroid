package com.szip.smartdream.Controller;

import static com.szip.smartdream.MyApplication.FILE;
import static com.szip.smartdream.Util.HttpMessgeUtil.DOWNLOADDATA_FLAG;
import static com.szip.smartdream.Util.HttpMessgeUtil.GETALARM_FLAG;
import static com.szip.smartdream.Util.HttpMessgeUtil.LOGIN_FLAG;
import static com.szip.smartdream.Util.HttpMessgeUtil.REGIST_FLAG;
import static com.szip.smartdream.Util.mutils.mLog;
import static com.szip.smartdream.Util.mutils.mToast;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import com.szip.smartdream.Adapter.MyPagerAdapter;
import com.szip.smartdream.Bean.HttpBean.ClockDataBean;
import com.szip.smartdream.Bean.HttpBean.LoginBean;
import com.szip.smartdream.Controller.Fragment.LoginForMailFragment;
import com.szip.smartdream.Controller.Fragment.LoginForMailFragmentTwo;
import com.szip.smartdream.Controller.Fragment.LoginForPhoneFragment;
import com.szip.smartdream.Controller.Fragment.RegisterForMailFragment;
import com.szip.smartdream.Dashboard;
import com.szip.smartdream.Interface.HttpCallbackWithClockData;
import com.szip.smartdream.Interface.HttpCallbackWithLogin;
import com.szip.smartdream.Interface.HttpCallbackWithReport;
import com.szip.smartdream.Interface.OnClickForLogin;
import com.szip.smartdream.Interface.OnClickForRegister;
import com.szip.smartdream.Model.ProgressHudModel;
import com.szip.smartdream.MyApplication;
import com.szip.smartdream.R;
import com.szip.smartdream.Service.BleService;
import com.szip.smartdream.Util.HttpMessgeUtil;
import com.szip.smartdream.Util.StatusBarCompat;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;

public class LoginActivity extends BaseActivity implements HttpCallbackWithLogin, HttpCallbackWithReport, HttpCallbackWithClockData {

    /**
     * 回调标识
     */
    private final static int REQUEST_CODE = 10;
    private Context mContext;
    private TabLayout mTab;
    private ViewPager mPager;
    private TextView registerTv;
    private boolean rememberPassword;
    private String passwordL;
    private String mailR,phoneR,passwordR,codeR;
    /**
     * 轻量级文件
     */
    private SharedPreferences sharedPreferencesp;
    /**
     * 隐私条款
     */
    private CheckBox checkBox;
    private TextView privacyTv;
    /**
     * 点击事件监听
     */
    private View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
//                case R.id.registerTv:{
//                    Intent intent = new Intent();
//                    intent.setClass(LoginActivity.this,RegisterActivity.class);
//                    startActivityForResult(intent,REQUEST_CODE);
//                }
//                break;
                case R.id.privacyTv: {
                    Intent intent = new Intent();
                    intent.setClass(LoginActivity.this, PrivacyActivity.class);
                    startActivity(intent);
                }
                break;
            }
        }
    };
    /**
     * 登录
     */
    private OnClickForLogin clickForLogin = new OnClickForLogin() {
        @Override
        public void onLogin(String code, String user, String password, boolean remember) {
            if (false){
                showToast(getString(R.string.checkPrivacy));
                return;
            }
            passwordL = password;
            rememberPassword = remember;
            ProgressHudModel.newInstance().show(LoginActivity.this,getString(R.string.logging),getString(R.string.httpError),10000);
            try {
                if (code.equals("")) {
                    HttpMessgeUtil.getInstance(mContext).postLogin("2", "", "", user, password, LOGIN_FLAG);//邮箱
                }  else {
                    HttpMessgeUtil.getInstance(mContext).postLogin("1", code, user, "", password, LOGIN_FLAG);//手机
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        getSupportActionBar().hide();
        setContentView(R.layout.activity_login);
        Toolbar myToolbar = findViewById(R.id.toolbar);
        setSupportActionBar(myToolbar);
        mContext = getApplicationContext();
        initView();
        initEvent();
        //initPager();
        initPagerTwo();

//        Intent intent = new Intent(this , Dashboard.class);
//        startActivity(intent);
    }

    private void initPagerTwo() {
        // 创建一个集合,装填Fragment
        ArrayList<Fragment> fragments = new ArrayList<>();
        // 装填
        fragments.add(LoginForMailFragmentTwo.newInstance("szip"));
        fragments.add(RegisterForMailFragment.newInstance("szip"));
        ((LoginForMailFragmentTwo) fragments.get(0)).setClickForLogin(clickForLogin);
        ((RegisterForMailFragment)fragments.get(1)).setOnClickForRegister(clickForRegister);
        // 创建ViewPager适配器
        MyPagerAdapter myPagerAdapter = new MyPagerAdapter(getSupportFragmentManager());
        myPagerAdapter.setFragmentArrayList(fragments);
        // 给ViewPager设置适配器
        mPager.setAdapter(myPagerAdapter);
        // TabLayout 指示器 (记得自己手动创建4个Fragment,注意是 app包下的Fragment 还是 V4包下的 Fragment)
        mTab.addTab(mTab.newTab());
        mTab.addTab(mTab.newTab());
        mTab.setTabTextColors(getResources().getColor(R.color.white), getResources().getColor(R.color.white));
        // 使用 TabLayout 和 ViewPager 相关联
        mTab.setupWithViewPager(mPager);
        // TabLayout指示器添加文本
        mTab.getTabAt(0).setText("SIGN IN");
        mTab.getTabAt(1).setText("SIGN UP");
    }

    @Override
    protected void onResume() {
        super.onResume();
        HttpMessgeUtil.getInstance(mContext).setHttpCallbackWithLogin(this);
        HttpMessgeUtil.getInstance(mContext).setHttpCallbackWithReport(this);
        HttpMessgeUtil.getInstance(mContext).setHttpCallbackWithClockData(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        HttpMessgeUtil.getInstance(mContext).setHttpCallbackWithLogin(null);
        HttpMessgeUtil.getInstance(mContext).setHttpCallbackWithReport(null);
        HttpMessgeUtil.getInstance(mContext).setHttpCallbackWithClockData(null);
    }

    /**
     * 初始化界面
     */
    private void initView() {
        StatusBarCompat.translucentStatusBar(LoginActivity.this, true);

        mTab = findViewById(R.id.tabLayout);
        //checkBox = findViewById(R.id.checkbox);
        privacyTv = findViewById(R.id.nextBtn);
        mPager = findViewById(R.id.loginViewPager);
        //registerTv = findViewById(R.id.registerTv);
    }

    /**
     * 初始化滑动页面
     */
    private void initPager() {
        // 创建一个集合,装填Fragment
        ArrayList<Fragment> fragments = new ArrayList<>();
        // 装填
        fragments.add(LoginForPhoneFragment.newInstance("szip"));
        fragments.add(LoginForMailFragment.newInstance("szip"));
        ((LoginForPhoneFragment) fragments.get(0)).setClickForLogin(clickForLogin);
        ((LoginForMailFragment) fragments.get(1)).setClickForLogin(clickForLogin);
        // 创建ViewPager适配器
        MyPagerAdapter myPagerAdapter = new MyPagerAdapter(getSupportFragmentManager());
        myPagerAdapter.setFragmentArrayList(fragments);
        // 给ViewPager设置适配器
        mPager.setAdapter(myPagerAdapter);
        // TabLayout 指示器 (记得自己手动创建4个Fragment,注意是 app包下的Fragment 还是 V4包下的 Fragment)
        mTab.addTab(mTab.newTab());
        mTab.addTab(mTab.newTab());
        mTab.setTabTextColors(getResources().getColor(R.color.white), getResources().getColor(R.color.white));
        // 使用 TabLayout 和 ViewPager 相关联
        mTab.setupWithViewPager(mPager);
        // TabLayout指示器添加文本
        mTab.getTabAt(0).setText("SIGN IN");
        mTab.getTabAt(1).setText("SIGN UP");
    }

    /**
     * 初始化事件监听
     */
    private void initEvent() {
        // registerTv.setOnClickListener(onClickListener);
        // privacyTv.setOnClickListener(onClickListener);
        //forgetTv.setOnClickListener(onClickListener);
    }

    /**
     * 登录接口回调
     */
    @Override
    public void onLogin(LoginBean loginBean) {
        ProgressHudModel.newInstance().diss();
        HttpMessgeUtil.getInstance(mContext).setToken(loginBean.getData().getToken());
        if (sharedPreferencesp == null)
            sharedPreferencesp = getSharedPreferences(FILE, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferencesp.edit();
        editor.putString("token", loginBean.getData().getToken());
        editor.putString("phone", loginBean.getData().getUserInfo().getPhoneNumber());
        editor.putString("mail", loginBean.getData().getUserInfo().getEmail());
        ((MyApplication) getApplicationContext()).setUserInfo(loginBean.getData().getUserInfo());
        if (rememberPassword)
            editor.putString("password", passwordL);
        else
            editor.putString("password", "");
        if (loginBean.getData().getUserInfo().getDeviceCode() == null) {//如果未绑定手环，跳到绑定页面
            startActivity(new Intent(mContext, FindDeviceActivity.class));
            finish();
        } else {//如果已绑定睡眠带，获取闹钟列表
            BleService.getInstance().setmMac(loginBean.getData().getUserInfo().getDeviceCode());
            try {
                HttpMessgeUtil.getInstance(mContext).getForGetClockList(GETALARM_FLAG);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        editor.commit();
    }

    /**
     * 获取数据接口回调
     */
    @Override
    public void onReport(boolean isNewData) {
        BleService.getInstance().startConnectDevice();
        startActivity(new Intent(mContext, MainActivity.class));
        finish();
    }

    /**
     * 获取闹钟接口回调
     */
    @Override
    public void onClockData(ClockDataBean clockDataBean) {
        if (clockDataBean.getData().getArray() != null) {
            ((MyApplication) getApplicationContext()).setClockList1(clockDataBean.getData().getArray());
        }
        try {
            Calendar calendar = Calendar.getInstance();
            calendar.set(Calendar.HOUR_OF_DAY, 5);
            calendar.set(Calendar.MINUTE, 0);
            calendar.set(Calendar.SECOND, 0);
            calendar.set(Calendar.MILLISECOND, 0);
            HttpMessgeUtil.getInstance(mContext).getForDownloadReportData("" + (calendar.getTimeInMillis() / 1000 - 30 * 24 * 60 * 60),
                    "30", DOWNLOADDATA_FLAG);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Activity回调函数
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE && resultCode == 10) {
            final String string = data.getStringExtra("STRING");
            if (string != null && string.equals("exit")) {
                finish();
            }
        }
    }
    private OnClickForRegister clickForRegister = new OnClickForRegister() {

        @Override
        public void onRegisterForPhone(String country, String code, String phone, String password, String verificationCode) {

        }

        @Override
        public void onRegisterForMail(String mail, String password,String verificationCode) {
            ProgressHudModel.newInstance().show(LoginActivity.this,
                    getString(R.string.waitting),getString(R.string.httpError),10000);
            mailR = mail;
            passwordR = password;
            try {
                HttpMessgeUtil.getInstance(mContext).postRegister("2","","",mail,verificationCode,
                        password,REGIST_FLAG);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    };
}
