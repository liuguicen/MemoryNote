package com.lgc.memorynote.wordDetail;


import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.MotionEvent;
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
import com.lgc.memorynote.wordList.WordListActivity;

import java.util.List;

public class WordDetailActivity extends AppCompatActivity implements WordDetailContract.View, View.OnTouchListener {

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
        bindView();
        initView();
        initListener();
        mPresenter.initAndShowData(getIntent()); // 要在View初始化之后调用
    }


    private void bindView() {
        mTvWordName = (EditText) findViewById(R.id.et_word_detail_name);
        mTvWordMeaning = (EditText) findViewById(R.id.et_word_detail_meaning);
        mTvSimilarWord = (EditText) findViewById(R.id.et_similar_word);
        mtvWordGroup = (EditText) findViewById(R.id.et_word_detail_group);
        mTvRememberWay = (EditText) findViewById(R.id.et_word_remember_way);
        mTvStrangeDegree = (TextView) findViewById(R.id.value_strange_degree);
        mTvLastRememberTime = (TextView) findViewById(R.id.last_remember_time);
        mBtnEdit = (TextView) findViewById(R.id.btn_word_detail_edit);
        mDeleteView = (TextView) findViewById(R.id.word_detail_delete);
    }

    private void initView() {
      /*  mTvWordName.setOnLongClickListener(this);
        mTvWordMeaning.setOnLongClickListener(this);
        mTvWordMeaning.setOnClickListener(this);
        mTvSimilarWord.setOnLongClickListener(this);
        mtvWordGroup.setOnLongClickListener(this);
        mTvRememberWay.setOnLongClickListener(this);*/

        mTvWordName.setTag(mTvWordName.getBackground());
        mTvWordMeaning.setTag(mTvWordName.getBackground());
        mTvSimilarWord.setTag(mTvSimilarWord.getBackground());
        mTvRememberWay.setTag(mTvRememberWay.getBackground());
        mtvWordGroup.setTag(mtvWordGroup.getBackground());
        lastInputType = mTvRememberWay.getInputType();
        mBtnEdit.setOnClickListener(this);
        mDeleteView.setOnClickListener(this);

        mTvWordName.setOnClickListener(this);
        mTvWordMeaning.setOnClickListener(this);
        mTvWordMeaning.setOnTouchListener(this);

        findViewById(R.id.add_strange_degree).setOnClickListener(this);
        findViewById(R.id.reduce_strange_degree).setOnClickListener(this);
        findViewById(R.id.btn_sync_similar).setOnClickListener(this);
        findViewById(R.id.btn_sync_group).setOnClickListener(this);
        findViewById(R.id.btn_sync_root_affix).setOnClickListener(this);
        findViewById(R.id.btn_save_assistant).setOnClickListener(this);
        findViewById(R.id.btn_word_detail_next).setOnClickListener(this);
    }


    private void initListener() {
        mTvWordName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                mPresenter.setClickName();
                mTvWordMeaning.setHint("");
            }
        });

        mTvWordMeaning.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                mPresenter.checkWordRepeat();
            }
        });
    }

    @Override
    public void setInputAssistant() {
        final String CIGEN = "cigen", QIANZUI = "qianzui", HOUZUI = "houzui", DIPIN = "dipin";
        String type = "dipin";
        switch (type) {
            case CIGEN:
                mTvWordName.setText("--");
                mTvWordName.setSelection("-".length());
                mTvWordMeaning.setText("@词根 ");
                mPresenter.setStrangeDegree(5);
                break;
            case QIANZUI:
                mTvWordName.setText("-");
                mTvWordMeaning.setText("@前缀 ");
                mPresenter.setStrangeDegree(5);
                break;
            case HOUZUI:
                mTvWordName.setText("-");
                mTvWordMeaning.setText("@后缀 ");
                mPresenter.setStrangeDegree(5);
                break;
            case DIPIN:
                mTvWordMeaning.setText("@低 ");
                mPresenter.setStrangeDegree(7);
                break;
        }

    }

    @Override
    public void onClick(View v) {
        // 排除重复的点击
        if (Util.RepetitiveEventFilter.isRepetitive(500))
            return;
        switch (v.getId()) {
            case R.id.et_word_detail_name:
                mPresenter.setClickName();
                break;
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
            case R.id.btn_sync_root_affix:
                mPresenter.syncRootAffix();
                break;
            case R.id.btn_save_assistant:
                mPresenter.saveInputAssistant();
                break;
            case R.id.btn_word_detail_next:
                if (mPresenter.isInEdit() && !TextUtils.isEmpty(getInputWordName().trim())) {
                    mPresenter.switchEdit();
                }
                startSelf();
                this.finish();
                break;
        }
    }

    private void startSelf() {
        Intent intent = WordDetailActivity.getStartIntent(this);
        intent.putExtra(WordDetailActivity.INTENT_EXTRA_IS_ADD, true);
        startActivity(intent);
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        switch (v.getId()) {
            case R.id.et_word_detail_meaning:
                mPresenter.checkWordRepeat();
                break;
        }
        return false;
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
            Util.showKeyboard(mTvWordName, 300);
        } else {
            mBtnEdit.setBackgroundResource(R.drawable.btn_bg_edit);
            mBtnEdit.setText(getString(R.string.edit));
            mDeleteView.setVisibility(View.VISIBLE);
            Util.hideKeyboard(mTvWordName);
        }
    }

    private void switchTvEditStyle(TextView tv, boolean isInEdit) {
        if (isInEdit) {
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
    public void showWordMeaning(Word word) {
        UIUtil.showMeaningString(mTvWordMeaning, word.getInputMeaning());
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
        if (!mPresenter.isInEdit() && (rememberWay == null || rememberWay.isEmpty())) {
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
            case AppConstant.REPETITIVE_WORD_CHECKED:
                msg = getString(R.string.word_repetitive_can_not_save);
                mTvWordMeaning.setHint(getString(R.string.word_repetitive_can_not_save));
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

    @Override
    public void showRememberWay(String rememberWay) {
        mTvRememberWay.setText(rememberWay);
    }

    public static Intent getStartIntent(Context context) {
        Intent intent = new Intent(context, WordDetailActivity.class);
        return intent;
    }

    @Override
    public void onBackPressed() {
        if (mPresenter.isInEdit() && !TextUtils.isEmpty(getInputWordName().trim())) {
            mPresenter.switchEdit();
        } else {
            Intent resIntent = new Intent();
            resIntent.putExtra(WordListActivity.IS_REFRESH_LIST, mPresenter.isRefreshList());
            setResult(WordListActivity.WORD_DETAIL_ACTIVITY_ONE, resIntent);
            super.onBackPressed();
        }
    }
}
