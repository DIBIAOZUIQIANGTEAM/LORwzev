package com.wsns.lor.fragment.login;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import com.wsns.lor.R;

import java.util.Timer;
import java.util.TimerTask;

import cn.smssdk.SMSSDK;

/**
 * Created by Administrator on 2017/1/5.
 */

public class ForgetPasswordSecondFragment extends Fragment{

    View view;
    Activity activity;

    ImageView ivBack;
    EditText etCode;
    TextView tvSend;
    Button btnNext;
    TextView tvTeap;

    String phone;
    String country;

    private int recLen =60;
    Timer timer;
    final Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg){
            switch (msg.what) {
                case 1:
                    tvSend.setText("重新发送"+"("+recLen+")");
                    if(recLen < 0){
                        tvSend.setEnabled(true);
                        tvSend.setText("重新发送");
                        tvSend.setTextColor(getResources().getColor(R.color.colorAccent));
                    }
            }
        }
    };

    TimerTask task = new TimerTask() {
        @Override
        public void run() {
            recLen--;
            Message message = new Message();
            message.what = 1;
            handler.sendMessage(message);
        }
    };
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        if(view==null){
            view = inflater.inflate(R.layout.activity_regist_second,null);
            activity = getActivity();
            init();
        }
        return view;
    }

    void init(){
        SharedPreferences sharedPreferences = activity.getSharedPreferences("forgetpassword", Context.MODE_PRIVATE);
        phone = sharedPreferences.getString("phone","");
        country = sharedPreferences.getString("country","");

        ivBack = (ImageView) view.findViewById(R.id.iv_back);
        etCode = (EditText) view.findViewById(R.id.et_code);
        tvSend = (TextView) view.findViewById(R.id.tv_recover_send);
        btnNext = (Button) view.findViewById(R.id.btn_next);
        tvTeap = (TextView) view.findViewById(R.id.tv_teap);

        tvTeap.setText("我们已经给你的手机号码"+country+"-"+phone+"发送了一条验证短信。");
        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goBack();
            }
        });
        tvSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SMSSDK.getVerificationCode(country,phone);
                tvSend.setEnabled(false);
                recLen = 60;
            }
        });
        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(etCode.getText().toString().equals("")){
                    Toast.makeText(activity,"输入不能为空",Toast.LENGTH_SHORT).show();
                    return;
                }
                SMSSDK.submitVerificationCode(country,phone,etCode.getText().toString());

            }
        });
        tvSend.setTextColor(Color.parseColor("#dddddd"));
        tvSend.setEnabled(false);
        timer = new Timer();
        timer.schedule(task, 1000, 1000);

    }

    @Override
    public void onResume() {
        super.onResume();
        SharedPreferences sharedPreferences = activity.getSharedPreferences("forgetpassword", Context.MODE_PRIVATE);
        String phones = sharedPreferences.getString("phone","");
        String countrys = sharedPreferences.getString("country","");
        if(!(phone.equals(phones)&&country.equals(countrys))){
            tvTeap.setText("我们已经给你的手机号码"+country+"-"+phone+"发送了一条验证短信。");
            tvSend.setTextColor(Color.parseColor("#dddddd"));
            tvSend.setEnabled(false);
            recLen = 60;
            phone = phones;
            country = countrys;
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        timer.cancel();
    }

    public static interface OnGoNextListener{
        void onGoNext();
    }

    OnGoNextListener onGoNextListener;

    public void setOnGoNextListener(OnGoNextListener onGoNextListener) {
        this.onGoNextListener = onGoNextListener;
    }

    void goNext(){
        if(onGoNextListener!=null){
            onGoNextListener.onGoNext();
        }
    }
    public static interface OnGoBackListener{
        void onGoBack();
    }
    OnGoBackListener onGoBackListener;
    public void setOnGoBackListener(OnGoBackListener onGoBackListener){
        this.onGoBackListener = onGoBackListener;
    }
    void goBack(){
        if(onGoBackListener!=null){
            onGoBackListener.onGoBack();
        }
    }
}
