package com.wsns.lor.activity.login;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.wsns.lor.R;
import com.wsns.lor.http.entity.User;
import com.wsns.lor.http.HttpMethods;
import com.wsns.lor.http.subscribers.ProgressSubscriber;
import com.wsns.lor.http.subscribers.SubscriberOnNextListener;
import com.wsns.lor.utils.MD5;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.jpush.im.android.api.JMessageClient;
import cn.jpush.im.api.BasicCallback;

/**
 * 注册界面，需要设置账号密码邮箱。
 */
public class RegistersActivity extends Activity {

    @BindView(R.id.et_account)
    EditText etAccount;
    @BindView(R.id.et_email)
    EditText etEmail;
    @BindView(R.id.et_password)
    EditText etPassword;
    @BindView(R.id.et_repeat_password)
    EditText etRepeatPassword;
    @BindView(R.id.btn_next)
    Button btnNext;
    @BindView(R.id.iv_back)
    ImageView ivBack;

    SubscriberOnNextListener getRegisterResult;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_regist_third);
        ButterKnife.bind(this,this);

        //监听到网络请求成功后的操作
        getRegisterResult = new SubscriberOnNextListener<User>() {
            @Override
            public void onNext(User user) {
                registerSuccess(user);
            }
        };
    }

    //注册IM，成功后跳转RegisterSuccessActivity界面
    private void registerSuccess(User user) {
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("注册IM中");
        progressDialog.setCancelable(false);
        progressDialog.show();
        /**=================     调用SDK注册接口    =================*/
        JMessageClient.register(etAccount.getText().toString(), MD5.getMD5(etPassword.getText().toString()), new BasicCallback() {
            @Override
            public void gotResult(int responseCode, String registerDesc) {
                progressDialog.dismiss();
                if (responseCode == 0) {
                    Intent itnt = new Intent(RegistersActivity.this, RegisterSuccessActivity.class);
                    startActivity(itnt);
                    finish();
                    overridePendingTransition(R.anim.slide_in_left, R.anim.none);
                }
            }
        });
    }

    @OnClick({R.id.iv_back, R.id.btn_next})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_back:
                finish();
                overridePendingTransition(R.anim.none, R.anim.slide_out_bottom);
                break;
            case R.id.btn_next:
                //发起网络请求
                if (etAccount.getText().toString().equals("")) {
                    Toast.makeText(this, "账号不能为空", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (etEmail.getText().toString().equals("")) {
                    Toast.makeText(this, "Email不能为空", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (etPassword.getText().toString().equals("")) {
                    Toast.makeText(this, "密码不能为空", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (!etPassword.getText().toString().equals(etRepeatPassword.getText().toString())) {
                    Toast.makeText(this, "密码不一致", Toast.LENGTH_SHORT).show();
                    return;
                }
                User user = new User();
                user.setAccount(etAccount.getText().toString());
                user.setEmail(etEmail.getText().toString());
                user.setPasswordHash(MD5.getMD5(etPassword.getText().toString()));
                HttpMethods.getInstance().getRegisterResult(new ProgressSubscriber(getRegisterResult, this, true), user);
                break;
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        ivBack.performClick();
    }
}

