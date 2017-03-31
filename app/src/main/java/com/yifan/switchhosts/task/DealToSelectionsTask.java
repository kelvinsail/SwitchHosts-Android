package com.yifan.switchhosts.task;

import android.text.TextUtils;
import android.util.Log;

import com.thinksky.utils.base.BaseAsyncTask;
import com.yifan.switchhosts.model.SelectionHost;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

/**
 * 拆分Hosts为可选数据数组 异步任务
 *
 * Created by yifan on 2016/11/25.
 */

public class DealToSelectionsTask extends BaseAsyncTask<String, Void, List<SelectionHost>> {

    public static final String TAG = "DealToSelectionsTask";

    @Override
    protected List<SelectionHost> doInBackground(String... strings) {
        List<SelectionHost> list = new ArrayList<>();
        if (null != strings && strings.length > 0 && !TextUtils.isEmpty(strings[0])) {
            //将整个hosts拆分成条目
            String[] hosts = strings[0].split("\n");
            //遍历每一个条目
            if (null != hosts && hosts.length > 0) {
                for (String string : hosts) {
                    //将条目以空格拆分
                    //格式：
                    // 空的
                    //# description
                    //# to from.....
                    //# ::1  from...
                    //#description
                    //#to from.....
                    //#to\tfrom.....
                    //#::1  from...
                    //to from...
                    //::1  from...
                    SelectionHost host = new SelectionHost();
                    if (TextUtils.isEmpty(string)) {
                        host.setDescription(true);
                        host.setHostTo("empty");
                        list.add(host);
                        continue;
                    }
                    String temp = deleteStartSpace(string);
                    //去除头部的“#”
                    if (temp.startsWith("#")) {
                        temp = deleteStartSpace(temp.substring(1, temp.length()));
                        host.setChecked(false);
                        host.setChecked(false);
                    } else {
                        host.setChecked(true);
                    }
                    //再以空格拆分
                    String[] content;
                    if (temp.indexOf("\t") >= 0) {
                        content = temp.split("\t");
                    } else {
                        content = temp.split(" ");
                    }
                    //是否已经判断过是不是host
                    boolean isJudge = false;
                    StringBuilder domain = new StringBuilder();
                    for (String str : content) {
                        if (!TextUtils.isEmpty(str)) {//取出第一段不为空的判断是否为重定向ip地址\
                            //根据地址格式判断
                            if (!isJudge) {
                                if (str.startsWith("::") || str.split("\\.").length == 4) {//是hosts条目
                                    host.setDescription(false);
                                    host.setHostTo(deleteStartSpace(str));
                                } else {//是说明
                                    host.setDescription(true);
                                    domain.append(str);
                                }
                                isJudge = true;
                            } else {//hosts条目剩余内容
                                domain.append(deleteStartSpace(str));
                            }
                        } else {
                            if (domain.length() > 0) {
                                domain.append(" ");
                            }
                        }
                    }
                    host.setHostFrom(domain.toString());
                    list.add(host);
                }
            }
        }
        return list;
    }

    private String deleteStartSpace(String string) {
        String temp = string;
        for (; ; ) {
            if (temp.startsWith(" ") || temp.startsWith("　")) {
                temp = temp.substring(1, temp.length());
            } else if (temp.startsWith("\t")) {
                temp = temp.substring(2, temp.length());
            } else {
                break;
            }
        }
        return temp;
    }
}
