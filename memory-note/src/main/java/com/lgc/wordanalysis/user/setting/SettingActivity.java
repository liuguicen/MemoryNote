package com.lgc.wordanalysis.user.setting;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.lgc.baselibrary.UIWidgets.CertainDialog;
import com.lgc.baselibrary.UIWidgets.NotifyDialog;
import com.lgc.baselibrary.baseComponent.BaseActivity;
import com.lgc.baselibrary.utils.Logcat;
import com.lgc.baselibrary.utils.SimpleObserver;
import com.lgc.wordanalysis.R;
import com.lgc.wordanalysis.base.AppConfig;
import com.lgc.wordanalysis.base.utils.FileEncodingUtil;
import com.lgc.wordanalysis.data.DataSync;
import com.lgc.wordanalysis.data.GlobalData;
import com.lgc.wordanalysis.data.WordLibsUtil;
import com.lgc.wordanalysis.user.User;
import com.lgc.wordanalysis.wordList.Command;

import java.io.IOException;
import java.io.InputStream;

import io.reactivex.Observable;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * <pre>
 *      author : liuguicen
 *      time : 2018/02/18
 *      version : 1.0
 * <pre>
 */

public class SettingActivity extends BaseActivity implements SettingContract.View {

    private static final int FILE_SELECT_CODE = 0;
    CertainDialog mCertainDialog;
    NotifyDialog mNotifyDialog;
    private SettingContract.Presenter mPresenter;
    private int mUploadNumber = 0;
    private GlobalData mGlobalData;
    private TextView mTvExportDirectly;
    private TextView mTvImportDirectly;
    private TextView mTvExportTxt;
    private TextView mTvImportTxt;
    private TextView mTvExportExcel;
    private TextView mTvImportExcel;
    private TextView mTvLogin;
    private TextView mTvAppGuide;
    private User mUser;
    private int mLastInputType;
    private boolean mIsInEdit;
    private RecyclerView mLvWordLib;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        mPresenter = new SettingPresenter(this, this);
        mGlobalData = GlobalData.getInstance();
        mUser = mGlobalData.getUser();

        mTvExportDirectly = ((TextView) findViewById(R.id.btn_export_directly));
        mTvImportDirectly = ((TextView) findViewById(R.id.btn_import_directly));
        findViewById(R.id.btn_export_directly).setOnClickListener(this);
        findViewById(R.id.btn_import_directly).setOnClickListener(this);
        findViewById(R.id.btn_export_to_txt).setOnClickListener(this);
        findViewById(R.id.btn_import_from_txt).setOnClickListener(this);
        findViewById(R.id.btn_export_to_excel).setOnClickListener(this);
        findViewById(R.id.btn_import_from_excel).setOnClickListener(this);
        findViewById(R.id.setting_return_btn).setOnClickListener(this);
        findViewById(R.id.tv_word_lib).setOnClickListener(this);

        mLvWordLib = findViewById(R.id.word_lib_lv);
        mTvAppGuide = findViewById(R.id.tv_app_guide);
        mTvAppGuide.setOnClickListener(this);

        mCertainDialog = new CertainDialog(this);
        mNotifyDialog = new NotifyDialog(this);
        findViewById(R.id.btn_upload_data).setOnClickListener(this);
        Logcat.e("Setting activitty init success");

        initWordLib();
        //        test();
    }

    private void initWordLib() {
        WordLibAdapter wordLibAdapter = new WordLibAdapter(this);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        mLvWordLib.setLayoutManager(linearLayoutManager);
        wordLibAdapter.setItemClickListener((v, viewHolder) -> {
            String assertName = WordLibsUtil.wordLibNameList[viewHolder.getAdapterPosition()];
            importWordLibFromAssert(assertName);
            showImportDialog(null, assertName);
        });
        mLvWordLib.setAdapter(wordLibAdapter);
    }

    private int importWordLibFromAssert(String assertName) {
        InputStream importStream = null;
        try {
            importStream = getAssets().open(assertName);
            String encodingWay = FileEncodingUtil.getCharSet(importStream);
            importStream = getAssets().open(assertName);
            DataSync.importFromStandardCsv(SettingActivity.this,
                    importStream, encodingWay, null);
        } catch (IOException e) {
            e.printStackTrace();
            return -1;
        } finally {
            if (importStream != null) {
                try {
                    importStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return 0;
    }

    void test() {
    }


    /**
     * Called when a view has been clicked.
     *
     * @param v The view that was clicked.
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_export_directly:
                mCertainDialog.showDialog("确认导出吗？", null, new CertainDialog.ActionListener() {
                    @Override
                    public void onSure() {
                        String resultPath = DataSync.export2JsonTxt(
                                SettingActivity.this, AppConfig.exportDataName);
                        String resultMsg = "导出成功! 位置：\n" + resultPath;
                        if (resultPath == null)
                            resultMsg = "导出失败";
                        mNotifyDialog.showDialog(null, resultMsg, null);
                    }
                });
                break;
            case R.id.btn_import_directly:
                showFileChooser();
                break;
            case R.id.btn_export_to_excel:
                mCertainDialog.showDialog("确认导出吗？", null, new CertainDialog.ActionListener() {
                    @Override
                    public void onSure() {
                        String resultPath = DataSync.exportAsCsv(
                                SettingActivity.this, AppConfig.exportDataName);
                        String resultMsg = "导出成功! 位置：\n" + resultPath;
                        if (resultPath == null)
                            resultMsg = "导出失败";
                        mNotifyDialog.showDialog(null, resultMsg, null);
                    }
                });
                break;
            case R.id.tv_app_guide:
                mPresenter.onClickAppGuide();
                break;
            case R.id.setting_return_btn:
                finish();
                break;

        }
    }

    private void showFileChooser() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("*/*");
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        try {
            startActivityForResult(Intent.createChooser(intent,
                    getString(R.string.choose_file_to_import)), FILE_SELECT_CODE);
        } catch (android.content.ActivityNotFoundException ex) {
            // Potentially direct the user to the Market with a Dialog
            showToast(getString(R.string.fail_to_launch_file_manager));
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case FILE_SELECT_CODE:
                if (resultCode == RESULT_OK) {
                    mPresenter.importDataPrepare(data.getData());
                }
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void showAppGuide() {
        mTvAppGuide.setText("搜索框里面可以输入命令，以--开头就表示命令，支持的命令如下：\n" + Command.commandGuide);
    }

    @Override
    public void hideAppGuide() {
        mTvAppGuide.setText(R.string.app_guide);
    }

    @Override
    public void showImportDialog(final String importPath, final String assertName) {
        mCertainDialog.showDialog("导入将替换旧的数据，现有数据将保存到SD卡中，确认导入吗？", null, new CertainDialog.ActionListener() {
            @Override
            public void onSure() {
                // 必须先备份
                String resultPath = DataSync.export2JsonTxt(SettingActivity.this, AppConfig.exportDataName);

                String resultMsg = "导出成功! 位置：\n" + resultPath;
                if (resultPath == null)
                    resultMsg = "导出失败";
                new CertainDialog(SettingActivity.this).showDialog(null,
                        "当前数据" + resultMsg + "\n是否替换当前数据？", new CertainDialog.ActionListener() {
                            @Override
                            public void onSure() {
                                Observable
                                        .create((ObservableOnSubscribe<String>) emitter -> {
                                            int result = 0;
                                            if (importPath != null) {
                                                result = DataSync.importFromTxt(SettingActivity.this,
                                                        importPath);
                                            } else if (assertName != null) {
                                                result = importWordLibFromAssert(assertName);
                                            }
                                            if (result > -1) {
                                                emitter.onComplete();
                                            } else {
                                                emitter.onError(new Exception());
                                            }
                                        })
                                        .subscribeOn(Schedulers.io())
                                        .observeOn(AndroidSchedulers.mainThread())
                                        .subscribe(new SimpleObserver<String>() {

                                            @Override
                                            public void onComplete() {
                                                Toast.makeText(SettingActivity.this, "导入完成", Toast.LENGTH_LONG).show();
                                            }

                                            @Override
                                            public void onError(Throwable throwable) {
                                                throwable.printStackTrace();
                                            }

                                            @Override
                                            public void onNext(String importPath) {

                                            }
                                        });
                            }
                        });
            }
        });
    }
}