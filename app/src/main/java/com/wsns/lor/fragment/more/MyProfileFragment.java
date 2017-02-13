package com.wsns.lor.fragment.more;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.squareup.picasso.Picasso;
import com.wsns.lor.Activity.LoginActivity;
import com.wsns.lor.Activity.more.CheckConsumptionRecordsActivity;
import com.wsns.lor.Activity.more.UserInfoActivity;
import com.wsns.lor.R;
import com.wsns.lor.entity.DataAndCodeBean;
import com.wsns.lor.entity.RepairGoods;
import com.wsns.lor.entity.User;
import com.wsns.lor.http.HttpMethods;
import com.wsns.lor.http.subscribers.ProgressSubscriber;
import com.wsns.lor.http.subscribers.SubscriberOnNextListener;
import com.wsns.lor.utils.MD5;

import java.io.IOException;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;


public class MyProfileFragment extends Fragment {
    View view;
    Activity activity;
    ImageView av;
    TextView tvName, tvEmail, tvLevel, tvXp;
    TextView tvMoney;
    ProgressBar pbXp;
    LinearLayout linearLayout;
    RelativeLayout linearLayout0;
    RelativeLayout relativeLayout, rlMe;
   SubscriberOnNextListener getCurrentUser;
    User user;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (view == null) {
            view = inflater.inflate(R.layout.fragment_page_my_profile, null);
            activity = getActivity();
            av = (ImageView) view.findViewById(R.id.av_user);
            tvName = (TextView) view.findViewById(R.id.tv_user_name);
            tvMoney = (TextView) view.findViewById(R.id.tv_money);

            linearLayout0 = (RelativeLayout) view.findViewById(R.id.linearLayout0);
            linearLayout0.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                }
            });
            linearLayout = (LinearLayout) view.findViewById(R.id.linearLayout1);
            linearLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    startActivity(new Intent(getActivity(), CheckConsumptionRecordsActivity.class));
                }
            });

            relativeLayout = (RelativeLayout) view.findViewById(R.id.chong_money);
            relativeLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                }
            });

            linearLayout = (LinearLayout) view.findViewById(R.id.linearLayout_about);
            linearLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                }
            });

            linearLayout = (LinearLayout) view.findViewById(R.id.linearLayout_version);
            linearLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                }
            });

            rlMe = (RelativeLayout) view.findViewById(R.id.linear_me);
            rlMe.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(getActivity(), UserInfoActivity.class);
                    intent.putExtra("user", user);
                    startActivity(intent);
                }
            });

            getCurrentUser= new SubscriberOnNextListener<User>() {
                @Override
                public void onNext(User user) {
                    MyProfileFragment.this.user=user;
                    setUser(user);
                }
            };

        }
        return view;
    }



    @Override
    public void onResume() {
        super.onResume();
        getUser();
    }

    /**
     * 获取当前用户信息
     */
    public void getUser() {
        HttpMethods.getInstance().getCurrentUser(new ProgressSubscriber(getCurrentUser,activity,false));
    }

    public void setUser(User user) {
        Picasso.with(activity).load(HttpMethods.BASE_URL + user.getAvatar()).error(R.drawable.unknow_avatar).into(av);
        tvName.setText(user.getName());
        tvMoney.setText(user.getCoin() + "");

    }
}
