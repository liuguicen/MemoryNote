package com.lgc.memorynote.base.network;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Loader;
import android.graphics.Typeface;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.lgc.memorynote.base.MemoryNoteApplication;
import com.lgc.memorynote.data.Word;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.datatype.BmobQueryResult;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.SQLQueryListener;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UpdateListener;
import rx.Observable;
import rx.Subscriber;
import rx.functions.Func1;

/**
 * <pre>
 *      author : liuguicen
 *      time : 2018/02/18
 *      version : 1.0
 * <pre>
 */

public class WordUtil {
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
                        Toast.makeText(MemoryNoteApplication.appContext,"网络出错，不能获取贴图", Toast.LENGTH_SHORT).show();
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

    private static void refreshWordDetail(){}


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

    public static void saveWordService(Word word) {
        word.save(new SaveListener<String>() {
            @Override
            public void done(String objectId, BmobException e) {
                if (e == null) {
                    Log.e("服务器保存成功：", objectId);
                } else {
                    Log.e("bmob", "服务器保存失败：" + e.getMessage() + "," + e.getErrorCode());
                }
            }
        });
    }

    public void upLoadWord(final Word localWord) {
        BmobQuery<Word> query = new BmobQuery<>();
        query.addWhereEqualTo("name", localWord.getName());
        //先查询，查到了更新
        query.findObjects(
                new FindListener<Word>() {
                    @Override
                    public void done(List<Word> list, BmobException e) {
                        if (e == null) {
                            Word serviceWord = null;
                            if (list != null && list.size() != 0)
                                serviceWord = list.get(0);
                            else { // 没有找到，直接上传
                                saveWordService(localWord);
                                return;
                            }

                            //更新使用天数
                            if (serviceWord.getLastUploadTime() > localWord.getLastUploadTime()) {
                                 // todo 发现服务器对象的上传时间本地记录的上传时间晚，说明可能在其它设备上上传了，
                                // 不要上传
                            }

                            String objectId = serviceWord.getObjectId();
                            localWord.update(objectId,
                                    new UpdateListener() {

                                        @Override
                                        public void done(BmobException ee) {
                                            if (ee == null) {
                                                Log.e("bmob", "更新成功");
                                            } else {
                                                Log.e("bmob", "更新失败：" + ee.getMessage() + "," + ee.getErrorCode());
                                            }
                                        }
                                    });
                        } else {
                            Log.e("bmob", "查询失败：" + e.getMessage() + "," + e.getErrorCode());
                        }
                    }
                });

    }
}
