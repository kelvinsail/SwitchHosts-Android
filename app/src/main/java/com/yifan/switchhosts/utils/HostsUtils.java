package com.yifan.switchhosts.utils;

import android.util.Log;

import com.yifan.switchhosts.model.Hosts;
import com.yifan.switchhosts.task.GetHostsListTask;

import org.apache.http.util.EncodingUtils;

import java.io.BufferedReader;
import java.io.Closeable;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;

/**
 * Hosts工具类
 *
 * Created by yifan on 2016/10/31.
 */
public class HostsUtils {

    private static final String TAG = "HostsUtils";

    /**
     * 获取当前的Hosts内容
     *
     * @return
     */
    public static Hosts getCurrentHosts() {
        try {
            File file = new File(Constants.FILE_PATH_SYSTEM_HOSTS);
            FileInputStream fis = new FileInputStream(file);
            byte[] buffer = new byte[fis.available()];
            fis.read(buffer);
            fis.close();
            String res = EncodingUtils.getString(buffer, "UTF-8");
            Hosts hosts = new Hosts();
            hosts.setName(file.getName());
            hosts.setPath(Constants.FILE_PATH_SYSTEM_HOSTS);
            hosts.setContent(res);
            if (res.length() > GetHostsListTask.SUB_LENGHT) {
                hosts.setPreview(res.substring(0, GetHostsListTask.SUB_LENGHT));
            } else {
                hosts.setPreview(res);
            }
            hosts.setModifiedTime(file.lastModified());
            hosts.setSystemHosts(true);
            return hosts;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 替代Hosts文件,String[]{替换文件路径，原文件路径}，替换文件路径为空时会清空原文件内容，使用之后必须关闭process
     *
     * @param process
     * @param strings
     * @return
     */
    public static boolean replaceHosts(Process process, String... strings) {
        if (null != strings && strings.length > 0) {
            BufferedReader bufferedReader = null;
            try {
                String[] mountLocation = FileUtils.getMountLocation();
                if (null != process) {
                    //获取带有root权限的数据输出流
                    DataOutputStream os = new DataOutputStream(process.getOutputStream());
//                    Log.i(TAG, "replaceHosts: " + "mount -o rw,remount -t " + mountLocation[1] + " " + mountLocation[0] + " /system\n");
                    os.writeBytes("mount -o rw,remount -t " + mountLocation[1] + " " + mountLocation[0] + " /system\n");
//                    os.writeBytes("mount -o remount,rw /dev/block/mtdblock3  /system\n");
                    if (strings[0] != null) {
                        if (strings[0].equals(Constants.ACTION_CLEAR)) {//清空Host
                            os.writeBytes("echo '' > " + strings[1] + "\n");
                        } else {//切换host，写入
                            bufferedReader = new BufferedReader(getReader(strings[0]));
                            String line = null;
                            boolean firstLine = true;
                            while ((line = bufferedReader.readLine()) != null) {
                                if (firstLine) {//如果是第一行，直接写入覆盖
                                    os.writeBytes("echo '" + line + "' > " + strings[1] + "\n");
                                    firstLine = false;
                                    continue;
                                }
                                //接着第一行写入
                                os.writeBytes("echo '" + line + "' >> " + strings[1] + "\n");
                            }
                        }
                    } else {//还原
                        os.writeBytes("echo '127.0.0.1 localhost' > " + strings[1] + "\n");
                    }
                    os.writeBytes("chmod 666 /system/etc/hosts\n");
                    os.writeBytes("exit\n");
                    os.flush();
                    os.close();
                    process.waitFor();
                    int exitValue = process.exitValue();
                    Log.d(TAG, "Exit Value For File Copier: " + exitValue);
                    if (exitValue != 255 && exitValue != 126)
                        return Boolean.TRUE;
                }
            } catch (InterruptedException ex) {
                Log.e(TAG, ex.getMessage(), ex);
            } catch (IOException ex) {
                Log.e(TAG, ex.getMessage(), ex);
            } catch (UnableToMountSystemException ex) {
                Log.e(TAG, ex.getMessage(), ex);
            } finally {
                closeStream(bufferedReader);
            }
        }
        return Boolean.FALSE;
    }

    /**
     * 读取本地Hosts文件
     *
     * @param input
     * @return
     * @throws FileNotFoundException
     */
    private static Reader getReader(Object input) throws FileNotFoundException {
        if (input instanceof InputStream)
            return new InputStreamReader((InputStream) input);
        if (input instanceof String)
            return new FileReader((String) input);
        else throw new FileNotFoundException("Unknown file type. " + input);
    }

    /**
     * 关闭流
     *
     * @param closeables
     */
    public static void closeStream(Closeable... closeables) {
        try {
            for (Closeable closeable : closeables) {
                if (closeable != null) {
                    closeable.close();
                }
            }
        } catch (IOException ex) {
            Log.e(TAG, "Error closing stream. ", ex);
        }
    }
}
