package com.lgc.memorynote.wordDetail;


import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.lgc.memorynote.R;
import com.lgc.memorynote.base.CertainDialog;
import com.lgc.memorynote.base.InputAnalyzerUtil;
import com.lgc.memorynote.base.UIUtil;
import com.lgc.memorynote.base.Util;
import com.lgc.memorynote.data.AppConstant;
import com.lgc.memorynote.data.Word;

import java.util.List;

public class WordDetailActivity extends AppCompatActivity implements WordDetailContract.View{

    public static final String INTENT_EXTRA_WORD_NAME = "intent_extra_word_detail_word_name";
    public static final String INTENT_EXTRA_IS_ADD = "intent_extra_word_detail_is_add";
    public static final String INTENT_EXTRA_ADD_NAME = "intent_extra_add_name";

    private WordDetailContract.Presenter mPresenter;
    private EditText mTvWordName;
    private EditText mTvWordMeaning;
    private EditText mTvSimilarWord;
    private TextView mTvStrangeDegree;
    private TextView mTvLastRememberTime;
    private EditText mTvRememberWay;
    private EditText mtvWordGroup;

    private TextView mBtnEdit;
    private CertainDialog mCertainDialog;
    private View mDeleteView;
    private int lastInputType;
    private boolean mWordNameChanged = false;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_word_detail);
        mPresenter = new WordDetailPresenter(this);
        initView();
        mPresenter.initAndShowData(getIntent()); // 要在View初始化之后调用
    }

    private void initView() {
        mTvWordName          = (EditText) findViewById(R.id.et_word_detail_name);
        mTvWordName.setOnLongClickListener(this);

        mTvWordMeaning       = (EditText) findViewById(R.id.et_word_detail_meaning);
        mTvWordMeaning.setOnLongClickListener(this);
        mTvWordMeaning.setOnClickListener(this);

        mTvSimilarWord       = (EditText) findViewById(R.id.et_similar_word);
        mTvSimilarWord.setOnLongClickListener(this);

        mtvWordGroup         = (EditText) findViewById(R.id.et_word_detail_group);
        mtvWordGroup.setOnLongClickListener(this);

        mTvRememberWay       = (EditText) findViewById(R.id.et_word_remember_way);
        mTvRememberWay.setOnLongClickListener(this);

        mTvStrangeDegree     = (TextView) findViewById(R.id.value_strange_degree);
        mTvLastRememberTime  = (TextView) findViewById(R.id.last_remember_time);
        mBtnEdit             = (TextView) findViewById(R.id.btn_word_detail_edit);

        mDeleteView          = (TextView) findViewById(R.id.word_detail_delete);
        mTvWordName.setTag(mTvWordName.getBackground());
        mTvWordMeaning.setTag(mTvWordName.getBackground());
        lastInputType = mTvWordMeaning.getInputType();
        mTvSimilarWord.setTag(mTvSimilarWord.getBackground());
        mTvRememberWay.setTag(mTvRememberWay.getBackground());
        mtvWordGroup.setTag(mtvWordGroup.getBackground());
        mBtnEdit.setOnClickListener(this);
        mDeleteView.setOnClickListener(this);
        findViewById(R.id.add_strange_degree).setOnClickListener(this);
        findViewById(R.id.reduce_strange_degree).setOnClickListener(this);
        findViewById(R.id.btn_sync_similar).setOnClickListener(this);
        findViewById(R.id.btn_sync_group).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        // 排除重复的点击
        if (Util.RepetitiveEventFilter.isRepetitive(500))
            return;
        switch (v.getId()) {
            case R.id.add_strange_degree:
                mPresenter.addStrangeDegree();
                break;
            case R.id.reduce_strange_degree:
                mPresenter.reduceStrangeDegree();
                break;
            case R.id.btn_word_detail_edit:
                mPresenter.switchEdit();
                break;
            case R.id.word_detail_delete:
                onclickDelete();
                break;
            case R.id.btn_sync_similar:
                mPresenter.syncSimilarWord();
                break;
            case R.id.btn_sync_group:
                mPresenter.syncWordGroup();
                break;

        }
    }

    /**
     * Called when a view has been clicked and held.
     *
     * @param v The view that was clicked and held.
     * @return true if the callback consumed the long click, false otherwise.
     */
    @Override
    public boolean onLongClick(View v) {
        switch (v.getId()) {
            case R.id.et_word_detail_name:
            case R.id.et_word_detail_meaning:
            case R.id.et_similar_word:
            case R.id.et_word_detail_group:
            case R.id.et_word_remember_way:
                mPresenter.switchEdit();
                break;
        }
        return false;
    }

    private void onclickDelete() {
        if (mCertainDialog == null) {
            mCertainDialog = new CertainDialog(this);
        }
        mCertainDialog.showDialog(null, getString(R.string.certain_delete),
                new CertainDialog.ActionListener() {
                    @Override
                    public void onSure() {
                        mPresenter.deleteWord();
                        WordDetailActivity.this.finish();
                    }
                });
    }

    @Override
    public void switchEditStyle(boolean isInEdit) {
        switchTvEditStyle(mTvWordName, isInEdit);
        switchTvEditStyle(mTvWordMeaning, isInEdit);
        switchTvEditStyle(mTvSimilarWord, isInEdit);
        switchTvEditStyle(mTvRememberWay, isInEdit);
        switchTvEditStyle(mtvWordGroup, isInEdit);
        if (isInEdit) {
            mBtnEdit.setBackgroundResource(R.drawable.btn_bg_finish);
            mBtnEdit.setText(getString(R.string.edit_save));
            mDeleteView.setVisibility(View.GONE);
        } else {
            mBtnEdit.setBackgroundResource(R.drawable.btn_bg_edit);
            mBtnEdit.setText(getString(R.string.edit));
            mDeleteView.setVisibility(View.VISIBLE);
        }
    }

    private void switchTvEditStyle(TextView tv, boolean isInEdit) {
        if(isInEdit) {
            tv.setInputType(lastInputType);
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

    @Override
    public String getInputWordName() {
        return mTvWordName.getText().toString();
    }

    @Override
    public String getInputWordMeaning() {
        return mTvWordMeaning.getText().toString();
    }

    @Override
    public String getInputSimilarWords() {
        return mTvSimilarWord.getText().toString();
    }

    @Override
    public String getInputRememberWay() {
        return mTvRememberWay.getText().toString();
    }

    @Override
    public String getInputWordGroup() {
        return mtvWordGroup.getText().toString();
    }

    @Override
    public void showWordName(String word) {
        mTvWordName.setText(word);
    }

    @Override
    public void showWordMeaning(List<Word.WordMeaning> wordMeaningList) {
        UIUtil.showMeaningList(mTvWordMeaning, wordMeaningList, "\n");
    }


    @Override
    public void showInputMeaning(String inputMeaning) {
        mTvWordMeaning.setText(inputMeaning);
    }

    @Override
    public void showSimilarWords(List<Word.SimilarWord> similarWordList) {
        UIUtil.showSimilarWords(mTvSimilarWord, similarWordList, "\n");
        if (!mPresenter.isInEdit() && mTvSimilarWord.getText().toString().trim().isEmpty()) {
            mTvSimilarWord.setVisibility(View.GONE);
        } else {
            mTvSimilarWord.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void showInputSimilarWords(String inputSimilarWords) {
        mTvSimilarWord.setVisibility(View.VISIBLE);
        mTvSimilarWord.setText(inputSimilarWords);
    }

    @Override
    public void showInputRememberWay(String rememberWay) {
        if (!mPresenter.isInEdit() && (rememberWay ==null || rememberWay.isEmpty())) {
            mTvRememberWay.setVisibility(View.GONE);
        } else {
            mTvRememberWay.setVisibility(View.VISIBLE);
            mTvRememberWay.setText(rememberWay);
        }
    }

    public void showWordGroupList(List<Word.SimilarWord> groupList) {
        UIUtil.showSimilarWords(mtvWordGroup, groupList, "\n");
        if (!mPresenter.isInEdit() && mtvWordGroup.getText().toString().trim().isEmpty()) {
            mtvWordGroup.setVisibility(View.GONE);
        } else {
            mtvWordGroup.setVisibility(View.VISIBLE);
        }
    }


    @Override
    public void showInputWordGroup(String wordGroup) {
        mtvWordGroup.setVisibility(View.VISIBLE);
        mtvWordGroup.setText(wordGroup);
    }

    @Override
    public void showStrangeDegree(int strangeDegree) {
        mTvStrangeDegree.setText(strangeDegree + "");
    }

    @Override
    public void showLastRememberTime(long lastRememberTime) {
        mTvLastRememberTime.setText(Util.long2DateDefult(lastRememberTime));
    }

    @Override
    protected void onStop() {
        mPresenter.setLastRememberTime();
        super.onStop();
    }


    @Override
    public void setPresenter(WordDetailContract.Presenter presenter) {

    }

    @Override
    public void showSaveFailed(int state) {
        String msg = null;
        switch (state) {
            case AppConstant.WORD_FORMAT_ERROR:
                msg = getString(R.string.word_format_erorr);
                break;
            case AppConstant.REPETITIVE_WORD:
                msg = getString(R.string.word_repetitive);
                break;
            case AppConstant.WORD_IS_NULL:
                msg = getString(R.string.word_name_is_null);
                break;
        }
        Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
    }

    @Override
    public void showInvalidName(int state) {
        String msg = null;
        switch (state) {
            case AppConstant.WORD_FORMAT_ERROR:
                msg = getString(R.string.word_invalid_format_erorr);
                break;
            case AppConstant.REPETITIVE_WORD:
                msg = getString(R.string.word_invalid_repetitive);
                break;
            case AppConstant.WORD_IS_NULL:
                msg = getString(R.string.word_invalid_name_is_null);
                break;
        }
        Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
    }

    @Override
    public void showAnalyzeFailed(int resultCode) {
        String msg = null;
        switch (resultCode) {
            case InputAnalyzerUtil.NO_VALID_MEANING:
                msg = "输入的词义无效";
                break;
            case InputAnalyzerUtil.IS_NULL:
                return;
            case InputAnalyzerUtil.TAG_FORMAT_ERROR:
                return;
        }
        Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
    }

    public static Intent getStartIntent(Context context) {
        Intent intent = new Intent(context, WordDetailActivity.class);
        return intent;
    }

    @Override
    public void onBackPressed() {
        if (mPresenter.isInEdit()) {
             mPresenter.switchEdit();
        } else {
            super.onBackPressed();
        }
    }

}
