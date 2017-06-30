package com.wsns.lor.activity;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.tencent.mm.opensdk.modelbase.BaseReq;
import com.tencent.mm.opensdk.modelbase.BaseResp;
import com.tencent.mm.opensdk.openapi.IWXAPIEventHandler;
import com.wsns.lor.activity.login.LoginActivity;
import com.wsns.lor.activity.personal.UpdateUserNameActivity;
import com.wsns.lor.activity.seller.SellerFragment;
import com.wsns.lor.R;
import com.wsns.lor.http.entity.User;
import com.wsns.lor.other.chatting.activity.ConversationListFragment;
import com.wsns.lor.fragment.personal.MyProfileFragment;
import com.wsns.lor.fragment.orders.OrderListFragment;
import com.wsns.lor.http.HttpMethods;
import com.wsns.lor.http.subscribers.ProgressSubscriber;
import com.wsns.lor.http.subscribers.SubscriberOnNextListener;
import com.wsns.lor.utils.MD5;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import cn.jpush.im.android.api.JMessageClient;
import cn.jpush.im.android.api.model.UserInfo;

import static com.wsns.lor.application.LorApplication.RESULT_LOGIN_FAIL;
import static com.wsns.lor.application.LorApplication.RESULT_LOGIN_SUCCESS;
import static com.wsns.lor.application.LorApplication.api;


/**
 * 登录成功后的客户端主页 主要功能页面都在这Activity之上
 */
public class MainActivity extends Activity implements View.OnClickListener,IWXAPIEventHandler {
    ProgressSubscriber progressSubscriber;
    private LinearLayout btnSeller, btnNotice, btnOrder, btnMore;
    private ImageView ivSeller, ivNotice, ivOrder, ivMore;
    private TextView tvSeller, tvNotice, tvOrder, tvMore;
    private SellerFragment sellerFragment;
    private ConversationListFragment messageFragment;
    private OrderListFragment orderFragment;
    private MyProfileFragment moreFragment;
    private Fragment flag_fragment;
    private int tab_select = 0;
    private SubscriberOnNextListener getUserDataOnNext;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        api.handleIntent(getIntent(),this);
        System.out.println("MainActivity at:"+getTaskId());
        initView();
        if (savedInstanceState == null) {
            // 设置默认的Fragment
            setDefaultFragment();
        }

        getUserDataOnNext = new SubscriberOnNextListener<User>() {
            @Override
            public void onNext(User user) {
                //昵称为空，则初始化
                if (user.getName()==null) {
                    Intent itnt = new Intent(MainActivity.this, UpdateUserNameActivity.class);
                    startActivity(itnt);
                    overridePendingTransition(R.anim.slide_in_left, R.anim.none);
                    progressSubscriber.setUser(user);
                }

            }
        };
    }

    @Override
    protected void onResume() {
        super.onResume();

        UserInfo myInfo = JMessageClient.getMyInfo();

        if (myInfo == null) {
            Intent itnt = new Intent(MainActivity.this, LoginActivity.class);
            startActivityForResult(itnt, 111);
            btnSeller.performClick();
        } else {
            List<String> accountList = new ArrayList<String>();
            SharedPreferences preferences = getSharedPreferences("user", Context.MODE_PRIVATE);
            Set<String> accountSet = preferences.getStringSet("accountset", null);
            if (accountSet == null) {
                return;
            }
            Iterator<String> iterator = accountSet.iterator();
            while (iterator.hasNext()) {
                accountList.add(iterator.next());
            }
            String password = null;
            for (String account : accountList) {
                if (account.equals(myInfo.getUserName())) {
                    password=preferences.getString(account + "password", "");
                    break;
                }
            }


             progressSubscriber=  new ProgressSubscriber(getUserDataOnNext, MainActivity.this, false);
            //登录，保持在线
            HttpMethods.getInstance().getUserData(progressSubscriber,
                    myInfo.getUserName(), MD5.getMD5(password));

        }
    }

    private void setDefaultFragment() {
        sellerFragment = new SellerFragment();
        FragmentManager fm = getFragmentManager();
        FragmentTransaction transaction = fm.beginTransaction();
        //add（）方法：在当前显示时，点击Back键不出现白板。是正确的相应Back键，即退出我们的Activity
        transaction.add(R.id.fragment_content, sellerFragment);
        transaction.commit();
        flag_fragment = sellerFragment;
    }

    public void initView() {
        btnSeller = (LinearLayout) findViewById(R.id.btn_seller);
        btnNotice = (LinearLayout) findViewById(R.id.btn_notice);
        btnOrder = (LinearLayout) findViewById(R.id.btn_order);
        btnMore = (LinearLayout) findViewById(R.id.btn_more);
        ivSeller = (ImageView) findViewById(R.id.iv_seller);
        ivNotice = (ImageView) findViewById(R.id.iv_notice);
        ivOrder = (ImageView) findViewById(R.id.iv_order);
        ivMore = (ImageView) findViewById(R.id.iv_more);
        tvSeller = (TextView) findViewById(R.id.tv_seller);
        tvNotice = (TextView) findViewById(R.id.tv_notice);
        tvOrder = (TextView) findViewById(R.id.tv_order);
        tvMore = (TextView) findViewById(R.id.tv_more);

        btnSeller.setOnClickListener(this);
        btnNotice.setOnClickListener(this);
        btnOrder.setOnClickListener(this);
        btnMore.setOnClickListener(this);
        changeTabSelect(tab_select);
    }

    /**
     * 初始化控件的颜色
     */
    private void changeTabSelect(int tab_select) {
        ivSeller.setImageResource(R.drawable.tab_main_normal);
        ivNotice.setImageResource(R.drawable.tab_message_normal);
        ivOrder.setImageResource(R.drawable.tab_order_normal);
        ivMore.setImageResource(R.drawable.tab_my_normal);
        tvSeller.setTextColor(getResources().getColor(R.color.main_bottom_textcolor_normal));
        tvNotice.setTextColor(getResources().getColor(R.color.main_bottom_textcolor_normal));
        tvOrder.setTextColor(getResources().getColor(R.color.main_bottom_textcolor_normal));
        tvMore.setTextColor(getResources().getColor(R.color.main_bottom_textcolor_normal));
        switch (tab_select) {
            case 0:
                ivSeller.setImageResource(R.drawable.tab_main_pressed);
                tvSeller.setTextColor(getResources().getColor(R.color.main_bottom_textcolor_pressed));
                break;
            case 1:
                ivNotice.setImageResource(R.drawable.tab_message_pressed);
                tvNotice.setTextColor(getResources().getColor(R.color.main_bottom_textcolor_pressed));
                break;
            case 2:
                ivOrder.setImageResource(R.drawable.tab_order_pressed);
                tvOrder.setTextColor(getResources().getColor(R.color.main_bottom_textcolor_pressed));
                break;
            case 3:
                ivMore.setImageResource(R.drawable.tab_my_pressed);
                tvMore.setTextColor(getResources().getColor(R.color.main_bottom_textcolor_pressed));
                break;
            default:
                break;
        }
    }

    @Override
    public void onClick(View v) {
        int mBtnid = v.getId();
        FragmentManager fm = getFragmentManager();
        FragmentTransaction transaction = fm.beginTransaction();
        transaction.hide(flag_fragment);

        switch (mBtnid) {
            case R.id.btn_seller:
                if (sellerFragment == null) {
                    sellerFragment = new SellerFragment();
                    transaction.add(R.id.fragment_content, sellerFragment);
                } else {
                    transaction.show(sellerFragment);
                }
                ivSeller.setImageResource(R.drawable.main_index_seller_pressed);
                tvSeller.setTextColor(getResources().getColor(R.color.main_bottom_textcolor_pressed));
                flag_fragment = sellerFragment;
                tab_select = 0;
                break;
            case R.id.btn_notice:
                if (messageFragment == null) {
                    messageFragment = new ConversationListFragment();
                    transaction.add(R.id.fragment_content, messageFragment);
                } else {
                    transaction.show(messageFragment);
                }
                ivNotice.setImageResource(R.drawable.main_index_notice_pressed);
                tvNotice.setTextColor(getResources().getColor(R.color.main_bottom_textcolor_pressed));
                flag_fragment = messageFragment;
                tab_select = 1;
                break;
            case R.id.btn_order:
                if (orderFragment == null) {
                    orderFragment = new OrderListFragment();
                    transaction.add(R.id.fragment_content, orderFragment);
                } else {
                    transaction.show(orderFragment);
                }
                ivOrder.setImageResource(R.drawable.main_index_order_pressed);
                tvOrder.setTextColor(getResources().getColor(R.color.main_bottom_textcolor_pressed));
                flag_fragment = orderFragment;
                tab_select = 2;
                break;
            case R.id.btn_more:
                if (moreFragment == null) {
                    moreFragment = new MyProfileFragment();
                    transaction.add(R.id.fragment_content, moreFragment);
                } else {
                    transaction.show(moreFragment);
                }
                ivMore.setImageResource(R.drawable.main_index_more_pressed);
                tvMore.setTextColor(getResources().getColor(R.color.main_bottom_textcolor_pressed));
                flag_fragment = moreFragment;
                tab_select = 3;
                break;

        }
        transaction.commit();
        changeTabSelect(tab_select);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_LOGIN_SUCCESS) {

        } else if (resultCode == RESULT_LOGIN_FAIL) {
            finish();
        }
    }

    @Override
    public void onReq(BaseReq baseReq) {

    }

    @Override
    public void onResp(BaseResp baseResp) {
        System.out.println("应修联盟"+baseResp.errCode+baseResp.errStr);
    }
}
