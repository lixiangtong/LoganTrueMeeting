package com.logansoft.lubo.logantruemeeting.utils;

import android.content.Context;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.widget.Toast;

import com.logansoft.lubo.logantruemeeting.Receivers.NetConnectionReceiver;

/**
 * Created by logansoft on 2017/8/29.
 */

public class ReceiverUtil {
    public static void registerReceiver(NetConnectionReceiver myReceiver, Context context){
        IntentFilter filter=new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        context.registerReceiver(myReceiver, filter);
    }

    public static void unregisterReceiver(NetConnectionReceiver myReceiver, Context context){
        context.unregisterReceiver(myReceiver);
    }

    public static void showToast(int type,Context context){
        switch (type){
            case 0:
                Toast.makeText(context, "当前无网络", Toast.LENGTH_SHORT).show();
                break;
            case 1:
                Toast.makeText(context, "已连接到wifi", Toast.LENGTH_SHORT).show();
                break;
            case 2:
            case 3:
            case 4:
                Toast.makeText(context, "已连接到移动网络", Toast.LENGTH_SHORT).show();
                break;
            case 5:
                Toast.makeText(context, "未知网络", Toast.LENGTH_SHORT).show();
                break;
            default:
                break;
        }
    }
}
