package com.logansoft.lubo.logantruemeeting;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.content.pm.ConfigurationInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.logansoft.lubo.logantruemeeting.Receivers.NetConnectionReceiver;
import com.logansoft.lubo.logantruemeeting.adapters.NavigationAdapter;
import com.logansoft.lubo.logantruemeeting.interfaces.NetConnectionObserver;
import com.logansoft.lubo.logantruemeeting.utils.ReceiverUtil;
import com.logansoft.lubo.logantruemeeting.widgets.CustomViewPager;
import com.zte.ucsp.vtcoresdk.jni.CallAgentNative;
import com.zte.ucsp.vtcoresdk.jni.EventCenterNotifier;
import com.zte.ucsp.vtcoresdk.jni.MediaControlAgentNative;
import com.zte.ucsp.vtcoresdk.jni.media.CameraGrabber;
import com.zte.ucsp.vtcoresdk.jni.media.VideoPlayer;
import com.zte.ucsp.vtcoresdk.util.Constants;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MeetingActivity extends BaseActivity implements EventCenterNotifier.ICallStatusListener, View.OnTouchListener, View.OnClickListener,Runnable,NetConnectionObserver {

    private static final String TAG = "MeetingActivity";
    @BindView(R.id.iv_screenshare)
    ImageView ivScreenshare;
    @BindView(R.id.left_button)
    ImageView leftButton;
    @BindView(R.id.tvMeetInfo)
    TextView tvMeetInfo;
    @BindView(R.id.right_setting)
    TextView rightSetting;
    @BindView(R.id.rlTopOptions)
    RelativeLayout rlTopOptions;
    @BindView(R.id.tvHideState)
    TextView tvHideState;
    @BindView(R.id.tvShowState)
    TextView tvShowState;
    @BindView(R.id.rlKeyboard)
    RelativeLayout rlKeyboard;
    @BindView(R.id.rbFirst)
    RadioButton rbFirst;
    @BindView(R.id.rbSecond)
    RadioButton rbSecond;
    @BindView(R.id.rbThird)
    RadioButton rbThird;
    @BindView(R.id.rbFourth)
    RadioButton rbFourth;
    @BindView(R.id.rbFifth)
    RadioButton rbFifth;
    @BindView(R.id.rgKeyboard)
    RadioGroup rgKeyboard;
    @BindView(R.id.rlKeyboardAll)
    RelativeLayout rlKeyboardAll;
    @BindView(R.id.rbVideo)
    RadioButton rbVideo;
    @BindView(R.id.rbAttendee)
    RadioButton rbAttendee;
    @BindView(R.id.rbDocument)
    RadioButton rbDocument;
    @BindView(R.id.rgOptions)
    RadioGroup rgOptions;
    @BindView(R.id.cbSwitchCamera)
    CheckBox cbSwitchCamera;
    @BindView(R.id.cbCamera)
    CheckBox cbCamera;
    @BindView(R.id.cbSpeaker)
    CheckBox cbSpeaker;
    @BindView(R.id.cbMicphone)
    CheckBox cbMicphone;
    @BindView(R.id.view_options)
    LinearLayout viewOptions;
    @BindView(R.id.tvSetting)
    TextView tvSetting;
    @BindView(R.id.tvClose)
    TextView tvClose;
    @BindView(R.id.cbMute)
    CheckBox cbMute;
    @BindView(R.id.tvMeetingID)
    TextView tvMeetingID;
    @BindView(R.id.tvMeetingStartTime)
    TextView tvMeetingStartTime;
    @BindView(R.id.tvMeetingPass)
    TextView tvMeetingPass;
    @BindView(R.id.tvLockState)
    TextView tvLockState;
    @BindView(R.id.llRightSettings)
    LinearLayout llRightSettings;
    private AlertDialog leaveMeetingDialog;
    private static final int HUNGUP_SUCCESS = Constants.EVENT_CONFING_ACTIVITY_BASE + 1;
    private static final int DIAPLAY_DELAY = Constants.EVENT_CONFING_ACTIVITY_BASE + 2;
    private static final int MSG_CHECK_BACKGROUND = 1001;
    private static final int MSG_HIDE_OPTION = 1002;

    private int mTouchLastX;
    private int mTouchLastY;
    private int mHalfWidth;
    private int mHalfHeight;
    private int mPositionLeft = 0;
    private int mPositionRight = 0;
    private int mPositionTop = 0;
    private int mPositionBottom = 0;
    private int mPositionBottomMar = 15;
    private boolean isMicOpen = true;
    private boolean isSpeakerOpen = true;
    private boolean isCameraOpen = true;
    private Handler uiHandlerMain = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case HUNGUP_SUCCESS:
                    finish();
                    break;
                case DIAPLAY_DELAY:
                    CameraGrabber.openCamera(1280, 720, 20);
                    CameraGrabber.startCamera(1);
                    isCameraOpen = true;
                    cbCamera.setChecked(true);
                    cbSwitchCamera.setVisibility(View.VISIBLE);
                    reLayoutView(mPositionLeft, mPositionTop);
                    break;
                case MSG_HIDE_OPTION:
                    hideOption();
                    break;
                default:
                    break;
            }
        }
    };
    private CustomViewPager mRemoteLocalViewPager;
    private LayoutInflater mLayoutInflater;
    private View mRemoteView;
    private View mLocalView;
    private ArrayList<View> mViewList;
    private SurfaceView mSurfaceRemoteView;
    private SurfaceView mSurfaceLocalView;
    private SurfaceView mSurfaceLocalInView;
    private NavigationAdapter mPagerAdapter;
    private RemoteCallBack mRemoteCallBack;
    private LocalCallBack mLocalCallBack;
    public static SurfaceHolder mRemoteSurfaceHolder;
    public SurfaceHolder mLocalSurfaceHolder;
    public SurfaceHolder mLocalInSurfaceHolder;
    private int mGlEsVersion = 0x00020000;
    private ViewGroup.LayoutParams mLocalInLayout;
    private long startTimeMillis;
    private SimpleDateFormat sdf;
    private long enterTimeCount;
    private NetConnectionReceiver mReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.activity_meeting);
        ButterKnife.bind(this);

        mReceiver = new NetConnectionReceiver();
        //注册网络监听广播
        ReceiverUtil.registerReceiver(mReceiver,this);
        BaseApplication.getInstance().addNetObserver(this);
//
        leftButton.setOnClickListener(this);
        cbSwitchCamera.setOnClickListener(this);
        cbCamera.setOnClickListener(this);
        cbSpeaker.setOnClickListener(this);
        cbMicphone.setOnClickListener(this);
        rightSetting.setOnClickListener(this);
        tvClose.setOnClickListener(this);
        View view = getWindow().getDecorView();
        view.setOnTouchListener(this);

        EventCenterNotifier.addListener(EventCenterNotifier.ICallStatusListener.class, this);

        initWidget();
        initVideoSurfaceView();
        updateStatusEnterMeeting();

        uiHandlerMain.sendEmptyMessageDelayed(DIAPLAY_DELAY, 200);
        showOption();
        sdf = new SimpleDateFormat("HH:mm:ss");

        startTimeMillis = System.currentTimeMillis();
        tvMeetInfo.setText("00:00:00");
        uiHandlerMain.post(this);
    }

    private void initWidget() {
        mRemoteLocalViewPager = (CustomViewPager) findViewById(R.id.remote_local_viewpager);

        mLayoutInflater = getLayoutInflater();
        mRemoteView = mLayoutInflater.inflate(R.layout.video_remote_view, null);
        mLocalView = mLayoutInflater.inflate(R.layout.video_local_view, null);

        mViewList = new ArrayList<View>();
        mViewList.add(mRemoteView);
        mViewList.add(mLocalView);

        mSurfaceRemoteView = (SurfaceView) mRemoteView.findViewById(R.id.video_remoteSurfaceView);//远端：显示的surfaceView
        mSurfaceLocalView = (SurfaceView) mLocalView.findViewById(R.id.surfaceview_local);
        mSurfaceLocalInView = (SurfaceView) mRemoteView.findViewById(R.id.video_localSurfaceView);//本地：采集预览的surfaceView

        mPagerAdapter = new NavigationAdapter(mViewList, this);
        mRemoteLocalViewPager.setAdapter(mPagerAdapter);
        mRemoteLocalViewPager.setDisableScroll(true);

        mSurfaceLocalInView.setOnClickListener(this);
        mSurfaceRemoteView.setOnClickListener(this);
    }

    @Override
    public void run() {
        long currentTimeMillis = System.currentTimeMillis();
        enterTimeCount = currentTimeMillis - startTimeMillis - 8* 60 * 60 *1000;
        Date date = new Date(enterTimeCount);
        String formatTime = sdf.format(date);
        tvMeetInfo.setText(formatTime);
        uiHandlerMain.postDelayed(this,1000);
    }

    @Override
    public void updateNetStatus(int type) {
        ReceiverUtil.showToast(type,this);
    }

    class LocalCallBack implements SurfaceHolder.Callback {

        @Override
        public void surfaceCreated(SurfaceHolder holder) {
            // TODO Auto-generated method stub
            Log.i(TAG, "LocalCallBack surfaceCreated");
            CameraGrabber.shareInstance(MeetingActivity.this).setSurfaceHolder(holder);

        }

        @Override
        public void surfaceChanged(SurfaceHolder holder, int format, int width,
                                   int height) {
            // TODO Auto-generated method stub
            Log.i(TAG, "LocalCallBack surfaceChanged");
        }

        @Override
        public void surfaceDestroyed(SurfaceHolder holder) {
            // TODO Auto-generated method stub

        }

    }

    class RemoteCallBack implements SurfaceHolder.Callback {
        @Override
        public void surfaceCreated(SurfaceHolder holder) {
            Log.i(TAG, "RemoteCallBack surfaceCreated");
        }

        @Override
        public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
            Log.i(TAG, "RemoteCallBack surfaceChanged");
            if (mRemoteSurfaceHolder == holder) {
                VideoPlayer.setSurface(mRemoteSurfaceHolder.getSurface());
            }
        }

        @Override
        public void surfaceDestroyed(SurfaceHolder holder) {
            Log.i(TAG, "RemoteCallBack surfaceDestroyed");
            if (mRemoteSurfaceHolder == holder) {
                VideoPlayer.setSurface(null);
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.left_button:
                leaveMeetinglistener();
                break;
            case R.id.cbSwitchCamera:
                if (isCameraOpen) {
                    CameraGrabber.changeFBCamera();
                }
                break;
            case R.id.cbCamera:
                Log.d(TAG, "onClick: cbCamera=" + cbCamera.isChecked());
                updateCameraStatus();
                break;
            case R.id.cbSpeaker:
                Log.d(TAG, "onClick: cbSpeaker=" + cbSpeaker.isChecked());
                updateSpeakerStatus();
                break;
            case R.id.cbMicphone:
                updateMicphoneStatus();
                break;
            case R.id.right_setting:
                llRightSettings.setVisibility(View.VISIBLE);
                break;
            case R.id.tvClose:
                llRightSettings.setVisibility(View.GONE);
                break;
            default:
                break;
        }
    }

    private void reLayoutView(int left, int top) {
        Log.i(TAG, "reLayoutView");
        int right = left + mSurfaceLocalInView.getWidth();
        int bootem = top + mSurfaceLocalInView.getHeight();
        Log.i(TAG, "  left=" + left + "  top=" + top + "  right=" + right + "  bootem=" + bootem
                + "  mSurfaceLocalInView.getWidth() =" + mSurfaceLocalInView.getWidth()
                + "  mSurfaceLocalInView.getHeight() = " + mSurfaceLocalInView.getHeight());
        mSurfaceLocalInView.layout(left, top, left + mSurfaceLocalInView.getWidth(), top + mSurfaceLocalInView.getHeight());
    }

    public int getStatusBarHeight() {
        int result = 0;
        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }

    private void initVideoSurfaceView() {
        Log.i(TAG, "initVideoSurfaceView");
        if (null == mSurfaceRemoteView) {
            Log.i(TAG, " mSurfaceRemoteView null");
        }

        mLocalCallBack = new LocalCallBack();
        mRemoteCallBack = new RemoteCallBack();

        mRemoteSurfaceHolder = mSurfaceRemoteView.getHolder();
        mRemoteSurfaceHolder.addCallback(mRemoteCallBack);

        ActivityManager am = (ActivityManager) getSystemService(this.ACTIVITY_SERVICE);
        ConfigurationInfo info = am.getDeviceConfigurationInfo();
        mGlEsVersion = info.reqGlEsVersion;

        Log.i(TAG, "[initVideoSurfaceView] mGlEsVersion [" + mGlEsVersion + "]");
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        Log.d(TAG, "  height=" + displayMetrics.heightPixels + "  width=" + displayMetrics.widthPixels);
        mHalfHeight = (int) (displayMetrics.heightPixels) / 2;
        mHalfWidth = (int) (displayMetrics.widthPixels) / 2;

        mLocalSurfaceHolder = mSurfaceLocalView.getHolder();
        mLocalSurfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        mLocalSurfaceHolder.addCallback(mLocalCallBack);

        int w = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        int h = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        viewOptions.measure(w, h);
        int height = viewOptions.getMeasuredHeight();
        int statutHeigth = getStatusBarHeight();

        mLocalInLayout = mSurfaceLocalInView.getLayoutParams();
        mLocalInLayout.width = (int) (displayMetrics.widthPixels * 0.30f);
        mLocalInLayout.height = mLocalInLayout.width * 9 / 16;
        mPositionRight = 2 * mHalfWidth - mPositionLeft - mLocalInLayout.width;
        mPositionBottom = 2 * mHalfHeight - mPositionBottomMar - mLocalInLayout.height - height - statutHeigth;
        Log.d(TAG, "屏幕一半高度=" + mHalfHeight + "  本端高度=" + mLocalInLayout.height
                + "  本端宽度=" + mLocalInLayout.width
                + "  底部按钮栏高度=" + height
                + "  状态栏高度=" + statutHeigth +
                "  mPositionBottom=" + mPositionBottom);
        mSurfaceLocalInView.setLayoutParams(mLocalInLayout);

        mLocalInSurfaceHolder = mSurfaceLocalInView.getHolder();
        mLocalInSurfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        mLocalInSurfaceHolder.addCallback(mLocalCallBack);


        mSurfaceLocalInView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                int action = event.getAction();
                //updateCountDownBegTime();
                switch (action) {
                    case MotionEvent.ACTION_DOWN:
                        mTouchLastX = (int) event.getRawX();
                        mTouchLastY = (int) event.getRawY();
                        break;
                    case MotionEvent.ACTION_MOVE:
                        int dx = (int) event.getRawX() - mTouchLastX;
                        int dy = (int) event.getRawY() - mTouchLastY;

                        int left = v.getLeft() + dx;
                        if (left < 0) {
                            left = 0;
                        } else if (left > 2 * mHalfWidth - mLocalInLayout.width) {
                            left = 2 * mHalfWidth - mLocalInLayout.width;
                        }

                        int top = v.getTop() + dy;
                        if (top < 0) {
                            top = 0;
                        } else if (top > 2 * mHalfHeight - mLocalInLayout.height) {
                            top = 2 * mHalfHeight - mLocalInLayout.height;
                        }
                        reLayoutView(left, top);

                        mTouchLastX = (int) event.getRawX();
                        mTouchLastY = (int) event.getRawY();

                        break;
                    case MotionEvent.ACTION_UP:

                        if (mTouchLastX < mHalfWidth) {
                            if (mTouchLastY < mHalfHeight) {
                                reLayoutView(mPositionLeft, mPositionTop);
                            } else {
                                Log.i("suxx", "--position--left--bottom--down  mPositionLeft= " + mPositionLeft + "  mPositionBottom=" + mPositionBottom);
                                reLayoutView(mPositionLeft, mPositionBottom);

                            }
                        } else {
                            if (mTouchLastY < mHalfHeight) {
                                reLayoutView(mPositionRight, mPositionTop);
                            } else {
                                Log.i(TAG, "--position--Right--bottom--down");
                                reLayoutView(mPositionRight, mPositionBottom);
                            }
                        }
                        break;
                }
                return true;
            }
        });

//        mSurfaceLocalInView.setZOrderOnTop(true);
        mSurfaceLocalInView.setZOrderMediaOverlay(true);
        mSurfaceLocalInView.bringToFront();

    }

    private void updateStatusEnterMeeting() {
        CameraGrabber.stopCamera();
        CameraGrabber.closeCamera(1);
        isCameraOpen = false;
        MediaControlAgentNative.setSpeakerEnabled(true);
        isSpeakerOpen = true;
        cbSpeaker.setChecked(true);
        MediaControlAgentNative.setMicEnabled(true);
        isMicOpen = true;
        cbMicphone.setChecked(true);
        cbSwitchCamera.setVisibility(View.GONE);
    }

    private void updateCameraStatus() {
        if (isCameraOpen) {
            CameraGrabber.stopCamera();
            CameraGrabber.closeCamera(1);
            cbCamera.setChecked(false);
            isCameraOpen = false;
            cbSwitchCamera.setVisibility(View.GONE);
        } else {
            CameraGrabber.openCamera(1280, 720, 20);
            CameraGrabber.startCamera(1);
            cbCamera.setChecked(true);
            isCameraOpen = true;
            cbSwitchCamera.setVisibility(View.VISIBLE);
        }
    }

    private void updateMicphoneStatus() {
        if (isMicOpen) {
            MediaControlAgentNative.setMicEnabled(false);
            cbMicphone.setChecked(false);
            isMicOpen = false;
        } else {
            MediaControlAgentNative.setMicEnabled(true);
            cbMicphone.setChecked(true);
            isMicOpen = true;
        }
    }

    private void updateSpeakerStatus() {
        if (isSpeakerOpen) {
            MediaControlAgentNative.setSpeakerEnabled(false);
            cbSpeaker.setChecked(false);
            isSpeakerOpen = false;
        } else {
            MediaControlAgentNative.setSpeakerEnabled(true);
            cbSpeaker.setChecked(true);
            isSpeakerOpen = true;
        }
    }

    private void leaveMeetinglistener() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View mView = this.getLayoutInflater().inflate(R.layout.dialog_leave_meeting, null, false);
        final Button btnLeaveMeeting = (Button) mView.findViewById(R.id.btnLeaveMeeting);
        btnLeaveMeeting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnLeaveMeeting.setText(getResources().getString(R.string.leaving_meeting));
                btnLeaveMeeting.setClickable(false);
                CameraGrabber.stopCamera();
                CameraGrabber.closeCamera(1);
                Toast.makeText(MeetingActivity.this, R.string.closed_camera, Toast.LENGTH_SHORT).show();
                CallAgentNative.hangupAllCall();
            }
        });
        leaveMeetingDialog = builder.setView(mView).create();
        leaveMeetingDialog.show();
    }

    @Override
    public void onCallStatus(int status, int type, String number) {
        Log.d(TAG, "status_=" + status);
        if (status == Constants.STATUS_CONF_DISCONNECTED) {
            uiHandlerMain.sendEmptyMessage(HUNGUP_SUCCESS);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        mSurfaceLocalInView.setVisibility(View.VISIBLE);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        leaveMeetingDialog.dismiss();
        EventCenterNotifier.removeListener(EventCenterNotifier.ICallStatusListener.class, this);
        BaseApplication.getInstance().removeNetObserver(this);
        ReceiverUtil.unregisterReceiver(mReceiver,this);
    }

    private void showOption() {
        Log.d(TAG, "showOption");
        uiHandlerMain.removeMessages(MSG_HIDE_OPTION);
        viewOptions.setVisibility(View.VISIBLE);
        rlTopOptions.setVisibility(View.VISIBLE);
        uiHandlerMain.sendEmptyMessageDelayed(MSG_HIDE_OPTION, 3 * 1000);
    }

    private void hideOption() {
        Log.d(TAG, "hideOption");
        uiHandlerMain.removeMessages(MSG_HIDE_OPTION);
        viewOptions.setVisibility(View.GONE);
        rlTopOptions.setVisibility(View.GONE);
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        int action = event.getAction();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
            case MotionEvent.ACTION_UP:
                showOption();
                Log.d(TAG, "onTouch: showOption()-----------");
                break;
        }
        return true;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // TODO Auto-generated method stub
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        // TODO Auto-generated method stub
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            leaveMeetinglistener();
            return true;
        }
        return super.onKeyUp(keyCode, event);
    }
}
