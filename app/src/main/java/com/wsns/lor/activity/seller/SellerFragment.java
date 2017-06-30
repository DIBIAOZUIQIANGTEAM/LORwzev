package com.wsns.lor.activity.seller;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.wsns.lor.adapter.RepairTypeAdapter;
import com.wsns.lor.adapter.SellersListAdapter;
import com.wsns.lor.R;
import com.wsns.lor.http.entity.DataAndCodeBean;
import com.wsns.lor.http.entity.PublishAd;
import com.wsns.lor.http.entity.Seller;
import com.wsns.lor.http.HttpMethods;
import com.wsns.lor.http.subscribers.ProgressSubscriber;
import com.wsns.lor.http.subscribers.SubscriberOnNextListener;
import com.wsns.lor.utils.BDUtil;
import com.wsns.lor.utils.Densityutils;
import com.wsns.lor.utils.GlideImageLoader;
import com.wsns.lor.view.layout.VRefreshLayout;
import com.wsns.lor.view.widgets.JDHeaderView;
import com.youth.banner.Banner;
import com.youth.banner.BannerConfig;
import com.youth.banner.listener.OnBannerListener;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

import static android.view.View.GONE;
import static com.baidu.mapapi.UIMsg.l_ErrorNo.REQUEST_OK;
import static com.wsns.lor.application.OnlineUserInfo.address;
import static com.wsns.lor.application.OnlineUserInfo.latitude;
import static com.wsns.lor.application.OnlineUserInfo.longitude;

/**
 * 商家列表页Fragment
 */
public class SellerFragment extends Fragment implements BDLocationListener, AdapterView.OnItemClickListener, View.OnClickListener {

    public int page = 0;//查询第几页的结果，从0开始
    public int PageCount;//搜索结果的总页数
    BDLocation mBDLocation;
    public List<Seller> sellersList = new ArrayList<>();
    @BindView(R.id.ll_seller_header)
    RelativeLayout rlSellerHeader;

    @BindView(R.id.ll_seller_header3)
    LinearLayout llSellerHeader3;

    RecyclerView recyclerView;

    private View mView, headView;
    private LinearLayout location;
    private TextView addressText;
    private LocationClient mlocationClient;

    private View mJDHeaderView;
    private VRefreshLayout mRefreshLayout;
    Activity activity;
    View btnLoadMore, repairType, searchCategory;
    TextView textLoadMore;
    ListView mListView;
    SellersListAdapter adapter;
    SubscriberOnNextListener getSellersOnNext;
    SubscriberOnNextListener getSellersByTypeOnNext;
    SubscriberOnNextListener getAdvertisementOnNext;
    SubscriberOnNextListener getSellersOrderByTunoverOnNext;
    SubscriberOnNextListener getSellersOrderByCommentCountOnNext;


    Banner banner;
    RepairTypeAdapter repairTypeAdapter;
    int[] drawableId = {R.drawable.pc, R.drawable.repairtype_phone, R.drawable.repairtype_tv, R.drawable.repairtype_kongtiao
            , R.drawable.repairtype_bingxiang, R.drawable.repairtype_fengshan, R.drawable.repairtype_reshui, R.drawable.repairtype_else};
    String[] text = {"电脑", "手机", "电视", "空调", "冰箱", "风扇", "热水", "全部"};

    private LinearLayout comment, sale, distance;
    private TextView commentTv, saleTv, distanceTv;
    private TextView commentCopyTv, saleCopyTv, distanceCopyTv;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_seller, container, false);
        ButterKnife.bind(this, mView);
        activity = getActivity();

        adapter = new SellersListAdapter(activity, sellersList);
        repairTypeAdapter = new RepairTypeAdapter(activity, drawableId, text);
        mListView = (ListView) mView.findViewById(R.id.listView);
        btnLoadMore = inflater.inflate(R.layout.list_foot, null);
        repairType = inflater.inflate(R.layout.list_repair_type, null);
        searchCategory = inflater.inflate(R.layout.list_seller_search_category, null);

        recyclerView = (RecyclerView) repairType.findViewById(R.id.recyclerView);
        textLoadMore = (TextView) btnLoadMore.findViewById(R.id.loadmore);

        mListView.addFooterView(btnLoadMore);

        mListView.setAdapter(adapter);
        btnLoadMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (hasMore()) {
                    NearbySearch();
                }
            }
        });
        mListView.setOnItemClickListener(this);
        initHeaderView();
        initLoacionSetting();
        initBanner(inflater);
        mListView.addHeaderView(banner);
        mListView.addHeaderView(repairType);
        mListView.addHeaderView(searchCategory);


        getSellersOnNext = new SubscriberOnNextListener<List<Seller>>() {
            @Override
            public void onNext(List<Seller> sellers) {
                commentTv.setTextColor(Color.parseColor("#000000"));
                saleTv.setTextColor(Color.parseColor("#000000"));
                distanceTv.setTextColor(Color.parseColor("#3c9aff"));
                commentCopyTv.setTextColor(Color.parseColor("#000000"));
                saleCopyTv.setTextColor(Color.parseColor("#000000"));
                distanceCopyTv.setTextColor(Color.parseColor("#3c9aff"));
                mRefreshLayout.refreshComplete();
                sellersList.addAll(sellers);
                adapter.notifyDataSetChanged();
            }
        };

        getSellersByTypeOnNext = new SubscriberOnNextListener<List<Seller>>() {
            @Override
            public void onNext(List<Seller> sellers) {


                sellersList.addAll(sellers);
                adapter.notifyDataSetChanged();
            }
        };

        getSellersOrderByCommentCountOnNext = new SubscriberOnNextListener<List<Seller>>() {
            @Override
            public void onNext(List<Seller> sellers) {
                sellersList.addAll(sellers);
                adapter.notifyDataSetChanged();
            }
        };

        getSellersOrderByTunoverOnNext = new SubscriberOnNextListener<List<Seller>>() {
            @Override
            public void onNext(List<Seller> sellers) {

                sellersList.addAll(sellers);
                adapter.notifyDataSetChanged();
            }
        };

        getAdvertisementOnNext = new SubscriberOnNextListener<List<PublishAd>>() {
            @Override
            public void onNext(List<PublishAd> adList) {


                ArrayList urllist = new ArrayList();
                ArrayList deslist = new ArrayList();
                final ArrayList linklist = new ArrayList();
                for (int i = 0; i < adList.size(); i++) {
                    urllist.add(adList.get(i).getImg());
                    deslist.add(adList.get(i).getDes());
                    linklist.add(adList.get(i).getLink());
                }

                //设置banner样式
                banner.setBannerStyle(BannerConfig.NUM_INDICATOR_TITLE);
                //设置图片加载器
                banner.setImageLoader(new GlideImageLoader());
                //设置图片集合
                banner.setImages(urllist);
                banner.setBannerTitles(deslist);
                banner.setOnBannerListener(new OnBannerListener() {
                    @Override
                    public void OnBannerClick(int position) {

                    }
                });
                //banner设置方法全部调用完毕时最后调用
                banner.start();
            }
        };
        HttpMethods.getInstance().getAdvertisement(new ProgressSubscriber(getAdvertisementOnNext, activity, false));


//        StatusBarUtil.setTranslucentForImageView(activity, 0, banner);
        mListView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView absListView, int i) {
            }

            @Override
            public void onScroll(AbsListView absListView, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

                if (firstVisibleItem >= 2) {
                    llSellerHeader3.setVisibility(View.VISIBLE);
                    searchCategory.setVisibility(GONE);
                } else {
                    searchCategory.setVisibility(View.VISIBLE);
                    llSellerHeader3.setVisibility(GONE);
                }

            }
        });
        recyclerView.setLayoutManager(new GridLayoutManager(activity, 4));
        recyclerView.setAdapter(repairTypeAdapter);
        repairTypeAdapter.setOnRefreshListener(new RepairTypeAdapter.OnRefreshListener() {
            @Override
            public void onRefresh() {
                commentTv.setTextColor(Color.parseColor("#000000"));
                saleTv.setTextColor(Color.parseColor("#000000"));
                distanceTv.setTextColor(Color.parseColor("#3c9aff"));
                commentCopyTv.setTextColor(Color.parseColor("#000000"));
                saleCopyTv.setTextColor(Color.parseColor("#000000"));
                distanceCopyTv.setTextColor(Color.parseColor("#3c9aff"));
                if (repairTypeAdapter.getPressedItem() == 7) {
                    HttpMethods.getInstance().getSellers(new ProgressSubscriber<DataAndCodeBean<List<Seller>>>(getSellersOnNext, activity, false)
                            , BDUtil.location2Str(mBDLocation));
                } else {
                    HttpMethods.getInstance().getSellersByType(new ProgressSubscriber<DataAndCodeBean<List<Seller>>>(getSellersByTypeOnNext, activity, false)
                            , BDUtil.location2Str(mBDLocation), text[repairTypeAdapter.getPressedItem()]);
                }
                sellersList.clear();
                adapter.notifyDataSetChanged();
            }
        });
        findView();
        return mView;
    }

    private void initBanner(LayoutInflater inflater) {
        headView = inflater.inflate(R.layout.seller_list_head, null);
        banner = (Banner) headView.findViewById(R.id.banner);
    }


    private void initHeaderView() {
        mRefreshLayout = (VRefreshLayout) mView.findViewById(R.id.refresh_layout);
        if (mRefreshLayout != null) {
            mJDHeaderView = new JDHeaderView(activity);
            mJDHeaderView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, dp2px(64)));
            mRefreshLayout.setBackgroundColor(Color.DKGRAY);
            mRefreshLayout.setAutoRefreshDuration(400);
            mRefreshLayout.setRatioOfHeaderHeightToReach(1.5f);
            mRefreshLayout.addOnRefreshListener(new VRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    page = 0;
                    sellersList.clear();
                    NearbySearch();
                }
            });
        }
        mRefreshLayout.setHeaderView(mJDHeaderView);
        mRefreshLayout.setBackgroundColor(Color.WHITE);
    }

    //################网络请求方法#######################
    public void NearbySearch() {
        ProgressSubscriber progressSubscriber  =new ProgressSubscriber<DataAndCodeBean<List<Seller>>>(getSellersOnNext, activity, false);
        HttpMethods.getInstance().getSellers(progressSubscriber,BDUtil.location2Str(mBDLocation));
        System.out.println(BDUtil.location2Str(mBDLocation)+"BDUtil.location2Str(mBDLocation)");
        progressSubscriber.setVRefreshLayout(mRefreshLayout);
    }


    //################判断是否还有数据#######################
    public boolean hasMore() {
        return page != PageCount;
    }

    public void initLoacionSetting() {
        location = (LinearLayout) mView.findViewById(R.id.ll_location);
        addressText = (TextView) mView.findViewById(R.id.tv_title);
        addressText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                //#################3.进行第一个网络请求#####################
                if (!s.toString().equals("") && mBDLocation != null) {
                    System.out.println("位置变更：" + s);
                    page = 0;
                    sellersList.clear();
                    NearbySearch();
                }
            }
        });
        location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mlocationClient.stop();
                Intent intent = new Intent(getActivity(), LocationActivity.class);
                startActivityForResult(intent, 321);
            }
        });
        if (mlocationClient == null) {
            // 定位初始化
            mlocationClient = new LocationClient(activity.getApplicationContext());
            mlocationClient.registerLocationListener(this);
            LocationClientOption option = new LocationClientOption();
            option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);
            option.setCoorType("bd09ll"); // 设置坐标类型
            option.setIsNeedLocationDescribe(true);
            option.setIsNeedAddress(true);//可选，设置是否需要地址信息，默认不需要

            option.setScanSpan(0);
            mlocationClient.setLocOption(option);
            mlocationClient.start();
        }
    }


    @Override
    public void onResume() {
        super.onResume();
    }


    protected int dp2px(float dp) {
        return Densityutils.dp2px(activity, dp);
    }

    @Override
    public void onReceiveLocation(BDLocation bdLocation) {
        if (location == null) {
            return;
        }
        System.out.println("onReceiveLocation" + bdLocation.getLocationDescribe());
        mBDLocation = bdLocation;
        addressText.setText(bdLocation.getLocationDescribe());
    }


    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        if (i - 3 < 0)
            return;
        Intent intent = new Intent(getActivity(), SellerDetailsActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable("seller", sellersList.get(i - 3));
        intent.putExtras(bundle);
        startActivity(intent);
    }

    protected void findView() {
        comment = (LinearLayout) mView.findViewById(R.id.ll_comment);
        sale = (LinearLayout) mView.findViewById(R.id.ll_sale);
        distance = (LinearLayout) mView.findViewById(R.id.ll_distance);

        commentTv = (TextView) mView.findViewById(R.id.tv_comment);
        saleTv = (TextView) mView.findViewById(R.id.tv_sale);
        distanceTv = (TextView) mView.findViewById(R.id.tv_distance);


        commentCopyTv = (TextView) mView.findViewById(R.id.tv_comment_copy);
        saleCopyTv = (TextView) mView.findViewById(R.id.tv_sale_copy);
        distanceCopyTv = (TextView) mView.findViewById(R.id.tv_distance_copy);

        commentCopyTv.setOnClickListener(this);
        saleCopyTv.setOnClickListener(this);
        distanceCopyTv.setOnClickListener(this);

        comment.setOnClickListener(this);
        sale.setOnClickListener(this);
        distance.setOnClickListener(this);
    }

    void textCopy() {
        commentCopyTv.setText(commentTv.getText().toString());
        saleCopyTv.setText(saleTv.getText().toString());
        distanceCopyTv.setText(distanceTv.getText().toString());

        commentCopyTv.setTextColor(commentTv.getTextColors());
        saleCopyTv.setTextColor(saleTv.getTextColors());
        distanceCopyTv.setTextColor(distanceTv.getTextColors());
    }

    @Override
    public void onStart() {
        super.onStart();
        //开始轮播
        banner.startAutoPlay();
    }

    @Override
    public void onStop() {
        super.onStop();
        //结束轮播
        banner.stopAutoPlay();
    }


    @Override
    public void onClick(View v) {
        commentTv.setTextColor(Color.parseColor("#000000"));
        saleTv.setTextColor(Color.parseColor("#000000"));
        distanceTv.setTextColor(Color.parseColor("#000000"));
        commentCopyTv.setTextColor(Color.parseColor("#000000"));
        saleCopyTv.setTextColor(Color.parseColor("#000000"));
        distanceCopyTv.setTextColor(Color.parseColor("#000000"));
        switch (v.getId()) {
            case R.id.ll_comment:
                commentTv.setTextColor(Color.parseColor("#3c9aff"));
                commentCopyTv.setTextColor(Color.parseColor("#3c9aff"));
                HttpMethods.getInstance().getSellersOrderByCommentCount(new ProgressSubscriber<DataAndCodeBean<List<Seller>>>(getSellersOrderByCommentCountOnNext, activity, false));
                break;
            case R.id.tv_comment_copy:
                commentTv.setTextColor(Color.parseColor("#3c9aff"));
                commentCopyTv.setTextColor(Color.parseColor("#3c9aff"));
                HttpMethods.getInstance().getSellersOrderByCommentCount(new ProgressSubscriber<DataAndCodeBean<List<Seller>>>(getSellersOrderByCommentCountOnNext, activity, false));

                break;

            case R.id.ll_sale:
                saleCopyTv.setTextColor(Color.parseColor("#3c9aff"));
                saleTv.setTextColor(Color.parseColor("#3c9aff"));
                HttpMethods.getInstance().getSellersOrderByTunover(new ProgressSubscriber<DataAndCodeBean<List<Seller>>>(getSellersOrderByTunoverOnNext, activity, false));

                break;
            case R.id.tv_sale_copy:
                saleCopyTv.setTextColor(Color.parseColor("#3c9aff"));
                saleTv.setTextColor(Color.parseColor("#3c9aff"));
                HttpMethods.getInstance().getSellersOrderByTunover(new ProgressSubscriber<DataAndCodeBean<List<Seller>>>(getSellersOrderByTunoverOnNext, activity, false));

                break;
            case R.id.ll_distance:
                distanceTv.setTextColor(Color.parseColor("#3c9aff"));
                distanceCopyTv.setTextColor(Color.parseColor("#3c9aff"));
                if (repairTypeAdapter.getPressedItem() == 7) {
                    HttpMethods.getInstance().getSellers(new ProgressSubscriber<DataAndCodeBean<List<Seller>>>(getSellersOnNext, activity, false)
                            , BDUtil.location2Str(mBDLocation));
                } else {
                    HttpMethods.getInstance().getSellersByType(new ProgressSubscriber<DataAndCodeBean<List<Seller>>>(getSellersByTypeOnNext, activity, false)
                            , BDUtil.location2Str(mBDLocation), text[repairTypeAdapter.getPressedItem()]);
                }
                break;
            case R.id.tv_distance_copy:
                distanceTv.setTextColor(Color.parseColor("#3c9aff"));
                distanceCopyTv.setTextColor(Color.parseColor("#3c9aff"));
                if (repairTypeAdapter.getPressedItem() == 7) {
                    HttpMethods.getInstance().getSellers(new ProgressSubscriber<DataAndCodeBean<List<Seller>>>(getSellersOnNext, activity, false)
                            , BDUtil.location2Str(mBDLocation));
                } else {
                    HttpMethods.getInstance().getSellersByType(new ProgressSubscriber<DataAndCodeBean<List<Seller>>>(getSellersByTypeOnNext, activity, false)
                            , BDUtil.location2Str(mBDLocation), text[repairTypeAdapter.getPressedItem()]);
                }
                break;
        }
        sellersList.clear();
        adapter.notifyDataSetChanged();
        textCopy();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == REQUEST_OK) {
            addressText.setText(address);
            mBDLocation.setLatitude(latitude);
            mBDLocation.setLongitude(longitude);
        }
    }
}
