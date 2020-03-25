package com.lgc.wordanalysis.launch;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.TextView;
import android.widget.Toast;

import com.lgc.baselibrary.utils.Util;
import com.lgc.wordanalysis.R;
import com.lgc.wordanalysis.base.AppConfig;
import com.lgc.wordanalysis.base.SpConstant;
import com.lgc.wordanalysis.user.AppAgreementActivity;
import com.lgc.wordanalysis.user.setting.SettingActivity;
import com.lgc.wordanalysis.wordList.WordListActivity;

import java.util.ArrayList;
import java.util.List;


/**
 * 入口Activity，在这里会首次调用广点通的SDK。
 */
public class LaunchActivity extends Activity implements ISplashAdStrategy {
    public static final String TAG = "LaunchActivity";
    public static final int REQUEST_CODE_APP_GUIDE = 1001;
    public static final int RESULT_CODE_ENTER = 10001;
    public static final int RESULT_CODE_OUT = 10002;

    // 第一个要足够长，5s, 保证第一个广告最大程度获取到，两个都设置短时间的反而不好
    public static final int SPLASH_FIRST_TIME_OUT = 5000;
    public static final int SPLASH_SECOND_TIME_OUT = 3000;

    private ViewGroup container;
    private TextView skipView;
    private static final int PERMISSION_REQUEST_CODE = 1;
    private Dialog mDealNeverDialog; // 权限申请的两个dialog
    private Dialog mDealBanDialog;


    // 权限请求相关
    private static final String[] mNecessaryPermissionsList = new String[]{
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };
    private static final int PERMISSION_SETTING = 3;

    private TextView pauseView;
    //这里用一个列表处理更好，目前只有两家，暂时这样做
//    private MyTTSplashAd mMyTTSplashAd;
//    private TencentSplashAd mTencentSplashAd;
    /**
     * 记录ac启动时间，用于第一个广告播放失败时，计算用户等待时间，
     * 如果等待时间不长，播放第二个广告
     */
    private long adStartTime = 0;
    private int splashTryNumber = 0;
    /**
     * 有些splashad依赖resume,作为第二个选择是resume可能不被调用，需要手动调用
     */
    private boolean hadResumed = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        checkPermissionAndStart();
    }

    private void checkPermissionAndStart() {
        // 如果targetSDKVersion >= 23，就要申请好权限。如果您的App没有适配到Android6.0（即targetSDKVersion < 23），那么只需要在这里直接调用fetchSplashAD接口。
        if (Build.VERSION.SDK_INT >= 23) {
            checkAndRequestPermission();
        } else {
            afterPermissionSuccess();
        }
    }

    /**
     * 权限申请完成开始执行
     */
    private void afterPermissionSuccess() {
        if (!AppConfig.hasReadPrivacyStatement()) {
            showAppPrivacyDialog();
        } else {
            realEnterApp();
        }
    }

    private void realEnterApp() {
        SharedPreferences sharedPreferences = getSharedPreferences(SpConstant.app_use_sp, Context.MODE_PRIVATE);
        if (!sharedPreferences.getBoolean(SpConstant.has_imported, false)) {
//            Util.showToast("请导入您需要的单词！");
            sharedPreferences.edit().putBoolean(SpConstant.has_imported, true).apply();
            Intent intent = new Intent(this, SettingActivity.class);
            intent.setAction(SettingActivity.ACTION_FIRST_ENTER);
            startActivity(intent);
        } else {
            startHomeAc();
        }
        finish();
    }


    /**
     * ----------非常重要----------
     */
    @TargetApi(Build.VERSION_CODES.M)
    private void checkAndRequestPermission() {
        List<String> lackedPermission = new ArrayList<>();
        for (String onePermission : mNecessaryPermissionsList) {
            if (ContextCompat.checkSelfPermission(this, onePermission) != PackageManager.PERMISSION_GRANTED) {
                lackedPermission.add(onePermission);
            }
        }

        // 权限都已经有了，那么直接调用SDK
        if (lackedPermission.size() == 0) {
            afterPermissionSuccess();
        } else {
            // 请求所缺少的权限，在onRequestPermissionsResult中再看是否获得权限，如果获得权限就可以调用SDK，否则不要调用SDK。
            String[] requestPermissions = new String[lackedPermission.size()];
            lackedPermission.toArray(requestPermissions);
            requestPermissions(requestPermissions, PERMISSION_REQUEST_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_CODE) {
            boolean isAllPermit = true;
            for (int i = 0; i < permissions.length; i++) {
                if (grantResults[i] == PackageManager.PERMISSION_GRANTED) { //选择了“始终允许”
                    Toast.makeText(this, "" + getString(R.string.permission) + permissions[i] + getString(R.string.apply_success), Toast.LENGTH_SHORT).show();
                } else {
                    isAllPermit = false;
                    if (!ActivityCompat.shouldShowRequestPermissionRationale(this, permissions[i])) {//用户选择了禁止不再询问

                        AlertDialog.Builder builder = new AlertDialog.Builder(LaunchActivity.this);
                        builder.setTitle(getString(R.string.permission))
                                .setMessage(getString(R.string.app_should_permission_notice) + getString(R.string.setting_permission_guide))
                                .setPositiveButton(getString(R.string.go_to_permit),
                                        (dialog, id) -> {
                                            if (mDealNeverDialog != null && mDealNeverDialog.isShowing()) {
                                                mDealNeverDialog.dismiss();
                                            }
                                            Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                            Uri uri = Uri.fromParts("package", getPackageName(), null);//注意就是"package",不用改成自己的包名
                                            intent.setData(uri);
                                            startActivityForResult(intent, PERMISSION_SETTING);
                                        });
                        mDealNeverDialog = builder.create();
                        mDealNeverDialog.setCanceledOnTouchOutside(false);
                        mDealNeverDialog.show();
                    } else {//选择禁止
                        AlertDialog.Builder builder = new AlertDialog.Builder(LaunchActivity.this);
                        builder.setTitle(getString(R.string.permission))
                                .setMessage(getString(R.string.app_should_permission_notice))
                                .setPositiveButton(getString(R.string.go_to_permit),
                                        (dialog, id) -> {
                                            if (mDealBanDialog != null && mDealBanDialog.isShowing()) {
                                                mDealBanDialog.dismiss();
                                            }
                                            ActivityCompat.requestPermissions(LaunchActivity.this,
                                                    mNecessaryPermissionsList, PERMISSION_REQUEST_CODE);
                                        });
                        mDealBanDialog = builder.create();
                        mDealBanDialog.setCanceledOnTouchOutside(false);
                        mDealBanDialog.show();
                    }

                }
            }
            if (isAllPermit) {
                afterPermissionSuccess();
            }
        }
    }


    private void startHomeAc() {
        Intent intent = new Intent(this, WordListActivity.class);
        Intent sourceIntent = getIntent();
        if (sourceIntent != null && sourceIntent.getData() != null)  //如果是从其它应用过来需要编辑图片的
            intent.setData(sourceIntent.getData());
        startActivity(intent);
    }


    private void showAppPrivacyDialog() {
        View view = LayoutInflater.from(this).inflate(R.layout.layout_sevice_and_privacy, null);
        View cancleView = view.findViewById(R.id.service_and_privacy_cancel);
        View sureView = view.findViewById(R.id.service_and_privacy_agree);
        initAppAgreementBtn(view);
        AlertDialog sapDialog =
                new AlertDialog.Builder(this)
                        .setView(view)
                        .setCancelable(false)
                        .create();

        sapDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        sureView.setOnClickListener(v -> {
            AppConfig.putAgreeSAP(true);
            sapDialog.dismiss();
            realEnterApp();
        });
        cancleView.setOnClickListener(v -> {
            sapDialog.dismiss();
            finish();
        });
        sapDialog.show();
    }

    private void initAppAgreementBtn(View view) {
        String text = "同意《用户协议》和《隐私政策》";
        SpannableString spannableString = new SpannableString(text);

        ClickableSpan clickableSpan = new ClickableSpan() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LaunchActivity.this, AppAgreementActivity.class);
                intent.setAction(AppAgreementActivity.INTENT_ACTION_USER_AGREEMENT);
                startActivity(intent);
            }

            @Override
            public void updateDrawState(TextPaint ds) {
                ds.setColor(ContextCompat.getColor(LaunchActivity.this, R.color.colorPrimary));//设置颜色
                ds.setUnderlineText(false); // 去掉下划线
            }
        };
        int start = text.indexOf('用'), end = text.indexOf('议') + 1;
        spannableString.setSpan(clickableSpan, start, end, Spannable.SPAN_INCLUSIVE_INCLUSIVE);
        ForegroundColorSpan colorSpan = new ForegroundColorSpan(ContextCompat.getColor(this, R.color.colorPrimary));
        spannableString.setSpan(colorSpan, start, end, Spannable.SPAN_EXCLUSIVE_INCLUSIVE);

        ClickableSpan clickableSpan2 = new ClickableSpan() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LaunchActivity.this, AppAgreementActivity.class);
                intent.setAction(AppAgreementActivity.INTENT_ACTION_PRIVACY_POLICY);
                startActivity(intent);
            }

            @Override
            public void updateDrawState(TextPaint ds) {
                ds.setColor(ContextCompat.getColor(LaunchActivity.this, R.color.colorPrimary));//设置颜色
                ds.setUnderlineText(false); // 去掉下划线
            }

        };
        start = text.indexOf('隐');
        end = text.indexOf("策") + 1;
        spannableString.setSpan(clickableSpan2, start, end, Spannable.SPAN_INCLUSIVE_INCLUSIVE);

        ForegroundColorSpan colorSpan2 = new ForegroundColorSpan(ContextCompat.getColor(this, R.color.colorPrimary));
        spannableString.setSpan(colorSpan2, start, end, Spannable.SPAN_EXCLUSIVE_INCLUSIVE);

        TextView tvAppAgreement = view.findViewById(R.id.service_and_privacy_tv);
        tvAppAgreement.setText(spannableString);
        tvAppAgreement.setMovementMethod(LinkMovementMethod.getInstance());
    }

    /**
     * 开屏页一定要禁止用户对返回按钮的控制，否则将可能导致用户手动退出了App而广告无法正常曝光和计费
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK || keyCode == KeyEvent.KEYCODE_HOME) {
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onAdError() {

    }

    @Override
    public void onAdFinish() {

    }
}
