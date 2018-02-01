package com.lgc.memorynote.wordList;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.lgc.memorynote.R;

import org.w3c.dom.Text;

public class WordListActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_word_list);
        TextView tvOrderWay = ((TextView) findViewById(R.id.tv_order_way));
        tvOrderWay.setHint(OrderUtil.getHingString());
    }


}
