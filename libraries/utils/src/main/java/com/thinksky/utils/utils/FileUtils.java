package com.thinksky.utils.utils;

import android.os.Environment;


import com.thinksky.utils.R;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * FileUtils
 *
 * Created by yifan on 2016/8/3..
 */
public class FileUtils {

    /**
     * 将字符串写入文件
     *
     * @param dirName
     * @param fileName
     * @param content
     */
    public static boolean printDataToFile(String dirName, String fileName, String content) {
        String path = new StringBuilder(Environment.getExternalStorageDirectory().getAbsolutePath())
                .append("/").append(ResourcesUtils.getString(R.string.root_dir_name))
                .append("/").append(dirName).toString();
        File dir = new File(path);
        dir.mkdirs();
        String name = new StringBuilder(fileName).append("_").append(System.currentTimeMillis()).append(".txt").toString();
        return printDataToFile(new StringBuilder(path).append("/").append(name).toString(), content);
    }

    /**
     * 将内容写进路径所属的文件中
     *
     * @param path    文件路径
     * @param content 内容
     * @return
     */
    public static boolean printDataToFile(String path, String content) {
        File file = new File(path);
        boolean isFileExisted;
        if (file.exists()) {
            isFileExisted = true;
        }else{
            try {
                isFileExisted = file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }
        }
        if (isFileExisted) {
            byte[] b = content.getBytes();
            BufferedOutputStream stream = null;
            try {
                FileOutputStream fstream = new FileOutputStream(file);
                stream = new BufferedOutputStream(fstream);
                stream.write(b);
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            } finally {
                if (stream != null) {
                    try {
                        stream.close();
                    } catch (IOException e1) {
                        e1.printStackTrace();
                    }
                }
            }
        }
        return true;
    }

}
