package com.lgc.wordanalysis.user.setting;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.lgc.baselibrary.utils.RecyclerViewItemClickListener;
import com.lgc.wordanalysis.R;

import java.util.ArrayList;
import java.util.List;

public class WordLibAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    List<String> wordLibList = new ArrayList<>();
    private final Context mContext;
    RecyclerViewItemClickListener itemClickListener;

    public WordLibAdapter(Context context) {
        mContext = context;
        wordLibList.add("高中词汇");
        wordLibList.add("考研词汇");
    }

    public void setItemClickListener(RecyclerViewItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View  layout = LayoutInflater.from(mContext).inflate(R.layout.item_word_lib_list, viewGroup, false);
        ItemHolder itemHolder = new ItemHolder(layout);
        View importBtn = layout.findViewById(R.id.tv_word_lib_item_import);
        importBtn.setOnClickListener(v -> {
            if (itemClickListener != null) {
                itemClickListener.onItemClick(v, itemHolder);
            }
        });
        return itemHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int position) {
        ((ItemHolder) viewHolder).name.setText(wordLibList.get(position));
    }

    @Override
    public int getItemCount() {
        return wordLibList.size();
    }

    private class ItemHolder extends RecyclerView.ViewHolder {
        TextView name;
        ItemHolder(View layout) {
            super(layout);
            name = layout.findViewById(R.id.tv_word_lib_item_name);
        }
    }
}
