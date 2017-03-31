package com.yifan.switchhosts.task;

import android.util.Log;

import com.thinksky.utils.base.BaseAsyncTask;
import com.yifan.switchhosts.model.Hosts;
import com.yifan.switchhosts.utils.Constants;
import com.yifan.switchhosts.utils.HostsUtils;

import org.apache.http.util.EncodingUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

/**
 * 获取当前Hosts文件数据
 *
 * Created by yifan on 2016/11/1.
 */
public class GetCurrentHostsTask extends BaseAsyncTask<Void, Void, Hosts> {

    @Override
    protected Hosts doInBackground(Void... voids) {
        return HostsUtils.getCurrentHosts();
    }
}
