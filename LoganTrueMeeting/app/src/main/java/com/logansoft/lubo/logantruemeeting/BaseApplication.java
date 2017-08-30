package com.logansoft.lubo.logantruemeeting;

import android.app.Application;

import com.logansoft.lubo.logantruemeeting.interfaces.NetConnectionObserver;
import com.logansoft.lubo.logantruemeeting.interfaces.NetConnectionSubject;
import com.logansoft.lubo.logantruemeeting.utils.NetWorkUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by logansoft on 2017/8/29.
 */

public class BaseApplication extends Application implements NetConnectionSubject {

    protected static BaseApplication instance;

    private int currentNetType = -1;

    private List<NetConnectionObserver> observers = new ArrayList<>();


    public static BaseApplication getInstance() {
        return instance;
    }

    /**
     * current net connection type
     *
     * @return
     */
    public int getCurrentNetType() {
        return currentNetType;
    }

    /**
     * current net connection status
     *
     * @return
     */
    public boolean isNetConnection() {
        return currentNetType == NetWorkUtil.NET_NO_CONNECTION ? false : true;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        instance = this;

        currentNetType = NetWorkUtil.getConnectionType(this);

    }

    @Override
    public void addNetObserver(NetConnectionObserver observer) {
        if (!observers.contains(observer)) {
            observers.add(observer);
        }
    }

    @Override
    public void removeNetObserver(NetConnectionObserver observer) {
        if (observers != null && observers.contains(observer)) {
            observers.remove(observer);
        }
    }

    @Override
    public void notifyNetObserver(int type) {

        /**
         * 避免多次发送相同的网络状态
         */
        if (currentNetType == type) {
            return;
        } else {
            currentNetType = type;
            if (observers != null && observers.size() > 0) {
                for (NetConnectionObserver observer : observers) {
                    observer.updateNetStatus(type);
                }
            }
        }
    }
}
