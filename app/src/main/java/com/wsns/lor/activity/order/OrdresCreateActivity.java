package com.wsns.lor.activity.order;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
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

import com.alipay.sdk.app.AuthTask;
import com.alipay.sdk.app.EnvUtils;
import com.alipay.sdk.app.PayTask;
import com.squareup.picasso.Picasso;
import com.wsns.lor.R;
import com.wsns.lor.http.entity.Orders;
import com.wsns.lor.http.entity.Seller;
import com.wsns.lor.http.HttpMethods;
import com.wsns.lor.http.subscribers.ProgressSubscriber;
import com.wsns.lor.http.subscribers.SubscriberOnNextListener;
import com.wsns.lor.other.pay.AuthResult;
import com.wsns.lor.other.pay.PayResult;
import com.wsns.lor.other.pay.util.OrderInfoUtil2_0;
import com.wsns.lor.utils.DataUtil;

import java.util.Map;

/**
 * 订单创建
 */
public class OrdresCreateActivity extends Activity {
    Orders orders;
    TextView contactNameText;
    TextView titleText;
    ImageView avatarImg, imageView;
    TextView quantityText;

    TextView sumText1;
    TextView sumText2;
    TextView paywayText;
    RelativeLayout payWayRL;
    TextView nameText, phoneText, schoolText;
    PopupWindow mPopupWindow;
    Button createBtn;
    boolean isPayOnline = true;
    Seller seller;
    private SubscriberOnNextListener getOrderCreateOnNext;

    /** 支付宝支付业务：入参app_id */
    public static final String APPID = "2016080400165870";

    /** 支付宝账户登录授权业务：入参pid值 */
    public static final String PID = "2088102169924642";
    /** 支付宝账户登录授权业务：入参target_id值 */
    public static final String TARGET_ID = "pxrsqh9084@sandbox.com";

    /** 商户私钥，pkcs8格式 */
    /** 如下私钥，RSA2_PRIVATE 或者 RSA_PRIVATE 只需要填入一个 */
    /** 如果商户两个都设置了，优先使用 RSA2_PRIVATE */
    /** RSA2_PRIVATE 可以保证商户交易在更加安全的环境下进行，建议使用 RSA2_PRIVATE */
    /** 获取 RSA2_PRIVATE，建议使用支付宝提供的公私钥生成工具生成， */
    /** 工具地址：https://doc.open.alipay.com/docs/doc.htm?treeId=291&articleId=106097&docType=1 */
    public static final String RSA2_PRIVATE = "MIIEvgIBADANBgkqhkiG9w0BAQEFAASCBKgwggSkAgEAAoIBAQCQOFRF2svOtPiOooFBmw5Q+h7Vin4nOmUhhKyLq7q9+pwafT0t5fS8hLyz7sdKQXQnzR5ecAIRKRMCfxd3GpDvEQ5Tzk6aBvBnfoo8n6Rx36R1jGJRc09k8U+cf2ozlESBGDKLAZGTEPDFv5JcPwuyG3BcmsOYF1XaaXejAuB/fXMiwaOrimjU8S9d0ZwiB2FtZe9tDK3IbZqmSrodE+wwg1vSGHA/9H6cMOsrvvCz9HJSTDX0Vhs0FTRZxsnYqOqsSsSC9zzyxGeixNoCoh+0SQY40zLr1ezrg8Ai93do7/LFpUYLbb09Kj3SmGQiKFPe51OdLUcEJolSuGdgVblnAgMBAAECggEAcHcs6Mil9M1Vo82AJMwpDubuUrJMjITSRqAy+jxyRSKAx3tw0TEpeDW+/kYvrW17imo/y39nbUBtrZ20i9Hwwi230Yzp6N2ObfbVEE+iKpcvpedS6JeWi25PGgiWpiByh4V6LMKNZnVofZ1WIo+brEoPhne+HgXckpV6kWl17pSOlk+1MBTFF5wSUbnYLuFhPA8stFBmuT0w4NPcOIXyRYsIKp+egkIs/Hf0C///gdcGH2APBhAcHw2bkZK5Yq/tUV6J4SAggpaZguk4WVV5/u3detIR6agZzj/JIxKHWHNol+/3yLG3Zx7wpEWwN1NYDjpKuTyCaAO5wvthYV7pUQKBgQDzxNgK57+c/XJw1fSprSthA3MnD5xW+nbA0J0DySc2shGvB1TWFTmjsmcAa4k7t879qYZ5S52caf9RmRGq+Eb+2Y50XsqTBRRvc5T8zfiYn4XHXqDsl8Pr2Uvuc2Qi8fs9kv89pusuQl0wMBO65EUhjNE28jHyx7p9gEr6A+HKiQKBgQCXdM12kFGio9m4SQ7krYZFM5zVLkrrwcR9JW9LKUgGnmus7xx1PEX6AvHaGc6sGsjIVZIEGSTr2gfjIKZ6rmKXphhhCJ6P/28fBbaFuqLt1Kl05aZBuK2Rxvp5yXG071xe8ccmOn8Fh+VDb76MfpUK7mYISxJFwxnc7usiOx2obwKBgQCJKpFaiY8DeoXDwlC6jUukejl4mEsjkinyUzCW6cBcAZT7xQdSs3zMhN16mggNh1880wBtDkRwuCBHvPb5Gl1wl+CV0KTyV2BpWkNSgV/KGbFAPxoUJ03CT3JviZSmKT1zJ1ziRyN80zD2pYbkmf/gYglzfpLmhsaESgVma3rI6QKBgF4C+qZl3sZ9vl9NuhjoPTgnWWpaWdcNZ9HmsraM4VB6dYvawGdlCmqt+UHQUyExXwA7XW99zBU6OTsawTvVkw1e5DMXDpWDkI5JFYrc1tWZ87XD9vdEkzixsjh2ekY9bbDhkyR/mujr+btqWXUL/afTYzIypxz2hc9F3l5ZI4exAoGBAI6xTL/1U5MN7503yDNVHgIWYP6d/ZK1MdU6EI45q4k6yT4sjCRRhhc9zBmZYJyx6gybivsGU5MPGTD0eJ6D/cATfqJ9beXcPauNyn3xADwOSSbkPaQml512AcQghce9akAQaJ4rNkUD3VsUoJdZFF5GZ/UU4oTBovZcJdz+AnKm";
    public static final String RSA_PRIVATE = "";

    public static final int SDK_PAY_FLAG = 1;
    public static final int SDK_AUTH_FLAG = 2;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ordres_create);
        EnvUtils.setEnv(EnvUtils.EnvEnum.SANDBOX);
        orders = (Orders) getIntent().getSerializableExtra("orders");
        seller = (Seller) getIntent().getSerializableExtra("sellers");
        initView();
    }

    private void initView() {
        imageView = (ImageView) findViewById(R.id.iv_add_contact_back);
        nameText = (TextView) findViewById(R.id.tv_publishers_name);
        avatarImg = (ImageView) findViewById(R.id.img_avatar);
        quantityText = (TextView) findViewById(R.id.tv_quantity);

        sumText1 = (TextView) findViewById(R.id.tv_sum_1);
        sumText2 = (TextView) findViewById(R.id.tv_sum_2);
        titleText = (TextView) findViewById(R.id.tv_title);
        paywayText = (TextView) findViewById(R.id.tv_pay_way);
        createBtn = (Button) findViewById(R.id.btn_orders_create);
        contactNameText = (TextView) findViewById(R.id.tv_name);
        phoneText = (TextView) findViewById(R.id.tv_phone);
        schoolText = (TextView) findViewById(R.id.tv_school);


        contactNameText.setText(orders.getRealName());
        phoneText.setText(orders.getPhone());
        schoolText.setText(orders.getAddress());
        titleText.setText(orders.getGoods());
        nameText.setText(seller.getName());
        quantityText.setText(orders.getWorkTime());
        sumText1.setText(DataUtil.doubleTrans1(seller.getService())+ "元");
        sumText2.setText(DataUtil.doubleTrans1(seller.getService())+ "");

        Picasso.with(this).load(HttpMethods.BASE_URL + seller.getAvatar()).resize(30, 30).centerInside().error(R.drawable.unknow_avatar).into(avatarImg);


        ;
        payWayRL = (RelativeLayout) findViewById(R.id.rl_pay_way);
        ;

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
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

        getOrderCreateOnNext = new SubscriberOnNextListener<Orders>() {
            @Override
            public void onNext(Orders orders) {
                OrdresCreateActivity.this.orders = orders;
                Intent intent = new Intent(OrdresCreateActivity.this, OrdersContentActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("orders_id", orders.getId() + "");
                intent.putExtras(bundle);
                setResult(RESULT_OK);
                startActivity(intent);
                finish();
            }
        };

    }

    private void createOrder() {
        orders.setPayOnline(isPayOnline);
        orders.setPrice(seller.getService());
        if (seller.getService() > 0 && isPayOnline) {
//            authV2();

            payV2("订单", DataUtil.doubleTrans1(seller.getService()));
        } else {
            HttpMethods.getInstance().getOrderCreateResult(new ProgressSubscriber(getOrderCreateOnNext, OrdresCreateActivity.this, true),
                    seller.getAccount(), orders);
        }


    }


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
                isPayOnline = true;
            }
        });
        payOutline.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                paywayText.setText("货到付款");
                isPayOnline = false;
                mPopupWindow.dismiss();
            }
        });

    }

    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {
        @SuppressWarnings("unused")
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case SDK_PAY_FLAG: {
                    @SuppressWarnings("unchecked")
                    PayResult payResult = new PayResult((Map<String, String>) msg.obj);
                    /**
                     对于支付结果，请商户依赖服务端的异步通知结果。同步通知结果，仅作为支付结束的通知。
                     */
                    String resultInfo = payResult.getResult();// 同步返回需要验证的信息
                    String resultStatus = payResult.getResultStatus();
                    // 判断resultStatus 为9000则代表支付成功
                    if (TextUtils.equals(resultStatus, "9000")) {
                        // 该笔订单是否真实支付成功，需要依赖服务端的异步通知。
                        Toast.makeText(OrdresCreateActivity.this, "支付成功", Toast.LENGTH_SHORT).show();
                        HttpMethods.getInstance().getOrderCreateResult(new ProgressSubscriber(getOrderCreateOnNext, OrdresCreateActivity.this, true),
                                seller.getAccount(), orders);
                    } else {
                        // 该笔订单真实的支付结果，需要依赖服务端的异步通知。
                        Toast.makeText(OrdresCreateActivity.this, "支付失败", Toast.LENGTH_SHORT).show();
                    }
                    break;
                }
                case SDK_AUTH_FLAG: {
                    @SuppressWarnings("unchecked")
                    AuthResult authResult = new AuthResult((Map<String, String>) msg.obj, true);
                    String resultStatus = authResult.getResultStatus();

                    // 判断resultStatus 为“9000”且result_code
                    // 为“200”则代表授权成功，具体状态码代表含义可参考授权接口文档
                    if (TextUtils.equals(resultStatus, "9000") && TextUtils.equals(authResult.getResultCode(), "200")) {
                        // 获取alipay_open_id，调支付时作为参数extern_token 的value
                        // 传入，则支付账户为该授权账户
                        Toast.makeText(OrdresCreateActivity.this,
                                "授权成功\n" + String.format("authCode:%s", authResult.getAuthCode()), Toast.LENGTH_SHORT)
                                .show();

                        payV2("订单", DataUtil.doubleTrans1(seller.getService()));

                    } else {
                        // 其他状态值则为授权失败
                        Toast.makeText(OrdresCreateActivity.this,
                                "授权失败" + String.format("authCode:%s", resultStatus), Toast.LENGTH_SHORT).show();

                    }
                    break;
                }
                default:
                    break;
            }
        }

        ;
    };

    /**
     * 支付宝支付业务
     */
    public void payV2(String name, String money) {
        if (TextUtils.isEmpty(APPID) || (TextUtils.isEmpty(RSA2_PRIVATE) && TextUtils.isEmpty(RSA_PRIVATE))) {
            new AlertDialog.Builder(this).setTitle("警告").setMessage("需要配置APPID | RSA_PRIVATE")
                    .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialoginterface, int i) {
                            //
                            finish();
                        }
                    }).show();
            return;
        }

        /**
         * 这里只是为了方便直接向商户展示支付宝的整个支付流程；所以Demo中加签过程直接放在客户端完成；
         * 真实App里，privateKey等数据严禁放在客户端，加签过程务必要放在服务端完成；
         * 防止商户私密数据泄露，造成不必要的资金损失，及面临各种安全风险；
         *
         * orderInfo的获取必须来自服务端；
         */
        boolean rsa2 = (RSA2_PRIVATE.length() > 0);
        Map<String, String> params = OrderInfoUtil2_0.buildOrderParamMap(APPID, rsa2, name, money);
        String orderParam = OrderInfoUtil2_0.buildOrderParam(params);

        String privateKey = rsa2 ? RSA2_PRIVATE : RSA_PRIVATE;
        String sign = OrderInfoUtil2_0.getSign(params, privateKey, rsa2);
        final String orderInfo = orderParam + "&" + sign;

        Runnable payRunnable = new Runnable() {

            @Override
            public void run() {
                PayTask alipay = new PayTask(OrdresCreateActivity.this);


                Map<String, String> result = alipay.payV2(orderInfo, true);
                Log.i("msp", result.toString());

                Message msg = new Message();
                msg.what = SDK_PAY_FLAG;
                msg.obj = result;
                mHandler.sendMessage(msg);
            }
        };

        Thread payThread = new Thread(payRunnable);
        payThread.start();
    }

    /**
     * 支付宝账户授权业务
     *
     */
    public void authV2() {
        if (TextUtils.isEmpty(PID) || TextUtils.isEmpty(APPID)
                || (TextUtils.isEmpty(RSA2_PRIVATE) && TextUtils.isEmpty(RSA_PRIVATE))
                || TextUtils.isEmpty(TARGET_ID)) {
            new AlertDialog.Builder(this).setTitle("警告").setMessage("需要配置PARTNER |APP_ID| RSA_PRIVATE| TARGET_ID")
                    .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialoginterface, int i) {
                        }
                    }).show();
            return;
        }

        /**
         * 这里只是为了方便直接向商户展示支付宝的整个支付流程；所以Demo中加签过程直接放在客户端完成；
         * 真实App里，privateKey等数据严禁放在客户端，加签过程务必要放在服务端完成；
         * 防止商户私密数据泄露，造成不必要的资金损失，及面临各种安全风险；
         *
         * authInfo的获取必须来自服务端；
         */
        boolean rsa2 = (RSA2_PRIVATE.length() > 0);
        Map<String, String> authInfoMap = OrderInfoUtil2_0.buildAuthInfoMap(PID, APPID, TARGET_ID, rsa2);
        String info = OrderInfoUtil2_0.buildOrderParam(authInfoMap);

        String privateKey = rsa2 ? RSA2_PRIVATE : RSA_PRIVATE;
        String sign = OrderInfoUtil2_0.getSign(authInfoMap, privateKey, rsa2);
        final String authInfo = info + "&" + sign;
        Runnable authRunnable = new Runnable() {

            @Override
            public void run() {
                // 构造AuthTask 对象
                AuthTask authTask = new AuthTask(OrdresCreateActivity.this);
                // 调用授权接口，获取授权结果
                Map<String, String> result = authTask.authV2(authInfo, true);

                Message msg = new Message();
                msg.what = SDK_AUTH_FLAG;
                msg.obj = result;
                mHandler.sendMessage(msg);
            }
        };

        // 必须异步调用
        Thread authThread = new Thread(authRunnable);
        authThread.start();
    }

}
