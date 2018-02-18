package com.lgc.memorynote.user.setting;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.lgc.memorynote.R;
import com.lgc.memorynote.wordDetail.WordDetailPresenter;

/**
 * <pre>
 *      author : liuguicen
 *      time : 2018/02/18
 *      version : 1.0
 * <pre>
 */

public class SettingActivity extends AppCompatActivity{

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        TextView tvAppGuide = ((TextView) findViewById(R.id.tv_app_guide));
        tvAppGuide.setText("搜索框里面可以输入命令，以--开头就表示命令，支持的命令如下：\n");
    }
}
