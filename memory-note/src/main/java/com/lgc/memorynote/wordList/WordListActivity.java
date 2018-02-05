package com.lgc.memorynote.wordList;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.util.Pair;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.TextView;

import com.lgc.memorynote.R;
import com.lgc.memorynote.base.Logcat;
import com.lgc.memorynote.base.Util;
import com.lgc.memorynote.wordDetail.Word;
import com.lgc.memorynote.wordDetail.WordDetailActivity;

import java.util.ArrayList;
import java.util.List;

public class WordListActivity extends AppCompatActivity implements WordListContract.View{

    private RecyclerView mWordListView;
    private WordListAdapter mWordListAdapter;
    private LinearLayoutManager linearLayoutManager;
    private TextView mTvCommand;
    private TextView mTvInputCommand;
    private WordListPresenter mPresenter;
    private Util.RepetitiveEventFilter searchFilter = new Util.RepetitiveEventFilter();

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
        mTvInputCommand.setHint(SortUtil.getHingString());
        mTvInputCommand.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH ||
                        (event != null && event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) {
                    onClickSearch(mTvCommand.getText().toString());
                    return true;
                }
                return false;
            }
        });
        findViewById(R.id.btn_expand_command_list).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mTvCommand.getMaxLines() == 1) {
                    mTvCommand.setMaxLines(100);
                } else {
                    mTvCommand.setMaxLines(1);
                }
                updateCommandText(Command.commandList, mPresenter.getChoseCommand());
                mTvCommand.setVisibility(View.GONE);
                mTvCommand.setVisibility(View.VISIBLE);
                mTvCommand.requestLayout();
            }
        });

        findViewById(R.id.btn_search).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!searchFilter.isRepetitive(1000)) {
                    onClickSearch(mTvInputCommand.getText().toString());
                } else {
                    Logcat.d("repetitive click");
                }
            }
        });

        // about recyclerView
        mWordListView = (RecyclerView) findViewById(R.id.lv_word_list);
        linearLayoutManager = new LinearLayoutManager(this,LinearLayoutManager.VERTICAL, false);
        mWordListView.setLayoutManager(linearLayoutManager);
    }

    private void onClickSearch(String search) {
        mPresenter.reorderWordList(search);
    }


    private void initData() {
        mWordListAdapter = new WordListAdapter(this);
        mWordListAdapter.setItemClickListener(new WordListAdapter.ItemClickListener() {
            @Override
            public void onItemClick(View v, WordListAdapter.ItemHolder itemHolder) {
                int position = itemHolder.getAdapterPosition();
                switch (v.getId()) {
                    case R.id.lv_item_layout:
                         startActivityWordDetail(mPresenter.getWordName(position));
                         break;
                    case R.id.lv_item_word_add_strange:
                        mPresenter.addStrange(position);
                        mWordListAdapter.notifyItemChanged(position);
                        break;
                    case R.id.lv_item_word_reduce_strange:
                        mPresenter.reduceStrange(position);
                        mWordListAdapter.notifyItemChanged(position);
                }
            }
        });
        mPresenter.start();
        mWordListView.setAdapter(mWordListAdapter);
        mWordListView.addItemDecoration(new RecycleViewDivider(this, LinearLayoutManager.HORIZONTAL));
    }

    private void startActivityWordDetail(String wordName) {
        Intent intent = WordDetailActivity.getStartIntent(WordListActivity.this);
        intent.putExtra(WordDetailActivity.INTENT_EXTRA_IS_ADD, false);
        intent.putExtra(WordDetailActivity.INTENT_EXTRA_WORD_NAME, wordName);
        startActivity(intent);
    }

    @Override
    public void updateCommandText(List<String> commandList, List<String> chosenList) {
        // 转话层UI上的字符串
        List<Pair<String, String>> UICommandList = new ArrayList<>();
        int maxNumber = commandList.size();
        if (mTvCommand.getMaxLines() <= 1) {
            maxNumber = Math.min(maxNumber, 4);
        }

        for (int i = 0; i < maxNumber; i++) {
            String one  = commandList.get(i);
            UICommandList.add(
                    new Pair<>(Command.UICommandMap.get(one), one));
        }

        // 构造出所有命令的字符串
        StringBuilder sb = new StringBuilder();
        sb.append("  ");
        for (int i = 0; i < UICommandList.size(); i++) {
            sb.append(UICommandList.get(i).first);
            if (i < UICommandList.size() -1) {
                if (i % 4 == 3) {
                    sb.append("\n  ");
                } else {
                    sb.append("     ");
                }
            }
        }
        String commandString = sb.toString();

        // 用Spanable装饰
        SpannableString ss = new SpannableString(commandString);

        for (int i = 0; i < maxNumber; i++) {
            Pair<String, String> pair = UICommandList.get(i);
            String UICommand = pair.first;
            String command = pair.second;
            // i 对应于commanList中的
            int startId = commandString.indexOf(UICommand);
            int endId = startId + UICommand.length();
            if (startId >= 0 && endId <= commandString.length()) {
                ss.setSpan(new clickSpan(command, chosenList.contains(command)),
                        startId, endId, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
            }
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

    @Override
    protected void onRestart() {
        onClickSearch(null);
        super.onRestart();
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

    public class clickSpan extends ClickableSpan {
        private final String mCommand;
        private final boolean mIsChosen;

        public clickSpan(String command, boolean isChosen) {
            this.mCommand = command;
            mIsChosen = isChosen;
        }

        @Override
        public void onClick(View widget) {
            mPresenter.switchOneCommand(mCommand);
        }

        @Override
        public void updateDrawState(TextPaint ds) {
            ds.setUnderlineText(false); //去掉下划线
            if (mIsChosen) {
                ds.setColor(getColor(R.color.command_chosen));
            } else {
                ds.setColor(getColor(R.color.command_not_choosed));
            }
        }
    }
}
