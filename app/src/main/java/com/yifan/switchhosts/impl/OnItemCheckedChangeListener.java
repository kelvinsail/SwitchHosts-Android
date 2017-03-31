package com.yifan.switchhosts.impl;

import android.view.View;

/**
 * 列表选中事件监听器
 *
 * Created by yifan on 2016/11/28.
 */
public interface OnItemCheckedChangeListener {

    void onItemChecked(View view, boolean isChecked, int position);

}
