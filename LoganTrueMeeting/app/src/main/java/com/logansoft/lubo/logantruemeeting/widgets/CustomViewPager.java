package com.logansoft.lubo.logantruemeeting.widgets;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 * Created by logansoft on 2017/8/25.
 */

public class CustomViewPager extends ViewPager {
    private boolean mDisablesroll = true;

    public CustomViewPager(Context context) {
        super(context);
    }

    public CustomViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public boolean onTouchEvent(MotionEvent ev) {
        return this.mDisablesroll?false:super.onTouchEvent(ev);
    }

    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return true;
    }

    public boolean isScrollble() {
        return this.mDisablesroll;
    }

    public void setDisableScroll(boolean scrollble) {
        this.mDisablesroll = scrollble;
    }
}

