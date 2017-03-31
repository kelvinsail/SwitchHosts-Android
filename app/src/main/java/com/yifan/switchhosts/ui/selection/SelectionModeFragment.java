package com.yifan.switchhosts.ui.selection;

import android.app.Activity;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.thinksky.utils.base.BaseAsyncTask;
import com.thinksky.utils.base.TitleBarFragment;
import com.thinksky.utils.base.widget.BaseRecyclerAdapter;
import com.yifan.switchhosts.R;
import com.yifan.switchhosts.impl.OnItemCheckedChangeListener;
import com.yifan.switchhosts.model.Hosts;
import com.yifan.switchhosts.model.SelectionHost;
import com.yifan.switchhosts.task.DealToSelectionsTask;
import com.yifan.switchhosts.task.SaveSelectionTask;
import com.yifan.switchhosts.ui.edit.EditHostsFragment;
import com.yifan.switchhosts.utils.Constants;

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Hosts编辑 - 选择模式界面
 *
 * Created by yifan on 2016/11/24.
 */

public class SelectionModeFragment extends TitleBarFragment implements OnItemCheckedChangeListener, BaseRecyclerAdapter.OnItemClickListener {

    public static final String TAG = "SelectionModeFragment";

    /**
     * 数据键值对key
     */
    public static final String BUNDLE_DATA_KEY = "hosts";

    /**
     * 数据存储键值对key
     */
    public static final String BUNDLE_DATA_SAVE_KEY = "save";

    /**
     * 列表控件
     */
    private RecyclerView mListView;

    /**
     * hosts数据
     */
    private Hosts mHostsData;

    /**
     * 数据适配器
     */
    private SelectionModeAdapter mAdapter;

    /**
     * hosts选择数组
     */
    private List<SelectionHost> mList;

    /**
     * 拆分Hosts异步任务
     */
    private DealToSelectionsTask mTask;

    /**
     * 拆分hosts 异步任务监听
     */
    private OnDealHostsListener mListener;

    /**
     * 保存选择异步任务
     */
    private SaveSelectionTask mSaveSelectionTask;

    /**
     * 保存异步任务
     */
    private OnSaveSelectionListener mSaveListener;

    @Override
    public String getTAG() {
        return TAG;
    }

    public static SelectionModeFragment newInstance(Hosts hosts) {
        Bundle args = new Bundle();
        SelectionModeFragment fragment = new SelectionModeFragment();
        args.putParcelable(BUNDLE_DATA_KEY, hosts);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mHostsData = getArguments().getParcelable(BUNDLE_DATA_KEY);
        mListView = new RecyclerView(this.getActivity());
        if (null != savedInstanceState) {
            mList = savedInstanceState.getParcelableArrayList(BUNDLE_DATA_SAVE_KEY);
        }
        if (null == mList) {
            mList = new ArrayList<>();
        }
        setContentView(mListView);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList(BUNDLE_DATA_SAVE_KEY, (ArrayList<? extends Parcelable>) mList);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
        inflater.inflate(R.menu.menu_selection_hosts, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_selection_save:
                saveSelection(mHostsData.getPath());
                return true;
            case R.id.action_selection_save_as:
                final AlertDialog.Builder builder = new AlertDialog.Builder(this.getActivity());
                final View view = LayoutInflater.from(this.getActivity()).inflate(R.layout.dialog_edittext, null);
                final AlertDialog dialog = builder.setTitle(R.string.input_file_name_tips).setView(view).create();
                final EditText editText = (EditText) view.findViewById(R.id.et_dialog_edittext);
                Button cancelButton = (Button) view.findViewById(R.id.button1);
                Button measureButton = (Button) view.findViewById(R.id.button2);
                cancelButton.setText(R.string.cancel);
                cancelButton.setVisibility(View.VISIBLE);
                measureButton.setText(R.string.measure);
                measureButton.setVisibility(View.VISIBLE);
                cancelButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });
                measureButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String name = editText.getText().toString();
                        //判断文件名输入是否为空
                        if (TextUtils.isEmpty(name)) {
                            Toast.makeText(SelectionModeFragment.this.getActivity(), R.string.input_file_name_tips, Toast.LENGTH_SHORT).show();
                            return;
                        }
                        //判断文件是否已存在
                        File file = new File(new StringBuilder().append(Constants.DIR_PATH_HOSTS).append(File.separator).append(name).toString());
                        if (file.exists()) {
                            Toast.makeText(SelectionModeFragment.this.getActivity(), R.string.input_file_existed_tips, Toast.LENGTH_SHORT).show();
                            return;
                        }
                        saveSelection(new StringBuilder(Constants.DIR_PATH_HOSTS).append(File.separator)
                                .append(name).toString());
                        dialog.dismiss();
                    }
                });
                dialog.show();

                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * 保存所选hosts
     *
     * @param path
     */
    private void saveSelection(String path) {
        if (null != mSaveSelectionTask) {
            mSaveSelectionTask.cancel(true);
            mSaveSelectionTask = null;
        }
        if (null == mSaveListener) {
            mSaveListener = new OnSaveSelectionListener(new WeakReference<SelectionModeFragment>(this));
        }
        mSaveSelectionTask = new SaveSelectionTask();
        mSaveSelectionTask.setOnAsyncListener(mSaveListener);
        mSaveSelectionTask.asyncExecute(mList, path);
    }

    @Override
    public void initView() {
        super.initView();
        getSupportTitleBar().setSubtitle(mHostsData.getName());
        mListView.setBackgroundResource(R.color.background_main);
        mListView.setLayoutManager(new LinearLayoutManager(this.getActivity()));
        mAdapter = new SelectionModeAdapter(mList);
        mListView.setAdapter(mAdapter);

        if (mList.size() <= 0) {
            //后台开始拆分hosts
            if (null != mTask) {
                mTask.cancel(true);
                mTask = null;
            }
            if (null == mListener) {
                mListener = new OnDealHostsListener(new WeakReference<>(this));
            }
            mTask = new DealToSelectionsTask();
            mTask.setOnAsyncListener(mListener);
            mTask.asyncExecute(mHostsData.getContent());
        }
    }

    @Override
    public void setListener() {
        super.setListener();
        mAdapter.setOnItemClickListener(this);
        mAdapter.setOnItemCheckedChangeListener(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (null != mTask) {
            mTask.cancel(true);
            mTask = null;
        }
    }

    @Override
    public void onItemChecked(View view, boolean isChecked, int position) {
        mList.get(position).setChecked(isChecked);
    }

    @Override
    public void onItemClick(View view, int itemType, int position) {
        ((CheckBox) view.findViewById(R.id.cb_selection_hosts)).toggle();
    }

    /**
     * 拆分hosts 异步任务监听
     */
    private class OnDealHostsListener implements BaseAsyncTask.OnAsyncListener {

        private WeakReference<SelectionModeFragment> mFragment;

        public OnDealHostsListener(WeakReference<SelectionModeFragment> fragment) {
            this.mFragment = fragment;
        }

        @Override
        public void onAsyncSuccess(Object data) {
            if (null != mFragment.get() && null != data) {
                if (null != mFragment.get().mList) {
                    mList.clear();
                } else {
                    mList = new ArrayList<>();
                }
                mFragment.get().mList.addAll((Collection<? extends SelectionHost>) data);
                mFragment.get().mAdapter.notifyDataSetChanged();
            }
        }

        @Override
        public void onAsyncFail() {

        }

        @Override
        public void onAsyncCancelled() {

        }

        @Override
        public void onAsyncStart() {
            if (null != mFragment.get()) {
                mFragment.get().createLoadingdialog();
            }
        }

        @Override
        public void onAsyncCompleted() {
            if (null != mFragment.get()) {
                mFragment.get().dissmissLoadingDialog();
            }
        }
    }

    /**
     * 保存选中项异步任务 监听
     */
    private static class OnSaveSelectionListener implements BaseAsyncTask.OnAsyncListener {

        private WeakReference<SelectionModeFragment> mFragment;

        public OnSaveSelectionListener(WeakReference<SelectionModeFragment> fragment) {
            this.mFragment = fragment;
        }

        @Override
        public void onAsyncSuccess(Object data) {
            if (null != mFragment.get() && null != data && data instanceof Boolean) {
                if ((boolean) data) {
                    mFragment.get().getActivity().setResult(Activity.RESULT_OK);
                    Toast.makeText(mFragment.get().getActivity(), R.string.save_success, Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(mFragment.get().getActivity(), R.string.save_fail, Toast.LENGTH_SHORT).show();
                }
            }
        }

        @Override
        public void onAsyncFail() {

        }

        @Override
        public void onAsyncCancelled() {

        }

        @Override
        public void onAsyncStart() {
            if (null != mFragment.get()) {
                mFragment.get().createLoadingdialog();
            }
        }

        @Override
        public void onAsyncCompleted() {
            if (null != mFragment.get()) {
                mFragment.get().dissmissLoadingDialog();
            }
        }
    }

    @Override
    public boolean isPrintLifeCycle() {
        return true;
    }

}
