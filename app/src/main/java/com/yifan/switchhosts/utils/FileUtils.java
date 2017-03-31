package com.yifan.switchhosts.utils;

import android.util.Log;

import com.yifan.switchhosts.HostsApplication;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;

/**
 * 文件工具
 *
 * Created by yifan on 2016/10/31.
 */
public class FileUtils {

    private static final String TAG = "FileUtils";

    /**
     * 获取文本文件内容
     *
     * @return
     */
    public static String getFileContent(File file) {
        if (null == file) {
            return null;
        }
        InputStreamReader inputStreamReader = null;
        try {
            InputStream inputStream = new FileInputStream(file);
            inputStreamReader = new InputStreamReader(inputStream, "gbk");
        } catch (UnsupportedEncodingException e1) {
            e1.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        BufferedReader reader = new BufferedReader(inputStreamReader);
        StringBuffer sb = new StringBuffer("");
        String line;
        try {
            while ((line = reader.readLine()) != null) {
                sb.append(line);
                sb.append("\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return sb.toString();
    }

    /**
     * 获取系统挂载的/system分区路径
     *
     * @return
     * @throws UnableToMountSystemException
     */
    public static String[] getMountLocation()
            throws UnableToMountSystemException {
        String[] mountStructure = new String[2];
        FileReader fReader = null;
        BufferedReader br = null;
        try {
            fReader = new FileReader("/proc/mounts");
            br = new BufferedReader(fReader);
            String line = null;

            while ((line = br.readLine()) != null) {
                if (line.contains("/system")) {

                    String[] mountLocationArray = line.split(" ");
                    if (mountLocationArray.length >= 3) {
                        mountStructure[0] = mountLocationArray[0];
                        mountStructure[1] = mountLocationArray[2];
                    }
                    return mountStructure;
                }
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            if (fReader != null) {
                try {
                    fReader.close();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }

            }

            if (br != null) {
                try {
                    br.close();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }

        }
        throw new UnableToMountSystemException(
                "Unable to mount /system folder as writable.");
    }


    /**
     * 删除文件
     *
     * @param path
     * @return
     */
    public static boolean deleteFile(String path) {
        if (null != path) {
            File file = new File(path);
            if (file.exists()) {
                return file.delete();
            }
        }
        return false;
    }

    /**
     * 复制assets资源文件到本地路径
     *
     * @param fileName asset文件名
     * @param toPath   复制目标路径（带文件名）
     * @throws IOException
     */
    public static void copyAssetFileToStroge(String fileName, String toPath) {
        InputStream myInput = null;
        OutputStream myOutput = null;
        try {
            File file = new File(toPath);
            boolean isSuccessful = false;
            if (!file.exists()) {
                isSuccessful = file.createNewFile();
            }
            //文件创建失败，返回
            if (!isSuccessful) {
                return;
            }
            //打开输出流
            myOutput = new FileOutputStream(file);
            //打开Asset文件输入流
            myInput = HostsApplication.getInstance().getAssets().open(fileName);
            if (null != myInput) {
                byte[] buffer = new byte[1024];
                int length = myInput.read(buffer);
                while (length > 0) {
                    myOutput.write(buffer, 0, length);
                    length = myInput.read(buffer);
                }
                myOutput.flush();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            HostsUtils.closeStream(myInput, myOutput);
        }
    }

}
