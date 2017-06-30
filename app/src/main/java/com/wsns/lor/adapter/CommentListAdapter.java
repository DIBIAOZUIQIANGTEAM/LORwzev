package com.wsns.lor.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.wsns.lor.R;
import com.wsns.lor.http.HttpMethods;
import com.wsns.lor.http.entity.OrdersComment;

import java.util.List;

/**
 * Created by Administrator on 2016/12/21.
 */

public class CommentListAdapter extends BaseAdapter {
    Context context;
    List<OrdersComment> mComments;
    RelativeLayout rl_parent_s;
    TextView name_u, name_s;
    TextView tv_comment_u, tv_comment_s;
    TextView tv_time_u, tv_time_s;
    ImageView avatar_s, avatar_u;


    public CommentListAdapter(Context context, List<OrdersComment> mComments) {
        this.context = context;
        this.mComments = mComments;
    }

    @Override
    public int getCount() {
        return mComments == null ? 0 : mComments.size();
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View convertView, ViewGroup viewGroup) {
        View view = null;
        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
            view = inflater.inflate(R.layout.item_comment, null);
        } else {
            view = convertView;
        }
        rl_parent_s = (RelativeLayout) view.findViewById(R.id.re_parent_s);
        name_s = (TextView) view.findViewById(R.id.tv_name_s);
        name_u = (TextView) view.findViewById(R.id.tv_name);
        tv_comment_s = (TextView) view.findViewById(R.id.tv_content_s);
        tv_comment_u = (TextView) view.findViewById(R.id.tv_content);
        tv_time_s = (TextView) view.findViewById(R.id.tv_time_s);
        tv_time_u = (TextView) view.findViewById(R.id.tv_time);
        avatar_s = (ImageView) view.findViewById(R.id.iv_avatar_s);
        avatar_u = (ImageView) view.findViewById(R.id.iv_avatar);

        name_u.setText(mComments.get(i).getOrders().getUser().getName());
        tv_time_u.setText(mComments.get(i).getCreateDate());
        tv_comment_u.setText(mComments.get(i).getComments());
        Picasso.with(context).load(HttpMethods.BASE_URL+mComments.get(i).getOrders().getUser().getAvatar()).resize(48, 48).centerInside().error(R.drawable.unknow_avatar).into(avatar_u);



        rl_parent_s.setVisibility(View.GONE);
        if (mComments.get(i).getReply()!=null){
            rl_parent_s.setVisibility(View.VISIBLE);
            tv_time_s.setText(mComments.get(i).getEditDate());
            name_s.setText(mComments.get(i).getOrders().getSeller().getName());
            tv_comment_s.setText(mComments.get(i).getReply());
            Picasso.with(context).load(HttpMethods.BASE_URL+mComments.get(i).getOrders().getSeller().getAvatar()).resize(48, 48).centerInside().error(R.drawable.unknow_avatar).into(avatar_s);

        }



        return view;
    }

}
