package com.lgc.memorynote.wordList;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.View;
import android.widget.TextView;

import com.lgc.memorynote.R;
import com.lgc.memorynote.base.Logcat;
import com.lgc.memorynote.wordDetail.Word;
import com.lgc.memorynote.wordDetail.WordDetailActivity;

import java.util.List;

public class WordListActivity extends AppCompatActivity implements WordListContract.View{

    private RecyclerView mWordListView;
    private WordListAdapter mWordListAdapter;
    private LinearLayoutManager linearLayoutManager;
    private TextView mTvCommand;
    private TextView mTvInputCommand;
    private WordListPresenter mPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_word_list);
        mPresenter = new WordListPresenter(this);
        //test();
        intView();
        initData();
    }

    private void test() {
        Intent intent  = WordDetailActivity.getStartIntent(WordListActivity.this);
        intent.putExtra(WordDetailActivity.INTENT_EXTRA_IS_ADD, true);
        startActivity(intent);
    }

    private void intView() {
        // base view widget
        mTvInputCommand = (TextView) findViewById(R.id.tv_input_command);
        mTvCommand = (TextView)findViewById(R.id.tv_command);
        findViewById(R.id.add_word).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent  = WordDetailActivity.getStartIntent(WordListActivity.this);
                intent.putExtra(WordDetailActivity.INTENT_EXTRA_IS_ADD, true);
                startActivity(intent);
            }
        });
        mTvInputCommand.setHint(OrderUtil.getHingString());
        findViewById(R.id.btn_expand_command_list).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mTvCommand.getMaxLines() == 1) {
                    mTvCommand.setMaxLines(100);
                } else {
                    mTvCommand.setMaxLines(1);
                }
                mTvCommand.setVisibility(View.GONE);
                mTvCommand.setVisibility(View.VISIBLE);
                mTvCommand.requestLayout();
            }
        });


        // about recyclerView
        mWordListView = (RecyclerView) findViewById(R.id.lv_word_list);
        linearLayoutManager = new LinearLayoutManager(this,LinearLayoutManager.VERTICAL, false);
        mWordListView.setLayoutManager(linearLayoutManager);
    }


    private void initData() {
        mWordListAdapter = new WordListAdapter(this);
        mWordListAdapter.setClickListener(new WordListAdapter.ItemClickListener() {
            @Override
            public void onItemClick(WordListAdapter.ItemHolder itemHolder) {
                Intent intent  = WordDetailActivity.getStartIntent(WordListActivity.this);
                intent.putExtra(WordDetailActivity.INTENT_EXTRA_IS_ADD, false);
                intent.putExtra(WordDetailActivity.INTENT_EXTRA_WORD_NAME,
                        mPresenter.getWordName(itemHolder.getAdapterPosition()));
                startActivity(intent);
            }
        });
        mPresenter.start();
        mWordListView.setAdapter(mWordListAdapter);
    }

    @Override
    public void updateCommandText(List<String> commandList) {
        Logcat.d();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < commandList.size(); i++) {
            sb.append(commandList.get(i));
            if (i < commandList.size() -1) {
                sb.append("   |   ");
            }
        }
        String commandString = sb.toString();
        SpannableString ss = new SpannableString(commandString);
        for (int i = 0; i < commandList.size(); i++) {
            String command = commandList.get(i);
            String UIComa
            // i 对应于commanList中的
            int startId = commandString.indexOf(command);
            int endId = startId + command.length();
            ss.setSpan(new clickSpan(command), startId, endId, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
        }
        mTvCommand.setMovementMethod(LinkMovementMethod.getInstance());
        mTvCommand.setText(ss);
    }

    @Override
    public void setPresenter(WordListContract.Presenter presenter) {

    }

    /**
     * 进入界面，并且获取到图片信息之后开始显示
     * @param mCurShowWordList
     */
    @Override
    public void showWordList(List<Word> mCurShowWordList) {
        if (mWordListAdapter != null) {
            mWordListAdapter.setWordList(mCurShowWordList);
        }
    }

    @Override
    public void deleteOneWord(String s) {

    }

    @Override
    public void onTogglePicList(WordListAdapter wordListAdapter) {

    }

    /**
     * picAdapter通知数据更新了,刷新图片列表视图
     *
     * @param resultList
     */
    @Override
    public void refreshWordList(List<Word> resultList) {
        mWordListAdapter.setWordList(resultList);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void showAddOneCommand(String command) {
        String lastCommand = mTvInputCommand.getText().toString().trim();
        if (!TextUtils.isEmpty(lastCommand))
            lastCommand += " ";
        mTvInputCommand.setText(lastCommand + command);
    }

    public class clickSpan extends ClickableSpan{
        private String mCommand;
        public clickSpan(String command) {
            this.mCommand = command;
        }

        @Override
        public void onClick(View widget) {
            mPresenter.addOneCommand(mCommand);
        }
    }
}
