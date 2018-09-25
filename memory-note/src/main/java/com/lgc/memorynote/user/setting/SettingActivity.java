package com.lgc.memorynote.user.setting;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.lgc.memorynote.R;
import com.lgc.memorynote.base.AppConfig;
import com.lgc.memorynote.base.CertainDialog;
import com.lgc.memorynote.base.Logcat;
import com.lgc.memorynote.base.Util;
import com.lgc.memorynote.base.network.NetWorkState;
import com.lgc.memorynote.base.network.NetWorkUtil;
import com.lgc.memorynote.data.BmobWord;
import com.lgc.memorynote.data.DataSync;
import com.lgc.memorynote.data.GlobalData;
import com.lgc.memorynote.data.SpUtil;
import com.lgc.memorynote.data.Word;
import com.lgc.memorynote.user.User;
import com.lgc.memorynote.wordList.Command;

import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.CountListener;
import cn.bmob.v3.listener.FindListener;

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
    private TextView tvDownloadState;
    private EditText mEtUserName;
    private EditText mEtPassword;
    private User mUser;
    private int mLastInputType;
    private boolean mIsInEdit;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        mGlobalData = GlobalData.getInstance();
        mUser = mGlobalData.getUser();

        initUser();

        TextView tvAppGuide = ((TextView) findViewById(R.id.tv_app_guide));
        mTvUploadState = ((TextView) findViewById(R.id.tv_upload_result));
        tvDownloadState = ((TextView) findViewById(R.id.tv_download_result));
        tvDownloadState.setText("上次下载到： " + SpUtil.getLastDownloadPosition());
        findViewById(R.id.setting_modify_name_password).setOnClickListener(this);
        findViewById(R.id.setting_verify_modify).setOnClickListener(this);
        findViewById(R.id.btn_download_continue).setOnClickListener(this);
        findViewById(R.id.btn_download_all).setOnClickListener(this);
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
        findViewById(R.id.btn_export_to_sd).setOnClickListener(this);
        findViewById(R.id.btn_import_to_sd).setOnClickListener(this);
        Logcat.e("Setting activitty init success");
//        test();
    }

    void test() {
    }

    private void initUser() {
        mEtUserName = ((EditText) findViewById(R.id.setting_user_name_input));
        mEtPassword = ((EditText) findViewById(R.id.setting_password_input));
        mLastInputType = mEtUserName.getInputType();
        mEtUserName.setText(mUser.getName());
        mEtPassword.setText(mUser.getPassword());
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
            case R.id.btn_download_continue:
                if (NetWorkState.detectNetworkType() == -1) {
                    Toast.makeText(this, "找不到网络，请稍后再试", Toast.LENGTH_LONG).show();
                    return;
                }
                mCertainDialog.showDialog("确认下载吗？", null, new CertainDialog.ActionListener() {
                    @Override
                    public void onSure() {
                        downloadDate();
                    }
                });
                break;
            case R.id.btn_download_all:
                if (NetWorkState.detectNetworkType() == -1) {
                    Toast.makeText(this, "找不到网络，请稍后再试", Toast.LENGTH_LONG).show();
                    return;
                }
                mCertainDialog.showDialog("确认下载吗？", null, new CertainDialog.ActionListener() {
                    @Override
                    public void onSure() {
                        downloadAll();
                    }
                });
                break;
            case R.id.setting_modify_name_password:
                if (!mIsInEdit) {
                    mIsInEdit = true;
                    switchTvEditStyle(mEtUserName, mIsInEdit);
                    switchTvEditStyle(mEtPassword, mIsInEdit);
                }
                break;
            case R.id.setting_verify_modify:
                if (mIsInEdit) {
                    mIsInEdit = false;
                    String userName = mEtUserName.getText().toString();
                    String password = mEtPassword.getText().toString();
                    switchTvEditStyle(mEtUserName, mIsInEdit);
                    switchTvEditStyle(mEtPassword, mIsInEdit);
                    if (User.checkName(userName) != User.VALID || User.checkPassword(password) != User.VALID) {

                        return;
                    }
                    mUser.setName(userName);
                    mUser.setPassword(password);
                    boolean res = mGlobalData.saveUserDate();
                    if (res) {
                        Util.T(this, "保存成功");
                    } else {
                        Util.T(this, "保存失败");
                    }
                }
                break;
            case R.id.btn_export_to_sd:
                mCertainDialog.showDialog("确认导出吗？", null, new CertainDialog.ActionListener() {
                    @Override
                    public void onSure() {
                        DataSync.exportData2Sd(SettingActivity.this, AppConfig.exportDataName);
                    }
                });
                break;
            case R.id.btn_import_to_sd:
                mCertainDialog.showDialog("导入将替换现有数据，现有数据将保存到SD卡中，确认导入吗？", null, new CertainDialog.ActionListener() {
                    @Override
                    public void onSure() {
                        // 必须先备份
                        DataSync.exportData2Sd(SettingActivity.this, AppConfig.exportDataName);
                        Toast.makeText(SettingActivity.this, "数据已导出到SD卡备份", Toast.LENGTH_LONG).show();

                        new CertainDialog(SettingActivity.this).showDialog("再次确认，是否导入？", null, new CertainDialog.ActionListener() {
                            @Override
                            public void onSure() {
                                DataSync.importFromSD(SettingActivity.this,
                                         "0worddata.txt");
                            }
                        });
                    }
                });

        }
    }


    private void switchTvEditStyle(TextView tv, boolean isInEdit) {
        if (isInEdit) {
            tv.setInputType(mLastInputType);
        } else {
            tv.setInputType(InputType.TYPE_NULL);
            tv.setSingleLine(false);
        }
        if (isInEdit) {
            tv.setBackground(((Drawable) tv.getTag()));
        } else {
            tv.setBackground(null);
            tv.setHint("");
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

                if (word.getLastModifyTime() < word.getLastUploadTime()) {
                    publishProgress(++mUploadNumber);
                    Logcat.d("word " + i + " = " + word.getName() + " had upload already");
                    continue;
                }


                // 只有修改时间大于上传时间才上传，否则不上传
                Logcat.e("start upload word " + i + " = " + word.getName());
                final int finalI = i;
                try {
                    Thread.sleep(NetWorkUtil.NET_INTERVAL_TIME);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                NetWorkUtil.upLoadWord(word, null, new NetWorkUtil.UploadListener() {
                    @Override
                    public void uploadSuccess() {
                        word.setLastUploadTime(System.currentTimeMillis());
                        mGlobalData.updateWord2DB(word, false);
                        publishProgress(++mUploadNumber);
                        Logcat.e("upload word " + finalI + " = " + word.getName() + " success");
                    }

                    @Override
                    public void
                    uploadFailed(BmobException e) {
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
            if (integer == null) {
                integer = 0;
            }
            showResult(integer);
            Toast.makeText(SettingActivity.this, "已取消", Toast.LENGTH_LONG).show();
        }

        @Override
        protected void onPostExecute(Integer integer) {
            showResult(integer);
        }

        private void showResult(Integer integer) {
            String longTimeMsg = "上次上传时间：" + Util.long2DateDefult(System.currentTimeMillis());
            String msg = "数据上传完成";
            if (integer > 0) {
                String tempS = "  " + integer + "  个数据上传失败";
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


    private void downloadAll() {
        SpUtil.saveLastDownloadPosition(0);
        downloadDate();
    }

    private void downloadDate() {
        // 首先获取单词数量
        // 按单词名称排序，然后根据单词数量分页查询
        // 每个单词下载之后，转回Java对象，
        // 比较更新时间，如果>=更新时间晚，丢掉，重要!
        // 如果是需要更新的单词，全局数据中， 数据库中的都更新掉

        BmobQuery<BmobWord> query = new BmobQuery<>();
        String sql = "select * " +
                " from BmobWord";
        query.setSQL(sql);
        query.count(BmobWord.class, new CountListener() {
            @Override
            public void done(Integer wordCount, BmobException e) {
                Downloader mDownLoader = new Downloader(wordCount);
                mDownLoader.execute();
            }
        });
    }

    private class Downloader {
        private int totalNumber = 0;
        private final int DownLoadLimit = 500;
        private int downLoadNumber;
        int lastDownloadPosition;


        Downloader(int totalNumber) {
            this.totalNumber = totalNumber;
        }

        public void execute() {
            lastDownloadPosition = SpUtil.getLastDownloadPosition();
            downLoadNumber = totalNumber - lastDownloadPosition;
            if (downLoadNumber > DownLoadLimit) {
                downLoadNumber = DownLoadLimit;
            }
            mProgressDialog.setMax(downLoadNumber);
            mProgressDialog.show();
            mUploadNumber = 0;

            // 分页下载
            BmobQuery<BmobWord> query = new BmobQuery<>();
            query.order("updatedAt");
            query.setSkip(lastDownloadPosition - 10);
            query.setLimit(downLoadNumber);
            query.findObjects(new FindListener<BmobWord>() {

                @Override
                public void done(List<BmobWord> resultList, BmobException e) {
                    if (e != null || resultList == null) {
                        Toast.makeText(SettingActivity.this, "本次下载失败", Toast.LENGTH_LONG).show();
                        mProgressDialog.dismiss();
                        return;
                    }
                    mProgressDialog.setProgress(downLoadNumber / 2);
                    mGlobalData.refreshByRemoteData(resultList);
                    lastDownloadPosition += resultList.size();
                    SpUtil.saveLastDownloadPosition(lastDownloadPosition);
                    tvDownloadState.setText("上次下载到:  " + lastDownloadPosition + "    共  " + totalNumber + "  个");
                    mProgressDialog.dismiss();
                }
            });
        }
    }
}