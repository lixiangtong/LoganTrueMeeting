package com.logansoft.lubo.logantruemeeting;

import android.app.Activity;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.widget.NestedScrollView;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MeetingLoginActivity extends Activity {
    private static final String TAG = "MeetingLoginActivity";
    @BindView(R.id.ivLogo)
    ImageView ivLogo;
    @BindView(R.id.ctl)
    CollapsingToolbarLayout ctl;
    @BindView(R.id.abl)
    AppBarLayout abl;
    @BindView(R.id.tvMeeting)
    TextView tvMeeting;
    @BindView(R.id.etMeeting)
    EditText etMeeting;
    @BindView(R.id.tvMeetingName)
    TextView tvMeetingName;
    @BindView(R.id.etMeetingNick)
    EditText etMeetingNick;
    @BindView(R.id.llMeetting)
    LinearLayout llMeetting;
    @BindView(R.id.btnMeetingLogin)
    Button btnMeetingLogin;
    @BindView(R.id.btnAccount)
    Button btnAccount;
    @BindView(R.id.tvNetworkSetting)
    TextView tvNetworkSetting;
    @BindView(R.id.nsv)
    NestedScrollView nsv;
    @BindView(R.id.ct)
    CoordinatorLayout ct;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_meeting_login);
        ButterKnife.bind(this);

        //CoordinatorLayout禁止滑动事件
        ct.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_MOVE:
                        return true;
                    default:
                        break;
                }
                return true;
            }
        });
    }
}
