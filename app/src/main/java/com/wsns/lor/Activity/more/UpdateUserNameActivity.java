package com.wsns.lor.Activity.more;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.wsns.lor.R;
import com.wsns.lor.entity.User;
import com.wsns.lor.http.HttpMethods;
import com.wsns.lor.http.subscribers.ProgressSubscriber;
import com.wsns.lor.http.subscribers.SubscriberOnNextListener;

import java.io.IOException;

import cn.jpush.im.android.api.JMessageClient;
import cn.jpush.im.android.api.model.UserInfo;
import cn.jpush.im.api.BasicCallback;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class UpdateUserNameActivity extends AppCompatActivity {
    Button btnUpdate;
    SubscriberOnNextListener updateNameOnNext;
    ImageView iv_back;
    EditText fragUserName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_user_name);

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
    }

    public void update() {
        HttpMethods.getInstance().updateName(new ProgressSubscriber(updateNameOnNext, UpdateUserNameActivity.this, false),
                fragUserName.getText().toString());
        }
    public void updateJM() {
        UserInfo myInfo = JMessageClient.getMyInfo();
        myInfo.setNickname(fragUserName.getText().toString());
        JMessageClient.updateMyInfo(UserInfo.Field.nickname, myInfo, new BasicCallback() {
            @Override
            public void gotResult(int i, String s) {
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
public void onBackPressed(){
        finish();
        overridePendingTransition(R.anim.none,R.anim.slide_out_left);
        super.onBackPressed();
        }
        }
