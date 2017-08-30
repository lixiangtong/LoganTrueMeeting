package com.logansoft.lubo.logantruemeeting.interfaces;

/**
 * Created by logansoft on 2017/8/29.
 */

public interface NetConnectionObserver {

    /**
     * 通知观察者更改状态
     *
     * @param type
     */
    public void updateNetStatus(int type);
}
