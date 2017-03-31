package com.yifan.switchhosts.ui.edit;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.text.Selection;
import android.text.TextUtils;
import android.text.method.ArrowKeyMovementMethod;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.thinksky.utils.base.TitleBarActivity;
import com.thinksky.utils.base.TitleBarFragment;
import com.thinksky.utils.utils.FileUtils;
import com.yifan.switchhosts.R;
import com.yifan.switchhosts.model.Hosts;
import com.yifan.switchhosts.task.GetHostsListTask;
import com.yifan.switchhosts.ui.selection.SelectionModeFragment;
import com.yifan.switchhosts.utils.Constants;

import java.io.File;

/**
 * 查看/编辑Hosts 界面
 *
 * Created by yifan on 2016/11/1.
 */
public class EditHostsFragment extends TitleBarFragment {

    public static final String TAG = "EditHostsFragment";

    /**
     * Hosts数据
     */
    private Hosts mHosts;

    /**
     * Hosts编辑控件
     */
    private EditText mHostsEditText;

    /**
     * 菜单控制
     */
    private Menu mMenu;

    @Override
    public String getTAG() {
        return TAG;
    }

    public static EditHostsFragment newInstance(Hosts hosts) {
        Bundle args = new Bundle();
        EditHostsFragment fragment = new EditHostsFragment();
        args.putParcelable(Constants.BUNDLE_KEY_DATA, hosts);
        fragment.setArguments(args);
        return fragment;
    }

    public EditHostsFragment() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mHosts = getArguments().getParcelable(Constants.BUNDLE_KEY_DATA);
        setContentView(R.layout.activity_edit_hosts, 0, false);
    }

    @Override
    public void initView() {
        super.initView();
        mHostsEditText = (EditText) getContentView().findViewById(R.id.et_hosts_check);
        if (null != mHosts) {
            setEditTextEnable(false);
            mHostsEditText.setText(mHosts.getContent());
            if (mHosts.isSystemHosts()) {
                getSupportTitleBar().setTitle(R.string.system_hosts);
                mHostsEditText.setTextIsSelectable(false);
                mHostsEditText.setMovementMethod(ArrowKeyMovementMethod.getInstance());
            } else {
                getSupportTitleBar().setTitle(R.string.edit_hosts);
                getSupportTitleBar().setSubtitle(mHosts.getName());
            }
        } else {
            mHosts = new Hosts();
            getSupportTitleBar().setTitle(R.string.new_hosts);
            setEditTextEnable(true);
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        menu.clear();
        //系统host不可修改
        if (mHosts.isSystemHosts()) {
            inflater.inflate(R.menu.menu_check_hosts_system, menu);
            return;
        }
        inflater.inflate(R.menu.menu_check_hosts, menu);
        mMenu = menu;
        if (null != mHosts.getPath()) {//路径不为空，初始化为预览状态
            setMenuItemVisible(R.id.action_save, false);
        } else {
            setMenuItemVisible(R.id.action_edit, false);
            setMenuItemVisible(R.id.action_save_as, false);
        }
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_edit://开启编辑模式
                setEditTextEnable(true);
                mHostsEditText.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        setMenuItemVisible(R.id.action_edit, false);
                        setMenuItemVisible(R.id.action_save, true);
                    }
                }, 100);
                return true;
            case R.id.action_save://保存
                if (TextUtils.isEmpty(mHosts.getPath())) {//新增
                    showSaveAsDialog();
                } else {//修改
                    saveHosts(mHosts.getPath(), mHostsEditText.getText().toString());
                }
                break;
            case R.id.action_save_as://另存为
                showSaveAsDialog();
                break;
            case R.id.action_selection_mode://选择模式
                getFragmentManager().beginTransaction().setCustomAnimations(android.R.anim.fade_in,
                        android.R.anim.fade_out, android.R.anim.fade_in, android.R.anim.fade_out)
                        .replace(android.R.id.content, SelectionModeFragment.newInstance(mHosts), SelectionModeFragment.TAG)
                        .remove(this)
//                        .addToBackStack(SelectionModeFragment.TAG)
                        .commit();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * 隐藏或展示菜单项
     *
     * @param menuID
     * @param isHide
     */
    private void setMenuItemVisible(@IdRes int menuID, boolean isHide) {
        if (null != mMenu && menuID > 0) {
            MenuItem menuItem = mMenu.findItem(menuID);
            if (null != menuItem) {
                menuItem.setVisible(isHide);
            }
        }
    }

    /**
     * 展示另存为弹窗
     */
    private void showSaveAsDialog() {
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
                    Toast.makeText(EditHostsFragment.this.getActivity(), R.string.input_file_name_tips, Toast.LENGTH_SHORT).show();
                    return;
                }
                //判断文件是否已存在
                File file = new File(new StringBuilder().append(Constants.DIR_PATH_HOSTS).append(File.separator).append(name).toString());
                if (file.exists()) {
                    Toast.makeText(EditHostsFragment.this.getActivity(), R.string.input_file_existed_tips, Toast.LENGTH_SHORT).show();
                    return;
                }
                saveHosts(new StringBuilder(Constants.DIR_PATH_HOSTS).append(File.separator)
                        .append(name).toString(), mHostsEditText.getText().toString());
                dialog.dismiss();
            }
        });
        dialog.show();
    }


    /**
     * 设置EditText是否可编辑
     *
     * @param isEnable
     */
    private void setEditTextEnable(boolean isEnable) {
        if (isEnable) {
            mHostsEditText.setTextIsSelectable(false);
            mHostsEditText.setMovementMethod(ArrowKeyMovementMethod.getInstance());
            mHostsEditText.setCursorVisible(true);
            mHostsEditText.setFocusable(true);
            mHostsEditText.setFocusableInTouchMode(true);
            mHostsEditText.setClickable(true);
            mHostsEditText.setLongClickable(true);
            Selection.setSelection(mHostsEditText.getText(), mHostsEditText.getText().length());
            mHostsEditText.requestFocus();
            //自动弹出软键盘
            mHostsEditText.post(new Runnable() {
                @Override
                public void run() {
                    getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
                    InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
                }
            });

        } else {
            mHostsEditText.setTextIsSelectable(true);
            mHostsEditText.setCursorVisible(false);
            mHostsEditText.setFocusable(true);
            mHostsEditText.setFocusableInTouchMode(true);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(mHostsEditText.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
    }

    /**
     * 保存Hosts
     *
     * @param path    文件路径
     * @param content 内容
     * @return
     */
    private boolean saveHosts(String path, String content) {
        boolean isSaveSuccess = FileUtils.printDataToFile(path, content);
        if (TextUtils.isEmpty(mHosts.getPath())) {
            mHosts.setPath(path);
        }
        if (isSaveSuccess) {
            if (content.length() > GetHostsListTask.SUB_LENGHT) {
                mHosts.setPreview(content.substring(0, GetHostsListTask.SUB_LENGHT));
            } else {
                mHosts.setPreview(content);
            }
            mHosts.setContent(content);
            getActivity().setResult(Activity.RESULT_OK);
            Toast.makeText(this.getActivity(), R.string.save_success, Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this.getActivity(), R.string.save_fail, Toast.LENGTH_SHORT).show();
        }
        return isSaveSuccess;
    }

    @Override
    public boolean isPrintLifeCycle() {
        return true;
    }
}
