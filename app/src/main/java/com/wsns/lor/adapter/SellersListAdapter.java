package com.wsns.lor.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.wsns.lor.R;
import com.wsns.lor.http.entity.Seller;
import com.wsns.lor.http.HttpMethods;
import com.wsns.lor.utils.DataUtil;

import java.util.List;

/**
 * Created by Administrator on 2016/12/21.
 */

public class SellersListAdapter extends BaseAdapter {
    Context context;
    List<Seller> mSellers;
    TextView storename;
    TextView storeType;
    TextView body;
    TextView head;
    TextView body2;
    TextView head2;
    TextView body3;
    TextView head3;
    TextView msales;
    TextView distance;
    TextView city;
    TextView minimums;
    TextView tv_comment;
    ImageView icon;
    private int[] valueViewID = {R.id.tv_storename,
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
            R.id.tv_minimums
    };

    public SellersListAdapter(Context context, List<Seller> mSellers) {
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


            initView(view);
            Seller seller = mSellers.get(i);
            setData(seller);

        return view;
    }


    private void initView(View view) {
        storename = (TextView) view.findViewById(valueViewID[0]);
        msales = (TextView) view.findViewById(valueViewID[1]);
        storeType = (TextView) view.findViewById(valueViewID[2]);
        icon = (ImageView) view.findViewById(valueViewID[3]);
        head = (TextView) view.findViewById(valueViewID[4]);
        body = (TextView) view.findViewById(valueViewID[5]);
        head2 = (TextView) view.findViewById(valueViewID[6]);
        body2 = (TextView) view.findViewById(valueViewID[7]);
        head3 = (TextView) view.findViewById(valueViewID[8]);
        body3 = (TextView) view.findViewById(valueViewID[9]);
        distance = (TextView) view.findViewById(valueViewID[10]);
        city = (TextView) view.findViewById(valueViewID[11]);

        minimums = (TextView) view.findViewById(valueViewID[12]);
        tv_comment = (TextView) view.findViewById(R.id.tv_comment);
    }

    private void setData(Seller seller) {
        storename.setText(seller.getName());
        msales.setText( seller.getTurnover() + "");
        tv_comment.setText(seller.getComment()+"");
        if (seller.getDistance() ==null) {
            distance.setText("");
        }else {
            distance.setText(seller.getDistance() + "m");
        }
        city.setText(seller.getAddress());
        storeType.setText(seller.getRepairsTypes());
        minimums.setText( DataUtil.doubleTrans1(seller.getService()));

        String avatarUrl = HttpMethods.BASE_URL + seller.getAvatar();
        Picasso.with(context).load(avatarUrl).fit().error(R.drawable.unknow_avatar).into(icon);
    }
}
