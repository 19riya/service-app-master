package com.hrproject.HelperClasses;

import android.content.Context;
import androidx.viewpager.widget.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

public class CustomViewPager extends ViewPager {
    private boolean pagingEnabled;

    public CustomViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.pagingEnabled=false;
    }

    public CustomViewPager(Context context) {
        super(context);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
        if (this.pagingEnabled) {
            return super.onInterceptTouchEvent(event);
        }
        return false;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (this.pagingEnabled) {
            return super.onTouchEvent(event);
        }
        return false;
    }
    public void setPagingEnabled(boolean enabled){

        this.pagingEnabled=enabled;
    }
}

