package com.thinksky.utils.utils;

import android.support.annotation.IdRes;
import android.support.v4.app.FragmentTransaction;

import com.thinksky.utils.R;
import com.thinksky.utils.base.BaseFragment;

/**
 * 意图工具
 *
 * Created by yifan on 2016/10/12.
 */
public class IntentUtils {


    /**
     * 启动一个Fragment
     *
     * @param transaction
     * @param layoutID
     * @param fragment
     * @param isReplace
     * @param isAddToBackStack
     * @param animIDs
     */
    public static void startFragemnt(FragmentTransaction transaction, @IdRes int layoutID,
                                     BaseFragment fragment, boolean isReplace, boolean isAddToBackStack, int[] animIDs) {
        if (animIDs.length == 4) {
            transaction.setCustomAnimations(animIDs[0], animIDs[1], animIDs[2], animIDs[3]);
        }
        if (isReplace) {
            transaction.replace(layoutID, fragment, fragment.getTAG());
        } else {
            transaction.add(layoutID, fragment, fragment.getTAG());
        }
        if (isAddToBackStack) {
            transaction.addToBackStack(fragment.getTAG());
        }
        transaction.commit();
    }


    /**
     * 返回4个动画资源id作为界面进入、退出动画效果
     *
     * @return
     */
    public static int[] getTrasactionAnimations() {
        return new int[]{R.anim.enter_from_right, R.anim.exit_to_left, R.anim.enter_from_left, R.anim.exit_to_right};
    }
}
