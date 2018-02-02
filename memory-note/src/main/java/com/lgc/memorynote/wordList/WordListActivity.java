package com.lgc.memorynote.wordList;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.lgc.memorynote.R;
import com.lgc.memorynote.wordDetail.WordDetailActivity;

public class WordListActivity extends AppCompatActivity {

    private RecyclerView mWordListView;
    private WordListAdapter mWordListAdapter;
    private LinearLayoutManager linearLayoutManager;
    private TextView mTvCommand;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_word_list);
        findViewById(R.id.add_word).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent  = WordDetailActivity.getStartIntent(WordListActivity.this);
                intent.putExtra(WordDetailActivity.INTENT_EXTRA_IS_ADD, true);
                startActivity(intent);
            }
        });
        TextView tvOrderWay = ((TextView) findViewById(R.id.tv_command_input));
        mWordListView = (RecyclerView) findViewById(R.id.lv_word_list);
        mWordListAdapter = new WordListAdapter(this);
        linearLayoutManager = new LinearLayoutManager(this,LinearLayoutManager.VERTICAL, false);
        mWordListView.setLayoutManager(linearLayoutManager);
        mWordListView.setAdapter(mWordListAdapter);
        mWordListAdapter.setClickListener(new WordListAdapter.ItemClickListener() {
            @Override
            public void onItemClick(WordListAdapter.ItemHolder itemHolder) {

            }
        });
        tvOrderWay.setHint(OrderUtil.getHingString());
        mTvCommand = (TextView)findViewById(R.id.tv_command);
    }
}
