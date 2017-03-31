package com.yifan.switchhosts.utils;

import android.os.Environment;

import com.thinksky.utils.utils.ResourcesUtils;
import com.yifan.switchhosts.R;

import java.io.File;

/**
 * 常量类
 *
 * Created by yifan on 2016/10/31.
 */
public class Constants {

    /**
     * 本地根目录路径
     */
    public static final String DIR_PATH_ROOT = new StringBuilder(Environment.getExternalStorageDirectory().getAbsolutePath())
            .append(File.separator).append(ResourcesUtils.getString(R.string.app_name)).toString();

    /**
     * hosts文件存放路径
     */
    public static final String DIR_PATH_HOSTS = new StringBuilder(DIR_PATH_ROOT).append(File.separator).append("hosts").toString();

    /**
     * 默认hosts资源文件存放路径
     */
    public static final String DIR_PATH_DEFAULT = new StringBuilder(DIR_PATH_ROOT).append(File.separator).append("default").toString();

    /**
     * 默认内网测试hosts资源文件名
     */
    public static final String FILE_DEFAULT_TEST_API_HOST = "itools_mobile_api_hosts.txt";

    /**
     * 默认hosts资源文件名
     */
    public static final String FILE_DEFAULT_API_HOST = "default_hosts.txt";

    /**
     * 系统Hosts文件存放位置
     */
    public static final String FILE_PATH_SYSTEM_HOSTS = "/system/etc/hosts";

    /**
     * 键值对key值
     */
    public static final String BUNDLE_KEY_DATA = "data";

    /**
     * 操作 - 清空host
     */
    public static final String ACTION_CLEAR = "clear";
}
