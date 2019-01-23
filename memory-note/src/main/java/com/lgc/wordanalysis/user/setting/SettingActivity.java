package com.lgc.wordanalysis.user.setting;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.TextView;

import com.lgc.baselibrary.UIWidgets.CertainDialog;
import com.lgc.baselibrary.UIWidgets.NotifyDialog;
import com.lgc.baselibrary.baseComponent.BaseActivity;
import com.lgc.baselibrary.utils.Logcat;
import com.lgc.wordanalysis.R;
import com.lgc.wordanalysis.base.AppConfig;
import com.lgc.wordanalysis.data.DataSync;
import com.lgc.wordanalysis.data.GlobalData;
import com.lgc.wordanalysis.user.User;
import com.lgc.wordanalysis.wordList.Command;

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

        mTvAppGuide = findViewById(R.id.tv_app_guide);
        mTvAppGuide.setOnClickListener(this);

        mCertainDialog = new CertainDialog(this);
        mNotifyDialog = new NotifyDialog(this);
        findViewById(R.id.btn_upload_data).setOnClickListener(this);
        Logcat.e("Setting activitty init success");
//        test();
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
                        String resultPath = DataSync.exportData2Sd(
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
                        String resultPath = DataSync.exportExcel2Sd(
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
            startActivityForResult( Intent.createChooser(intent,
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
    public void showImportDialog(final String importPath) {
        mCertainDialog.showDialog("导入将替换旧的数据，现有数据将保存到SD卡中，确认导入吗？", null, new CertainDialog.ActionListener() {
            @Override
            public void onSure() {
                // 必须先备份
                String resultPath = DataSync.exportData2Sd(SettingActivity.this, AppConfig.exportDataName);

                String resultMsg = "导出成功! 位置：\n" + resultPath;
                if (resultPath == null)
                    resultMsg = "导出失败";
                new CertainDialog(SettingActivity.this).showDialog(null,
                        "当前数据" + resultMsg + "\n是否替换当前数据？", new CertainDialog.ActionListener() {
                            @Override
                            public void onSure() {
                                DataSync.importFromSD(SettingActivity.this,
                                        importPath);
                            }
                        });
            }
        });
    }
}