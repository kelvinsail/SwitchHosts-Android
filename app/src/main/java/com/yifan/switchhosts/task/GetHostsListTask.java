package com.yifan.switchhosts.task;

import com.thinksky.utils.base.BaseAsyncTask;
import com.yifan.switchhosts.model.Hosts;
import com.yifan.switchhosts.utils.Constants;
import com.yifan.switchhosts.utils.FileUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * 获取Hosts文件异步任务
 *
 * Created by yifan on 2016/10/31.
 */
public class GetHostsListTask extends BaseAsyncTask<Void, Void, List<Hosts>> {

    public static final int SUB_LENGHT = 1024 ;

    @Override
    protected List doInBackground(Void... voids) {
        List<Hosts> list = new ArrayList<>();
        //遍历app默认自带并复制到sd卡的host
        File defaultDir = new File(Constants.DIR_PATH_DEFAULT);
        if (defaultDir.exists()) {
            String[] files = defaultDir.list();
            if (null==files){
                return list;
            }
            for (String fileName : files) {
                String path = new StringBuilder(Constants.DIR_PATH_DEFAULT)
                        .append(File.separator).append(fileName).toString();
                File hostsFile = new File(path);
                Hosts hosts = new Hosts();
                hosts.setName(fileName);
                hosts.setPath(path);
                hosts.setModifiedTime(hostsFile.lastModified());
                hosts.setContent(FileUtils.getFileContent(hostsFile));
                if (hosts.getContent().length() > SUB_LENGHT) {
                    hosts.setPreview(hosts.getContent().substring(0, SUB_LENGHT));
                }else{
                    hosts.setPreview(hosts.getContent());
                }
                list.add(hosts);
            }
        }

        //遍历用户保存的host文件夹子文件
        File hostsDir = new File(Constants.DIR_PATH_HOSTS);
        if (hostsDir.exists()) {
            String[] files = hostsDir.list();
            for (String fileName : files) {
                String path = new StringBuilder(Constants.DIR_PATH_HOSTS)
                        .append(File.separator).append(fileName).toString();
                File hostsFile = new File(path);
                Hosts hosts = new Hosts();
                hosts.setName(fileName);
                hosts.setPath(path);
                hosts.setModifiedTime(hostsFile.lastModified());
                hosts.setContent(FileUtils.getFileContent(hostsFile));
                if (hosts.getContent().length() > SUB_LENGHT) {
                    hosts.setPreview(hosts.getContent().substring(0, SUB_LENGHT));
                }else{
                    hosts.setPreview(hosts.getContent());
                }
                list.add(hosts);
            }
        }
        return list;
    }
}
