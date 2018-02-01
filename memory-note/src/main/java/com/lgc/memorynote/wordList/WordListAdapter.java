package com.lgc.memorynote.wordList;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.lgc.memorynote.R;
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

    public List<Word> getImagUrls() {
        return WordList;
    }

    private List<Word> WordList;

    private static int ITEM = 1;

    public void setWordList(List<Word> WordList) {
        this.WordList = WordList;
    }

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

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LinearLayout layout = createItemLayout(parent);
            final ItemHolder itemHolder = new ItemHolder(layout);
            itemHolder.iv = createItemImage(layout);
            itemHolder.iv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    clickListener.onItemClick(itemHolder);
                }
            });
            itemHolder.iv.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    return longClickListener.onItemLongClick(itemHolder);
                }
            });
            return itemHolder;
        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (position >= WordList.size()) return;
        holder.itemView.setTag(WordList.get(position));
        myBindViewHolder(holder, position);
    }

    //b
    void myBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (position >= WordList.size()) return;
        //如果是分组标题
        ItemHolder itemHolder;
        itemHolder = (ItemHolder) holder;
        // 这个地方主义，imageLoader启动了一个新线程获取图片到cacheImage里面，新线程运行，本线程也会运行，
        // 因为新线程耗时，所以本线程已经执行到后面了，先加载了一张预设的图片，然后这个新线程会使用handler类更新UI线程， 妙啊！
        itemHolder.iv.setTag(position);
        String path = WordList.get(position);
        Bitmap cachedImage = imageLoader.getword(path);
        if (cachedImage != null && itemHolder.iv != null) {
            itemHolder.iv.setImageBitmap(cachedImage);
        } else if (itemHolder.iv != null) {
            itemHolder.iv.setImageResource(R.mipmap.instead_icon);
        }
    }


    @Override
    public int getItemCount() {
        return WordList.size();
    }

    private ImageView createItemImage(LinearLayout linearLayout) {
        ImageView imageView = new ImageView(mContext);
        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        LinearLayout.LayoutParams mLayoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, GlobalData.screenWidth / 3 - 4);
        linearLayout.addView(imageView, mLayoutParams);
        return imageView;
    }

    public static class ItemHolder extends RecyclerView.ViewHolder {
        ImageView iv;

        public ItemHolder(View itemView) {
            super(itemView);
        }
    }
}
