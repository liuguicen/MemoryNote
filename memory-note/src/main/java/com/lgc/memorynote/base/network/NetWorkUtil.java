package com.lgc.memorynote.base.network;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.util.Log;
import android.widget.Toast;

import com.lgc.memorynote.base.MemoryNoteApplication;
import com.lgc.memorynote.data.BmobWord;
import com.lgc.memorynote.data.Word;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.datatype.BmobQueryResult;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.SQLQueryListener;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UpdateListener;
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
    /**
     * update all word form service
     */
    private void updateAllWord(final Activity activity, List<Word> wordsList) {
        int state = NetWorkState.detectNetworkType();
        String notice = "注意服务器与本地出现冲突的单词，本地数据将丢失，可以先上传再更新";
        String msg;
        if (state == -1) { // 没有网络
            Toast.makeText(activity, "网络未连接，请稍后再试！", Toast.LENGTH_LONG).show();
            return;
        }
        if (state == 1) //是wifi
        {
            msg = "已连接到WiFi，确认同步吗？" + notice;
        } else {
            msg = "WiFi未连接，会产生流量消耗，确定同步吗?" + notice;
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setTitle("字体下载")
                .setMessage(msg)
                .setNegativeButton("下载", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        realDownloadAllWord();
                        dialog.dismiss();
                    }
                })
                .setPositiveButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .create()
                .show();
    }

    private void realDownloadAllWord() {
        refreshWordSketch();
    }

    public static void refreshWordSketch() {
        Observable
                .create(new Observable.OnSubscribe<List<Word>>() {

                    @Override
                    public void call(Subscriber<? super List<Word>> subscriber) {
                        queryWordModifyInfo(subscriber);
                    }
                })
                .subscribe(new Subscriber<List<Word>>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable throwable) {
                        Toast.makeText(MemoryNoteApplication.appContext, "网络出错，不能获取贴图", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onNext(List<Word> tietuMaterials) {
                        Log.e("TAG", "onNext: 获取到的单词数量" + tietuMaterials.size());

                    }
                });
    }

    private static ArrayList<Word> getWordNeedRefresh(ArrayList<Word> words) {
        return null;
    }

    private static void refreshWordDetail() {
    }


    private static void queryWordModifyInfo(final Subscriber<? super List<Word>> subscriber) {
        Log.e("---------", "queryAllExpressions: ");
        BmobQuery<Word> query = new BmobQuery<>();
        String sql = "select name, last uploadtime " +
                " from word" +
                " order by name";
        query.setSQL(sql);
        query.doSQLQuery(
                new SQLQueryListener<Word>() {
                    @Override
                    public void done(BmobQueryResult<Word> result, BmobException e) {
                        if (e != null || result == null) {
                            subscriber.onError(e);
                        }
                        List<Word> resultList = result.getResults();
                        if (resultList == null)
                            resultList = new ArrayList<>();
                        subscriber.onNext(resultList);
                    }
                });
    }

    public static void saveWordService(BmobWord bmobWord, final UploadListener uploadListener) {
        bmobWord.selfSave(uploadListener);
    }

    public static void upLoadWord(final Word localWord, final UploadListener uploadListener) {
        final BmobWord localBmobWord = new BmobWord(localWord);
        BmobQuery<BmobWord> query = new BmobQuery<>();
        query.addWhereEqualTo("name", localBmobWord.getName());

        //先查询，查到了更新
        query.findObjects(
                new FindListener<BmobWord>() {
                    @Override
                    public void done(List<BmobWord> list, BmobException e) {
                        if (e != null) {
                            Log.e("bmob", "查询失败：" + e.getMessage() + "," + e.getErrorCode());
                            saveWordService(localBmobWord, uploadListener);
                            if (uploadListener != null)
                                uploadListener.uploadFailed(e);
                            return;
                        }

                        BmobWord serviceBmob = null;
                        Word serviceWord;
                        if (list == null || list.size() == 0) { // 没有找到，直接上传
                            saveWordService(localBmobWord, uploadListener);
                            return;
                        }

                        serviceBmob = list.get(0);
                        serviceWord = serviceBmob.toWord();

                        if (serviceWord.getLastUploadTime() > localWord.getLastUploadTime()) {
                            // todo 发现服务器对象的上传时间本地记录的上传时间晚，说明可能在其它设备上上传了，
                            // 不要上传
                        }

                        // 上传时间在修改时间之后，不用上传
                        if (serviceWord.getLastUploadTime() >= localWord.getLastModifyTime()) {
                            return;
                        }

                        String objectId = serviceBmob.getObjectId();
                        localBmobWord.selfUpdate(objectId, uploadListener);
                    }
                });

    }

    public static void deleteWord(final Word localWord, final UploadListener uploadListener) {
        final BmobWord localBmobWord = new BmobWord(localWord);
        BmobQuery<BmobWord> query = new BmobQuery<>();
        query.addWhereEqualTo("name", localBmobWord.getName());

        //先查询，查到了更新
        query.findObjects(
                new FindListener<BmobWord>() {
                    @Override
                    public void done(List<BmobWord> list, BmobException e) {
                        if (e != null) {
                            Log.e("bmob", "查询失败：" + e.getMessage() + "," + e.getErrorCode());
                            if (uploadListener != null)
                                uploadListener.uploadFailed(e);
                            return;
                        }


                        if (list == null || list.size() == 0) { // 没有找到，直接上传
                            return;
                        }
                        String objectId = list.get(0).getObjectId();
                        localBmobWord.setObjectId(objectId);
                        localBmobWord.selfDelete(uploadListener);
                    }
                });
    }

    public interface UploadListener {
    void uploadSuccess();

    void uploadFailed(BmobException e);
}
}
