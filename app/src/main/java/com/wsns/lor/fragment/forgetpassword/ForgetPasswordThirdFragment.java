package com.wsns.lor.fragment.forgetpassword;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;


import com.wsns.lor.Activity.more.UpdatePasswordActivity;
import com.wsns.lor.R;
import com.wsns.lor.entity.User;
import com.wsns.lor.http.HttpMethods;
import com.wsns.lor.http.subscribers.ProgressSubscriber;
import com.wsns.lor.http.subscribers.SubscriberOnNextListener;
import com.wsns.lor.utils.MD5;

import java.io.IOException;

import cn.jpush.im.android.api.JMessageClient;
import cn.jpush.im.api.BasicCallback;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by Administrator on 2017/1/5.
 */

public class ForgetPasswordThirdFragment extends Fragment{

    View view;
    Activity activity;

    String phone = "";

    Button btnNext;
    ImageView ivback;
    EditText etPassword;
    EditText etRepatedPassword;
    SubscriberOnNextListener forgetPasswordOnNext;
    SubscriberOnNextListener findUserOnNext;
    User user;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        if(view == null){
            view = inflater.inflate(R.layout.fragment_regist_third,null);
            activity = getActivity();
            SharedPreferences sharedPreferences = activity.getSharedPreferences("forgetpassword", Context.MODE_PRIVATE);
            phone = sharedPreferences.getString("phone","");
            init();
        }
        return view;
    }

    void init(){
        btnNext = (Button) view.findViewById(R.id.btn_next);
        ivback = (ImageView) view.findViewById(R.id.iv_back);
        etPassword = (EditText) view.findViewById(R.id.et_mobile);
        etRepatedPassword = (EditText) view.findViewById(R.id.et_repated_password);
        btnNext.setEnabled(false);
        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                regist();
            }
        });
        ivback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goBack();
            }
        });
        forgetPasswordOnNext= new SubscriberOnNextListener<User>() {
            @Override
            public void onNext(User user) {
                updatePasswordJM();
            }
        };
        findUserOnNext= new SubscriberOnNextListener<User>() {
            @Override
            public void onNext(User user) {
               ForgetPasswordThirdFragment.this.user= user;
                btnNext.setEnabled(true);
            }
        };

        HttpMethods.getInstance().findUser(new ProgressSubscriber(findUserOnNext, activity, false),
              phone);
    }

    private void updatePasswordJM() {

        JMessageClient.login(user.getAccount(), user.getPasswordHash(), new BasicCallback() {
            @Override
            public void gotResult(int i, String s) {
                if (i == 0) {
                    JMessageClient.updateUserPassword(user.getPasswordHash(), MD5.getMD5(etPassword.getText().toString()), new BasicCallback() {
                        @Override
                        public void gotResult(int responseCode, String updadePasswordDesc) {
                            if (responseCode == 0) {
                                activity.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(activity, "修改成功", Toast.LENGTH_SHORT).show();
                                        JMessageClient.logout();
                                        activity.finish();
                                    }
                                });
                            } else {
                                activity.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(activity, "修改失败", Toast.LENGTH_SHORT).show();
                                        JMessageClient.logout();
                                        activity.finish();
                                    }
                                });
                            }
                        }
                    });
                }
            }
        });
    }

    public void regist(){
        final String password = etPassword.getText().toString();
        if(!password.equals(etRepatedPassword.getText().toString())){
            Toast.makeText(activity,"前后密码不一致",Toast.LENGTH_SHORT).show();
            return;
        }
        HttpMethods.getInstance().forgetPassword(new ProgressSubscriber(forgetPasswordOnNext, activity, true),
                phone,MD5.getMD5(etPassword.getText().toString()));
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
    public void goBack(){
        if(onGoBackListener!=null){
            onGoBackListener.onGoBack();
        }
    }

}
