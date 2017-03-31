package com.yifan.switchhosts.task;

import android.util.Log;

import com.thinksky.utils.base.BaseAsyncTask;
import com.thinksky.utils.utils.FileUtils;
import com.yifan.switchhosts.model.SelectionHost;

import java.util.List;

/**
 * 保存选择修改后的hosts文件数据
 *
 * Created by yifan on 2016/11/29.
 */
public class SaveSelectionTask extends BaseAsyncTask<Object, Void, Boolean> {

    private static final String TAG = "SaveSelectionTask";

    @Override
    protected Boolean doInBackground(Object... objects) {
        StringBuilder content = new StringBuilder();
        if (null != objects && null != objects[0] && null != objects[1]
                && objects[0] instanceof List && objects[1] instanceof String) {
            for (SelectionHost selectionHost : ((List<SelectionHost>) objects[0])) {
                if (selectionHost.isDescription()) {
                    if (null != selectionHost.getHostFrom()) {
                        content.append("# ").append(selectionHost.getHostFrom());
                    }
                    content.append("\n");
                } else {
                    if (!selectionHost.isChecked()) {//未被选中的添加注释符号
                        content.append("# ");
                    }
                    content.append(selectionHost.getHostTo());
                    int i = 15 - (null != selectionHost.getHostTo() ? selectionHost.getHostTo().length() : 0);
                    while (i > 0) {
                        content.append(" ");
                        i--;
                    }
                    content.append("\t").append(selectionHost.getHostFrom()).append("\n");
                }
            }
            return FileUtils.printDataToFile((String) objects[1], content.toString());
        }
        return false;
    }

    /**
     * @param params 1、实现了继承{@link SelectionHost} 的{@link List} 类型；2、{@link String} 保存路径
     */
    @Override
    public void asyncExecute(Object... params) {
        super.asyncExecute(params);
    }
}
