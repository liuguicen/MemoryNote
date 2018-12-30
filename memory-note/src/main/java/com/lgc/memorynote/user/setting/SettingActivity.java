package com.lgc.memorynote.user.setting;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.lgc.memorynote.R;
import com.lgc.memorynote.base.AppConfig;
import com.lgc.memorynote.base.CertainDialog;
import com.lgc.baselibrary.utils.Logcat;
import com.lgc.memorynote.data.DataSync;
import com.lgc.memorynote.data.GlobalData;
import com.lgc.memorynote.user.User;
import com.lgc.memorynote.wordList.Command;

/**
 * <pre>
 *      author : liuguicen
 *      time : 2018/02/18
 *      version : 1.0
 * <pre>
 */

public class SettingActivity extends AppCompatActivity implements SettingContract.View {

    CertainDialog mCertainDialog;
    private SettingPresenter mPresenter;
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
        mPresenter = new SettingPresenter(this);
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
                        DataSync.exportData2Sd(SettingActivity.this, AppConfig.exportDataName);
                    }
                });
                break;
            case R.id.btn_import_directly:
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
                break;
            case R.id.tv_app_guide:
                mPresenter.onClickAppGuide();
                break;
            case  R.id.setting_return_btn:
                finish();
                break;

        }
    }

    @Override
    public void showAppGuide() {
        mTvAppGuide.setText("搜索框里面可以输入命令，以--开头就表示命令，支持的命令如下：\n" + Command.commandGuide);
    }

    @Override
    public void hideAppGuide() {
        mTvAppGuide.setText(R.string.app_guide);
    }
}