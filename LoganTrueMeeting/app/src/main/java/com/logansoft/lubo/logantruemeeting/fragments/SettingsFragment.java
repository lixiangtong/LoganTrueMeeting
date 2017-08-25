package com.logansoft.lubo.logantruemeeting.fragments;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

import com.logansoft.lubo.logantruemeeting.R;
import com.zte.ucsp.vtcoresdk.jni.AuthAgentNative;
import com.zte.ucsp.vtcoresdk.jni.EventCenterNotifier;
import com.zte.ucsp.vtcoresdk.util.Constants;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * Created by logansoft on 2017/8/23.
 */

public class SettingsFragment extends Fragment implements EventCenterNotifier.ILogOutResultListener {
    private static final String TAG = "SettingsFragment";
    @BindView(R.id.llChangeMeetingPassword)
    LinearLayout llChangeMeetingPassword;
    @BindView(R.id.vChangeMeetingPass)
    View vChangeMeetingPass;
    @BindView(R.id.llChangePassword)
    LinearLayout llChangePassword;
    @BindView(R.id.llAbout)
    LinearLayout llAbout;
    @BindView(R.id.llMeetting)
    LinearLayout llMeetting;
    Unbinder unbinder;
    @BindView(R.id.btnLogout)
    Button btnLogout;
    private View view;
    private static final int LOGOUT_SUCCESS = Constants.EVENT_MAIN_ACTIVITY_BASE + 3;
    private Handler uiHandlerMain = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case LOGOUT_SUCCESS:
                    getActivity().finish();
                    break;
            }
        }
    };

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventCenterNotifier.addListener(EventCenterNotifier.ILogOutResultListener.class, this);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (view == null) {
            view = inflater.inflate(R.layout.fragment_settings, container, false);
            unbinder = ButterKnife.bind(this, view);
        }
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventCenterNotifier.removeListener(EventCenterNotifier.ILogOutResultListener.class, this);
    }

    @Override
    public void onLogOutResult() {
        Log.d(TAG, "Logout Success");
        uiHandlerMain.sendEmptyMessage(LOGOUT_SUCCESS);
    }

    @OnClick(R.id.btnLogout)
    public void onViewClicked() {
        AuthAgentNative.logout(true);
    }
}
