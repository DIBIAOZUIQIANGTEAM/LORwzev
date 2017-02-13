package com.wsns.lor.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;


import com.squareup.picasso.Picasso;
import com.wsns.lor.R;
import com.wsns.lor.entity.Sellers;
import com.wsns.lor.http.HttpMethods;

import java.util.List;

/**
 * Created by Administrator on 2016/12/21.
 */

public class SellersListAdapter extends BaseAdapter {
    Context context;
    List<Sellers> mSellers;
    TextView storename;
    TextView storeType;
    TextView body;
    TextView head;
    TextView body2;
    TextView head2;
    TextView body3;
    TextView head3;
    RatingBar star;
    TextView msales;
    TextView distance;
    TextView city;
    TextView tradeType;
    TextView minimums;
    ImageView icon;
    private int[] valueViewID = {R.id.tv_storename,
            R.id.star,
            R.id.tv_mSales,
            R.id.tv_storeType,
            R.id.iv_icon,
            R.id.tv_activity_head1,
            R.id.tv_activity_body1,
            R.id.tv_activity_head2,
            R.id.tv_activity_body2,
            R.id.tv_activity_head3,
            R.id.tv_activity_body3,
            R.id.tv_distance,
            R.id.tv_city,
            R.id.tv_trade_type,
            R.id.tv_minimums
    };

    public SellersListAdapter(Context context, List<Sellers> mSellers) {
        this.context = context;
        this.mSellers = mSellers;
    }

    @Override
    public int getCount() {
        return mSellers == null ? 0 : mSellers.size();
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
            view = inflater.inflate(R.layout.recyclerview_item_seller, null);
        } else {
            view = convertView;
        }
        Sellers seller = mSellers.get(i);
        initView(view);
        setData(seller);
        return view;
    }


    private void initView(View view) {
        storename = (TextView) view.findViewById(valueViewID[0]);
        star = (RatingBar) view.findViewById(valueViewID[1]);
        msales = (TextView) view.findViewById(valueViewID[2]);
        storeType = (TextView) view.findViewById(valueViewID[3]);
        icon = (ImageView) view.findViewById(valueViewID[4]);
        head = (TextView) view.findViewById(valueViewID[5]);
        body = (TextView) view.findViewById(valueViewID[6]);
        head2 = (TextView) view.findViewById(valueViewID[7]);
        body2 = (TextView) view.findViewById(valueViewID[8]);
        head3 = (TextView) view.findViewById(valueViewID[9]);
        body3 = (TextView) view.findViewById(valueViewID[10]);
        distance = (TextView) view.findViewById(valueViewID[11]);
        city = (TextView) view.findViewById(valueViewID[12]);
        tradeType = (TextView) view.findViewById(valueViewID[13]);
        minimums = (TextView) view.findViewById(valueViewID[14]);
    }
    private void setData(Sellers seller) {
        storename.setText(seller.getTitle());
        star.setRating(seller.getStar().floatValue());
        msales.setText("已完成"+seller.getTurnover()+"单");
        distance.setText("距离"+seller.getDistance()+"米");
        city.setText(seller.getCity());
        storeType.setText(seller.getRepairsTypes());
        tradeType.setText(seller.getTradeTypes()==1?"上门":"邮寄");
        minimums.setText(""+seller.getMinimums()+"元以上");

        String avatarUrl = HttpMethods.BASE_URL + seller.getAvatar();
        Picasso.with(context).load(avatarUrl).fit().error(R.drawable.unknow_avatar).into(icon);
    }
}
