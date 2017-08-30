package com.logansoft.lubo.logantruemeeting.Receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;

import com.logansoft.lubo.logantruemeeting.BaseApplication;
import com.logansoft.lubo.logantruemeeting.utils.NetWorkUtil;

/**
 * Created by logansoft on 2017/8/29.
 */

public class NetConnectionReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(ConnectivityManager.CONNECTIVITY_ACTION)) {
            int connectionType = NetWorkUtil.getConnectionType(context);

            /**
             * 更改网络状态
             */
            if (BaseApplication.getInstance() != null) {
                BaseApplication.getInstance().notifyNetObserver(connectionType);
            }
        }
    }
}
