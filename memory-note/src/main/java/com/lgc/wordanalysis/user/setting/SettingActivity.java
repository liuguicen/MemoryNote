package com.lgc.wordanalysis.user.setting;

import android.app.AlertDialog;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import com.lgc.baselibrary.UIWidgets.CertainDialog;
import com.lgc.baselibrary.UIWidgets.NotifyDialog;
import com.lgc.baselibrary.UIWidgets.ProgressCallback;
import com.lgc.baselibrary.baseComponent.BaseActivity;
import com.lgc.baselibrary.utils.Logcat;
import com.lgc.baselibrary.utils.SimpleObserver;
import com.lgc.baselibrary.utils.Util;
import com.lgc.wordanalysis.R;
import com.lgc.wordanalysis.base.AppConfig;
import com.lgc.wordanalysis.base.utils.FileEncodingUtil;
import com.lgc.wordanalysis.data.CSVUtil;
import com.lgc.wordanalysis.data.DataSync;
import com.lgc.wordanalysis.data.GlobalData;
import com.lgc.wordanalysis.data.WordLibsUtil;
import com.lgc.wordanalysis.user.AppAgreementActivity;
import com.lgc.wordanalysis.user.FeedBackActivity;
import com.lgc.wordanalysis.user.HelpActivity;
import com.lgc.wordanalysis.user.User;
import com.lgc.wordanalysis.wordList.Command;
import com.lgc.wordanalysis.wordList.WordListActivity;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Set;

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
    public static final boolean isTest = false;
    public static final String ACTION_FIRST_ENTER = "first_enter";

    public static final int FILE_SELECT_CODE_JSON = 0;
    public static final int FILE_SELECT_CODE_CSV = 1;
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
    private ProgressCallback normalProgressCallback;
    /**
     * 导入单词弹出的最后一个弹窗
     */
    private AlertDialog importLastDialog;
    private boolean isImporting = false;
    private boolean needImportWord = false;

    public SettingActivity() {
    }

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
        findViewById(R.id.btn_import_from_csv).setOnClickListener(this);
        findViewById(R.id.setting_return_btn).setOnClickListener(this);
        findViewById(R.id.tv_word_lib).setOnClickListener(this);
        findViewById(R.id.btn_to_feed_back).setOnClickListener(this);
        findViewById(R.id.tv_app_guide).setOnClickListener(this);

        mLvWordLib = findViewById(R.id.word_lib_lv);
        mTvAppGuide = findViewById(R.id.tv_app_help);
        mTvAppGuide.setOnClickListener(this);

        mCertainDialog = new CertainDialog(this);
        mNotifyDialog = new NotifyDialog(this);
        findViewById(R.id.btn_upload_data).setOnClickListener(this);
        Logcat.e("Setting activitty init success");

        normalProgressCallback = new ProgressCallback() {
            @Override
            public void progress(float percentage) {

            }

            @Override
            public void msg(String msg) {
                runOnUiThread(() -> com.lgc.baselibrary.utils.Util.showToast(msg));
            }


            @Override
            public void msg(String msg, boolean isShort) {
                runOnUiThread(() -> Toast.makeText(SettingActivity.this, msg, Toast.LENGTH_SHORT).show());
            }
        };

        initWordLib();
        initAppAgreementBtn();
        //        test();
    }

    private void initWordLib() {
        if (ACTION_FIRST_ENTER.equals(getIntent().getAction())) {
            needImportWord = true;
            new CertainDialog(this).showDialog(null, "请导入您学习的单词！",
                    () -> {
                    });
        }

        WordLibAdapter wordLibAdapter = new WordLibAdapter(this);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        mLvWordLib.setLayoutManager(linearLayoutManager);
        wordLibAdapter.setItemClickListener((v, viewHolder) -> {
            String assertName = WordLibsUtil.wordLibNameList[viewHolder.getAdapterPosition()];
//            importWord_secondStep(null, assertName, "");
            importWord_firstStep(null, assertName, normalProgressCallback);
        });
        mLvWordLib.setAdapter(wordLibAdapter);
    }

    private int importWordFromAssert(String assertName, boolean isDelete, boolean isReplace) {
        InputStream importStream = null;
        try {
            importStream = getAssets().open(assertName);
            String encodingWay = FileEncodingUtil.getCharSet(importStream);
            importStream = getAssets().open(assertName);
            DataSync.importFromStandardCsv(SettingActivity.this,
                    importStream, encodingWay, isDelete, isReplace,
                    normalProgressCallback);

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


    private int importWordFromCsvPath(String path, boolean isDelete, boolean isReplace) {
        InputStream importStream = null;
        try {
            importStream = new FileInputStream(path);
            String encodingWay = FileEncodingUtil.getCharSet(importStream);
            importStream = new FileInputStream(path);
            DataSync.importFromStandardCsv(SettingActivity.this,
                    importStream, encodingWay, isDelete, isReplace,
                    normalProgressCallback);

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
                        mNotifyDialog.showDialog(null, resultMsg, new CertainDialog.ActionListener() {
                            @Override
                            public void onSure() {
                                ClipboardManager clip = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                                clip.setText(resultPath); // 复制
                                Util.showToast("已复制路径到剪切板中");
                            }
                        });
                    }
                });
                break;
            case R.id.btn_import_directly:
                showFileChooser(FILE_SELECT_CODE_JSON);
                break;
            case R.id.btn_export_to_excel:
                mCertainDialog.showDialog("确认导出吗？", null, () -> {
                    if (normalProgressCallback != null)
                        normalProgressCallback.msg("开始导出");
                    String resultPath = DataSync.exportAsCsv(
                            SettingActivity.this, AppConfig.exportDataName,
                            normalProgressCallback);
                    String resultMsg = "导出成功! 位置：\n" + resultPath;
                    if (resultPath == null)
                        resultMsg = "导出失败";
                    mNotifyDialog.showDialog(null, resultMsg, null);
                });
                break;
            case R.id.btn_import_from_csv:
                mCertainDialog.showDialog("请注意！",
                        "请确保您是csv文件，并且列的顺序按照如下形式排列\n" + CSVUtil.ROW_ORDER + "\n否则无法导入成功\n 更多请参考帮助文档",
                        () -> showFileChooser(FILE_SELECT_CODE_CSV));
                break;
            case R.id.tv_app_help:
                mPresenter.onClickAppGuide();
                break;
            case R.id.setting_return_btn:
                returnBackFinish();
                break;
            case R.id.btn_to_feed_back:
                startActivity(new Intent(this, FeedBackActivity.class));
                break;
            case R.id.tv_app_guide:
                startActivity(new Intent(SettingActivity.this, HelpActivity.class));
                break;

        }
    }

    @Override
    public void onBackPressed() {
        if (needImportWord) {
            new CertainDialog(this).showDialog(null, "确定不导入单词吗，退出后您可以重新进入此界面进行导入", new CertainDialog.ActionListener() {
                @Override
                public void onSure() {
                    needImportWord = false;
                }
            });
            return;
        }
        if (!isImporting) {
            super.onBackPressed();
        }
    }

    private void returnBackFinish() {
        if (!isImporting) {
            finish();
        }
    }

    private void showFileChooser(int fileSelectCode) {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("*/*");
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        try {
            startActivityForResult(Intent.createChooser(intent,
                    getString(R.string.choose_file_to_import)), fileSelectCode);
        } catch (android.content.ActivityNotFoundException ex) {
            // Potentially direct the user to the Market with a Dialog
            showToast(getString(R.string.fail_to_launch_file_manager));
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {

            case FILE_SELECT_CODE_JSON:
            case FILE_SELECT_CODE_CSV:
                String needSuffix = "txt";
                if (requestCode == SettingActivity.FILE_SELECT_CODE_JSON) {
                    needSuffix = "txt";
                } else if (requestCode == SettingActivity.FILE_SELECT_CODE_CSV) {
                    needSuffix = "csv";
                }
                if (resultCode == RESULT_OK) {
                    mPresenter.analyzeImportPath(data.getData(), needSuffix);
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

    /**
     * 导入单词第一步，备份原有单词
     */
    @Override
    public void importWord_firstStep(final String importPath, final String assertName, ProgressCallback progressCallBack) {
        mCertainDialog.showDialog("导入将替换旧的数据，现有数据将保存到手机中，确认导入吗？", null, new CertainDialog.ActionListener() {
            @Override
            public void onSure() {
                // 必须先备份
                Observable
                        .create((ObservableOnSubscribe<String>) emitter -> {
                            ld("开始备份旧数据");
                            if (progressCallBack != null) {
                                progressCallBack.msg("开始备份");
                            }
                            String resultPath = DataSync.exportAsCsv(
                                    SettingActivity.this, AppConfig.exportDataName, progressCallBack);
                            emitter.onNext(resultPath);
                        })
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeOn(Schedulers.io())
                        .subscribe(new SimpleObserver<String>() {
                            @Override
                            public void onNext(String resultPath) {
                                String resultMsg = "现有单词备份成功! 位置：\n" + resultPath;
                                if (resultPath == null)
                                    resultMsg = "备份失败";
                                importWord_secondStep(importPath, assertName, resultMsg);
                            }
                        });


            }
        });
    }

    /**
     * 导入单词第二步， 显示导入选项对话框
     *
     * @param importPath
     * @param assertName
     * @param resultMsg
     */
    private void importWord_secondStep(String importPath, String assertName, String resultMsg) {
        ld("显示导入选项弹窗");
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_import_choosing, null);
        View cancleView = view.findViewById(R.id.tv_save_set_cancel);
        View sureView = view.findViewById(R.id.tv_save_set_sure);
        TextView contentTv = view.findViewById(R.id.import_choosing_content);
        contentTv.setText(resultMsg + "\n\n 现在导入新单词");
        View deleteOldLayout = view.findViewById(R.id.import_item_delete_old);
        View replaceOldLayout = view.findViewById(R.id.import_item_replace_old);

        TextView doName = deleteOldLayout.findViewById(R.id.importChoseItemName);
        doName.setText("删除原有单词吗");
        TextView roName = replaceOldLayout.findViewById(R.id.importChoseItemName);
        roName.setText("替换原有数据");
        CheckBox deleteCheckBox = deleteOldLayout.findViewById(R.id.importItemCheckView);
        deleteOldLayout.setOnClickListener(v -> {
            deleteCheckBox.setChecked(!deleteCheckBox.isChecked());
        });
        CheckBox replaceCheckBox = replaceOldLayout.findViewById(R.id.importItemCheckView);
        replaceOldLayout.setOnClickListener(v -> {
            replaceCheckBox.setChecked(!replaceCheckBox.isChecked());
        });


        importLastDialog = new AlertDialog.Builder(this).setView(view)
                .create();

        importLastDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

        cancleView.setOnClickListener(v -> importLastDialog.dismiss());

        sureView.setOnClickListener(v -> {
            if (isImporting) {
                return;
            }
            contentTv.setText("单词导入中，请勿返回或关闭应用！");
            importLastDialog.setCancelable(false);
            cancleView.setVisibility(View.GONE);
            sureView.setVisibility(View.GONE);
            replaceOldLayout.setVisibility(View.GONE);
            deleteOldLayout.setVisibility(View.GONE);
            isImporting = true;
            importWord_thirdStep(importPath, assertName, deleteCheckBox.isChecked(), replaceCheckBox.isChecked());
        });
        importLastDialog.show();
    }

    /**
     * 导入单词第三步，导入
     */
    private void importWord_thirdStep(String importPath, String assertName, boolean isDelete, boolean isReplace) {
        Observable
                .create((ObservableOnSubscribe<String>) emitter -> {
                    ld("开始导入新数据");
                    int result = 0;
                    if (importPath != null) {
                        if (importPath.endsWith(".txt")) {
                            result = DataSync.importFromTxt(SettingActivity.this,
                                    importPath, isDelete, isReplace,
                                    normalProgressCallback);
                        } else if (importPath.endsWith(".csv")) {
                            result = importWordFromCsvPath(importPath, isDelete, isReplace);
                        }

                    } else if (assertName != null) {
                        result = importWordFromAssert(assertName, isDelete, isReplace);
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
                        importLastDialog.dismiss();
                        isImporting = false;
                        needImportWord = false;
                        if (ACTION_FIRST_ENTER.equals(getIntent().getAction())) {
                            Intent intent = new Intent(SettingActivity.this, WordListActivity.class);
                            intent.setAction(ACTION_FIRST_ENTER);
                            startActivity(intent);
                            finish();
                        }
                    }

                    @Override
                    public void onError(Throwable throwable) {
                        throwable.printStackTrace();
                    }

                    @Override
                    public void onNext(String msg) {

                    }
                });
    }


    private void initAppAgreementBtn() {
        String text = "《用户协议》和《隐私政策》";
        SpannableString spannableString = new SpannableString(text);

        ClickableSpan clickableSpan = new ClickableSpan() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SettingActivity.this, AppAgreementActivity.class);
                intent.setAction(AppAgreementActivity.INTENT_ACTION_USER_AGREEMENT);
                startActivity(intent);
            }

            @Override
            public void updateDrawState(TextPaint ds) {
                ds.setColor(ContextCompat.getColor(SettingActivity.this, R.color.colorPrimary));//设置颜色
                ds.setUnderlineText(false); // SettingActivity
            }
        };
        int start = text.indexOf('用'), end = text.indexOf('议') + 1;
        spannableString.setSpan(clickableSpan, start, end, Spannable.SPAN_INCLUSIVE_INCLUSIVE);
        ForegroundColorSpan colorSpan = new ForegroundColorSpan(ContextCompat.getColor(this, R.color.colorPrimary));
        spannableString.setSpan(colorSpan, start, end, Spannable.SPAN_EXCLUSIVE_INCLUSIVE);

        ClickableSpan clickableSpan2 = new ClickableSpan() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SettingActivity.this, AppAgreementActivity.class);
                intent.setAction(AppAgreementActivity.INTENT_ACTION_PRIVACY_POLICY);
                startActivity(intent);
            }

            @Override
            public void updateDrawState(TextPaint ds) {
                ds.setColor(ContextCompat.getColor(SettingActivity.this, R.color.colorPrimary));//设置颜色
                ds.setUnderlineText(false); // 去掉下划线
            }

        };
        start = text.indexOf('隐');
        end = text.indexOf("策") + 1;
        spannableString.setSpan(clickableSpan2, start, end, Spannable.SPAN_INCLUSIVE_INCLUSIVE);

        ForegroundColorSpan colorSpan2 = new ForegroundColorSpan(ContextCompat.getColor(this, R.color.colorPrimary));
        spannableString.setSpan(colorSpan2, start, end, Spannable.SPAN_EXCLUSIVE_INCLUSIVE);

        TextView tvAppAgreement = findViewById(R.id.to_privacy);
        tvAppAgreement.setText(spannableString);
        tvAppAgreement.setMovementMethod(LinkMovementMethod.getInstance());
    }


    @Override
    public ProgressCallback getProgressCallBack() {
        return normalProgressCallback;
    }

    private void ld(Object msg) {
        if (msg != null) {
            Log.d("SettingActivity", msg.toString());
        }
    }
}