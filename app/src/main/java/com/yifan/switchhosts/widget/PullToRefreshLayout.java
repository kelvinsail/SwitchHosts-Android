package com.yifan.switchhosts.widget;

import android.content.Context;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.AttributeSet;

import com.yifan.switchhosts.R;

/**
 * 下拉刷新控件
 *
 * Created by yifan on 2016/10/31.
 */
public class PullToRefreshLayout extends SwipeRefreshLayout {

    public PullToRefreshLayout(Context context) {
        this(context, null);
    }

    public PullToRefreshLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    /**
     * 初始化并刷新控件
     */
    private void initView() {
        setColorSchemeResources(R.color.colorPrimary);
    }
}
