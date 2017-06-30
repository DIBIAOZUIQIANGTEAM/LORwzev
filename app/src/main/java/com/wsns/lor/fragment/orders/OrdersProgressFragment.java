package com.wsns.lor.fragment.orders;


import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.wsns.lor.R;
import com.wsns.lor.activity.order.CommentEditActivity;
import com.wsns.lor.application.LorApplication;
import com.wsns.lor.http.HttpMethods;
import com.wsns.lor.http.entity.Orders;
import com.wsns.lor.http.entity.OrdersProgress;
import com.wsns.lor.http.subscribers.ProgressSubscriber;
import com.wsns.lor.http.subscribers.SubscriberOnNextListener;
import com.wsns.lor.other.chatting.ChatActivity;
import com.wsns.lor.receiver.MyReceiver;
import com.wsns.lor.view.layout.UnderLineLinearLayout;
import com.wsns.lor.view.layout.VRefreshLayout;

import java.util.ArrayList;
import java.util.List;


public class OrdersProgressFragment extends Fragment implements View.OnClickListener {
    private SubscriberOnNextListener getOrdersProgressOnNext;
    private SubscriberOnNextListener addOrdersProgressOnNext;
    private UnderLineLinearLayout mUnderLineLinearLayout;
    public View view;
    Orders orders;
    Activity activity;
    List<OrdersProgress> mProgress = new ArrayList<>();
    List<OrdersProgress> progressPage;
    LinearLayout ll_handle;
    Button leftBtn, rightBtn;
    VRefreshLayout mRefreshLayout;
    MyReceiver myReceiver;

    @Override

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (view == null) {
            activity = getActivity();
            view = inflater.inflate(R.layout.fragment_orders_progress, null);
        }
        if (orders != null) {
            initView();
        }

        return view;
    }

    public void initView() {
        if (view == null)
            return;
        initHeaderView();

        myReceiver = new MyReceiver();
        myReceiver.addOrderRefreshView(mRefreshLayout);
        myReceiver.setOrdersId(orders.getId()+"");

        IntentFilter filter = new IntentFilter();
        filter.addAction("cn.jpush.android.intent.REGISTRATION");
        filter.addAction("cn.jpush.android.intent.MESSAGE_RECEIVED");
        filter.addAction("cn.jpush.android.intent.NOTIFICATION_RECEIVED");
        filter.addAction("cn.jpush.android.intent.NOTIFICATION_OPENED");
        filter.addAction("cn.jpush.android.intent.ACTION_RICHPUSH_CALLBACK");
        filter.addAction(" cn.jpush.android.intent.CONNECTION");
        filter.addCategory("com.wsns.lor");
        filter.setPriority(1000);
        activity.registerReceiver(myReceiver, filter);


        ll_handle = (LinearLayout) view.findViewById(R.id.ll_handle);
        mUnderLineLinearLayout = (UnderLineLinearLayout) view.findViewById(R.id.underline_layout);
        mUnderLineLinearLayout.removeAllViews();
        leftBtn = (Button) view.findViewById(R.id.btn_left);
        rightBtn = (Button) view.findViewById(R.id.btn_right);
        leftBtn.setOnClickListener(this);
        rightBtn.setOnClickListener(this);


        addOrdersProgressOnNext = new SubscriberOnNextListener<OrdersProgress>() {

            @Override
            public void onNext(OrdersProgress ordersProgress) {
                addProgressItem(ordersProgress);

            }
        };
        getOrdersProgressOnNext = new SubscriberOnNextListener<List<OrdersProgress>>() {
            @Override
            public void onNext(final List<OrdersProgress> progressPage) {
                mRefreshLayout.refreshComplete();
                final List<OrdersProgress> datas = progressPage;
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mProgress.clear();
                        mProgress.addAll(datas);
                        mUnderLineLinearLayout.removeAllViews();
                        for (int i = 0; i < mProgress.size(); i++) {
                            addProgressItem(datas.get(i));
                        }

                        if ((myReceiver.getOrdersState() != null)) {
                            orders.setState(Integer.valueOf(myReceiver.getOrdersState()));
                        }else {
                            orders.setState(progressPage.get(0).getOrders().getState());
                        }
                        refreshBtn();

                    }
                });
            }
        };
        loadOrdersProgress();
        refreshBtn();
    }

    private void refreshBtn() {
        leftBtn.setText("取消订单");
        switch (orders.getState()) {
            case 1:
                rightBtn.setText("等待接单");
                break;
            case 2:
                rightBtn.setText("等待发货");
                break;
            case 3:
                rightBtn.setText("确认收货");
                break;
            case 4:
                leftBtn.setText("私信");
                rightBtn.setText("去评价");
                break;
            case 5:
                leftBtn.setText("私信");
                rightBtn.setText("等待取消");
                break;
            case 6:
                leftBtn.setText("私信");
                rightBtn.setText("拒绝退款");
                break;
            case 7:
                leftBtn.setText("私信");
                rightBtn.setText("退款成功");
                break;
            case 8:
                leftBtn.setText("私信");
                rightBtn.setText("已评价");
                break;

        }

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
                    loadOrdersProgress();
                }
            });
        }

        mRefreshLayout.setHeaderView(mRefreshLayout.getDefaultHeaderView());
        mRefreshLayout.setBackgroundColor(Color.WHITE);

    }

    private void loadOrdersProgress() {
        HttpMethods.getInstance().getOrdersProgressPage(new ProgressSubscriber(getOrdersProgressOnNext, activity, false), 0, orders.getId());
    }

    private void addProgressItem(OrdersProgress progress) {
        View v = LayoutInflater.from(activity).inflate(R.layout.fragment_orders_progress_item, mUnderLineLinearLayout, false);
        ((TextView) v.findViewById(R.id.tx_action)).setText(progress.getContent());
        ((TextView) v.findViewById(R.id.tx_action_time)).setText(progress.getCreateDate());
        ((TextView) v.findViewById(R.id.tx_action_status)).setText(progress.getTitle());
        mUnderLineLinearLayout.addView(v);

    }

    public void setOrder(Orders orders) {
        this.orders = orders;
        initView();
    }

    public Orders getOrders() {
        return orders;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        activity.unregisterReceiver(myReceiver);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_left:
                if (leftBtn.getText().toString().equals("取消订单")) {
                    changeState("申请取消", "等待卖方答复", 6);
                } else if (leftBtn.getText().toString().equals("私信")) {
                    Intent intent = new Intent(activity, ChatActivity.class);
                    intent.putExtra(LorApplication.TARGET_ID, orders.getSeller().getAccount());
                    intent.putExtra(LorApplication.TARGET_APP_KEY, LorApplication.LOR_SELLER_IM_KEY);
                    activity.startActivity(intent);
                }
                break;
            case R.id.btn_right:
                if (rightBtn.getText().toString().equals("确认收货")) {
                    changeState("交易完成", "如有疑问请联系客服", 4);
                } else if (rightBtn.getText().toString().equals("去评价")) {
                    Intent itnt = new Intent(activity,CommentEditActivity.class);
                    itnt.putExtra("orders_id",orders.getId()+"");
                    activity.startActivity(itnt);
                }
                break;
        }
    }

    private Handler scaleHandler = new Handler();
    private Runnable scaleRunnable = new Runnable() {
        @Override
        public void run() {
            mRefreshLayout.autoRefresh();

        }
    };

    @Override
    public void onResume() {
        super.onResume();
        activity.getWindow().getDecorView().post(new Runnable() {
            @Override
            public void run() {
                scaleHandler.post(scaleRunnable);
            }
        });
    }

    private void changeState(String content, String title, final int state) {
        HttpMethods.getInstance().addOrdersProgress(
                new ProgressSubscriber(addOrdersProgressOnNext, activity, false),
                content, title, orders.getId(), state);
    }
}