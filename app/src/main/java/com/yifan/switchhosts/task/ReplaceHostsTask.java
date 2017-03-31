package com.yifan.switchhosts.task;


import com.thinksky.utils.base.BaseAsyncTask;
import com.yifan.switchhosts.model.RootActionResutlt;
import com.yifan.switchhosts.utils.Constants;
import com.yifan.switchhosts.utils.HostsUtils;
import com.yifan.switchhosts.utils.RootHelper;

import java.io.IOException;

/**
 * 替代Hosts文件 异步任务
 *
 * Created by yifan on 2016/10/31.
 */
public class ReplaceHostsTask extends BaseAsyncTask<String, Void, RootActionResutlt> {

    @Override
    protected RootActionResutlt doInBackground(String... strings) {
        RootActionResutlt resutlt = new RootActionResutlt();
        if (null != strings && strings.length == 2) {
            try {
                resutlt.isRootSucess = RootHelper.getInstance().getRoot();
                if (resutlt.isRootSucess) {//每次获取root之后，只能执行一次，但一次可以包含多条指令
                    resutlt.isActionDone = HostsUtils.replaceHosts(RootHelper.getInstance().getProcess(), strings[0], strings[1]);
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                RootHelper.getInstance().destoryProcess();
            }
        }
        return resutlt;
    }
}
