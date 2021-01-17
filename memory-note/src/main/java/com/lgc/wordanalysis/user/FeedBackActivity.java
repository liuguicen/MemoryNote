package com.lgc.wordanalysis.user;

import android.os.Build;
import android.os.Bundle;

import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import com.lgc.baselibrary.baseComponent.BaseActivity;
import com.lgc.baselibrary.utils.ToastUtils;
import com.lgc.baselibrary.utils.Util;
import com.lgc.wordanalysis.R;

import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.SaveListener;

public class FeedBackActivity extends AppCompatActivity {
    String lastComment;
    private EditText contactEdit;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback);
        initView();
    }

    private void initView() {
        final EditText commentEdit = findViewById(R.id.feedback_comment);
        contactEdit = findViewById(R.id.feedback_contact_edit);
        Button btnCommit = findViewById(R.id.feedback_btn_commit);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            btnCommit.setBackground(Util.getDrawable(R.drawable.rip_blue_cornor_backgound));
        } else {
            btnCommit.setBackground(Util.getDrawable(R.drawable.background_round_corner_blue));
        }
        btnCommit.setOnClickListener(
                v -> {
                    if (Util.DoubleClick.isDoubleClick()) return;
                    if (commentEdit.getText() == null) return;
                    commitComment(commentEdit.getText().toString());
                }
        );
    }

    private void commitComment(String comment) {
//        检查
        if (comment.trim().isEmpty()) { // 没有输入内容不提交
            return;
        } else if (comment.length() > 500) {//太长
            comment = comment.substring(0, 500);
        } else if (comment.equals(lastComment)) {
            ToastUtils.show(this, getString(R.string.feedback_has_commit));
            return;
        }
        lastComment = comment;
        final Comment commentObj = new Comment(comment);
//        附加信息
        if (contactEdit.getText() != null)
            commentObj.setContact(contactEdit.getText().toString());

//        提交
        commentObj.save(new SaveListener<String>() {
            @Override
            public void done(String s, BmobException e) {
                if (e == null) {
                    ToastUtils.show(FeedBackActivity.this, getString(R.string.commit_success_thanks));
                } else {
//                    ToastUtils.show(FeedBackActivity.this, getString(R.string.network_error_try_latter));
                }
            }
        });
    }

}
