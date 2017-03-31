package com.thinksky.utils.utils;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.Display;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;

import com.thinksky.utils.R;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * WidgetUtils
 *
 * Created by yifan on 2016/8/18.
 */
public class WidgetUtils {

    /**
     * 获取状态栏高度
     *
     * @return
     */
    public static int getStatusBarHeight() {
        Class<?> c = null;
        Object obj = null;
        Field field = null;
        int x = 0, statusbarHeight = 0;
        try {
            c = Class.forName("com.android.internal.R$dimen");
            obj = c.newInstance();
            field = c.getField("status_bar_height");
            x = Integer.parseInt(field.get(obj).toString());
            statusbarHeight = ResourcesUtils.getResources().getDimensionPixelSize(x);
        } catch (Exception e1) {
            statusbarHeight = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                    24, ResourcesUtils.getDisplayMetrics());
        }
        return statusbarHeight;
    }

    /**
     * 获取NavigationBar高度
     *
     * @return
     */
    public static int getNavigationBarHeight() {
        int navigationBarHeight = 0;
        int id = ResourcesUtils.getIdentifier("navigation_bar_height", "dimen", "android");
        if (id > 0 && checkDeviceHasNavigationBar()) {
            navigationBarHeight = ResourcesUtils.getDimensionPixelSize(id);
        }
        return navigationBarHeight;
    }

    /**
     * 判断系统是否有NavigationBar
     *
     * @return
     */
    private static boolean checkDeviceHasNavigationBar() {
        boolean hasNavigationBar = false;
        int id = ResourcesUtils.getIdentifier("config_showNavigationBar", "bool", "android");
        if (id > 0) {
            hasNavigationBar = ResourcesUtils.getBoolean(id);
        }
        try {
            Class systemPropertiesClass = Class.forName("android.os.SystemProperties");
            Method m = systemPropertiesClass.getMethod("get", String.class);
            String navBarOverride = (String) m.invoke(systemPropertiesClass, "qemu.hw.mainkeys");
            if ("1".equals(navBarOverride)) {
                hasNavigationBar = false;
            } else if ("0".equals(navBarOverride)) {
                hasNavigationBar = true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return hasNavigationBar;

    }

    public static int getScreentHeight(Display d) {
        int heightPixels;
//        WindowManager w = context.getWindowManager();
//        Display d = w.getDefaultDisplay();
        DisplayMetrics metrics = new DisplayMetrics();
        d.getMetrics(metrics);
        // since SDK_INT = 1;
        heightPixels = metrics.heightPixels;
        // includes window decorations (statusbar bar/navigation bar)
        if (Build.VERSION.SDK_INT >= 14 && Build.VERSION.SDK_INT < 17)
            try {
                heightPixels = (Integer) Display.class
                        .getMethod("getRawHeight").invoke(d);
            } catch (Exception ignored) {
            }
            // includes window decorations (statusbar bar/navigation bar)
        else if (Build.VERSION.SDK_INT >= 17)
            try {
                android.graphics.Point realSize = new android.graphics.Point();
                Display.class.getMethod("getRealSize",
                        android.graphics.Point.class).invoke(d, realSize);
                heightPixels = realSize.y;
            } catch (Exception ignored) {
            }
        Log.e("getScreentHeight", "getScreentHeight" + heightPixels);
        return heightPixels;
    }

    /**
     * 获取添加向下边距，避免被NavigationBar覆盖的LayoutParams
     *
     * @return
     */
    public static FrameLayout.LayoutParams getMarginNaviBarParams() {
        FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        lp.setMargins(0, 0, 0, getNavigationBarHeight());
        return lp;
    }

    /**
     * 为列表控件添加向下的边距以支持不被NavigationBar覆盖
     *
     * @param views
     */
    public static void makeAdapterViewAboveNavigationBar(ViewGroup... views) {
        if (null != views && views.length > 0) {
            for (ViewGroup view : views) {
                view.setClipToPadding(false);
                view.setPadding(0, 0, 0, getNavigationBarHeight());
            }
        }
    }

    /**
     * 根据系统版本自动设置布局点击背景效果
     *
     * @param view
     */
    public static void setItemClickBackground(View view) {
        //获取主题定义的点击效果
        TypedArray array = view.getContext().getTheme().obtainStyledAttributes(
                new int[]{R.attr.item_click_background});
        Drawable background = (Drawable) array
                .getDrawable(0);
        //设置点击效果
        if (null != background) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                view.setBackground(background);
            } else {
                view.setBackgroundDrawable(background);
            }
        }
        array.recycle();
    }

    /**
     * 根据系统版本自动设置布局点击背景效果(透明底色)
     *
     * @param view
     */
    public static void setItemClickBackgroundTransParent(View view) {
        //获取主题定义的点击效果
        TypedArray array = view.getContext().getTheme().obtainStyledAttributes(
                new int[]{R.attr.item_click_background_transparent});
        Drawable background = (Drawable) array
                .getDrawable(0);
        //设置点击效果
        if (background != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                view.setBackground(background);
            } else {
                view.setBackgroundDrawable(background);
            }
        }
        array.recycle();
    }
}
