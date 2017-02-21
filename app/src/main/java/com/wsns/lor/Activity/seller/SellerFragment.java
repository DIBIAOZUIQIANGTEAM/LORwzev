package com.wsns.lor.Activity.seller;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.cloud.CloudListener;
import com.baidu.mapapi.cloud.CloudManager;
import com.baidu.mapapi.cloud.CloudPoiInfo;
import com.baidu.mapapi.cloud.CloudRgcResult;
import com.baidu.mapapi.cloud.CloudSearchResult;
import com.baidu.mapapi.cloud.DetailSearchResult;
import com.baidu.mapapi.cloud.NearbySearchInfo;
import com.wsns.lor.Adapter.SellersListAdapter;
import com.wsns.lor.R;
import com.wsns.lor.entity.Sellers;
import com.wsns.lor.utils.BDUtil;
import com.wsns.lor.utils.Densityutils;
import com.wsns.lor.view.layout.VRefreshLayout;
import com.wsns.lor.view.widgets.JDHeaderView;

import java.util.ArrayList;
import java.util.List;

/**
 * 商家列表页Fragment
 */
public class SellerFragment extends Fragment implements BDLocationListener, CloudListener,AdapterView.OnItemClickListener{

    public int page = 0;//查询第几页的结果，从0开始
    public int PageCount;//搜索结果的总页数
    BDLocation mBDLocation;
    public List<Sellers> sellersList = new ArrayList<>();
    private View mView;
    private LinearLayout location;
    private TextView addressText;
    private LocationClient mlocationClient;

    private View mJDHeaderView;
    private VRefreshLayout mRefreshLayout;
    Activity activity;
    View btnLoadMore;
    TextView textLoadMore;
    boolean firstBuilt = true;
    ListView mListView;
    SellersListAdapter adapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_seller, container, false);
        activity = getActivity();
        adapter = new SellersListAdapter(activity, sellersList);
        CloudManager.getInstance().init(this);

        mListView = (ListView) mView.findViewById(R.id.listView);
        btnLoadMore = inflater.inflate(R.layout.list_foot, null);
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

        return mView;
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
        NearbySearchInfo info = new NearbySearchInfo();
        info.ak = "IwiUF0Rcfn1ckrPLeABUw4r9OwXEL6NP";
        info.geoTableId = 115331;
        info.radius = 3000;
        info.sortby = "distance:1";
        info.location = BDUtil.location2Str(mBDLocation);

        CloudManager.getInstance().nearbySearch(info);
    }


    //################判断是否还有数据#######################
    public boolean hasMore() {
        return page != PageCount;
    }

    public void initLoacionSetting() {
        location = (LinearLayout) mView.findViewById(R.id.linearLayout);
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
                    NearbySearch();
                }
            }
        });
        location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), LocationActivity.class);
                startActivity(intent);
            }
        });
        if (mlocationClient == null) {
            // 定位初始化
            mlocationClient = new LocationClient(activity);
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
    public void onGetSearchResult(CloudSearchResult result, int i) {
        mRefreshLayout.refreshComplete();
        if (result != null && result.poiList != null
                && result.poiList.size() > 0) {
            for (CloudPoiInfo info : result.poiList) {

//                System.out.println(info.distance + " " + info.title);
//                Sellers sellers = new Sellers();
//                sellers.setTitle(info.title);
//                sellers.setStar((Double) info.extras.get("star"));
//                sellers.setTurnover((int) info.extras.get("turnover"));
//                sellers.setDistance(info.distance);
//                sellers.setTradeTypes(0);
//                sellers.setCity(info.city);
//                sellers.setRepairsTypes((String) info.extras.get("repairsTypes"));
////                sellers.setMinimums((int) info.extras.get("minimums"));
//                sellers.setAvatar((String) info.extras.get("avatar"));
//                sellers.setAccount((String) info.extras.get("account"));
//
//                sellersList.add(sellers);
            }
        }
        adapter.notifyDataSetChanged();

    }

    @Override
    public void onGetDetailSearchResult(DetailSearchResult detailSearchResult, int i) {

    }

    @Override
    public void onGetCloudRgcResult(CloudRgcResult cloudRgcResult, int i) {

    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        Intent intent = new Intent(getActivity(), SellerDetailsActivity.class);
        Bundle bundle=new Bundle();
        bundle.putSerializable("seller",sellersList.get(i));
        intent.putExtras(bundle);
        startActivity(intent);
    }
}
