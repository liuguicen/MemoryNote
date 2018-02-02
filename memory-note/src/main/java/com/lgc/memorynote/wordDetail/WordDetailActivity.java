package com.lgc.memorynote.wordDetail;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.lgc.memorynote.R;
import com.lgc.memorynote.base.Util;

import java.util.List;

public class WordDetailActivity extends AppCompatActivity implements WordDetailContract.View{

    public static final String INTENT_EXTRA_WORD_NAME = "intent_extra_word_detail_word_name";
    public static final String INTENT_EXTRA_IS_ADD = "intent_extra_word_detail_is_add";

    private WordDetailContract.Presenter mPresenter;
    private EditText mTvWordName;
    private EditText mTvWordMeaning;
    private EditText mTvSimilarWord;
    private TextView mTvStrangeDegree;
    private TextView mTvLastRememberTime;
    private Button mBtnEdit;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_word_detail);
        mPresenter = new WordDetailPresenter(
                new WordDetailDataSourceImpl(this.getApplicationContext()), this);
        initView();
        mPresenter.initAndShowData(getIntent()); // 要在View初始化之后调用
    }

    private void initView() {
        mTvWordName          = (EditText) findViewById(R.id.word_detail_word);
        mTvWordMeaning       = (EditText) findViewById(R.id.word_detail_meaning);
        mTvSimilarWord       = (EditText) findViewById(R.id.similar_form_word);
        mTvStrangeDegree     = (TextView) findViewById(R.id.value_strange_degree);
        mTvLastRememberTime  = (TextView) findViewById(R.id.last_remember_time);
        mBtnEdit             = (Button) findViewById(R.id.btn_word_detail_edit);
        mBtnEdit.setOnClickListener(this);
        findViewById(R.id.add_strange_degree).setOnClickListener(this);
        findViewById(R.id.reduce_strange_degree).setOnClickListener(this);

        mTvWordName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.add_strange_degree:
                mPresenter.addStrangeDegree();
                break;
            case R.id.reduce_strange_degree:
                mPresenter.reduceStrangeDegree();
                break;
            case R.id.btn_word_detail_edit:
                mPresenter.switchEdit();
        }
    }

    @Override
    public void switchEdit(boolean isInEdit) {
        mTvWordName.setFocusable(isInEdit);
        mTvWordMeaning.setFocusable(isInEdit);
        mTvSimilarWord.setFocusable(isInEdit);
        if (isInEdit) {
            mBtnEdit.setText(getString(R.string.edit_finish));
        } else {
            mBtnEdit.setText(getString(R.string.edit));
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
    public void showWord(String word) {
        mTvWordName.setText(word);
    }

    @Override
    public void showWordMeaning(List<Word.WordMeaning> wordMeaningList) {

    }

    @Override
    public void showInputMeaning(String inputMeaning) {

    }

    @Override
    public void showSimilarWords(List<String> similarWordList) {

    }

    @Override
    public void showInputSimilarWords(String inputSimilarWords) {

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

    public static Intent getStartIntent(Context context) {
        Intent intent = new Intent(context, WordDetailActivity.class);
        return intent;
    }
}
