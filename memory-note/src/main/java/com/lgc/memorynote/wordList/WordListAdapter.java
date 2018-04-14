package com.lgc.memorynote.wordList;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.lgc.memorynote.R;
import com.lgc.memorynote.base.UIUtil;
import com.lgc.memorynote.data.Word;

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
    private boolean mIsHideWord = false;
    private boolean mIsHideMeaning = false;

    public interface ItemClickListener {
        void onItemClick(View v, ItemHolder itemHolder);
    }

    public interface LongClickListener {
        boolean onItemLongClick(View v, ItemHolder itemHolder);
    }

    private ItemClickListener itemClickListener;
    private LongClickListener longClickListener;

    public void setItemClickListener(ItemClickListener clickListenner) {
        this.itemClickListener = clickListenner;
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

    public void setHideMeaning(boolean isHide) {
        mIsHideMeaning = isHide;
        notifyDataSetChanged();
    }

    public void setHideWord(boolean isHide) {
        mIsHideWord = isHide;
        notifyDataSetChanged();
    }

    public void setWordList(List<Word> WordList) {
        this.mWordList = WordList;
        notifyDataSetChanged();
    }

    public Word getItemDate(int position) {
        if (mWordList != null && position >= 0 && position < mWordList.size()) {
            return mWordList.get(position);
        }
        return null;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View layout = LayoutInflater.from(mContext).inflate(R.layout.lv_item_word, parent, false);
        final ItemHolder itemHolder = new ItemHolder(layout);
        layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                itemClickListener.onItemClick(v, itemHolder);
            }
        });
        layout.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (longClickListener != null) {
                    return longClickListener.onItemLongClick(v, itemHolder);
                }
                return false;
            }
        });
        return itemHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ItemHolder itemHolder = ((ItemHolder) holder);
        Word word = mWordList.get(position);
        if (!mIsHideWord) {
            itemHolder.tvName.setText(word.getName());
        } else {
            itemHolder.tvName.setText("_________");
        }
        itemHolder.tvStrange.setText(word.getStrangeDegree() + "");
        if (word.getMeaningList() == null || word.getMeaningList().size() == 0) {
            itemHolder.tvMeaning.setVisibility(View.GONE);
        } else {
            itemHolder.tvMeaning.setVisibility(View.VISIBLE);
            if (!mIsHideMeaning) {
                UIUtil.showMeaningString(itemHolder.tvMeaning, word.getInputMeaning(), " ;   ");
            } else {
                itemHolder.tvMeaning.setText("_________");
            }
        }

        UIUtil.showRelated(itemHolder.tvRelated, word);
    }

    @Override
    public int getItemCount() {
        int count = 0;
        if (mWordList != null) {
            count = mWordList.size();
        }
        return count;
    }

    public class ItemHolder extends RecyclerView.ViewHolder {
        private final TextView tvRelated;
        private final TextView tvMeaning;
        private final TextView tvName;
        private final TextView tvStrange;
        private final TextView tvAddStrange;
        private final TextView tvReduceStrange;

        public ItemHolder(View itemView) {
            super(itemView);
            tvName = (TextView) itemView.findViewById(R.id.lv_item_word_name);
            tvMeaning = (TextView) itemView.findViewById(R.id.lv_item_word_meaning);
            tvRelated = (TextView) itemView.findViewById(R.id.lv_item_word_related);

            tvStrange = (TextView) itemView.findViewById(R.id.lv_item_strange_value);
            tvAddStrange = (TextView)itemView.findViewById(R.id.lv_item_word_add_strange);
            tvReduceStrange = (TextView)itemView.findViewById(R.id.lv_item_word_reduce_strange);

            tvReduceStrange.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    itemClickListener.onItemClick(v, ItemHolder.this);
                }
            });
            tvAddStrange.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    itemClickListener.onItemClick(v, ItemHolder.this);
                }
            });
            tvStrange.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                }
            });
        }
    }
}
