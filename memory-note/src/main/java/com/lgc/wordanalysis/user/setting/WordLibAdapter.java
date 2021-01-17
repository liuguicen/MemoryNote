package com.lgc.wordanalysis.user.setting;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.lgc.baselibrary.utils.RecyclerViewItemClickListener;
import com.lgc.wordanalysis.R;
import com.lgc.wordanalysis.data.WordLibsUtil;

import java.util.ArrayList;
import java.util.List;

public class WordLibAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private final Context mContext;
    RecyclerViewItemClickListener itemClickListener;

    public WordLibAdapter(Context context) {
        mContext = context;
    }

    public void setItemClickListener(RecyclerViewItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View layout = LayoutInflater.from(mContext).inflate(R.layout.item_word_lib_list, viewGroup, false);
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
        String fileName = WordLibsUtil.wordLibNameList[position];
        String name = fileName.substring(0, fileName.lastIndexOf('.'));
        ((ItemHolder) viewHolder).name.setText(name);
    }

    @Override
    public int getItemCount() {
        return WordLibsUtil.wordLibNameList.length;
    }

    private class ItemHolder extends RecyclerView.ViewHolder {
        TextView name;

        ItemHolder(View layout) {
            super(layout);
            name = layout.findViewById(R.id.tv_word_lib_item_name);
        }
    }
}
