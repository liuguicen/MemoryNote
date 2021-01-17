package com.lgc.wordanalysis.user;

import android.os.Build;
import android.os.Bundle;
import android.view.WindowManager;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.lgc.wordanalysis.R;


/**
 * <pre>
 *      author : liuguicen
 *      time : 2019/09/24
 *      version : 1.0
 * <pre>
 */
public class AppAgreementActivity extends AppCompatActivity {
    public static final String INTENT_ACTION_USER_AGREEMENT = "user agreement";
    public static final String INTENT_ACTION_PRIVACY_POLICY = "privacy policy";
    public static final String INTENT_ACTION_VIP_SERVICE_AGREEMENT = "vip_service_agreement";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_agreement);


        findViewById(R.id.app_agreement_return_icon).setOnClickListener(v-> finish());
        String action = getIntent().getAction();
        if (INTENT_ACTION_USER_AGREEMENT.equals(action)) {
            ((TextView) findViewById(R.id.app_agreement_title)).setText("用户协议");
            ((TextView) findViewById(R.id.app_agreement_content)).setText(R.string.user_agreement_content);
        } else if (INTENT_ACTION_PRIVACY_POLICY.equals(action)) {
            ((TextView) findViewById(R.id.app_agreement_title)).setText("隐私政策");
            ((TextView) findViewById(R.id.app_agreement_content)).setText(R.string.privacy_policies_content);
        } /*else if (INTENT_ACTION_VIP_SERVICE_AGREEMENT.equals(action)) {
            ((TextView) findViewById(R.id.app_agreement_title)).setText(R.string.vip_service_agreement_title);
            ((TextView) findViewById(R.id.app_agreement_content)).setText(R.string.vip_service_agreement_content);

        }*/
    }
}
