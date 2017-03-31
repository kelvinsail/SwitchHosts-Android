package com.thinksky.utils.base;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

import com.thinksky.utils.R;
import com.thinksky.utils.base.impl.LoadImpl;
import com.thinksky.utils.base.lifecycle.LifeCycleActivity;
import com.thinksky.utils.utils.IntentUtils;
import com.thinksky.utils.utils.WidgetUtils;

/**
 * Activity功能基类
 *
 * Created by yifan on 2016/7/14.
 */
public abstract class BaseActivity extends LifeCycleActivity implements LoadImpl {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void setContentView(View view, ViewGroup.LayoutParams params) {
        super.setContentView(view, params);
        initView();
        setListener();
    }

    @Override
    public void setContentView(@LayoutRes int layoutResID) {
        super.setContentView(layoutResID);
        initView();
        setListener();
    }

    @Override
    public void setContentView(View view) {
        super.setContentView(view);
        initView();
        setListener();
    }

    @Override
    public void initView() {

    }

    @Override
    public void setListener() {

    }

    @Override
    public void createLoadingdialog() {

    }

    @Override
    public void createLoadingdialog(String message) {

    }

    @Override
    public void createLoadingdialog(String message, boolean canelable, boolean touchOutsideCancelable) {

    }

    @Override
    public void createLoadingdialog(String message, boolean canelable, boolean touchOutsideCancelable, boolean isFullScreen) {

    }

    @Override
    public void dissmissLoadingDialog() {

    }

    /**
     * 启动一个Fragment
     *
     * @param layoutID         资源ID
     * @param fragment         Fragment实例，必须继承{@link BaseFragment} 或 {@link TitleBarFragment}
     * @param isReplace        是否为replace，不是的话则add
     * @param isAddToBackStack 是否添加到回退栈
     */
    public void startFragment(@IdRes int layoutID, BaseFragment fragment, boolean isReplace, boolean isAddToBackStack) {
        IntentUtils.startFragemnt(getSupportFragmentManager().beginTransaction(),
                layoutID, fragment, isReplace, isAddToBackStack, getTrasactionAnimations());
    }

    @Override
    public void startActivityForResult(Intent intent, int requestCode, @Nullable Bundle options) {
        super.startActivityForResult(intent, requestCode, options);
        if (getTrasactionAnimations().length > 1) {
            overridePendingTransition(getTrasactionAnimations()[0], getTrasactionAnimations()[1]);
        }
    }

    @Override
    public void startActivityFromChild(Activity child, Intent intent, int requestCode, Bundle options) {
        super.startActivityFromChild(child, intent, requestCode, options);
        if (getTrasactionAnimations().length > 1) {
            overridePendingTransition(getTrasactionAnimations()[0], getTrasactionAnimations()[1]);
        }
    }

    @Override
    public void startActivityFromFragment(Fragment fragment, Intent intent, int requestCode, @Nullable Bundle options) {
        super.startActivityFromFragment(fragment, intent, requestCode, options);
        if (getTrasactionAnimations().length > 1) {
            overridePendingTransition(getTrasactionAnimations()[0], getTrasactionAnimations()[1]);
        }
    }

    @Override
    public void finish() {
        super.finish();
        if (getTrasactionAnimations().length > 3) {
            overridePendingTransition(getTrasactionAnimations()[2], getTrasactionAnimations()[3]);
        }
    }

    /**
     * 返回4个动画资源id作为界面进入、退出动画效果
     *
     * @return
     */
    public int[] getTrasactionAnimations() {
        return IntentUtils.getTrasactionAnimations();
    }

    /**
     * 为布局控件添加向下的边距以支持不被NavigationBar覆盖
     *
     * @param views
     */
    public void makeAboveFromNavi(ViewGroup... views) {
        WidgetUtils.makeAdapterViewAboveNavigationBar(views);
    }
}
