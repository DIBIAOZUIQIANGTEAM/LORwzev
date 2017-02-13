package com.wsns.lor.fragment.orders;


import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.wsns.lor.Activity.LoginActivity;
import com.wsns.lor.R;
import com.wsns.lor.entity.Orders;
import com.wsns.lor.entity.OrdersProgress;
import com.wsns.lor.entity.Page;
import com.wsns.lor.entity.User;
import com.wsns.lor.http.HttpMethods;
import com.wsns.lor.http.subscribers.ProgressSubscriber;
import com.wsns.lor.http.subscribers.SubscriberOnNextListener;
import com.wsns.lor.utils.DateToString;
import com.wsns.lor.utils.MD5;
import com.wsns.lor.view.layout.UnderLineLinearLayout;
import com.wsns.lor.view.layout.VRefreshLayout;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MultipartBody;
import okhttp3.Request;
import okhttp3.Response;


public class OrdersProgressFragment extends Fragment implements View.OnClickListener {
    private SubscriberOnNextListener getOrdersProgressOnNext;
    private SubscriberOnNextListener addOrdersProgressOnNext;
    private UnderLineLinearLayout mUnderLineLinearLayout;
    public View view;
    Orders orders;
    Activity activity;
    List<OrdersProgress> mProgress = new ArrayList<>();
    Page<OrdersProgress> progressPage;
    LinearLayout ll_handle;
    Button leftBtn, rightBtn;
    VRefreshLayout mRefreshLayout;

    @Override

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (view == null) {
            activity = getActivity();
            view = inflater.inflate(R.layout.fragment_orders_progress, null);
            initHeaderView();
        }
        return view;
    }

    public void initView() {
        ll_handle = (LinearLayout) view.findViewById(R.id.ll_handle);
        mUnderLineLinearLayout = (UnderLineLinearLayout) view.findViewById(R.id.underline_layout);
        mUnderLineLinearLayout.removeAllViews();
        leftBtn = (Button) view.findViewById(R.id.btn_left);
        rightBtn = (Button) view.findViewById(R.id.btn_right);
        leftBtn.setOnClickListener(this);
        rightBtn.setOnClickListener(this);

        leftBtn.setText("取消订单");
        switch (orders.getState()) {
            case 1:
                rightBtn.setText("等待接单");
                break;
            case 2:
                rightBtn.setText("已接单");
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
                rightBtn.setText("查看评价");
                break;
            case 6:
                leftBtn.setText("私信");
                rightBtn.setText("等待退款");
                break;
            case 7:
                leftBtn.setText("私信");
                rightBtn.setText("退款成功");
                break;
            case 8:
                leftBtn.setText("私信");
                rightBtn.setText("联系客服");
                break;
        }

        addOrdersProgressOnNext = new SubscriberOnNextListener<OrdersProgress>() {

            @Override
            public void onNext(OrdersProgress ordersProgress) {
                addProgressItem(ordersProgress);
            }
        };
        getOrdersProgressOnNext = new SubscriberOnNextListener<Page<OrdersProgress>>() {
            @Override
            public void onNext(Page<OrdersProgress> progressPage) {
                mRefreshLayout.refreshComplete();
                final List<OrdersProgress> datas = progressPage.getContent();
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mProgress.clear();
                        mProgress.addAll(datas);
                        mUnderLineLinearLayout.removeAllViews();
                        for (int i = 0; i < mProgress.size(); i++) {
                            addProgressItem(i);
                        }
                    }
                });
            }
        };
        loadOrdersProgress();
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

    private void addProgressItem(int i) {
        View v = LayoutInflater.from(activity).inflate(R.layout.fragment_orders_progress_item, mUnderLineLinearLayout, false);
        ((TextView) v.findViewById(R.id.tx_action)).setText(mProgress.get(i).getContent());
        ((TextView) v.findViewById(R.id.tx_action_time)).setText(mProgress.get(i).getCreateDate());
        ((TextView) v.findViewById(R.id.tx_action_status)).setText(mProgress.get(i).getTitle());
        mUnderLineLinearLayout.addView(v);
    }

    private void addProgressItem(OrdersProgress progress) {
        View v = LayoutInflater.from(activity).inflate(R.layout.fragment_orders_progress_item, mUnderLineLinearLayout, false);
        ((TextView) v.findViewById(R.id.tx_action)).setText(progress.getContent());
        ((TextView) v.findViewById(R.id.tx_action_time)).setText(progress.getCreateDate());
        ((TextView) v.findViewById(R.id.tx_action_status)).setText(progress.getTitle());
        mUnderLineLinearLayout.addView(v);

        if (rightBtn.getText().toString().equals("确认收货")) {
            rightBtn.setEnabled(true);
            rightBtn.setText("去评价");
            leftBtn.setText("私信");
        } else if (leftBtn.getText().toString().equals("取消订单")) {
            leftBtn.setText("私信");
            leftBtn.setEnabled(true);
            rightBtn.setText("等待退款");
        }
    }

    public void setOrder(Orders orders) {
        this.orders = orders;
        initView();
    }

    @Override
    public void onClick(View view) {

        switch (view.getId()) {
            case R.id.btn_left:
                if (leftBtn.getText().toString().equals("取消订单")) {
                    changeState("申请取消", "等待卖方答复", 6);
                    leftBtn.setEnabled(false);

                } else if (leftBtn.getText().toString().equals("私信")) {
//                    Intent intent = new Intent(activity, SendMessageActivity.class);
//                    if ( orders.getGoods().getPublishers().getId()== CurrentUserInfo.user_id) //判断订单是不是当前用户购买的
//                    {
//                        intent.putExtra("account", orders.getBuyer().getAccount());
//
//                    } else {
//                        intent.putExtra("account", orders.getGoods().getPublishers().getAccount());
//                    }
//                    startActivity(intent);
                }
                break;
            case R.id.btn_right:
                if (rightBtn.getText().toString().equals("确认收货")) {
                    rightBtn.setEnabled(false);
                    changeState("交易完成", "如有疑问请联系客服", 4);
                } else if (rightBtn.getText().toString().equals("去评价")) {
//                    Intent itnt = new Intent(activity, OrdersCommentActivity.class);
//                    itnt.putExtra("orders",orders);
//                    activity.startActivity(itnt);
                }


                break;
        }


    }


    private void changeState(String content, String title, final int state) {


        HttpMethods.getInstance().addOrdersProgress(
                new ProgressSubscriber(addOrdersProgressOnNext, activity, false),
                content, title, orders.getId(), state);

//                activity.runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//                        //发送新订单消息
//                        Map<String, String> valuesMap = new HashMap<>();
//                        valuesMap.put("msg_type", MsgType.MSG_ORDERS);
//                        valuesMap.put("orders_state", state);
//                        valuesMap.put("orders_id", orders.getId() + "");
//                        CreateSigMsg.context = activity;
//
//                        String account;
//                        if (state.equals("4")||state.equals("5")||state.equals("6"))
//                            account = orders.getGoods().getPublishers().getAccount();
//                        else
//                            account = orders.getBuyer().getAccount();
//
//                        CreateSigMsg.CreateSigTextMsg(account,
//                                "订单消息", getOrdersStateString(state), valuesMap);
//                    }
//
//                });


    }

//    private Handler scaleHandler = new Handler();
//    private Runnable scaleRunnable = new Runnable() {
//
//        @Override
//        public void run() {
//            mRefreshLayout.autoRefresh();
//        }
//    };
//
//    @Override
//    public void onResume() {
//        super.onResume();
//        activity.getWindow().getDecorView().post(new Runnable() {
//
//            @Override
//            public void run() {
//                scaleHandler.post(scaleRunnable);
//            }
//        });
//    }
//    private String getOrdersStateString(String state) {
//        switch (state) {
//            case "2":
//                return orders.getGoods().getPublishers().getName() + "已接单";
//            case "3":
//                return orders.getGoods().getPublishers().getName() + "已经发货";
//            case "4":
//                return orders.getBuyer().getName() + "确认收货";
//            case "5":
//                return "收到一条来自" + orders.getBuyer().getName() + "的评价";
//            case "6":
//                return orders.getBuyer().getName() + "申请取消订单";
//            case "7":
//                return orders.getGoods().getPublishers().getName() + "同意您的退款申请";
//            case "8":
//                return orders.getGoods().getPublishers().getName() + "拒绝您的退款申请";
//        }
//        return "";
//    }
}