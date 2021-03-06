package com.logansoft.lubo.logantruemeeting;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.logansoft.lubo.logantruemeeting.Receivers.NetConnectionReceiver;
import com.logansoft.lubo.logantruemeeting.interfaces.NetConnectionObserver;
import com.logansoft.lubo.logantruemeeting.utils.ReceiverUtil;
import com.readystatesoftware.systembartint.SystemBarTintManager;
import com.zte.ucsp.vtcoresdk.jni.AuthAgentNative;
import com.zte.ucsp.vtcoresdk.jni.EventCenterNotifier;
import com.zte.ucsp.vtcoresdk.jni.VTCoreSDKAgentNative;
import com.zte.ucsp.vtcoresdk.util.Constants;

import java.lang.reflect.Field;

import butterknife.BindView;
import butterknife.ButterKnife;


public class LoginActivity extends BaseActivity implements EventCenterNotifier.ILoginResultListener ,NetConnectionObserver{
    public static final String TAG = "LoginActivity";
    private static final int LOGIN_SUCCESS = Constants.EVENT_LOGIN_ACTIVITY_BASE + 1;
    private static final int LOGINED = Constants.EVENT_LOGIN_ACTIVITY_BASE + 2;

    AlertDialog.Builder goOnLoginDialogBuilder;
    @BindView(R.id.ivLogo)
    ImageView ivLogo;
    @BindView(R.id.ctl)
    CollapsingToolbarLayout ctl;
    @BindView(R.id.abl)
    AppBarLayout abl;
    @BindView(R.id.tvAccount)
    TextView tvAccount;
    @BindView(R.id.etAccount)
    EditText etAccount;
    @BindView(R.id.tvAccountPass)
    TextView tvAccountPass;
    @BindView(R.id.etAccountPassword)
    EditText etAccountPassword;
    @BindView(R.id.llAccount)
    LinearLayout llAccount;
    @BindView(R.id.btnAccountLogin)
    Button btnAccountLogin;
    @BindView(R.id.btnMeeting)
    Button btnMeeting;
    @BindView(R.id.tvNetworkSetting)
    TextView tvNetworkSetting;
    @BindView(R.id.nsv)
    NestedScrollView nsv;
    @BindView(R.id.ct)
    CoordinatorLayout ct;
    private Handler uiHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                case LOGINED: {
                    showGoOnLoginDialog();
                    break;
                }
                case LOGIN_SUCCESS: {
                    showMainPage();
                    break;
                }
                default: {
                    break;
                }
            }
            return false;
        }
    });
    private View.OnClickListener loginClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            VTCoreSDKAgentNative.setLicenseServerAddress("112.74.176.200");
            AuthAgentNative.login(etAccount.getText().toString(),etAccountPassword.getText().toString(),true);
        }
    };
    private NetConnectionReceiver mReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);
        mReceiver = new NetConnectionReceiver();
        //注册广播
        ReceiverUtil.registerReceiver(mReceiver,this);
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

        btnAccountLogin.setOnClickListener(loginClickListener);
        EventCenterNotifier.addListener(EventCenterNotifier.ILoginResultListener.class, this);
        BaseApplication.getInstance().addNetObserver(this);
    }

    @Override
    public void onLoginResult(int result) {
        if (0 == result) {
            Log.d(TAG, "Login Sucessed");
            uiHandler.sendEmptyMessage(LOGIN_SUCCESS);
        } else {
            // 当前帐号已经登录，上报是否继续登录或直接强制登录
            if (3 == result) {
                uiHandler.sendEmptyMessage(LOGINED);
            } else {
                Log.d(TAG, "Login Failed");
            }
        }
    }

    private void showMainPage() {
        startActivity(new Intent(this, MainActivity.class));
    }

    private void showGoOnLoginDialog() {
        if (null != goOnLoginDialogBuilder) {
            return;
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("当前帐号已经登录，是否强制登录？");
        builder.setTitle("提示");
        builder.setPositiveButton("确认", new AlertDialog.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                AuthAgentNative.goOnLogin();
                goOnLoginDialogBuilder = null;
            }
        });
        builder.setNegativeButton("取消", new AlertDialog.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                goOnLoginDialogBuilder = null;
            }
        });
        builder.create().show();

        goOnLoginDialogBuilder = builder;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ReceiverUtil.unregisterReceiver(mReceiver,this);
        EventCenterNotifier.removeListener(EventCenterNotifier.ILoginResultListener.class, this);
        BaseApplication.getInstance().removeNetObserver(this);
    }

    @Override
    public void updateNetStatus(int type) {
        Log.d(TAG, "updateNetStatus: ");
        ReceiverUtil.showToast(type,this);
    }

}
