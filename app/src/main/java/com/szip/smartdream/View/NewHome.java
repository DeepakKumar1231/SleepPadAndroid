package com.szip.smartdream.View;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.szip.smartdream.Controller.Fragment.AlarmClockFragment;
import com.szip.smartdream.Controller.Fragment.SleepFragment;
import com.szip.smartdream.MyApplication;
import com.szip.smartdream.R;
import com.szip.smartdream.Service.BleService;
import com.zhuoting.health.write.ProtocolWriter;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link NewHome#newInstance} factory method to
 * create an instance of this fragment.
 */
public class NewHome extends Fragment implements View.OnClickListener {

    private MyApplication app;
    private FragmentManager fm;
    private FragmentTransaction transaction;


    AlarmClockFragment alarmClockFragment = AlarmClockFragment.newInstance("");
    SleepFragment sleepFragment = SleepFragment.newInstance("");


    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private Button setUpAlarmBtn;
    private Button startmonitoringBtn;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public NewHome() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment NewHome.
     */
    // TODO: Rename and change types and number of parameters
    public static NewHome newInstance(String param1, String param2) {
        NewHome fragment = new NewHome();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.new_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initAllComponents(view);
        checkForMonitoring();
    }

    private void checkForMonitoring() {
        if(app.isStartSleep()){
            startmonitoringBtn.setText(getString(R.string.stopSleep));
        }else{
            startmonitoringBtn.setText(getString(R.string.startSleep));
        }
    }

    private void initAllComponents(View view) {
        app = (MyApplication) getActivity().getApplicationContext();

        setUpAlarmBtn=view.findViewById(R.id.setUpAlarmBtn);
        startmonitoringBtn=view.findViewById(R.id.startmonitoringBtn);

        setUpAlarmBtn.setOnClickListener(this);
        startmonitoringBtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.setUpAlarmBtn:
//                alarmClockFragment
                fm = requireFragmentManager();
                transaction = fm.beginTransaction();

                transaction.replace(R.id.fragment, alarmClockFragment);
                transaction.addToBackStack("SANJAY");
                transaction.commit();
                break;

            case R.id.startmonitoringBtn:
                if (BleService.getInstance().getConnectState()==2){//蓝牙连接着
                    if (startmonitoringBtn.getText().toString().equals(getString(R.string.startSleep))){
                        startmonitoringBtn.setText(getString(R.string.stopSleep));

                        app.setStartSleep(true);
                        BleService.getInstance().write(ProtocolWriter.writeForReadHealth((byte) 0x01));


                    }else {
                        startmonitoringBtn.setText(getString(R.string.startSleep));
                        app.setStartSleep(false);
                        BleService.getInstance().write(ProtocolWriter.writeForReadHealth((byte) 0x00));

                    }
                }else {
                    Toast.makeText(requireContext(), getString(R.string.lineError), Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

}