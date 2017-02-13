package com.wsns.lor.Activity;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.wsns.lor.Activity.message.MessageFragment;
import com.wsns.lor.fragment.chat.ConversationListFragment;
import com.wsns.lor.fragment.orders.OrderListFragment;
import com.wsns.lor.Activity.seller.SellerFragment;
import com.wsns.lor.R;
import com.wsns.lor.fragment.more.MyProfileFragment;

import cn.jpush.im.android.api.JMessageClient;
import cn.jpush.im.android.api.event.LoginStateChangeEvent;
import cn.jpush.im.android.api.event.MessageEvent;
import cn.jpush.im.android.api.model.UserInfo;
import im.sdk.debug.activity.setting.ShowLogoutReasonActivity;

/**
 * 登录成功后的客户端主页 主要功能页面都在这Activity之上
 */

public class MainActivity extends Activity implements View.OnClickListener {
    public static final String CREATE_GROUP_CUSTOM_KEY = "create_group_custom_key";
    public static final String SET_DOWNLOAD_PROGRESS = "set_download_progress";
    public static final String IS_DOWNLOAD_PROGRESS_EXISTS = "is_download_progress_exists";
    public static final String CUSTOM_MESSAGE_STRING = "custom_message_string";
    public static final String CUSTOM_FROM_NAME = "custom_from_name";
    public static final String DOWNLOAD_INFO = "download_info";
    public static final String DOWNLOAD_ORIGIN_IMAGE = "download_origin_image";
    public static final String DOWNLOAD_THUMBNAIL_IMAGE = "download_thumbnail_image";
    public static final String IS_UPLOAD = "is_upload";
    public static final String LOGOUT_REASON = "logout_reason";

    private LinearLayout btnSeller, btnNotice, btnOrder, btnMore;
    private ImageView ivSeller, ivNotice, ivOrder, ivMore;
    private TextView tvSeller, tvNotice, tvOrder, tvMore;
    private SellerFragment sellerFragment;
    private ConversationListFragment messageFragment;
    private OrderListFragment orderFragment;
    private MyProfileFragment moreFragment;

    private Fragment flag_fragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        JMessageClient.registerEventReceiver(this);
        initView();
        if (savedInstanceState == null) {
            // 设置默认的Fragment
            setDefaultFragment();
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
    }

    /**
     * 初始化控件的颜色
     */
    private void initBottemBtn() {
        ivSeller.setImageResource(R.drawable.main_bottom_seller);
        ivNotice.setImageResource(R.drawable.main_bottom_notice);
        ivOrder.setImageResource(R.drawable.main_bottom_order);
        ivMore.setImageResource(R.drawable.main_bottom_more);
        tvSeller.setTextColor(getResources().getColor(R.color.main_bottom_textcolor_normal));
        tvNotice.setTextColor(getResources().getColor(R.color.main_bottom_textcolor_normal));
        tvOrder.setTextColor(getResources().getColor(R.color.main_bottom_textcolor_normal));
        tvMore.setTextColor(getResources().getColor(R.color.main_bottom_textcolor_normal));
    }

    @Override
    public void onClick(View v) {
        int mBtnid = v.getId();
        FragmentManager fm = getFragmentManager();
        FragmentTransaction transaction = fm.beginTransaction();
        transaction.hide(flag_fragment);
        initBottemBtn();
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
                break;

        }
        transaction.commit();

    }

    public void onEventMainThread(MessageEvent event) {

    }


    public void onEvent(LoginStateChangeEvent event) {
        LoginStateChangeEvent.Reason reason = event.getReason();
        UserInfo myInfo = event.getMyInfo();
        Intent intent = new Intent(getApplicationContext(), ShowLogoutReasonActivity.class);
        intent.putExtra(LOGOUT_REASON, "reason = " + reason + "\n" + "logout user name = " + myInfo.getUserName());
        startActivity(intent);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        JMessageClient.unRegisterEventReceiver(this);
    }

}
