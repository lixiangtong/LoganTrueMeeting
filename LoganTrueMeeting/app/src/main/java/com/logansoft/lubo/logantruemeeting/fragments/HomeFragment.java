package com.logansoft.lubo.logantruemeeting.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.app.Fragment;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.logansoft.lubo.logantruemeeting.MeetingActivity;
import com.logansoft.lubo.logantruemeeting.R;
import com.logansoft.lubo.logantruemeeting.adapters.RecyclerViewAdapter;
import com.logansoft.lubo.logantruemeeting.utils.DividerItemDecoration;
import com.zte.ucsp.vtcoresdk.jni.CallAgentNative;
import com.zte.ucsp.vtcoresdk.jni.ConferenceAgentNative;
import com.zte.ucsp.vtcoresdk.jni.EventCenterNotifier;
import com.zte.ucsp.vtcoresdk.jni.conference.OrderConfInfo;
import com.zte.ucsp.vtcoresdk.util.Constants;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * Created by logansoft on 2017/8/23.
 */

public class HomeFragment extends Fragment implements EventCenterNotifier.ICallStatusListener, EventCenterNotifier.IIncomingCallListener ,RecyclerViewAdapter.OnItemClickListener{

    private static final String TAG = "HomeFragment";
    @BindView(R.id.ctl)
    CollapsingToolbarLayout ctl;
    @BindView(R.id.abl)
    AppBarLayout abl;
    @BindView(R.id.left_button)
    TextView leftButton;
    @BindView(R.id.title)
    TextView title;
    @BindView(R.id.right_button)
    TextView rightButton;
    @BindView(R.id.rl)
    RelativeLayout rl;
    @BindView(R.id.rv)
    RecyclerView rv;
    @BindView(R.id.lv)
    ListView lv;
    @BindView(R.id.nsv)
    NestedScrollView nsv;
    @BindView(R.id.cl)
    CoordinatorLayout cl;
    Unbinder unbinder;

    private static final int CALL_SUCCESS = Constants.EVENT_MAIN_ACTIVITY_BASE + 1;
    private static final int CALL_FAIL = Constants.EVENT_MAIN_ACTIVITY_BASE + 2;
    private static final int COMING_CALL = Constants.EVENT_MAIN_ACTIVITY_BASE + 4;
    private static final int HANGUP_SUCCESS = Constants.EVENT_MAIN_ACTIVITY_BASE + 5;
    private View view;
    private Handler uiHandlerMain = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case CALL_SUCCESS:
                    startActivity(new Intent(getActivity(), MeetingActivity.class));
                    break;
                case CALL_FAIL:
                    Toast.makeText(getActivity(), R.string.call_fail, Toast.LENGTH_SHORT).show();
                    break;
                case COMING_CALL:
                    break;
                case HANGUP_SUCCESS:
                    break;
            }
        }
    };
    private int intComingCallType;
    private String strComingNumber;
    private ArrayList<OrderConfInfo> mmcConfList;
    private RecyclerViewAdapter recyclerViewAdapter;
    private AlertDialog alertDialog;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventCenterNotifier.addListener(EventCenterNotifier.ICallStatusListener.class, this);
        EventCenterNotifier.addListener(EventCenterNotifier.IIncomingCallListener.class, this);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (view == null) {
            view = inflater.inflate(R.layout.fragment_home, container, false);
            unbinder = ButterKnife.bind(this, view);

            mmcConfList = ConferenceAgentNative.getMMCConfList("112.74.176.200","4");
//            Log.d(TAG, "onCreateView: mmcConfList="+mmcConfList.size());
            nsv.smoothScrollTo(0,0);
            LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
            layoutManager.setSmoothScrollbarEnabled(true);
            layoutManager.setAutoMeasureEnabled(true);
            //设置布局管理器
            rv.setLayoutManager(layoutManager);
            //设置为垂直布局，这也是默认的
            layoutManager.setOrientation(OrientationHelper.VERTICAL);
            //设置分隔线
            rv.addItemDecoration(new DividerItemDecoration(getActivity(), LinearLayoutManager.VERTICAL));
            //设置增加或删除条目的动画
            rv.setItemAnimator(new DefaultItemAnimator());
            rv.setHasFixedSize(true);
            //解决RecyclerView和NestedScrollViewd的滑动冲突
            rv.setNestedScrollingEnabled(false);

            recyclerViewAdapter = new RecyclerViewAdapter(mmcConfList,getActivity());
            rv.setAdapter(recyclerViewAdapter);

        }
        return view;
    }

    @OnClick(R.id.rl)
    public void OnRlClick(View v) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View mLayout = inflater.inflate(R.layout.dialog_join, null);
        builder.setView(mLayout);
        Button btn_join_room = (Button) mLayout.findViewById(R.id.btn_join_room);
        final EditText et_room_number = (EditText) mLayout.findViewById(R.id.et_room_number);
        btn_join_room.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String meetIDStr = et_room_number.getText().toString();
                CallAgentNative.createCall(1, meetIDStr, "");
            }
        });
        alertDialog = builder.create();
        alertDialog.show();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @Override
    public void onPause() {
        super.onPause();
        if (alertDialog!=null){
            alertDialog.dismiss();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventCenterNotifier.removeListener(EventCenterNotifier.ICallStatusListener.class, this);
        EventCenterNotifier.removeListener(EventCenterNotifier.IIncomingCallListener.class, this);
    }

    @Override
    public void onCallStatus(int status, int type, String number) {
        Log.d(TAG, "CallStatus Report  status_=" + status);
        if (status == Constants.STATUS_CONF_CONNECTED) {
            uiHandlerMain.sendEmptyMessage(CALL_SUCCESS);
        } else if (status == Constants.STATUS_CONF_BUSY || status == Constants.STATUS_CONF_TIME_OUT || status == Constants.STATUS_CONF_ROUTE_FAILED || status == Constants.STATUS_CONF_NO_REACHABLE) {
            uiHandlerMain.sendEmptyMessage(CALL_FAIL);
        }else if (status==Constants.STATUS_CONF_DISCONNECTED){
            uiHandlerMain.sendEmptyMessage(HANGUP_SUCCESS);
        }
    }

    @Override
    public void onIncomingCall(int type, String remoteNumber) {
        Log.d(TAG, "onIncomingCall Report  type_=" + type + "  remoteNumber_=" + remoteNumber);
        intComingCallType = type;
        strComingNumber = remoteNumber;
        uiHandlerMain.sendEmptyMessage(COMING_CALL);
    }


    @Override
    public void onClick(int position) {

    }

    @Override
    public void onLongClick(int postion) {

    }
}
