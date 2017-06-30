package com.wsns.lor.activity.personal;

import android.app.ProgressDialog;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.wsns.lor.R;
import com.wsns.lor.http.entity.User;
import com.wsns.lor.http.HttpMethods;
import com.wsns.lor.http.subscribers.ProgressSubscriber;
import com.wsns.lor.http.subscribers.SubscriberOnNextListener;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.jpush.im.android.api.JMessageClient;
import cn.jpush.im.android.api.model.UserInfo;
import cn.jpush.im.api.BasicCallback;

public class UpdateUserNameActivity extends AppCompatActivity {
    Button btnUpdate;
    SubscriberOnNextListener updateNameOnNext;
    ImageView iv_back;
    EditText fragUserName;
    @BindView(R.id.tv_init_nickname)
    TextView tvInitNickname;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_user_name);
        ButterKnife.bind(this);

        fragUserName = (EditText) findViewById(R.id.et_new_name);
        btnUpdate = (Button) findViewById(R.id.btn_update_name);
        iv_back = (ImageView) findViewById(R.id.iv_back);

        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                update();

            }
        });
        iv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
                overridePendingTransition(R.anim.none, R.anim.slide_out_left);
            }
        });

        updateNameOnNext = new SubscriberOnNextListener<User>() {
            @Override
            public void onNext(User user) {
                updateJM();
            }
        };

        fragUserName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if(charSequence.toString().length()>0){
                    btnUpdate.setBackground(getResources().getDrawable(R.color.colorPrimary));
                    btnUpdate.setTextColor(Color.parseColor("#ffffff"));
                    btnUpdate.setEnabled(true);
                }else{
                    btnUpdate.setBackgroundColor(Color.parseColor("#e4e2e2"));
                    btnUpdate.setTextColor(Color.parseColor("#c4c3c3"));
                    btnUpdate.setEnabled(false);
                }
            }
            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        if (JMessageClient.getMyInfo().getNickname().equals(""))
        {
            iv_back.setVisibility(View.GONE);
        }
        else {
            tvInitNickname.setVisibility(View.GONE);
        }

        btnUpdate.setEnabled(false);
    }

    public void update() {
        try {
            HttpMethods.getInstance().updateName(new ProgressSubscriber(updateNameOnNext, UpdateUserNameActivity.this, true),
                    URLEncoder.encode(fragUserName.getText().toString(), "utf-8") );
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    public void updateJM() {
        final ProgressDialog progressDialog=new ProgressDialog(this);
        progressDialog.setMessage("修改昵称中");
        progressDialog.setCancelable(false);
        progressDialog.show();
        UserInfo myInfo = JMessageClient.getMyInfo();
        myInfo.setNickname(fragUserName.getText().toString());
        JMessageClient.updateMyInfo(UserInfo.Field.nickname, myInfo, new BasicCallback() {
            @Override
            public void gotResult(int i, String s) {
                progressDialog.dismiss();
                if (i == 0) {
                    Log.i("UpdateUserInfoActivity", "updateNickName," + " responseCode = " + i + "; desc = " + s);
                    Toast.makeText(UpdateUserNameActivity.this, "修改成功", Toast.LENGTH_SHORT).show();
                    finish();
                    overridePendingTransition(R.anim.none, R.anim.slide_out_left);
                } else {
                    Log.i("UpdateUserInfoActivity", "updateNickName," + " responseCode = " + i + "; desc = " + s);
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        if (iv_back.getVisibility()!=View.GONE) {
            finish();
            overridePendingTransition(R.anim.none, R.anim.slide_out_left);
        }
    }
}
