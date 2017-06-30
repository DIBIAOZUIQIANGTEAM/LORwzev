package com.wsns.lor.fragment.orders;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.wsns.lor.activity.order.OrdersContentActivity;
import com.wsns.lor.adapter.OrdersListAdapter;
import com.wsns.lor.R;
import com.wsns.lor.http.entity.Orders;
import com.wsns.lor.http.HttpMethods;
import com.wsns.lor.http.subscribers.ProgressSubscriber;
import com.wsns.lor.http.subscribers.SubscriberOnNextListener;
import com.wsns.lor.view.layout.VRefreshLayout;

import java.util.ArrayList;
import java.util.List;


public class OrderListFragment extends Fragment {
    View view;
    Activity activity;
    public static VRefreshLayout mRefreshLayout;

    View btnLoadMore;
    TextView textLoadMore;
    private ListView mListView;
    OrdersListAdapter adpter;
    int page = 0;
    int NOT_MORE_PAGE = -1;
    private static List<Orders> mOrders = new ArrayList<Orders>();
    List<Orders> ordersPage;
    boolean firstBuilt = true;
    private SubscriberOnNextListener getMyOrderOnNext;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (view == null) {
            view = inflater.inflate(R.layout.fragment_page_order_list, null);
            activity = getActivity();
            adpter = new OrdersListAdapter(activity, mOrders);
            mListView = (ListView) view.findViewById(R.id.listView);
            btnLoadMore = inflater.inflate(R.layout.list_foot, null);
            textLoadMore = (TextView) btnLoadMore.findViewById(R.id.loadmore);
            mListView.addFooterView(btnLoadMore);
            mListView.setAdapter(adpter);
            btnLoadMore.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (page != NOT_MORE_PAGE) {
                        refreshOrdersList();
                    }
                }
            });
            mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    Intent intent = new Intent(activity, OrdersContentActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putString("orders_id", mOrders.get(i).getId()+"");
                    intent.putExtras(bundle);
                    startActivity(intent);
                }
            });
            initHeaderView();
            getMyOrderOnNext = new SubscriberOnNextListener<List<Orders>>() {
                @Override
                public void onNext(List<Orders> ordersPage) {
                    OrderListFragment.this.ordersPage = ordersPage;
                    setDate();

                }
            };

        }
        return view;
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
                    mOrders.clear();
                    refreshOrdersList();
                }
            });
        }

        mRefreshLayout.setHeaderView(mRefreshLayout.getDefaultHeaderView());
        mRefreshLayout.setBackgroundColor(Color.parseColor("#ffffff"));
    }

    private void setDate() {
        mRefreshLayout.refreshComplete();
        mOrders.clear();
        mOrders.addAll(ordersPage);
        adpter.notifyDataSetChanged();

    }

    private void refreshOrdersList() {
        HttpMethods.getInstance().getMyOrderPage(new ProgressSubscriber(getMyOrderOnNext, activity, false), page++);
    }

    @Override
    public void onResume() {
        super.onResume();
        page=0;
        refreshOrdersList() ;
    }
}
