package com.logansoft.lubo.logantruemeeting;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.zte.ucsp.vtcoresdk.jni.JniHelperNative;
import com.zte.ucsp.vtcoresdk.jni.VTCoreSDKAgentNative;
import com.zte.ucsp.vtcoresdk.jni.media.AudioMgr;
import com.zte.ucsp.vtcoresdk.jni.media.CameraGrabber;

import java.util.Timer;
import java.util.TimerTask;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SplashActivity extends Activity implements Runnable {

    @BindView(R.id.tvLogo)
    TextView tvLogo;
    @BindView(R.id.tv)
    TextView tv;
    @BindView(R.id.pbLoadingSplash)
    ProgressBar pbLoadingSplash;
    private int max = 100, currentProgress, step;
    private Handler handler = new Handler();

    static {
        System.loadLibrary("DLFramework");
        System.loadLibrary("TinyXPath");
        System.loadLibrary("zteh264");
        System.loadLibrary("yuv");
        System.loadLibrary("UCSCore");
        System.loadLibrary("VTCoreSDK");
        System.loadLibrary("VTCoreSDK_JNI");
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        ButterKnife.bind(this);

        JniHelperNative.setClassLoader(this);
        AudioMgr.shareInstance(this);
        CameraGrabber.shareInstance(this);

        String appPath = Environment.getExternalStorageDirectory().getPath();
        VTCoreSDKAgentNative.init(appPath);
        VTCoreSDKAgentNative.start("");
        Timer timer = new Timer();
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                startActivity(new Intent(SplashActivity.this, LoginActivity.class));
                SplashActivity.this.finish();
            }
        };
        timer.schedule(task, 2000);

        pbLoadingSplash.setMax(max);
        pbLoadingSplash.setProgress(0);
        step = max / 100;
        handler.post(this);
    }

    @Override
    public void run() {
        currentProgress = pbLoadingSplash.getProgress();
        pbLoadingSplash.setProgress(currentProgress + step);
        handler.postDelayed(this, 20);
    }
}
