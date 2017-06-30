package com.wsns.lor.fragment.login;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.wsns.lor.R;

import java.lang.reflect.Field;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cn.smssdk.SMSSDK;

/**
 * Created by Administrator on 2017/1/5.
 */

public class ForgetPasswordFirstFragment extends Fragment{

    View view;
    Activity activity;

    EditText etMobile;
    Button btnNext;
    ImageView ivBack;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {

        if(view == null){
            view = inflater.inflate(R.layout.activity_regist,null);
            activity  = getActivity();
            init();
        }
        return view;
    }

    void init(){
        etMobile = (EditText) view.findViewById(R.id.et_mobile);
        btnNext = (Button) view.findViewById(R.id.btn_next);
        ivBack = (ImageView) view.findViewById(R.id.iv_back);

        etMobile.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if(charSequence.toString().length()==11){
                    btnNext.setBackground(getResources().getDrawable(R.color.colorPrimary));
                    btnNext.setTextColor(Color.parseColor("#ffffff"));
                    btnNext.setEnabled(true);
                }else{
                    btnNext.setBackgroundColor(Color.parseColor("#e4e2e2"));
                    btnNext.setTextColor(Color.parseColor("#c4c3c3"));
                    btnNext.setEnabled(false);
                }
            }
            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String phoneNum = etMobile.getText().toString();
                if(isPhone(phoneNum)) {
                  judgeUser(phoneNum);
                }else {
                    Toast.makeText(activity, "电话号码格式错误", Toast.LENGTH_SHORT).show();
                }
            }
        });

        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               goBack();
            }
        });

        btnNext.setEnabled(false);
    }
    /**
     * 判断电话号码格式
     * @param phoneNum
     * @return
     */
    boolean isPhone(String phoneNum){
        boolean flag = false;
        try {
            // 13********* ,15********,18*********
            Pattern p = Pattern.compile("^((13[0-9])|(15[^4,\\D])|(18[0,5-9]))\\d{8}$");
            Matcher m = p.matcher(phoneNum);
            flag = m.matches();

        } catch (Exception e) {
            flag = false;
        }
        return flag;
    };



    void judgeUser(String account){

          SMSSDK.getVerificationCode("86", etMobile.getText().toString());
         SharedPreferences sharedPreferences = activity.getSharedPreferences("forgetpassword", Context.MODE_PRIVATE);
         SharedPreferences.Editor editor = sharedPreferences.edit();
       editor.putString("phone", etMobile.getText().toString());
           editor.putString("country", "86");
      editor.commit();

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

    @Override
    public void onDetach() {
        super.onDetach();
        try {
            Field childFragmentManager = Fragment.class.getDeclaredField("mChildFragmentManager");
            childFragmentManager.setAccessible(true);
            childFragmentManager.set(this, null);

        } catch (NoSuchFieldException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }

    }

}
