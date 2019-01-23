package com.lgc.wordanalysis.base.log;

import android.util.Log;
import android.widget.Toast;

import com.lgc.baselibrary.utils.Logcat;
import com.lgc.baselibrary.baseComponent.BaseApplication;
import com.lgc.baselibrary.utils.FileUtil;
import com.lgc.wordanalysis.base.MemoryNoteApplication;
import com.lgc.wordanalysis.user.UserExclusiveIdentify;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;

import cn.bmob.v3.BmobObject;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.SaveListener;

/**
 * Created by LiuGuicen on 2017/1/6 0006.
 */
public class CrashLog extends BmobObject {
    static final String TAG = "CrashLog";
    private static final String PATH = FileUtil.getApplicationDir(MemoryNoteApplication.appContext) + "/crashLog/";
    private static final String NAME = "crash";
    private static final String SUFFIX = ".trace";
    private static final String FILE_PATH = PATH + NAME + SUFFIX;

    private Integer crash_version;
    private String crashMessage;
    private String deviceIdentify;

    public String getCrashMessage() {
        return crashMessage;
    }

    public String getDeviceIdentify() {
        return deviceIdentify;
    }

    public void setCrashMessage(String crashMessage) {
        this.crashMessage = crashMessage;
    }

    public void setDeviceIdentify(String deviceIdentify) {
        this.deviceIdentify = deviceIdentify;
    }

    public Integer getCrash_version() {
        return crash_version;
    }

    public void setCrash_version(Integer crash_version) {
        this.crash_version = crash_version;
    }


    public CrashLog() {
        crash_version = 1;//crash版本高于版本号
    }

    /**
     * 注意，还有发生多次crash而没有提交的情况，没做处理，有需要的话这里的处理的
     *
     * @param thread
     * @param ex
     */
    public void commit(Thread thread, Throwable ex) {
        //获取异常常栈信息到字符串中
        if (ex == null) return;
        StringBuilder sb = new StringBuilder();
        sb.append("线程名称:")
                .append(thread.getName())
                .append("\n");
        sb.append("异常信息：")
                .append("类:")
                .append(ex.getClass().getSimpleName())
                .append("\n")
                .append(ex.getMessage())
                .append("\n");
        for (StackTraceElement stackTraceElement : ex.getStackTrace()) {
            sb.append(stackTraceElement.toString());
            sb.append("\n");
        }

        File crashFile = new File(FILE_PATH);
        PrintWriter crashPw = null;
        String commitResult = "";
        try {
            if (!crashFile.exists())
                if (!FileUtil.createNewFile(crashFile))
                    throw new IOException();
            crashPw = new PrintWriter(new FileOutputStream(crashFile));
            crashPw.write(sb.toString());
            commitResult += "commit: 将Crash提交到本地成功";
        } catch (Exception e) {
            commitResult +=  "  commit: 将Crash提交到本地失败";
            e.printStackTrace();
        } finally {
            if (crashPw != null)
                crashPw.close();
            commitResult += "  commit: crash内容" + sb.toString();
        }
        Logcat.e(commitResult);
        Toast.makeText(BaseApplication.appContext, commitResult, Toast.LENGTH_LONG).show();
    }

    public static boolean hasNew() {
        //不常用的sharedPreference
        return new File(FILE_PATH).exists();
    }

    public void serviceCreate() {
        //获取上传crash信息，如果失败，就不上传了
        if (!createMessage()) return;
        save(new SaveListener<String>() {
            @Override
            public void done(String objectId, BmobException e) {
                if (e == null) {
                    Log.e("Crash信息服务器保存成功：", objectId);
                    //服务器添加成功,删除本地的crash文件
                    new File(FILE_PATH).delete();
                } else {
                    Log.e("bmob", "crash信息服务器保存失败：" + e.getMessage() + "," + e.getErrorCode());
                }
            }
        });

    }

    private boolean createMessage() {
        deviceIdentify = UserExclusiveIdentify.getExclusiveIndentify();
        BufferedReader br = null;
        StringBuilder sb = new StringBuilder();
        try {
            //从文件读取数据要注意，用readLine读出来是会损失换行的，所以要重新加上去
            br = new BufferedReader(new FileReader(FILE_PATH));
            String line;
            while ((line = br.readLine()) != null) {
                sb.append(line);
                sb.append("\n");
            }
            crashMessage = sb.toString();
            return true;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (br != null)
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
        }
        return false;
    }
}
