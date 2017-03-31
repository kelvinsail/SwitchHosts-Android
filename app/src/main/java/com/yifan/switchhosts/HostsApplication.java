package com.yifan.switchhosts;

import com.thinksky.utils.base.BaseApplication;
import com.yifan.switchhosts.utils.Constants;
import com.yifan.switchhosts.utils.FileUtils;

import java.io.File;
import java.io.IOException;

/**
 * 自定义Application实例
 *
 * Created by yifan on 2016/10/31.
 */
public class HostsApplication extends BaseApplication {

    /**
     * 单例模式
     */
    private static HostsApplication mInstances;

    /**
     * 获取单例模式
     *
     * @return
     */
    public static HostsApplication getInstances() {
        return mInstances;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mInstances = this;
    }

}
