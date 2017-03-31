package com.yifan.switchhosts.ui;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.text.Selection;
import android.text.TextUtils;
import android.text.method.ArrowKeyMovementMethod;
import android.text.method.MovementMethod;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.thinksky.utils.base.BaseActivity;
import com.thinksky.utils.base.TitleBarActivity;
import com.thinksky.utils.utils.FileUtils;
import com.yifan.switchhosts.R;
import com.yifan.switchhosts.model.Hosts;
import com.yifan.switchhosts.ui.edit.EditHostsFragment;
import com.yifan.switchhosts.ui.selection.SelectionModeFragment;
import com.yifan.switchhosts.utils.Constants;

import java.io.File;

/**
 * 查看/编辑Hosts 界面
 *
 * Created by yifan on 2016/11/1.
 */
public class EditHostsActivity extends BaseActivity {

    private static final String TAG = "EditHostsActivity";

    /**
     * Hosts数据
     */
    private Hosts mHosts;

    @Override
    public String getTAG() {
        return TAG;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mHosts = getIntent().getParcelableExtra(Constants.BUNDLE_KEY_DATA);
        getSupportFragmentManager().beginTransaction().replace(android.R.id.content,
                EditHostsFragment.newInstance(mHosts), EditHostsFragment.TAG).commit();
    }

}
