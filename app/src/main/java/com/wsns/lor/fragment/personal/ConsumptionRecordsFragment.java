package com.wsns.lor.fragment.personal;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Fragment;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.wsns.lor.R;
import com.wsns.lor.http.entity.Records;
import com.wsns.lor.http.HttpMethods;
import com.wsns.lor.http.subscribers.ProgressSubscriber;
import com.wsns.lor.http.subscribers.SubscriberOnNextListener;
import com.wsns.lor.view.layout.VRefreshLayout;

import java.util.ArrayList;
import java.util.List;

//消费列表
public class ConsumptionRecordsFragment extends Fragment {

    View view;
    ListView listView;
    int page = 0;
    int NOT_MORE_PAGE = -1;
    View LoadMore;
    TextView textLoadMore;

    Activity activity;
    VRefreshLayout mRefreshLayout;
    List<Records> mRecords = new ArrayList<Records>();
    private SubscriberOnNextListener getMyRecordsOnNext;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        if (view == null) {

            activity = getActivity();

            view = inflater.inflate(R.layout.fragment_records_consumption, null);
            LoadMore = inflater.inflate(R.layout.list_foot, null);
            textLoadMore = (TextView) LoadMore.findViewById(R.id.loadmore);

            listView = (ListView) view.findViewById(R.id.consumption_list);
            listView.addFooterView(LoadMore);
            listView.setAdapter(listAdapter);

            LoadMore.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {

                    if (page != NOT_MORE_PAGE) {
                        load();
                    }
                }
            });
            initHeaderView();
            getMyRecordsOnNext = new SubscriberOnNextListener<List<Records>>() {
                @Override
                public void onNext(List<Records> recordsPage) {
                    mRefreshLayout.refreshComplete();
                    setDate(recordsPage);
                }
            };
        }

        return view;
    }

    private void setDate(List<Records> recordsPage) {

        mRecords.clear();
        mRecords.addAll(recordsPage);
        listAdapter.notifyDataSetChanged();

    }

    private void initHeaderView() {
        mRefreshLayout = (VRefreshLayout) view.findViewById(R.id.refresh_layout);
        if (mRefreshLayout != null) {
            mRefreshLayout.setBackgroundColor(Color.DKGRAY);
            mRefreshLayout.setAutoRefreshDuration(400);
            mRefreshLayout.setRatioOfHeaderHeightToReach(1.5f);
            mRefreshLayout.addOnRefreshListener(new VRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    page = 0;
                    load();
                }
            });
        }

        mRefreshLayout.setHeaderView(mRefreshLayout.getDefaultHeaderView());
        mRefreshLayout.setBackgroundColor(Color.WHITE);
    }

    BaseAdapter listAdapter = new BaseAdapter() {

        @Override
        public int getCount() {
            return mRecords == null ? 0 : mRecords.size();
        }

        @Override
        public Object getItem(int i) {
            return null;
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }

        @SuppressLint("InflateParams")
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            View view = null;

            if (convertView == null) {
                LayoutInflater inflater = LayoutInflater.from(parent.getContext());
                view = inflater.inflate(R.layout.widget_records_item, null);
            } else {
                view = convertView;
            }

            ImageView avatarView = (ImageView) view.findViewById(R.id.consumption_image);
            TextView textCoin = (TextView) view.findViewById(R.id.money);
            TextView textCause = (TextView) view.findViewById(R.id.cause);
            TextView textDate = (TextView) view.findViewById(R.id.date);
            Records records = mRecords.get(position);

            textCoin.setText(records.getCause());
            textCause.setText(records.getCoin() + " 元");

            String dateStr = records.getCreateDate();
            textDate.setText(dateStr);

            Picasso.with(activity).load(HttpMethods.BASE_URL + records.getOrders().getSeller().getAvatar()).
                    resize(40, 40).centerInside().error(R.drawable.unknow_avatar).into(avatarView);

            return view;
        }
    };

    @Override
    public void onResume() {
        super.onResume();
        load();
    }

    void load() {
        HttpMethods.getInstance().getMyRecordsPage(new ProgressSubscriber(getMyRecordsOnNext, activity, false), page++);
    }
}