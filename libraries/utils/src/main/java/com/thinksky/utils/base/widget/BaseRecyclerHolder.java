package com.thinksky.utils.base.widget;

import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.View;


/**
 * BaseRecyclerHolder RecyclerView.ViewHolder封装
 *
 * Created by yifan on 2016/8/25.
 */
public abstract class BaseRecyclerHolder extends RecyclerView.ViewHolder {

    private static final String POSITION = "position";

    public BaseRecyclerHolder(View itemView) {
        super(itemView);
    }

    /**
     * 设置序号等数据
     *
     * @param view
     * @param position
     */
    public void setPosition(View view, int position) {
        Bundle data;
        if (null != view
                && null != view.getTag()
                && view.getTag() instanceof Bundle) {
            data = (Bundle) view.getTag();
        } else {
            data = new Bundle();
        }
        data.putInt(POSITION, position);
        view.setTag(data);
    }


    /**
     * 从View的tag中取出序号
     *
     * @param view
     * @return
     */
    public static int getPositionFroView(View view) {
        int posotion = -1;
        //取出序号
        if (null != view && null != view.getTag()
                && view.getTag() instanceof Bundle) {
            posotion = ((Bundle) view.getTag()).getInt(POSITION, -1);
        }
        return posotion;
    }
}
