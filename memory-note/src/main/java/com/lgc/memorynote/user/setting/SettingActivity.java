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
import com.lgc.memorynote.base.Util;
import com.lgc.memorynote.base.network.NetWorkState;
import com.lgc.memorynote.base.network.NetWorkUtil;
import com.lgc.memorynote.data.GlobalData;
import com.lgc.memorynote.data.SpUtil;
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
    CertainDialog mCertainDialog;
    private int mUploadNumber = 0;
    private UpLoadTask mUpLoadTask;
    private GlobalData mGlobalData;
    private TextView mTvUploadState;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        mGlobalData = GlobalData.getInstance();
        TextView tvAppGuide = ((TextView) findViewById(R.id.tv_app_guide));
        mTvUploadState = ((TextView)findViewById(R.id.tv_upload_result));
        String lastUpladMsg = SpUtil.getUploadState();
        if (lastUpladMsg.isEmpty()) {
            lastUpladMsg = "未上传过";
        }
        mTvUploadState.setText(lastUpladMsg);

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

        mCertainDialog = new CertainDialog(this);
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
                    return;
                }
                mCertainDialog.showDialog("确认上传吗？", null, new CertainDialog.ActionListener() {
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
            mProgressDialog.setMax(mGlobalData.getAllWord().size());
            mProgressDialog.show();
            mUploadNumber = 0;
            super.onPreExecute();
        }

        @Override
        protected Integer doInBackground(Void... voids) {
            final int[] failedNumber = {0};
            List<Word> allWord = mGlobalData.getAllWord();
            for (int i = 0; i < allWord.size(); i++) {
                if (isCancelled()) break;

                final Word word = allWord.get(i);

                if (word.getLastModifyTime() < word.getLastUploadTime())
                {
                    publishProgress(++mUploadNumber);
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
                        mGlobalData.updateWord(word, false);
                        publishProgress(++mUploadNumber);
                        Logcat.e("upload word " + finalI + " = "+ word.getName() + " success");
                    }

                    @Override
                    public void uploadFailed(BmobException e) {
                        failedNumber[0]++;
                        publishProgress(++mUploadNumber);
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
            String longTimeMsg = "上次上传时间：" + Util.long2DateDefult(System.currentTimeMillis());
            String msg = "数据上传完成";
            if (integer > 0) {
                String tempS = "  " + integer +  "  个数据上传失败";
                msg += tempS;
                longTimeMsg += "\n" + tempS;
            } else {
                longTimeMsg += "\n全部上传完成";
            }
            boolean res = SpUtil.saveUploadState(longTimeMsg);
            mTvUploadState.setText(longTimeMsg);

            Toast.makeText(SettingActivity.this, msg, Toast.LENGTH_LONG).show();
            Logcat.d(msg);
            mProgressDialog.dismiss();
        }
    }
}
