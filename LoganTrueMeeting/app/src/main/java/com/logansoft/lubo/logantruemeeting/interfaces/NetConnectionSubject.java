package com.logansoft.lubo.logantruemeeting.interfaces;

/**
 * Created by logansoft on 2017/8/29.
 */

public interface NetConnectionSubject {
    /**
     * 注册观察者
     *
     * @param observer
     */
    public void addNetObserver(NetConnectionObserver observer);

    /**
     * 移除观察者
     *
     * @param  observer
     */
    public void removeNetObserver(NetConnectionObserver observer);

    /**
     * 状态更新
     *
     * @param type
     */
    public void notifyNetObserver(int type);
}
