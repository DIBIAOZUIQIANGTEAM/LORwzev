package com.wsns.lor.adapter;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.wsns.lor.R;

/**
 * Created by Administrator on 2017/3/19.
 */

public class RepairTypeAdapter extends RecyclerView.Adapter<RepairTypeAdapter.ViewHolder> {

    private Context mContext;
    private String[] mText;
    private int[] mDrawableId;
    private int pressedItem = 7;

    public RepairTypeAdapter(Context context, int[] drawableId, String[] text) {
        mContext = context;
        mDrawableId = drawableId;
        mText = text;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(mContext).inflate(R.layout.recyclerview_item_repair_type, null);
        return new ViewHolder(v);
    }

    public int getPressedItem(){
        return  pressedItem;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        holder.tv.setText(mText[position]);
        holder.iv.setImageResource(mDrawableId[position]);
        if (pressedItem == position) {
            holder.rlBg.setBackgroundColor(Color.parseColor("#3c9aff"));
        } else {
            holder.rlBg.setBackgroundColor(Color.parseColor("#dddddd"));
            ;
        }
        holder.rlBg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pressedItem = position;
                notifyDataSetChanged();
                refresh();
            }
        });
    }
    public static interface OnRefreshListener{
        void onRefresh();
    }
    OnRefreshListener onRefreshListener;
    public void setOnRefreshListener(OnRefreshListener onRefreshListener){
        this.onRefreshListener = onRefreshListener;
    }
    public void refresh(){
        if(onRefreshListener!=null){
            onRefreshListener.onRefresh();
        }
    }


    @Override
    public int getItemCount() {
        return mText.length;
    }

    //自定义的ViewHolder,减少findViewById调用次数
    class ViewHolder extends RecyclerView.ViewHolder {
        FrameLayout rlBg;
        TextView tv;
        ImageView iv;

        //在布局中找到所含有的UI组件
        public ViewHolder(View itemView) {
            super(itemView);
            rlBg = (FrameLayout) itemView.findViewById(R.id.rl_bg);
            tv = (TextView) itemView.findViewById(R.id.tv);
            iv = (ImageView) itemView.findViewById(R.id.iv);
        }
    }
}
