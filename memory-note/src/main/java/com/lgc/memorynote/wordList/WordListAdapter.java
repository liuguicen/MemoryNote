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

import com.lgc.wordnote.R;

import java.util.List;

/**
 * Created by liuguicen on 2016/8/31.
 */
class WordListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int STATIC_SHOW_NUMBER = 25;

    private final Context mContext;
    private final LayoutInflater layoutInflater;
    private final AsyncWordLoader imageLoader;
    boolean isScrollWidthoutTouch;

    public List<String> getImagUrls() {
        return imageUrls;
    }

    private List<String> imageUrls;
    private final UsuPathManger usuallyProcessor;

    private static int ITEM = 1;
    static int GROUP_HEADER = 2;


    AsyncWordLoader.ImageCallback imageCallback = new AsyncWordLoader.ImageCallback() {
        @Override
        public void imageLoaded(Bitmap imageDrawable, ImageView image, int position, String imageUrl) {

            if (image != null && position == (int) image.getTag()) {
                if (imageDrawable == null) {
                    image.setImageResource(R.mipmap.decode_failed_icon);
                } else
                    image.setImageBitmap(imageDrawable);
            }

        }
    };


    public void setImageUrls(List<String> imageUrls) {
        this.imageUrls = imageUrls;
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

    public WordListAdapter(Context context, UsuPathManger usuallyProcessor) {
        mContext = context;
        layoutInflater = LayoutInflater.from(context);
        imageLoader = GlobalData.imageLoader3;
        this.usuallyProcessor = usuallyProcessor;
        isScrollWidthoutTouch = false;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == ITEM) {
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
        }
        if (viewType == GROUP_HEADER) {
            View view = layoutInflater.inflate(R.layout.pic_gird_group_header, parent, false);
            return new HeaderHolder(view);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (position >= imageUrls.size()) return;
        holder.itemView.setTag(imageUrls.get(position));
        myBindViewHolder(holder, position);
    }

    //b
    void myBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (position >= imageUrls.size()) return;
        //如果是分组标题
        ItemHolder itemHolder;
        if (usuallyProcessor.isUsuPic(imageUrls)) {
            if (holder instanceof HeaderHolder) {
                int headerType = getHeaderType(position);
                if (headerType == -1) headerType = searchHeaderType(position);
                if (headerType == 0) {//// TODO: 2017/3/1 0001 这里有个挺麻烦的地方，header的View和adapter中的数据对不上，
                    //   可能原因是RecyclerView获取到了类型到显示期间，其他线程让Adapter的数据更新了，但是RecyclerView的没有更新，导致View和数据错位。
                    // 目前只使用了简单的判断holder类型的方法，但是会出现不显示header的问题
                    //  解决方法之一（源码异常说明）是将所有的数据修改操作交到UI线程，扫描等才用其它线程，然后调用notifyDataSetChanged()
                    //(目前改掉一个file图与常用图切换问题，可能是主原因）。
                    ((HeaderHolder) holder).tv.setText(R.string.latest_use);
                    return;
                } else if (headerType == 1) {
                    ((HeaderHolder) holder).tv.setText(R.string.recent_pic);
                    return;
                } else if (headerType == 2) {
                    ((HeaderHolder) holder).tv.setText(R.string.prefer_pic);
                    return;
                } else {
                    ((HeaderHolder) holder).tv.setText(" ");
                }
                return;
            } else {
                itemHolder = (ItemHolder) holder;
                if (getHeaderType(position) != -1) {//如果数据错位，item位置的数据时header的
                    itemHolder.iv.setImageResource(R.mipmap.instead_icon);
                    return;
                }
            }
        } else {
            itemHolder = (ItemHolder) holder;
        }
        // 这个地方主义，imageLoader启动了一个新线程获取图片到cacheImage里面，新线程运行，本线程也会运行，
        // 因为新线程耗时，所以本线程已经执行到后面了，先加载了一张预设的图片，然后这个新线程会使用handler类更新UI线程， 妙啊！
        itemHolder.iv.setTag(position);
        String path = imageUrls.get(position);
        Bitmap cachedImage = imageLoader.getword(path);
        if (cachedImage == null && (!isScrollWidthoutTouch || position <= STATIC_SHOW_NUMBER)) {
            imageLoader.loadBitmap(path, itemHolder.iv, position, imageCallback, GlobalData.screenWidth / 3);
        }
        if (cachedImage != null && itemHolder.iv != null) {
            itemHolder.iv.setImageBitmap(cachedImage);
        } else if (itemHolder.iv != null) {
            itemHolder.iv.setImageResource(R.mipmap.instead_icon);
        }
    }


    @Override
    public int getItemCount() {
        if (usuallyProcessor.isUsuPic(imageUrls))
            return imageUrls.size();
        return imageUrls.size();
    }

    @Override
    public int getItemViewType(int position) {
        if (usuallyProcessor.isUsuPic(imageUrls) && getHeaderType(position) >= 0) {
            return GROUP_HEADER;
        }
        return ITEM;
    }

    /**
     * 获取分组的值
     */
    private int getHeaderType(int position) {
        String path;
        if (position >= imageUrls.size()) return -1;
        path = imageUrls.get(position);
        if (path.equals(UsuPathManger.USED_FLAG))//存在使用过的图片
            return 0;
        if (path.equals(UsuPathManger.RECENT_FLAG))
            return 1;
        if (path.equals(UsuPathManger.PREFER_FLAG))
            return 2;
        return -1;
    }

    /**
     * 临时方法，pictureListView的View数据和Adapter数据不同步的问题
     *
     * @param position 搜索的中间起始位置
     */
    private int searchHeaderType(int position) {
        int resId;
        for (int i = 1; position - i >= 0 || position + i < imageUrls.size(); i++) {
            if (position - i >= 0)
                if ((resId = getHeaderType(position - i)) != -1) return resId;
            if (position + i < imageUrls.size())
                if ((resId = getHeaderType(position + i)) != -1) return resId;
        }
        return -1;
    }

    LinearLayout createItemLayout(ViewGroup parent) {
        // 创建LinearLayout对象
        LinearLayout mLinearLayout = new LinearLayout(mContext);

        RecyclerView.LayoutParams layoutParams = new RecyclerView.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, GlobalData.screenWidth / 3 - Util.dp2Px(12));
        layoutParams.setMargins(Util.dp2Px(1.5f), Util.dp2Px(1.5f), Util.dp2Px(1.5f),
                Util.dp2Px(1.5f));
        mLinearLayout.setLayoutParams(layoutParams);

        mLinearLayout.setOrientation(LinearLayout.VERTICAL);
        mLinearLayout.setGravity(Gravity.CENTER);
        return mLinearLayout;
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

    public static class HeaderHolder extends RecyclerView.ViewHolder {
        TextView tv;

        public HeaderHolder(View itemView) {
            super(itemView);
            itemView.setClickable(false);
            itemView.setLongClickable(false);
            tv = (TextView) itemView.findViewById(R.id.tv_pic_header_name);
        }
    }
}
