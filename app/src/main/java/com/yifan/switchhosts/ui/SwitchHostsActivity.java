package com.yifan.switchhosts.ui;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.thinksky.utils.base.BaseAsyncTask;
import com.thinksky.utils.base.TitleBarActivity;
import com.thinksky.utils.base.widget.BaseRecyclerAdapter;
import com.thinksky.utils.base.widget.BaseRecyclerHolder;
import com.thinksky.utils.utils.ResourcesUtils;
import com.yifan.switchhosts.R;
import com.yifan.switchhosts.model.Hosts;
import com.yifan.switchhosts.model.RootActionResutlt;
import com.yifan.switchhosts.task.GetCurrentHostsTask;
import com.yifan.switchhosts.task.GetHostsListTask;
import com.yifan.switchhosts.task.ReplaceHostsTask;
import com.yifan.switchhosts.utils.Constants;
import com.yifan.switchhosts.utils.FileUtils;
import com.yifan.switchhosts.widget.PullToRefreshLayout;

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Hosts列表展示界面
 *
 * Created by yifan on 2016/10/31.
 */
public class SwitchHostsActivity extends TitleBarActivity
        implements BaseRecyclerAdapter.OnItemClickListener, BaseRecyclerAdapter.OnItemLongClickListener {

    private static final String TAG = "SwitchHostsActivity";

    /**
     * 编辑Hosts文件标记
     */
    public static final int REQUEST_CODE_EDIT_HOSTS = 0x001;

    /**
     * 下拉刷新控件
     */
    private PullToRefreshLayout mPullToRefreshLayout;

    /**
     * hosts列表控件
     */
    private RecyclerView mRecyclerView;

    /**
     * 数据适配器
     */
    private HostsAdapter mAdapter;

    /**
     * Hosts文件数组
     */
    private List<Hosts> mHostsList;

    /**
     * 获取Hosts文件异步任务
     */
    private GetHostsListTask mGetHostsTask;

    /**
     * 切换Hosts异步任务
     */
    private ReplaceHostsTask mReplaceHostsTask;

    /**
     * 获取Hosts文件异步任务 数据监听器
     */
    private OnGetHostsListener mGetHostsListener;

    /**
     * 查看当前hosts异步任务
     */
    private GetCurrentHostsTask mGetCurrentHostsTask;

    /**
     * 查看当前Hosts异步任务
     */
    private OnCheckCurrentHostsListener mCheckCurrentHostsListener;

    /**
     * Hosts切换异步任务监听
     */
    private OnSwitchHostsListener mSwitchHostsListener;

    /**
     * 添加新Hosts按钮
     */
    private FloatingActionButton mAddNewHostsBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mHostsList = new ArrayList<>();
        setContentView(R.layout.activity_switch_hosts, 0, false);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
                && PackageManager.PERMISSION_GRANTED != checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
        } else {
            checkDirExist();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (null != permissions && null != grantResults
                && permissions.length == grantResults.length) {
            for (int i = 0; i < permissions.length; i++) {
                if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                    checkDirExist();
                } else {
                    Toast.makeText(this, R.string.no_storage_permission, Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    @Override
    public String getTAG() {
        return TAG;
    }

    @Override
    public void initView() {
        super.initView();
        mAddNewHostsBtn = (FloatingActionButton) findViewById(R.id.fab_switch_hosts_add);
        mRecyclerView = (RecyclerView) findViewById(R.id.rv_switch_hosts);
        mPullToRefreshLayout = (PullToRefreshLayout) findViewById(R.id.pulltorefresh_switch_hosts);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        mAdapter = new HostsAdapter();
        mAdapter.setOnItemClickListener(this);
        mAdapter.setOnItemLongClickListener(this);
        mRecyclerView.setAdapter(mAdapter);
    }

    @Override
    public void setListener() {
        super.setListener();
        mPullToRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                loadData();
            }
        });
        mAddNewHostsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(new Intent(SwitchHostsActivity.this, EditHostsActivity.class), REQUEST_CODE_EDIT_HOSTS);
            }
        });

        //加载数据
        loadData();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_switch_hosts, menu);
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_EDIT_HOSTS && resultCode == RESULT_OK) {
            loadData();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_check_current:
                if (null != mGetCurrentHostsTask) {
                    mGetCurrentHostsTask.cancel(true);
                    mGetCurrentHostsTask = null;
                }
                if (null == mCheckCurrentHostsListener) {
                    mCheckCurrentHostsListener = new OnCheckCurrentHostsListener(new WeakReference<SwitchHostsActivity>(this));
                }
                mGetCurrentHostsTask = new GetCurrentHostsTask();
                mGetCurrentHostsTask.setOnAsyncListener(mCheckCurrentHostsListener);
                mGetCurrentHostsTask.asyncExecute();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean isTitleBarBackEnable() {
        return false;
    }

    /**
     * 加载数据
     */
    private void loadData() {
        if (null != mGetHostsTask) {
            mGetHostsTask.cancel(true);
            mGetHostsTask = null;
        }
        if (null == mGetHostsListener) {
            mGetHostsListener = new OnGetHostsListener(new WeakReference(this));
        }
        mGetHostsTask = new GetHostsListTask();
        mGetHostsTask.setOnAsyncListener(mGetHostsListener);
        mGetHostsTask.asyncExecute();
    }

    @Override
    public void onItemClick(View view, int itemType, int position) {
        showReplaceDialog(mHostsList.get(position).getPath());
    }

    @Override
    public boolean onItemLongClick(View view, int itemType, final int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setAdapter(new MenuAdapter(), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case 0://查看
                        Intent intent = new Intent(SwitchHostsActivity.this, EditHostsActivity.class);
                        intent.putExtra(Constants.BUNDLE_KEY_DATA, mHostsList.get(position));
                        startActivityForResult(intent, REQUEST_CODE_EDIT_HOSTS);
                        break;
                    case 1://删除
                        showDeleteDialog(mHostsList.get(position).getPath());
                        break;
                    case 2://重命名
                        AlertDialog.Builder renameBuilder = new AlertDialog.Builder(SwitchHostsActivity.this);
                        final View view = LayoutInflater.from(SwitchHostsActivity.this).inflate(R.layout.dialog_edittext, null);
                        final AlertDialog renameDialog = renameBuilder.setTitle(R.string.input_file_name_tips).setView(view).create();
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
                                renameDialog.dismiss();
                            }
                        });
                        measureButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                String name = editText.getText().toString();
                                //判断文件名输入是否为空
                                if (TextUtils.isEmpty(name)) {
                                    Toast.makeText(SwitchHostsActivity.this, R.string.input_file_name_tips, Toast.LENGTH_SHORT).show();
                                    return;
                                }
                                //判断文件是否已存在
                                File file = new File(new StringBuilder().append(Constants.DIR_PATH_HOSTS).append(File.separator).append(name).toString());
                                if (file.exists()) {
                                    Toast.makeText(SwitchHostsActivity.this, R.string.input_file_existed_tips, Toast.LENGTH_SHORT).show();
                                    return;
                                }
                                File originFile = new File(mHostsList.get(position).getPath());
                                if (originFile.renameTo(file.getAbsoluteFile())) {
                                    mHostsList.get(position).setName(file.getName());
                                    mHostsList.get(position).setPath(file.getPath());
                                    mAdapter.notifyDataSetChanged();
                                    renameDialog.dismiss();
                                } else {
                                    Toast.makeText(SwitchHostsActivity.this, R.string.save_fail, Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                        renameDialog.show();

                        break;
                    case 3://使用
                        showReplaceDialog(mHostsList.get(position).getPath());
                        break;
                }
            }
        });
        builder.create().show();
        return true;
    }

    /**
     * 展示确认替换为选中Hosts文件的弹窗
     *
     * @param path
     */
    private void showReplaceDialog(final String path) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.confirm);
        builder.setMessage(R.string.confirm_to_replace_from_this_host);
        builder.setNegativeButton(R.string.cancel, null);
        builder.setPositiveButton(R.string.measure, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (null != mReplaceHostsTask) {
                    mReplaceHostsTask.cancel(true);
                    mReplaceHostsTask = null;
                }
                if (null == mSwitchHostsListener) {
                    mSwitchHostsListener = new OnSwitchHostsListener(new WeakReference<>(SwitchHostsActivity.this));
                }
                mReplaceHostsTask = new ReplaceHostsTask();
                mReplaceHostsTask.setOnAsyncListener(mSwitchHostsListener);
                mReplaceHostsTask.asyncExecute(path, Constants.FILE_PATH_SYSTEM_HOSTS);
            }
        });
        builder.create().show();
    }

    /**
     * 展示确认删除该Hosts文件的弹窗
     *
     * @param path
     */
    private void showDeleteDialog(final String path) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.confirm);
        builder.setMessage(R.string.confirm_to_delete_this_host);
        builder.setNegativeButton(R.string.cancel, null);
        builder.setPositiveButton(R.string.measure, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                FileUtils.deleteFile(path);
                loadData();
            }
        });
        builder.create().show();
    }

    /**
     * 菜单数据适配器
     */
    private class MenuAdapter extends BaseAdapter {

        private String[] mList;

        private LayoutInflater mLayoutInflater;

        public MenuAdapter() {
            this.mList = ResourcesUtils.getStringArray(R.array.item_hosts_menu);
            this.mLayoutInflater = LayoutInflater.from(SwitchHostsActivity.this);
        }

        @Override
        public int getCount() {
            return mList.length;
        }

        @Override
        public Object getItem(int position) {
            return mList[position];
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            if (null != convertView) {
                holder = (ViewHolder) convertView.getTag();
            } else {
                holder = new ViewHolder();
                convertView = mLayoutInflater.inflate(R.layout.item_menu, parent, false);
                holder.itemText = (TextView) convertView;
                convertView.setTag(holder);
            }
            holder.itemText.setText(mList[position]);
            return convertView;
        }

        private class ViewHolder {

            TextView itemText;

        }

    }


    /**
     * Hosts文件适配器
     */
    private class HostsAdapter extends BaseRecyclerAdapter<HostsAdapter.HostsHolder> {

        LayoutInflater mLayoutInflater;

        public HostsAdapter() {
            this.mLayoutInflater = LayoutInflater.from(SwitchHostsActivity.this);
        }

        @Override
        public HostsHolder onCreate(ViewGroup parent, int viewType) {
            return new HostsHolder(mLayoutInflater.inflate(R.layout.item_hosts, parent, false));
        }

        @Override
        public void onBind(HostsHolder viewHolder, int realPosition) {
            viewHolder.setData(mHostsList.get(realPosition));
        }

        @Override
        public int getRealItemCount() {
            return mHostsList.size();
        }

        @Override
        public HostsHolder getFakeHolder(View view) {
            return null;
        }

        public class HostsHolder extends BaseRecyclerHolder {

            /**
             * Hosts文件名文本控件
             */
            private TextView mNameText;

            public HostsHolder(View itemView) {
                super(itemView);
                mNameText = (TextView) itemView.findViewById(R.id.tv_item_host_name);
            }

            public void setData(Hosts hosts) {
                SpannableStringBuilder builder = new SpannableStringBuilder(hosts.getName());
                builder.append("    ").append(DateFormat.format(ResourcesUtils.getString(R.string.date_format_default), hosts.getModifiedTime()))
                        .append("\n").append(hosts.getPreview());
                builder.setSpan(new ForegroundColorSpan(ResourcesUtils.getColor(R.color.text_gray)),
                        hosts.getName().length() + 1, builder.length(), SpannableStringBuilder.SPAN_INCLUSIVE_EXCLUSIVE);
                builder.setSpan(new AbsoluteSizeSpan(ResourcesUtils.getDimensionPixelSize(R.dimen.text_little)),
                        hosts.getName().length() + 1, builder.length(), SpannableStringBuilder.SPAN_INCLUSIVE_EXCLUSIVE);
                mNameText.setText(builder);
            }
        }
    }

    /**
     * 获取Host文件列表 异步任务监听器
     */
    private static class OnGetHostsListener implements BaseAsyncTask.OnAsyncListener {

        /**
         * Activity引用
         */
        private WeakReference<SwitchHostsActivity> mActivity;

        public OnGetHostsListener(WeakReference<SwitchHostsActivity> weakReference) {
            this.mActivity = weakReference;
        }

        @Override
        public void onAsyncSuccess(Object data) {
            if (null != data && null != mActivity.get()) {
                if (null == mActivity.get().mHostsList) {
                    mActivity.get().mHostsList = new ArrayList<>();
                } else {
                    mActivity.get().mHostsList.clear();
                }
                mActivity.get().mHostsList.addAll((Collection<? extends Hosts>) data);
                mActivity.get().mAdapter.notifyDataSetChanged();
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
            if (null != mActivity.get()) {
                mActivity.get().mPullToRefreshLayout.setRefreshing(true);
            }
        }

        @Override
        public void onAsyncCompleted() {
            if (null != mActivity.get()) {
                mActivity.get().mPullToRefreshLayout.setRefreshing(false);
                mActivity.get().mGetHostsTask = null;
            }
        }
    }

    /**
     * 查看当前Hosts异步任务 监听
     */
    private static class OnCheckCurrentHostsListener implements BaseAsyncTask.OnAsyncListener {

        private WeakReference<SwitchHostsActivity> mActivity;

        public OnCheckCurrentHostsListener(WeakReference<SwitchHostsActivity> activity) {
            this.mActivity = activity;
        }

        @Override
        public void onAsyncSuccess(Object data) {
            if (null != mActivity.get() && null != data && data instanceof Hosts) {
                Intent intent = new Intent(mActivity.get(), EditHostsActivity.class);
                intent.putExtra(Constants.BUNDLE_KEY_DATA, (Hosts) data);
                mActivity.get().startActivityForResult(intent, REQUEST_CODE_EDIT_HOSTS);
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
            if (null != mActivity.get()) {
                mActivity.get().createLoadingdialog();
            }
        }

        @Override
        public void onAsyncCompleted() {
            if (null != mActivity.get()) {
                mActivity.get().dissmissLoadingDialog();
                mActivity.get().mGetCurrentHostsTask = null;
            }
        }
    }

    /**
     * 切换Hosts异步任务 监听
     */
    private static class OnSwitchHostsListener implements BaseAsyncTask.OnAsyncListener {

        private WeakReference<SwitchHostsActivity> mActivity;

        public OnSwitchHostsListener(WeakReference<SwitchHostsActivity> activity) {
            this.mActivity = activity;
        }

        @Override
        public void onAsyncSuccess(Object data) {
            if (null != mActivity.get() && data instanceof RootActionResutlt) {
                if (((RootActionResutlt) data).isRootSucess) {
                    if (((RootActionResutlt) data).isActionDone) {
                        Toast.makeText(mActivity.get(), R.string.switch_hosts_success, Toast.LENGTH_SHORT).show();
                    } else {
                        onAsyncFail();
                    }
                } else {
                    Toast.makeText(mActivity.get(), R.string.switch_hosts_fail_witout_root, Toast.LENGTH_SHORT).show();
                }
            }
        }

        @Override
        public void onAsyncFail() {
            if (null != mActivity.get()) {
                Toast.makeText(mActivity.get(), R.string.switch_hosts_fail, Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        public void onAsyncCancelled() {

        }

        @Override
        public void onAsyncStart() {
            if (null != mActivity.get()) {
                mActivity.get().createLoadingdialog(null, false, false);
            }
        }

        @Override
        public void onAsyncCompleted() {
            if (null != mActivity.get()) {
                mActivity.get().dissmissLoadingDialog();
            }
        }
    }


    /**
     * 检查文件夹是否已创建
     */
    private void checkDirExist() {
        //文件夹路径，root必须在第一位
        String[] paths = new String[]{
                Constants.DIR_PATH_ROOT,//根目录
                Constants.DIR_PATH_DEFAULT,//默认资源文件路径
                Constants.DIR_PATH_HOSTS//host文件夹
        };
        //开始检查
        for (String path : paths) {
            File dir = new File(path);
            if (!dir.exists()) {
                dir.mkdirs();
            }
        }
        //开始复制自带Host文件
        new Thread(new Runnable() {
            @Override
            public void run() {
                //需要复制的文件
                String[] fileNames = new String[]{Constants.FILE_DEFAULT_API_HOST, Constants.FILE_DEFAULT_TEST_API_HOST};
                //遍历文件名复制
                for (String name : fileNames) {
                    File file = new File(new StringBuilder(Constants.DIR_PATH_DEFAULT).append(File.separator).append(name).toString());
                    if (!file.exists()) {
                        FileUtils.copyAssetFileToStroge(name, file.getAbsolutePath());
                    }
                }
                SwitchHostsActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        //加载数据
                        loadData();
                    }
                });
            }
        }).start();
    }

}
