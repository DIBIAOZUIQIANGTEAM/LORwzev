package com.wsns.lor.Activity.order;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;


import com.wsns.lor.Activity.LoginActivity;
import com.wsns.lor.R;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.squareup.picasso.Picasso;
import com.wsns.lor.entity.Orders;
import com.wsns.lor.entity.RepairGoods;
import com.wsns.lor.entity.Sellers;
import com.wsns.lor.entity.User;
import com.wsns.lor.http.HttpMethods;
import com.wsns.lor.http.subscribers.ProgressSubscriber;
import com.wsns.lor.http.subscribers.SubscriberOnNextListener;
import com.wsns.lor.utils.MD5;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * 订单创建
 */
public class OrdresCreateActivity extends Activity {
    private static final int RESULT_GETNOTE = 101;
    Orders orders;
    TextView contactNameText;
    TextView titleText;
    ImageView avatarImg,imageView;
    TextView quantityText;
    TextView priceText;
    TextView sumText1;
    TextView sumText2;
    TextView paywayText;
    TextView noteText;
    RelativeLayout  noteRL, payWayRL;
    TextView nameText, phoneText, schoolText;
    PopupWindow mPopupWindow;
    Button createBtn;
    boolean isPayOnline=true;
    Sellers seller;
    private SubscriberOnNextListener getOrderCreateOnNext;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ordres_create);
        orders = (Orders) getIntent().getSerializableExtra("orders");
        seller = (Sellers) getIntent().getSerializableExtra("sellers");
        initView();
    }

    private void initView() {
        imageView = (ImageView)findViewById(R.id.iv_add_contact_back);
        nameText = (TextView) findViewById(R.id.tv_publishers_name);
        avatarImg = (ImageView) findViewById(R.id.img_avatar);
        quantityText = (TextView) findViewById(R.id.tv_quantity);
        priceText = (TextView) findViewById(R.id.tv_price);
        sumText1 = (TextView) findViewById(R.id.tv_sum_1);
        sumText2 = (TextView) findViewById(R.id.tv_sum_2);
        titleText = (TextView) findViewById(R.id.tv_title);
        paywayText = (TextView) findViewById(R.id.tv_pay_way);
        noteText = (TextView) findViewById(R.id.tv_note);
        createBtn = (Button) findViewById(R.id.btn_orders_create);
        contactNameText = (TextView) findViewById(R.id.tv_name);
        phoneText = (TextView) findViewById(R.id.tv_phone);
        schoolText = (TextView) findViewById(R.id.tv_school);


        noteText.setText(orders.getNote());
        contactNameText.setText(orders.getRealName());
        phoneText.setText(orders.getPhone());
        schoolText.setText(orders.getAddress());
        titleText.setText(orders.getGoods());
        nameText.setText(seller.getTitle());
        quantityText.setText(orders.getWorkTime());
        priceText.setText(orders.getPrice() + "");
        sumText1.setText(orders.getPrice() + "元");
        sumText2.setText(orders.getPrice() + "");

        Picasso.with(this).load(HttpMethods.BASE_URL+seller.getAvatar()).resize(30, 30).centerInside().error(R.drawable.unknow_avatar).into(avatarImg);


        noteRL = (RelativeLayout) findViewById(R.id.rl_note);
        ;
        payWayRL = (RelativeLayout) findViewById(R.id.rl_pay_way);
        ;

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });


        noteRL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(OrdresCreateActivity.this, NoteEditActivity.class);
                startActivityForResult(intent, RESULT_GETNOTE);
                overridePendingTransition(R.anim.slide_in_left, R.anim.none);
            }
        });

        payWayRL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setPaywayPopupView();
                mPopupWindow.showAtLocation(view, Gravity.BOTTOM, 0, -200);
            }
        });

        createBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createOrder();
            }
        });

        getOrderCreateOnNext= new SubscriberOnNextListener<Orders>() {
            @Override
            public void onNext(Orders orders) {
                 OrdresCreateActivity.this.orders=orders;
                Intent intent=new Intent(OrdresCreateActivity.this,OrdersContentActivity.class);
                Bundle bundle=new Bundle();
                bundle.putSerializable("orders",orders);
                intent.putExtras(bundle);
                setResult(RESULT_OK);
                startActivity(intent);
                finish();
            }
        };
    }

    private void createOrder() {
        HttpMethods.getInstance().getOrderCreateResult(new ProgressSubscriber(getOrderCreateOnNext, OrdresCreateActivity.this, true),
               seller.getAccount(),orders.getGoods(),orders.getWorkTime(),orders.getRealName()
                ,orders.getAddress(),orders.getPhone(),orders.getPrice(),orders.getNote(),orders.isPayOnline());
    }


//                            Toast.makeText(OrdresCreateActivity.this, "订单创建成功" , Toast.LENGTH_SHORT).show();
//                            //发送新订单消息
//                            Map<String, String> valuesMap  = new HashMap<>();
//                            valuesMap.put("msg_type", MsgType.MSG_ORDERS);
//                            valuesMap.put("orders_state", "1");
//                            valuesMap.put("orders_id", orders.getId()+"");
//                            CreateSigMsg.context=getApplicationContext();
////                            CreateSigMsg.CreateSigCustomMsg(orders.getGoods().getPublishers().getAccount(),valuesMap);
//                            CreateSigMsg.CreateSigTextMsg(orders.getGoods().getPublishers().getAccount(),
//                                    "订单消息","您有一份来自"+orders.getBuyer().getName()+"的新订单",valuesMap);
//                            Intent intent=new Intent(OrdresCreateActivity.this,OrdersContentActivity.class);
//                            intent.putExtra("orders_id",orders.getId());
//                            setResult(RESULT_OK);
//                            startActivity(intent);
//                            finish();



    private void setPaywayPopupView() {
        View popupView = getLayoutInflater().inflate(R.layout.dialog_pay_way, null);

        mPopupWindow = new PopupWindow(popupView, RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT, true);
        mPopupWindow.setTouchable(true);
        mPopupWindow.setOutsideTouchable(true);
        mPopupWindow.setAnimationStyle(R.style.mystyle);
        mPopupWindow.getContentView().setFocusableInTouchMode(true);
        mPopupWindow.getContentView().setFocusable(true);
        // 设置背景颜色变暗
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        lp.alpha = 0.7f;
        getWindow().setAttributes(lp);

        mPopupWindow.getContentView().setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_MENU && event.getRepeatCount() == 0
                        && event.getAction() == KeyEvent.ACTION_DOWN) {
                    if (mPopupWindow != null && mPopupWindow.isShowing()) {
                        mPopupWindow.dismiss();
                    }
                    return true;
                }
                return false;
            }
        });
        mPopupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {

            @Override
            public void onDismiss() {
                WindowManager.LayoutParams lp = getWindow().getAttributes();
                lp.alpha = 1f;
                getWindow().setAttributes(lp);
            }
        });

        TextView payOnline = (TextView) popupView.findViewById(R.id.tv_pay_online);
        TextView payOutline = (TextView) popupView.findViewById(R.id.tv_pay_outline);
        payOnline.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                paywayText.setText("在线支付");
                mPopupWindow.dismiss();
                isPayOnline=true;
            }
        });
        payOutline.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                paywayText.setText("货到付款");
                isPayOnline=false;
                mPopupWindow.dismiss();
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case RESULT_GETNOTE:
                    noteText.setVisibility(View.VISIBLE);
                    noteText.setText(data.getExtras().getString("content"));
                    break;
                default:
                    break;
            }

        }


    }
}
