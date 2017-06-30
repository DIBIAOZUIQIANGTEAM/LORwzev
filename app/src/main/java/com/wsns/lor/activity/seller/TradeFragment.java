package com.wsns.lor.activity.seller;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.wsns.lor.activity.order.OrdresCreateActivity;
import com.wsns.lor.adapter.TypeTagAdapter;
import com.wsns.lor.view.layout.listener.OnTagSelectListener;
import com.wsns.lor.R;
import com.wsns.lor.http.entity.Orders;
import com.wsns.lor.http.entity.RepairGoods;
import com.wsns.lor.http.entity.Seller;
import com.wsns.lor.http.HttpMethods;
import com.wsns.lor.http.subscribers.ProgressSubscriber;
import com.wsns.lor.http.subscribers.SubscriberOnNextListener;
import com.wsns.lor.utils.StringDivide;
import com.wsns.lor.utils.ToastUtil;
import com.wsns.lor.view.layout.FlowTagLayout;

import java.util.ArrayList;
import java.util.List;

import static android.app.Activity.RESULT_OK;


public class TradeFragment extends Fragment {
    private TextView tvstorename;
    private EditText etdescribe;
    private EditText etworktime;
    private EditText etname;
    private EditText etaddress;
    private EditText ettel;
    private FlowTagLayout mBrandFlowTagLayout;
    private FlowTagLayout mTypeFlowTagLayout;
    private FlowTagLayout mChoiceFlowTagLayout;
    private TypeTagAdapter<String> mBrandTagAdapter;
    private TypeTagAdapter<String> mTypeTagAdapter;
    private TypeTagAdapter<String> mChoiceTagAdapter;
    private List<RepairGoods> allDataSource = new ArrayList<>();
    String selectbrand;
    String selecttype;
    private Button submit;
    private List<String> dataSource = new ArrayList<>();
    View view;
    SubscriberOnNextListener<List<RepairGoods>> getGoodsResult;
    Seller seller;
    String goods;
    Activity activity;
    LinearLayout goodsLL;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        activity = getActivity();
        view = inflater.inflate(R.layout.fragment_trade, container, false);
        mBrandFlowTagLayout = (FlowTagLayout) view.findViewById(R.id.brand_flow_layout);
        mTypeFlowTagLayout = (FlowTagLayout) view.findViewById(R.id.type_flow_layout);
        mChoiceFlowTagLayout = (FlowTagLayout) view.findViewById(R.id.choice_flow_layout);
        tvstorename = (TextView) view.findViewById(R.id.tv_storename);
        etdescribe = (EditText) view.findViewById(R.id.et_describe);
        etworktime = (EditText) view.findViewById(R.id.et_worktime);
        etname = (EditText) view.findViewById(R.id.et_name);
        etaddress = (EditText) view.findViewById(R.id.et_address);
        ettel = (EditText) view.findViewById(R.id.et_tel);
        submit = (Button) view.findViewById(R.id.bt_submit);
        goodsLL = (LinearLayout) view.findViewById(R.id.ll_goods);
        goodsLL.setVisibility(View.GONE);
        tvstorename.setText(seller.getName());

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UploadOrder();
            }
        });

        //品牌
        mBrandTagAdapter = new TypeTagAdapter<>(getActivity());
        mBrandFlowTagLayout.setTagCheckedMode(FlowTagLayout.FLOW_TAG_CHECKED_SINGLE);
        mBrandFlowTagLayout.setAdapter(mBrandTagAdapter);
        mBrandFlowTagLayout.setOnTagSelectListener(new OnTagSelectListener() {
            @Override
            public void onItemSelect(FlowTagLayout parent, List<Integer> selectedList) {
                if (selectedList != null && selectedList.size() > 0) {
                    StringBuilder sb = new StringBuilder();
                    for (int i : selectedList) {
                        sb.append(parent.getAdapter().getItem(i));
                        selectbrand = parent.getAdapter().getItem(i).toString();
                        initChoiceData(selectbrand);
                        initTypeData(i);
                    }
                } else {
                    initChoiceData("");
                }
            }
        });

        //型号标签
        mTypeTagAdapter = new TypeTagAdapter<>(getActivity());
        mTypeFlowTagLayout.setTagCheckedMode(FlowTagLayout.FLOW_TAG_CHECKED_SINGLE);
        mTypeFlowTagLayout.setAdapter(mTypeTagAdapter);
        mTypeFlowTagLayout.setOnTagSelectListener(new OnTagSelectListener() {
            @Override
            public void onItemSelect(FlowTagLayout parent, List<Integer> selectedList) {
                submit.setText("提交");
                submit.setClickable(true);
                if (selectedList != null && selectedList.size() > 0) {
                    StringBuilder sb = new StringBuilder();

                    for (int i : selectedList) {
                        sb.append(parent.getAdapter().getItem(i));
                        selecttype = parent.getAdapter().getItem(i).toString();
                        goods = selectbrand + "," + selecttype;
                        initChoiceData(goods);
                    }

                } else {
                    initChoiceData("");
                }
            }
        });
        //结果
        mChoiceTagAdapter = new TypeTagAdapter<>(getActivity());
        mChoiceFlowTagLayout.setTagCheckedMode(FlowTagLayout.FLOW_TAG_CHECKED_NONE);
        mChoiceFlowTagLayout.setAdapter(mChoiceTagAdapter);


        getGoodsResult = new SubscriberOnNextListener<List<RepairGoods>>() {
            @Override
            public void onNext(List<RepairGoods> list) {
                if (list != null) {
                    goodsLL.setVisibility(View.VISIBLE);

                    getGoodsList(list);
                }
            }
        };

        initChoiceData("");
        initTypeData(-1);
        LoadGoodsData();
        return view;
    }

    private void UploadOrder() {
        String type;
        if (selectbrand != null && selecttype != null)
            type = selectbrand + selecttype;
        else
            type = "其他";

        if(TextUtils.isEmpty(etdescribe.getText().toString())||TextUtils.isEmpty(etworktime.getText().toString())
                ||TextUtils.isEmpty(etname.getText().toString())||TextUtils.isEmpty(etaddress.getText().toString())
                ||TextUtils.isEmpty(ettel.getText().toString()))
        {
            ToastUtil.show(activity,"请完整填写预约信息");
            return;
        }

        Intent intent = new Intent(getActivity(), OrdresCreateActivity.class);
        Bundle bundle = new Bundle();
        Orders orders = new Orders();
        orders.setGoods(type);
        orders.setNote(etdescribe.getText().toString());
        orders.setWorkTime(etworktime.getText().toString());
        orders.setRealName(etname.getText().toString());
        orders.setAddress(etaddress.getText().toString());
        orders.setPhone(ettel.getText().toString());
        bundle.putSerializable("orders", orders);
        bundle.putSerializable("sellers", seller);
        intent.putExtras(bundle);
        startActivityForResult(intent, 0);
    }


    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }


    private void initChoiceData(String choice) {
        List<String> dataSource = new ArrayList<>();
        if (!choice.equals("")) {
            dataSource.add(choice.toString());
            mChoiceTagAdapter.clearAndAddAll(dataSource);
        } else {
            dataSource.add("未选择");
            mChoiceTagAdapter.clearAndAddAll(dataSource);

        }

    }

    private void initTypeData(int i) {
        List<String> dataSource = new ArrayList<>();
        if (i == -1) {
            dataSource.add("请先选择生厂商");
        } else if (i < allDataSource.size()) {
            StringDivide dd = new StringDivide(allDataSource.get(i).getType());
            for (int j = 0; j < dd.getCount(); j++) {
                dataSource.add(dd.getItem(j));
            }
            dataSource.add("其他");
        }

        mTypeTagAdapter.clearAndAddAll(dataSource);
    }


    /**
     * 初始化数据
     */
    private void initBrandData() {
        dataSource.clear();
        for (int i = 0; i < allDataSource.size(); i++) {

            dataSource.add(allDataSource.get(i).getBrand());
        }
        for (int i = 0; i < allDataSource.size(); i++) {
            System.out.println(dataSource.get(i) + "~" + allDataSource.get(i).getBrand());
        }
        dataSource.add("其他");
        mBrandTagAdapter.clearAndAddAll(dataSource);
    }


    public void LoadGoodsData() {
        HttpMethods.getInstance().getGoodsResult(new ProgressSubscriber(getGoodsResult, getActivity(), false),
                seller.getAccount());
    }

    private void getGoodsList(List<RepairGoods> list) {
        allDataSource.clear();
        for (int i = 0; i < list.size(); i++) {
            allDataSource.add(list.get(i));
        }
        initBrandData();
    }


    public void setSeller(Seller seller) {
        this.seller = seller;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
//            MainActivity.ordersPage=2;
            activity.finish();

        }
    }
}
