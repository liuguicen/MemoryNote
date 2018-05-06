package com.lgc.memorynote.base.network;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.util.Log;
import android.widget.Toast;

import com.lgc.memorynote.base.Logcat;
import com.lgc.memorynote.base.MemoryNoteApplication;
import com.lgc.memorynote.base.Util;
import com.lgc.memorynote.data.BmobWord;
import com.lgc.memorynote.data.Word;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.datatype.BmobQueryResult;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.SQLQueryListener;
import rx.Observable;
import rx.Subscriber;

/**
 * <pre>
 *      author : liuguicen
 *      time : 2018/02/18
 *      version : 1.0
 * <pre>
 */

public class NetWorkUtil {
    public static final long NET_INTERVAL_TIME = 200;

    /**
     * update all word form service
     */
    private void updateAllWord(final Activity activity, List<Word> wordsList) {

    }

    private void realDownloadAllWord() {

    }

    public static void refreshWordSketch() {

    }

    private static ArrayList<Word> getWordNeedRefresh(ArrayList<Word> words) {
        return null;
    }

    private static void refreshWordDetail() {
    }


    private static void queryWordModifyInfo(final Subscriber<? super List<Word>> subscriber) {

    }

    public static void saveWordService(BmobWord bmobWord, final UploadListener uploadListener) {

    }

    public static void upLoadWord(final Word localWord, final UploadListener uploadListener) {

    }

    public static void deleteWord(final Word localWord, final UploadListener uploadListener) {

    }

    public interface UploadListener {
    void uploadSuccess();

    void uploadFailed(BmobException e);
}
}
