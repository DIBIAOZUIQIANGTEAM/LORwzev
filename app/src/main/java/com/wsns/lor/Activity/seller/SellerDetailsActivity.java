package com.wsns.lor.Activity.seller;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.wsns.lor.R;
import com.wsns.lor.entity.Sellers;
import com.wsns.lor.http.HttpMethods;
import com.wsns.lor.utils.ToastUtil;


/**
 * Created by cuiyang on 15/12/16.
 */
public class SellerDetailsActivity extends Activity {

    TextView tvmonthlytrading;
    TextView tvcomment;
    TextView tvstorename;
    TextView tvstoretype;
    CoordinatorLayout root_layout;
    LinearLayout linearLayout;
    ImageButton ibchat;
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
    TextView tvtitle;
    RatingBar star;
    CollapsingToolbarLayoutState state;
    Sellers sellers;

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
        sellers= (Sellers) getIntent().getSerializableExtra("seller");
        initView();
//        initViewPagerAndTabs();
        setData();
  }

    private void initView() {
        root_layout = (CoordinatorLayout) findViewById(R.id.root_layout);
        appBarLayout = (AppBarLayout) findViewById(R.id.appBarLayout);
        linearLayout = (LinearLayout) findViewById(R.id.ll_comment);
        ibchat = (ImageButton) findViewById(R.id.ib_chat);
        contentLayout = (FrameLayout) findViewById(R.id.content_layout);
        icon = (ImageView) findViewById(R.id.iv_icon);
        iconSmall= (ImageView) findViewById(R.id.iv_icon_small);
        transLayout = (LinearLayout) findViewById(R.id.trans_layout);
        alphaLayout = (LinearLayout) findViewById(R.id.alpha_layout);
        tabLayout = (LinearLayout) findViewById(R.id.tab_layout);
        tvcomment = (TextView) findViewById(R.id.tv_comment);
        tvstorename = (TextView) findViewById(R.id.tv_name);
        tvstoretype = (TextView) findViewById(R.id.tv_category);
        backButton = (ImageView) findViewById(R.id.iv_back);
        star = (RatingBar) findViewById(R.id.star);
        tvmonthlytrading = (TextView) findViewById(R.id.tv_monthlytrading);
        tvtitle = (TextView) findViewById(R.id.tv_title);
        collapsingToolbar = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);

        if (collapsingToolbar != null) {
            //设置隐藏图片时候ToolBar的颜色
            collapsingToolbar.setContentScrimColor(Color.parseColor("#ed844b"));
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


        linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ToastUtil.show(SellerDetailsActivity.this, "评论");
            }
        });
        ibchat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ToastUtil.show(SellerDetailsActivity.this, "咨询");
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
        tradeFragment.setSeller(sellers);
    }

//    private void initViewPagerAndTabs() {
//        //等待绘图完成再进行动画这样才能取到控件的宽高 oncreate中只是绘制
//        root_layout.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
//            @Override
//            public boolean onPreDraw() {
//                root_layout.getViewTreeObserver().removeOnPreDrawListener(this);
//                startCircularReveal();
//                return true;
//            }
//        });
//    }

//    /**
//     * 模仿createCircularReveal方法实现的效果.
//     */
//    //  ViewAnimationUtils.createCircularReveal(contentLayout, 0, 0, 0, (float) Math.hypot(contentLayout.getWidth(), contentLayout.getHeight())).setDuration(1500).start();不能在主线程直接.目前试验只能在点击事件里才行.
//    private void startCircularReveal() {
//        ViewPropertyAnimator animator = animationLayout.animate();
//        animator.scaleY(contentLayout.getHeight()).scaleX(contentLayout.getWidth()).setDuration(800).setStartDelay(300).setInterpolator(new AccelerateInterpolator()).start();
//        animator.setListener(new AnimatorListenerAdapter() {
//            @Override
//            public void onAnimationEnd(Animator animation) {
//                startTitleViewAimation();
//            }
//        });
//    }

//    private void startTitleViewAimation() {
//        root_layout.setVisibility(View.VISIBLE);
//
//        icon.setTranslationY(-icon.getHeight());
//        icon.animate().translationY(0f).setDuration(800).setInterpolator(mDecelerateInterpolator);
//
//        transLayout.setTranslationY(-transLayout.getHeight());
//        transLayout.animate().translationY(0f).setDuration(1300).setInterpolator(mDecelerateInterpolator);
//
//        ObjectAnimator animator1 = new ObjectAnimator().ofFloat(alphaLayout, "alpha", 0, 1f);
//        ObjectAnimator animator2 = new ObjectAnimator().ofFloat(tabLayout, "alpha", 0, 1f);
//        AnimatorSet set = new AnimatorSet();
//        set.playTogether(animator1, animator2);
//        set.setDuration(1000);
//        set.setInterpolator(mDecelerateInterpolator);
//        set.start();
//    }


    public void back(View view) {
        finish();
    }

    private void setData() {
        tvcomment.setText(sellers.getTurnover()+"");
        tvstorename.setText(sellers.getTitle());
        tvstoretype.setText("维修类别:" + sellers.getRepairsTypes());
        tvtitle.setText(sellers.getTitle());//设置title
        star.setRating(sellers.getStar().floatValue());
        tvmonthlytrading.setText(sellers.getMinimums()+"");
        Picasso.with(getApplicationContext()).load(HttpMethods.BASE_URL+sellers.getAvatar()).fit().error(R.drawable.unknow_avatar).into(icon);
        Picasso.with(getApplicationContext()).load(HttpMethods.BASE_URL+sellers.getAvatar()).fit().error(R.drawable.unknow_avatar).into(iconSmall);
        setDefaultFragment();
    }


    @Override
    public void onBackPressed() {
        finish();
    }
}
