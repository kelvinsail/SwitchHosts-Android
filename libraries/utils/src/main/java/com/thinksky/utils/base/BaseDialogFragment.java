package com.thinksky.utils.base;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.View;

import com.thinksky.utils.R;
import com.thinksky.utils.base.lifecycle.LifeCycleDialogFragment;
import com.thinksky.utils.utils.IntentUtils;

/**
 * BaseDialogFragment DialogFragment基类
 *
 * Created by yifan on 2016/9/18.
 */
public abstract class BaseDialogFragment extends LifeCycleDialogFragment {

    @Override
    public abstract String getTAG();

    @Override
    public void initView() {
    }

    @Override
    public void setListener() {
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = new Dialog(getActivity(), R.style.BaseDialog);
        if (getLayoutResID() > 0) {
            dialog.setContentView(getLayoutResID());
        } else {
            dialog.setContentView(getLayoutView());
        }
        dialog.setCancelable(isCancelable());
        dialog.setCanceledOnTouchOutside(isCanceledOnTouchOutside());
        return dialog;
    }

    /**
     * 获取资源布局ID
     *
     * @return 资源布局ID
     */
    public abstract int getLayoutResID();

    /**
     * 获取布局控件对象
     *
     * @return
     */
    public abstract View getLayoutView();

    /**
     * 窗口是否可以消除
     *
     * @return
     */
    public boolean isCancelable() {
        return true;
    }


    /**
     * 点击窗口外区域是否可以消除
     *
     * @return
     */
    public boolean isCanceledOnTouchOutside() {
        return true;
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
}
