package com.thinksky.utils.widget;

import android.content.Context;
import android.os.Build;
import android.support.v7.widget.CardView;
import android.util.AttributeSet;

/**
 * Created by yifan on 2016/10/31.
 */
public class RadiusLayout extends CardView {

    public RadiusLayout(Context context) {
        this(context, null);
    }

    public RadiusLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RadiusLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        //LOLLIPOP以下的版本无法自动裁剪子控件边角，所以设置为直角
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            setPreventCornerOverlap(false);
            setFadingEdgeLength(0);
            setRadius(0);
        }
    }

}