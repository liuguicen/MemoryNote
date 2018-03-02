package com.lgc.memorynote.user.setting;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.lgc.memorynote.R;
import com.lgc.memorynote.base.CertainDialog;
import com.lgc.memorynote.base.Logcat;
import com.lgc.memorynote.base.network.NetWorkState;
import com.lgc.memorynote.base.network.NetWorkUtil;
import com.lgc.memorynote.data.BmobWord;
import com.lgc.memorynote.data.GlobalData;
import com.lgc.memorynote.data.Word;
import com.lgc.memorynote.wordList.Command;

import java.util.List;

import cn.bmob.v3.exception.BmobException;

/**
 * <pre>
 *      author : liuguicen
 *      time : 2018/02/18
 *      version : 1.0
 * <pre>
 */

public class SettingActivity extends AppCompatActivity implements View.OnClickListener {

    ProgressDialog mProgressDialog;
    CertainDialog certainDialog;
    private int uploadNumber = 0;
    private UpLoadTask mUpLoadTask;
    private GlobalData globalData;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        globalData = GlobalData.getInstance();
        TextView tvAppGuide = ((TextView) findViewById(R.id.tv_app_guide));
        tvAppGuide.setText("搜索框里面可以输入命令，以--开头就表示命令，支持的命令如下：\n" + Command.commandGuide);
        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setTitle(getString(R.string.upload_data));
        mProgressDialog.setProgressStyle(
                ProgressDialog.STYLE_HORIZONTAL);
        mProgressDialog.setCancelable(false);
        mProgressDialog.setButton(DialogInterface.BUTTON_NEUTRAL, "取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                cancelUploadData();
            }
        });

        certainDialog = new CertainDialog(this);
        findViewById(R.id.btn_upload_data).setOnClickListener(this);
        Logcat.e("Setting activitty init success");
    }

    private void cancelUploadData() {
        if (mUpLoadTask != null) {
            mUpLoadTask.cancel(false);
        }
    }

    /**
     * Called when a view has been clicked.
     *
     * @param v The view that was clicked.
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_upload_data:
                if (NetWorkState.detectNetworkType() == -1) {
                    Toast.makeText(this, "找不到网络，请稍后再试", Toast.LENGTH_LONG).show();
                }
                certainDialog.showDialog("确认上传吗？", null, new CertainDialog.ActionListener() {
                    @Override
                    public void onSure() {
                        uploadData();
                    }
                });
                break;
        }
    }

    private void uploadData() {
        mUpLoadTask = new UpLoadTask();
        mUpLoadTask.execute();
    }

    private class UpLoadTask extends AsyncTask<Void, Integer, Integer> {

        @Override
        protected void onPreExecute() {
            mProgressDialog.setMax(globalData.getAllWord().size());
            mProgressDialog.show();
            uploadNumber = 0;
            super.onPreExecute();
        }

        @Override
        protected Integer doInBackground(Void... voids) {
            final int[] failedNumber = {0};
            List<Word> allWord = globalData.getAllWord();
            for (int i = 0; i < allWord.size(); i++) {
                if (isCancelled()) break;

                final Word word = allWord.get(i);

                if (word.getLastModifyTime() < word.getLastUploadTime())
                {
                    publishProgress(++uploadNumber);
                    Logcat.e("upload word " + i + " = "+ word.getName() + " success");
                    continue;
                }

                // 只有修改时间大于上传时间才上传，否则不上传
                Logcat.e("start upload word " + i + " = "+ word.getName());
                final int finalI = i;
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                NetWorkUtil.upLoadWord(word, new NetWorkUtil.UploadListener() {
                    @Override
                    public void uploadSuccess() {
                        word.setLastUploadTime(System.currentTimeMillis());
                        globalData.updateWord(word, false);
                        publishProgress(++uploadNumber);
                        Logcat.e("upload word " + finalI + " = "+ word.getName() + " success");
                    }

                    @Override
                    public void uploadFailed(BmobException e) {
                        failedNumber[0]++;
                        publishProgress(++uploadNumber);
                        Logcat.e("upload word " + finalI + " = " + word.getName() + " fail because  ", e.toString());
                    }
                });
            }
            return failedNumber[0];
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            mProgressDialog.setProgress(values[0]);
        }

        @Override
        protected void onCancelled(Integer integer) {
            super.onCancelled(integer);
            Toast.makeText(SettingActivity.this, "已取消", Toast.LENGTH_LONG).show();
        }

        @Override
        protected void onPostExecute(Integer integer) {
            String msg = "数据上传完成";
            if (integer > 0) {
                msg += "  " + integer +  "  个数据上传失败";
            }

            Toast.makeText(SettingActivity.this, msg, Toast.LENGTH_LONG).show();
            Logcat.d(msg);
            mProgressDialog.dismiss();
        }
    }
}
