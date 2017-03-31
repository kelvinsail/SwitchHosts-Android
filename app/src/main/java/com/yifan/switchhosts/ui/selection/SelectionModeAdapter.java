package com.yifan.switchhosts.ui.selection;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.thinksky.utils.base.widget.BaseRecyclerAdapter;
import com.thinksky.utils.base.widget.BaseRecyclerHolder;
import com.yifan.switchhosts.R;
import com.yifan.switchhosts.impl.OnItemCheckedChangeListener;
import com.yifan.switchhosts.model.Hosts;
import com.yifan.switchhosts.model.SelectionHost;

import java.util.List;

/**
 * Hosts选择模式数据适配器
 *
 * Created by yifan on 2016/11/24.
 */

public class SelectionModeAdapter extends BaseRecyclerAdapter<SelectionModeAdapter.BaseSelectionHolder>
        implements CompoundButton.OnCheckedChangeListener {

    /**
     * 列表数据
     */
    private List<SelectionHost> mList;

    /**
     * hosts选项
     */
    public static final int ITEM_HOSTS = 0x001;

    /**
     * 说明
     */
    public static final int ITEM_DESPRICTION = 0x002;

    /**
     * 布局加载器
     */
    private LayoutInflater mLayoutInflater;

    /**
     * 列表项选中事件监听
     */
    private OnItemCheckedChangeListener mItemCheckedChangeListener;

    public SelectionModeAdapter(List<SelectionHost> list) {
        this.mList = list;
    }

    @Override
    public int getRealItemType(int position) {
        SelectionHost host = mList.get(position);
        if (null != host) {
            if (host.isDescription()) {
                return ITEM_DESPRICTION;
            } else {
                return ITEM_HOSTS;
            }
        }
        return super.getRealItemType(position);
    }

    @Override
    public BaseSelectionHolder onCreate(ViewGroup parent, int viewType) {
        if (null == mLayoutInflater) {
            mLayoutInflater = LayoutInflater.from(parent.getContext());
        }
        if (viewType == ITEM_HOSTS) {
            return new SelectionHolder(mLayoutInflater.inflate(R.layout.item_selection_hosts, parent, false));
        }
        return new BaseSelectionHolder(new View(parent.getContext()));
    }

    @Override
    public void onBind(BaseSelectionHolder viewHolder, int realPosition) {
        if (null != viewHolder) {
            viewHolder.setData(mList.get(realPosition));
            if (viewHolder instanceof SelectionHolder) {
                ((SelectionHolder) viewHolder).setPosition(realPosition);
            }
        }
    }

    @Override
    public int getRealItemCount() {
        if (null == mList) {
            return 0;
        }
        return mList.size();
    }

    @Override
    public BaseSelectionHolder getFakeHolder(View view) {
        return null;
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if (null != buttonView && null != mItemCheckedChangeListener) {
            int position = BaseRecyclerHolder.getPositionFroView(buttonView);
            if (position >= 0) {
                mItemCheckedChangeListener.onItemChecked(buttonView, isChecked, position);
            }
        }
    }

    public class BaseSelectionHolder extends BaseRecyclerHolder {

        public BaseSelectionHolder(View itemView) {
            super(itemView);
        }

        public void setData(SelectionHost host) {

        }
    }

    public class SelectionHolder extends BaseSelectionHolder {

        CheckBox itemCheckText;
        TextView fromText;
        TextView toText;

        public SelectionHolder(View itemView) {
            super(itemView);
            itemCheckText = (CheckBox) itemView.findViewById(R.id.cb_selection_hosts);
            fromText = (TextView) itemView.findViewById(R.id.tv_selection_hosts_from);
            toText = (TextView) itemView.findViewById(R.id.tv_selection_hosts_to);
        }

        @Override
        public void setData(SelectionHost host) {
            itemCheckText.setOnCheckedChangeListener(null);
            itemCheckText.setChecked(host.isChecked());
            itemCheckText.setOnCheckedChangeListener(SelectionModeAdapter.this);
            fromText.setText(host.getHostFrom());
            toText.setText(host.getHostTo());
        }

        public void setPosition(int position) {
            setPosition(itemCheckText, position);
        }
    }

    /**
     * 设置列表项选中事件监听
     *
     * @param listener
     */
    public void setOnItemCheckedChangeListener(OnItemCheckedChangeListener listener) {
        this.mItemCheckedChangeListener = listener;
    }
}
