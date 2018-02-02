package com.lgc.memorynote.wordList;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.lgc.memorynote.R;
import com.lgc.memorynote.wordDetail.MeaningUtil;
import com.lgc.memorynote.wordDetail.Word;


import java.util.List;

/**
 * Created by liuguicen on 2016/8/31.
 */
class WordListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int STATIC_SHOW_NUMBER = 25;

    private final Context mContext;
    private final LayoutInflater layoutInflater;
    boolean isScrollWidthoutTouch;

    private List<Word> mWordList;

    private static int ITEM = 1;

    public interface ItemClickListener {
        void onItemClick(ItemHolder itemHolder);
    }

    public interface LongClickListener {
        boolean onItemLongClick(ItemHolder itemHolder);
    }

    ItemClickListener clickListener;
    LongClickListener longClickListener;

    public void setClickListener(ItemClickListener clickListenner) {
        this.clickListener = clickListenner;
    }

    public void setLongClickListener(LongClickListener longClickListenner) {
        this.longClickListener = longClickListenner;
    }

    public WordListAdapter(Context context) {
        mContext = context;
        layoutInflater = LayoutInflater.from(context);
        isScrollWidthoutTouch = false;
    }

    public List<Word> getWordList() {
        return mWordList;
    }

    public void setWordList(List<Word> WordList) {
        this.mWordList = WordList;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View layout = LayoutInflater.from(mContext).inflate(R.layout.lv_item_word, parent, false);
        final ItemHolder itemHolder = new ItemHolder(layout);
        layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickListener.onItemClick(itemHolder);
            }
        });
        layout.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                return longClickListener.onItemLongClick(itemHolder);
            }
        });
        return itemHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ((ItemHolder) holder).ivName.setText(mWordList.get(position).getWord());
        MeaningUtil.showMeaningList(((ItemHolder)holder).ivMeaning, mWordList.get(position).getMeaningList());
    }

    @Override
    public int getItemCount() {
        return mWordList.size();
    }

    public static class ItemHolder extends RecyclerView.ViewHolder {
        TextView ivMeaning;
        TextView ivName;

        public ItemHolder(View itemView) {
            super(itemView);
            ivName = (TextView) itemView.findViewById(R.id.lv_item_word_name);
            ivMeaning = (TextView) itemView.findViewById(R.id.lv_item_word_meaning);
        }
    }
}
