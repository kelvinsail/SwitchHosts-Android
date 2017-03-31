package com.thinksky.utils.base;

import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.ViewGroup;

import com.thinksky.utils.base.impl.LoadImpl;
import com.thinksky.utils.base.lifecycle.LifeCycleFragment;
import com.thinksky.utils.utils.IntentUtils;
import com.thinksky.utils.utils.WidgetUtils;

/**
 * Fragmnet基类
 * <p>
 * Created by yifan on 2016/7/16.
 */
public abstract class BaseFragment extends LifeCycleFragment implements LoadImpl {

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
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
     * 获取标题的名字
     *
     * @return
     */
    public String getTitleName() {
        return null;
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
        IntentUtils.startFragemnt(getActivity().getSupportFragmentManager().beginTransaction(),
                layoutID, fragment, isReplace, isAddToBackStack, getTrasactionAnimations());
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
