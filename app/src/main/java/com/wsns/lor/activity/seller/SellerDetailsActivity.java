package com.wsns.lor.activity.seller;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.wsns.lor.R;
import com.wsns.lor.activity.personal.OrdersCommenActivity;
import com.wsns.lor.application.LorApplication;
import com.wsns.lor.http.HttpMethods;
import com.wsns.lor.http.entity.Seller;
import com.wsns.lor.other.chatting.ChatActivity;


/**
 * Created by cuiyang on 15/12/16.
 */
public class SellerDetailsActivity extends Activity {

    TextView tvmonthlytrading;
    TextView tvcomment;
    TextView tvstorename;
    TextView tvstoretype;
    CoordinatorLayout root_layout;
    LinearLayout ll_comment,ll_chat;

    ImageView ibchat;
    FrameLayout contentLayout;
    ImageView icon;
    ImageView iconSmall;
    LinearLayout transLayout;
    LinearLayout alphaLayout;
    LinearLayout tabLayout;
    TradeFragment tradeFragment;
    AppBarLayout appBarLayout;
    CollapsingToolbarLayout collapsingToolbar;
    ImageView backButton;
    TextView tvtitle, tvnotice;

    CollapsingToolbarLayoutState state;
    Seller seller;

    private enum CollapsingToolbarLayoutState {
        EXPANDED,
        COLLAPSED,
        INTERNEDIATE
    }

    private final DecelerateInterpolator mDecelerateInterpolator = new DecelerateInterpolator(2f);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sellerdetails);
        seller = (Seller) getIntent().getSerializableExtra("seller");
        initView();
//        initViewPagerAndTabs();
        setData();
    }

    private void initView() {
        root_layout = (CoordinatorLayout) findViewById(R.id.root_layout);
        appBarLayout = (AppBarLayout) findViewById(R.id.appBarLayout);
        ll_comment = (LinearLayout) findViewById(R.id.ll_comment);
        ll_chat = (LinearLayout) findViewById(R.id.ll_chat);
        ibchat = (ImageView) findViewById(R.id.ib_chat);
        contentLayout = (FrameLayout) findViewById(R.id.content_layout);
        icon = (ImageView) findViewById(R.id.iv_icon);
        iconSmall = (ImageView) findViewById(R.id.iv_icon_small);
        transLayout = (LinearLayout) findViewById(R.id.trans_layout);
        alphaLayout = (LinearLayout) findViewById(R.id.alpha_layout);
        tabLayout = (LinearLayout) findViewById(R.id.tab_layout);
        tvcomment = (TextView) findViewById(R.id.tv_comment);
        tvstorename = (TextView) findViewById(R.id.tv_name);
        tvstoretype = (TextView) findViewById(R.id.tv_category);
        backButton = (ImageView) findViewById(R.id.iv_back);
        tvmonthlytrading = (TextView) findViewById(R.id.tv_monthlytrading);
        tvtitle = (TextView) findViewById(R.id.tv_title);
        tvnotice = (TextView) findViewById(R.id.tv_notice);

        collapsingToolbar = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);

        if (collapsingToolbar != null) {
            //设置隐藏图片时候ToolBar的颜色
            collapsingToolbar.setContentScrimColor(Color.parseColor("#3c9aff"));
            //设置工具栏标题
            collapsingToolbar.setTitleEnabled(false);
        }

        appBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                if (verticalOffset == 0) {
                    if (state != CollapsingToolbarLayoutState.EXPANDED) {
                        state = CollapsingToolbarLayoutState.EXPANDED;//修改状态标记为展开
                        collapsingToolbar.setTitle("");//设置title不显示
                        iconSmall.setVisibility(View.GONE);
                    }
                } else if (Math.abs(verticalOffset) >= appBarLayout.getTotalScrollRange()) {
                    if (state != CollapsingToolbarLayoutState.COLLAPSED) {
                        state = CollapsingToolbarLayoutState.COLLAPSED;//修改状态标记为折叠
                        tvtitle.setVisibility(View.VISIBLE);
                        iconSmall.setVisibility(View.VISIBLE);
                    }
                } else {
                    if (state != CollapsingToolbarLayoutState.INTERNEDIATE) {
                        if (state == CollapsingToolbarLayoutState.COLLAPSED) {
                            tvtitle.setVisibility(View.GONE);//由折叠变为中间状态时隐藏播放按钮
                            iconSmall.setVisibility(View.GONE);
                        }
                        state = CollapsingToolbarLayoutState.INTERNEDIATE;//修改状态标记为中间
                    }
                }
            }
        });


        ll_comment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LorApplication.IS_MY_COMMENT=false;
                LorApplication.SELLER_ACCOUNT=seller.getAccount();
                Intent intent=new Intent(SellerDetailsActivity.this, OrdersCommenActivity.class);
                startActivity(intent);
            }
        });
        ll_chat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(SellerDetailsActivity.this, ChatActivity.class);
                intent.putExtra(LorApplication.TARGET_ID, seller.getAccount());
                intent.putExtra(LorApplication.TARGET_APP_KEY, LorApplication.LOR_SELLER_IM_KEY);
                startActivity(intent);
            }
        });
    }

    private void setDefaultFragment() {
        tradeFragment = new TradeFragment();
        FragmentManager fm = getFragmentManager();
        FragmentTransaction transaction = fm.beginTransaction();
        //add（）方法：在当前显示时，点击Back键不出现白板。是正确的相应Back键，即退出我们的Activity
        transaction.add(R.id.fragment_content, tradeFragment);
        transaction.commit();
        tradeFragment.setSeller(seller);
    }


    public void back(View view) {
        finish();
    }

    private void setData() {
        tvcomment.setText(seller.getComment()+"评");
        tvstorename.setText(seller.getName());
        tvstoretype.setText("维修类别:" + seller.getRepairsTypes());
        tvtitle.setText(seller.getName());//设置title
        if (seller.getNotice() != null) tvnotice.setText(seller.getNotice());
        tvmonthlytrading.setText(seller.getTurnover() + "单");
        Picasso.with(getApplicationContext()).load(HttpMethods.BASE_URL + seller.getAvatar()).fit().error(R.drawable.unknow_avatar).into(icon);
        Picasso.with(getApplicationContext()).load(HttpMethods.BASE_URL + seller.getAvatar()).fit().error(R.drawable.unknow_avatar).into(iconSmall);
        setDefaultFragment();
    }


    @Override
    public void onBackPressed() {
        finish();
    }
}
