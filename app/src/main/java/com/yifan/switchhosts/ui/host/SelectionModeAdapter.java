package com.yifan.switchhosts.ui.host;

import android.view.View;
import android.view.ViewGroup;

import com.thinksky.utils.base.widget.BaseRecyclerAdapter;
import com.thinksky.utils.base.widget.BaseRecyclerHolder;

/**
 * Created by yifan on 2016/11/24.
 */

public class SelectionModeAdapter extends BaseRecyclerAdapter<SelectionModeAdapter.SelectionMoadeHolder> {

    @Override
    public SelectionMoadeHolder onCreate(ViewGroup parent, int viewType) {
        return null;
    }

    @Override
    public void onBind(SelectionMoadeHolder viewHolder, int realPosition) {

    }

    @Override
    public int getRealItemCount() {
        return 0;
    }

    @Override
    public SelectionMoadeHolder getFakeHolder(View view) {
        return null;
    }

    public class SelectionMoadeHolder extends BaseRecyclerHolder {
        public SelectionMoadeHolder(View itemView) {
            super(itemView);
        }
    }
}
