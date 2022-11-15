package com.szip.smartdream.Controller.Fragment;

import static com.jonas.jgraph.graph.JcoolGraph.LINE_DASH_0;
import static com.szip.smartdream.Util.mutils.mLog;

import android.animation.AnimatorSet;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.jonas.jgraph.graph.JcoolGraph;
import com.jonas.jgraph.models.Jchart;
import com.szip.smartdream.Bean.DeviceClockIsUpdataBean;
import com.szip.smartdream.Bean.HealthBean;
import com.szip.smartdream.Bean.UpdataReportBean;
import com.szip.smartdream.Controller.RealTimeActivity;
import com.szip.smartdream.DB.DBModel.SleepInDayData;
import com.szip.smartdream.DB.LoadDataUtil;
import com.szip.smartdream.MyApplication;
import com.szip.smartdream.R;
import com.szip.smartdream.Service.BleService;
import com.szip.smartdream.Util.DateUtil;
import com.szip.smartdream.Util.MathUitl;
import com.szip.smartdream.Util.SharedPrefUtility;
import com.szip.smartdream.View.DateSelectView;
import com.szip.smartdream.View.intro.HeartRate;
import com.szip.smartdream.View.intro.Respiration;
import com.szip.smartdream.View.intro.SleepScore;
import com.zhuoting.health.write.ProtocolWriter;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import antonkozyriatskyi.circularprogressindicator.BuildConfig;
import antonkozyriatskyi.circularprogressindicator.CircularProgressIndicator;
import cn.aigestudio.datepicker.cons.DPMode;
import cn.aigestudio.datepicker.views.DatePicker;

/**
 * Created by Administrator on 2019/1/23.
 */

public class SleepFragment extends BaseFragment {


    private static final String TAG = "";
    private final AnimatorSet set = new AnimatorSet();
    private final List<Jchart> lines1 = new ArrayList<>();
    private final List<Jchart> lines2 = new ArrayList<>();
    private final List<Jchart> lines3 = new ArrayList<>();
    private final List<Jchart> lines4 = new ArrayList<>();
    /**
     * 按钮出现的动画
     */
    private final ScaleAnimation scaleAnimation = new ScaleAnimation(1f, 1f, 0f,
            1f, Animation.RELATIVE_TO_PARENT, 0.5f, Animation.RELATIVE_TO_PARENT, 1f);
    private final ScaleAnimation scaleAnimation1 = new ScaleAnimation(1f, 1f, 0f,
            1f, Animation.RELATIVE_TO_PARENT, 0.5f, Animation.RELATIVE_TO_PARENT, 1f);
    Respiration respiration = Respiration.newInstance("", "");
    HeartRate heartRate = HeartRate.newInstance("", "");
    SleepScore sleepFragment = SleepScore.newInstance("", "");
    private FragmentManager fm;
    private FragmentTransaction transaction;
    private DateSelectView dateSelectView;
    private String getTime;
    private SharedPrefUtility mSharedPref;
    private String mTimeStat = "";
    private String mTimeStop = "";
    //---------
    private JcoolGraph mLineChar;
    /**
     * 事件监听
     */


    private final Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 100:
                    if (lines1.size() != 0 && lines2.size() != 0 && lines3.size() != 0 && lines4.size() != 0) {
                        mLineChar.aniChangeData(lines1);
                    }
                    break;
            }
        }
    };
    //---------
    private int mondayDate;
    private int monthSize;
    private int monthDate;
    /**
     * 开始睡眠/闹钟控件
     */
    private ConstraintLayout sleepRl;
    private TextView sleepTv;
    private ConstraintLayout clockRl;
    private TextView clockTv, dayTv, timeTv, timeStopTv, timeinBedTv, timeinAsleepTv;
    private CircularProgressIndicator circular_progressCurrentDay, mondayProgressBar, tuesdayProgressBar, wednesdayProgressBar, thursdayProgressBar, fridayProgressBar, saturdayProgressBar, sundayProgressBar;
    /**
     * 实时健康数据以及连接状态
     */
    private ConstraintLayout breathLl, heartLl, clockLl;
    private TextView heartTv, dateTv, dateByCalender;
    private TextView breathTv;
    private MyApplication app;
    private final View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.respirationRateIv:

                    fm = requireFragmentManager();
                    transaction = fm.beginTransaction();
                    transaction.replace(R.id.fragment, respiration);
                    transaction.commit();
                    break;
                case R.id.heartBeatIv:
                    fm = requireFragmentManager();
                    transaction = fm.beginTransaction();
                    transaction.replace(R.id.fragment, heartRate);
                    transaction.commit();

                    break;
                case R.id.circular_progressCurrentDay:
                    fm = requireFragmentManager();
                    transaction = fm.beginTransaction();
                    transaction.replace(R.id.fragment, sleepFragment);
                    transaction.commit();
                    break;
                case R.id.heartLl:
                case R.id.breathLl: {
                    if (BleService.getInstance().getConnectState() == 2) {//蓝牙连接着
                        Intent intent = new Intent();
                        intent.setClass(getActivity(), RealTimeActivity.class);
                        startActivity(intent);
                    } else {
                        showToast(getString(R.string.lineError));
                    }
                }
                break;
                case R.id.sleepRl:
                    if (BleService.getInstance().getConnectState() == 2) {//蓝牙连接着
                        if (sleepTv.getText().toString().equals(getString(R.string.startSleep))) {
                            sleepTv.setText(getString(R.string.stopSleep));
                            set.start();
                            app.setStartSleep(true);
                            BleService.getInstance().write(ProtocolWriter.writeForReadHealth((byte) 0x01));
                        } else {
                            sleepTv.setText(getString(R.string.startSleep));
                            app.setStartSleep(false);
                            BleService.getInstance().write(ProtocolWriter.writeForReadHealth((byte) 0x00));
                            set.end();
                        }
                    } else {
                        showToast(getString(R.string.lineError));
                    }
                    break;

                case R.id.calenderIv:
                    String date = DateUtil.getDateToString(app.getReportDate());
                    //TODO 选择时间
                    final AlertDialog dialog = new AlertDialog.Builder(requireActivity()).create();
                    dialog.show();
                    final DatePicker picker = new DatePicker(requireActivity());
                    picker.setDate(Integer.valueOf(date.substring(0, 4)), Integer.valueOf(date.substring(5, 7)), Integer.valueOf(date.substring(8, 10)));
                    picker.setMode(DPMode.SINGLE);
                    picker.setFestivalDisplay(false);
                    picker.setHolidayDisplay(false);
                    picker.setTodayDisplay(true);


                    picker.setOnDatePickedListener(new DatePicker.OnDatePickedListener() {
                        @RequiresApi(api = Build.VERSION_CODES.O)
                        @Override
                        public void onDatePicked(String date) {

                            if (date != null) {
                                dateTv.setVisibility(View.GONE);
                                dateByCalender.setVisibility(View.VISIBLE);
                                dateByCalender.setText(date);
                            }

                            if (LoadDataUtil.newInstance().dataCanGet(date)) {
                                app.setReportDate(DateUtil.getStringToDate(date));
                                EventBus.getDefault().post(new UpdataReportBean(true));
                                dialog.dismiss();
                            } else {
                                showToast(getString(R.string.future));
                            }
                        }
                    });
                    ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(
                            ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                    dialog.getWindow().setContentView(picker, params);
                    dialog.getWindow().setGravity(Gravity.CENTER);
                    break;

                case R.id.backView:
//                    startSector(false);
                    break;

                case R.id.clockRl:
//                    fm = requireFragmentManager();
//                    transaction = fm.beginTransaction();
//
//                    transaction.replace(R.id.fragment, AlarmClockFragment );
//                    transaction.addToBackStack("SANJAY");
//                    transaction.commit();
//                    break;
            }
        }
    };
    private boolean isDisconnect;
    private ImageView menuIv;
    private ImageView heartBeatIv;
    private ImageView respirationRateIv;
    private ImageView calenderIv;

    /**
     * 返回一个fragment实例，Activity中调用
     */

    public static SleepFragment newInstance(String param) {
        Bundle bundle = new Bundle();
        bundle.putString("param", param);
        SleepFragment fragment = new SleepFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

//    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
//        View view = inflater.inflate(R.layout.fragment_sleep, container, false);
//        return view;
//    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return super.onCreateView(inflater, container, savedInstanceState);

    }


    private void checkForMonitoring() {
        if (app.isStartSleep()) {
            sleepTv.setText(getString(R.string.stopSleep));
        } else {
            sleepTv.setText(getString(R.string.startSleep));
        }
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_sleep;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void afterOnCreated(Bundle savedInstanceState) {
        app = (MyApplication) getActivity().getApplicationContext();
        initView();
        initEvent();
        initAnimator();
        checkForMonitoring();


        Calendar cal = Calendar.getInstance();
        SimpleDateFormat format1 = new SimpleDateFormat("yyyy-MM-dd");
        String formatted = format1.format(cal.getTime());
        dateTv.setText(formatted.toString());

    }

    @Override
    public void onResume() {
        super.onResume();
        EventBus.getDefault().register(this);
        if (BleService.getInstance().getConnectState() == 2) {
            if (app.isStartSleep())
                BleService.getInstance().write(ProtocolWriter.writeForReadHealth((byte) 0x01));
        }
        startAnimator();
        if (app.getClockList() != null && app.getClockList().size() != 0) {
            Log.d("CLOCK******", "update clock = " + MathUitl.getNearClock(app.getClockList()));
            // clockTv.setText(MathUitl.getNearClock(app.getClockList()));
        } else {
            //clockTv.setText("");
        }


        timeTv.setText(mTimeStat.substring(mTimeStat.indexOf(" ") + 1,mTimeStat.indexOf(":") + 3));
        if(!mTimeStop.isEmpty()){
            timeStopTv.setText(mTimeStop.substring(mTimeStop.indexOf(" ") + 1,mTimeStop.indexOf(":") + 3));
        }


        long one =Date.parse(mTimeStat);
        long two =Date.parse(mTimeStat);

        //long startTime = Date.parse(mTimeStat);

        //Log.e(TAG, "startTime"+startTime );

        mLog("-----------------------");
        mLog(mTimeStat);
        mLog(mTimeStop);
        mLog("-----------------------");

        timeinBedTv.setText("SleepingTimeGap");
        timeinAsleepTv.setText("WokingTimeGap");

    }

    @Override
    public void onPause() {
        super.onPause();
        EventBus.getDefault().unregister(this);
        if (app.isStartSleep())
            BleService.getInstance().write(ProtocolWriter.writeForReadHealth((byte) 0x00));

    }

    /**
     * 初始化界面
     */

    private void updataDate() {
//        int []date;
//        date = DateUtil.getMonth(app.getReportDate());
//        monthDate = app.getReportDate()-date[0];
//        monthSize = date[1];

        mondayDate = app.getReportDate() - DateUtil.getWeek(app.getReportDate());
    }

    private void initView() {
        updataDate();

        mSharedPref = new SharedPrefUtility(requireContext());
        mTimeStat = mSharedPref.getStringData("timeStat", "");

        mTimeStop = mSharedPref.getStringData("timeStop", "");


        // mSharedPref.setStringData("timeStat", "");

        respirationRateIv = getView().findViewById(R.id.respirationRateIv);
        circular_progressCurrentDay = getView().findViewById(R.id.circular_progressCurrentDay);
        mondayProgressBar = getView().findViewById(R.id.circular_progressMonday);
        tuesdayProgressBar = getView().findViewById(R.id.circular_progressTuesday);
        wednesdayProgressBar = getView().findViewById(R.id.circular_progressWednesday);
        thursdayProgressBar = getView().findViewById(R.id.circular_progressThursday);
        fridayProgressBar = getView().findViewById(R.id.circular_progressFriday);
        saturdayProgressBar = getView().findViewById(R.id.circular_progressSaturday);
        sundayProgressBar = getView().findViewById(R.id.circular_progressSunday);
        // clockTv = getView().findViewById(R.id.clockTv);
        heartBeatIv = getView().findViewById(R.id.heartBeatIv);
        clockRl = getView().findViewById(R.id.clockRl);
        sleepRl = getView().findViewById(R.id.sleepRl);
        sleepTv = getView().findViewById(R.id.sleepTv);
        breathLl = getView().findViewById(R.id.breathLl);
        heartLl = getView().findViewById(R.id.heartLl);
        breathTv = getView().findViewById(R.id.breathTv);
        heartTv = getView().findViewById(R.id.heartTv);
        dateTv = getView().findViewById(R.id.dateTv);
        dateByCalender = getView().findViewById(R.id.dateByCalenderTv);
        menuIv = getView().findViewById(R.id.menuIv);
        dayTv = getView().findViewById(R.id.dayTv);
        timeTv = getView().findViewById(R.id.timeTv);
        timeStopTv = getView().findViewById(R.id.clockTv);
        calenderIv = getView().findViewById(R.id.calenderIv);
        timeinBedTv = getView().findViewById(R.id.timeinBedTv);
        timeinAsleepTv = getView().findViewById(R.id.timeinAsleepTv);

        menuIv.setClickable(true);


        //Function to calculate thime for sleeping
        sleepingTime();


        //Enter progress bar Static Values
        tuesdayProgressBar.setMaxProgress(480f);
        tuesdayProgressBar.setCurrentProgress(210D);

        wednesdayProgressBar.setMaxProgress(480f);
        wednesdayProgressBar.setCurrentProgress(280D);

        thursdayProgressBar.setMaxProgress(480f);
        thursdayProgressBar.setCurrentProgress(310D);

        fridayProgressBar.setMaxProgress(480f);
        fridayProgressBar.setCurrentProgress(390D);

        saturdayProgressBar.setMaxProgress(480f);
        saturdayProgressBar.setCurrentProgress(200D);

        sundayProgressBar.setMaxProgress(480f);
        sundayProgressBar.setCurrentProgress(110D);


        mLineChar = getView().findViewById(R.id.sug_recode_line);
        mLineChar.setXvelue(5, 0, 1560);
        mLineChar.setGraphStyle(1);
        mLineChar.setLineStyle(1);
        mLineChar.setLineMode(LINE_DASH_0);
        mLineChar.setSleepFlag(2);
        mLineChar.drawPoint(true);
        mLineChar.setYaxisValues(0, 255, 5);
        mLineChar.setShaderAreaColors(Color.parseColor("#bb21a0bf"), Color.TRANSPARENT);
        mLineChar.setLinePointRadio((int) mLineChar.getLineWidth());
        mLineChar.setNormalColor(Color.parseColor("#21a0bf"));
        // mLineChar.setOnTouchListener(onTouchListener);
        if (!mLineChar.isDetachFlag())
            mLineChar.feedData(lines1);


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            dayTv.setText(LocalDate.now().getDayOfWeek().name());
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            dayTv.setText(LocalDate.now().getDayOfWeek().name());
        }


        //----------

        List<SleepInDayData> sleepInDayDataArrayList = LoadDataUtil.newInstance().loadSleepStateListInWeek(mondayDate);
//        List<SleepInDayData> sleepInLastDayArrayList = LoadDataUtil.newInstance().loadSleepStateListInDayLast()


        for (int i = 0; i < sleepInDayDataArrayList.size(); i++) {
            /**
             * 拿周睡眠数据
             * */
            lines1.add(new Jchart(sleepInDayDataArrayList.get(i).getAllTime(), String.format("%d", i + 1),
                    (float) sleepInDayDataArrayList.get(i).deepSleepInDay / (float) sleepInDayDataArrayList.get(i).getAllTime(),
                    (float) (sleepInDayDataArrayList.get(i).deepSleepInDay + sleepInDayDataArrayList.get(i).middleSleepInDay) / (float)
                            sleepInDayDataArrayList.get(i).getAllTime(),
                    (float) (sleepInDayDataArrayList.get(i).deepSleepInDay + sleepInDayDataArrayList.get(i).middleSleepInDay +
                            sleepInDayDataArrayList.get(i).lightSleepInDay) / (float) sleepInDayDataArrayList.get(i).getAllTime()));
        }
        getAverageData(sleepInDayDataArrayList);
    }

    private void sleepingTime() {


    }


    /**
     * 获取周平均数据
     */
    private void getAverageData(List<SleepInDayData> sleepInDayDataList) {
        int allSum = 0;
        int deepSum = 0;
        int midSum = 0;
        int lightSum = 0;
        int awakeSum = 0;
        int heartSum = 0;
        int breathSum = 0;
        int turnSum = 0;
        int size = 0;

        for (int i = 0; i < sleepInDayDataList.size(); i++) {
            if (sleepInDayDataList.get(i).getAllTime() != 0) {
                allSum += sleepInDayDataList.get(i).getAllTime();
//                deepSum += sleepInDayDataList.get(i).deepSleepInDay;
//                midSum += sleepInDayDataList.get(i).middleSleepInDay;
//                lightSum += sleepInDayDataList.get(i).lightSleepInDay;
//                awakeSum += sleepInDayDataList.get(i).wakeSleepInDay;
                size++;


                circular_progressCurrentDay.setMaxProgress(480);
                circular_progressCurrentDay.setCurrentProgress(allSum += sleepInDayDataList.get(i).getAllTime());

                mondayProgressBar.setMaxProgress(480);
                mondayProgressBar.setCurrentProgress(allSum += sleepInDayDataList.get(i).getAllTime());

            }

        }


    }

    //----------

//        animatorIv1 = getView().findViewById(R.id.animIv1);
//        animatorIv2 = getView().findViewById(R.id.animIv2);
//        animatorIv3 = getView().findViewById(R.id.animIv3);


    /**
     * 初始化事件监听
     *
     * @return
     */


    private void initEvent() {

        sleepRl.setOnClickListener(onClickListener);
        breathLl.setOnClickListener(onClickListener);
        heartLl.setOnClickListener(onClickListener);
        heartBeatIv.setOnClickListener(onClickListener);
        // clockLl.setOnClickListener(onClickListener);
        circular_progressCurrentDay.setOnClickListener(onClickListener);
        calenderIv.setOnClickListener(onClickListener);

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // menuIv.setOnClickListener(functionOne());

        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(requireContext());
        String startTime = pref.getString("timeStat", "");
        Log.e(TAG, "onViewCreated: " + startTime);

        if (BuildConfig.DEBUG) {
            // do something for a debug build
            List<SleepInDayData> sleepInDayDataArrayList = LoadDataUtil.newInstance().loadSleepStateListInWeek(mondayDate);
            Log.i("currentDayData", new Gson().toJson(sleepInDayDataArrayList));


        }
    }

    /**
     * 初始化动画
     */
    private void initAnimator() {

//        anim1 = ObjectAnimator.ofFloat(animatorIv1,"alpha",0,1,1,1,0,0,0);
//        anim2 = ObjectAnimator.ofFloat(animatorIv2,"alpha",0,0,1,1,1,0,0);
//        anim3 = ObjectAnimator.ofFloat(animatorIv3,"alpha",0,0,0,1,1,1,0);
//        anim1.setInterpolator(new LinearInterpolator());
//        anim1.setRepeatCount(-1);
//        anim2.setInterpolator(new LinearInterpolator());
//        anim2.setRepeatCount(-1);
//        anim3.setInterpolator(new LinearInterpolator());
//        anim3.setRepeatCount(-1);
//
//        set.setDuration(6000);
//        set.play(anim1).with(anim2).with(anim3);
//
//        scaleAnimation.setDuration(1000);//设置动画持续时间
//        scaleAnimation.setRepeatCount(0);//设置重复次数
//        scaleAnimation.setFillAfter(true);//动画执行完后是否停留在执行完的状态
//
//        scaleAnimation1.setDuration(1500);//设置动画持续时间
//        scaleAnimation1.setRepeatCount(0);//设置重复次数
//        scaleAnimation1.setFillAfter(true);//动画执行完后是否停留在执行完的状态
    }

    /**
     * 开始起始动画
     */
    private void startAnimator() {

//        animatorIv1.setVisibility(View.VISIBLE);
//        new Handler().postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                animatorIv2.setVisibility(View.VISIBLE);
//            }
//        },500);
//
//        new Handler().postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                animatorIv3.setVisibility(View.VISIBLE);
//            }
//        },1000);
//
//        sleepRl.startAnimation(scaleAnimation);
//        clockRl.startAnimation(scaleAnimation1);
    }

    /**
     * 实时更新心率呼吸率
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onUpdateHealth(HealthBean healthBean) {
        breathTv.setText(healthBean.getBreath());
        heartTv.setText(healthBean.getHeart());
    }

    /**
     * 显示最近一次的闹钟
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onUpdateClock(DeviceClockIsUpdataBean connectBean) {
        Log.d("CLOCK******", "update clock");
        if (app.getClockList() != null && app.getClockList().size() != 0)
            clockTv.setText(MathUitl.getNearClock(app.getClockList()));
        else
            clockTv.setText("");
    }
}
