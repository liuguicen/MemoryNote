package com.lgc.memorynote.base;

import android.content.Context;
import android.os.Environment;

import java.io.File;
import java.io.IOException;

/**
 * <pre>
 *      author : liuguicen
 *      time : 2018/05/19
 *      version : 1.0
 * <pre>
 */

public class FileTool {
    /**
     * 处理路径不存在的情况
     *
     * @param file 文件
     * @return 是否创建成功
     */
    public static boolean createNewFile(File file) {
        //处理目录
        File dir = file.getParentFile();
        if (!dir.exists()) {
            if (!dir.mkdirs())
                return false;
        }
        //文件
        if (!file.exists())
            try {
                return file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }
        return true;
    }

    public static String getApplicationDir(Context context) {
        return context.getApplicationContext().getFilesDir().getAbsolutePath();
    }

    /**
     * 获取内置SD卡路径
     * @return
     */
    public static String getInnerSDCardPath() {
        return Environment.getExternalStorageDirectory().getPath();
    }

}
