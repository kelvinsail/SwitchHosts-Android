package com.yifan.switchhosts.utils;

import java.lang.Process;

import java.io.IOException;

/**
 * Root工具
 *
 * Created by yifan on 2016/11/4.
 */
public class RootHelper {

    private static Process mProcess;

    private RootHelper() {
    }

    private static class RootInstance {
        private static RootHelper mInstances = new RootHelper();
    }

    public static RootHelper getInstance() {
        return RootInstance.mInstances;
    }

    /**
     * 获取Root权限
     *
     * @return
     */
    public boolean getRoot() throws IOException {
        final Runtime runtime = Runtime.getRuntime();
        mProcess = runtime.exec("su");
        return null != mProcess;
    }

    /**
     * 获取root进程
     *
     * @return
     */
    public Process getProcess() {
        return mProcess;
    }

    /**
     * 销毁root进程
     */
    public void destoryProcess() {
        if (null != mProcess) {
            mProcess.destroy();
        }
    }
}
