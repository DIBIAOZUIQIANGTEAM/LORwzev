package com.wsns.lor.fragment.personal;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;

import com.hengda.zwf.httputil.FileCallback;
import com.squareup.picasso.Picasso;
import com.tencent.mm.opensdk.modelbase.BaseReq;
import com.tencent.mm.opensdk.modelbase.BaseResp;
import com.tencent.mm.opensdk.modelmsg.SendMessageToWX;
import com.tencent.mm.opensdk.modelmsg.WXMediaMessage;
import com.tencent.mm.opensdk.modelmsg.WXWebpageObject;
import com.tencent.mm.opensdk.openapi.IWXAPIEventHandler;
import com.wsns.lor.R;
import com.wsns.lor.activity.personal.CheckConsumptionRecordsActivity;
import com.wsns.lor.activity.personal.OrdersCommenActivity;
import com.wsns.lor.activity.personal.UserInfoActivity;
import com.wsns.lor.application.Config;
import com.wsns.lor.http.HttpMethods;
import com.wsns.lor.http.entity.User;
import com.wsns.lor.http.subscribers.ProgressSubscriber;
import com.wsns.lor.http.subscribers.SubscriberOnNextListener;
import com.wsns.lor.other.download.DataManager;
import com.wsns.lor.other.download.FileRequester;
import com.wsns.lor.other.download.HDialogBuilder;

import java.io.File;

import cn.jpush.android.api.JPushInterface;
import cn.jpush.im.android.api.JMessageClient;
import okhttp3.ResponseBody;
import retrofit2.Call;

import static com.wsns.lor.application.LorApplication.IS_MY_COMMENT;
import static com.wsns.lor.application.LorApplication.api;

;


public class MyProfileFragment extends Fragment implements IWXAPIEventHandler {
    View view;
    Activity activity;
    ImageView av;
    TextView tvName;
    TextView tvMoney;
    ProgressBar pbXp;
    LinearLayout linearLayout, ll_share;
    RelativeLayout rl_my_comment, rl_my_coin, rl_my_record;
    RelativeLayout rlMe;
    SubscriberOnNextListener getCurrentUser;
    SubscriberOnNextListener getVersion;
    User user;
    TextView txtProgress;
    HDialogBuilder hDialogBuilder;
    Switch notificationSwitch;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (view == null) {
            view = inflater.inflate(R.layout.fragment_page_my_profile, null);
            activity = getActivity();
            av = (ImageView) view.findViewById(R.id.av_user);
            tvName = (TextView) view.findViewById(R.id.tv_user_name);
            tvMoney = (TextView) view.findViewById(R.id.tv_my_coin);
            api.handleIntent(activity.getIntent(),this);
            rl_my_comment = (RelativeLayout) view.findViewById(R.id.rl_1);
            rl_my_comment.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    IS_MY_COMMENT = true;
                    Intent intent = new Intent(activity, OrdersCommenActivity.class);
                    startActivity(intent);
                }
            });
            rl_my_record = (RelativeLayout) view.findViewById(R.id.rl_3);
            rl_my_record.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    startActivity(new Intent(getActivity(), CheckConsumptionRecordsActivity.class));
                }
            });

            rl_my_coin = (RelativeLayout) view.findViewById(R.id.rl_2);
            rl_my_coin.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                }
            });


            notificationSwitch = (Switch) view.findViewById(R.id.switch_notification);
            notificationSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    setNotificationSwitch(isChecked);
                    if (isChecked) {
                        JPushInterface.setAlias(activity, JMessageClient.getMyInfo().getUserName(), null);
                    } else {
                        JPushInterface.setAlias(activity, "", null);
                    }
                }
            });

            SharedPreferences preferences = activity.getSharedPreferences("notification", Context.MODE_PRIVATE);
            notificationSwitch.setChecked(preferences.getBoolean("check", true));

            linearLayout = (LinearLayout) view.findViewById(R.id.linearLayout_version);
            ll_share = (LinearLayout) view.findViewById(R.id.linearLayout_share);
            linearLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    HttpMethods.getInstance().getVersion(new ProgressSubscriber(getVersion, activity, false), Config.localVersion);
                }
            });
            ll_share.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    WXWebpageObject webpage = new WXWebpageObject();
                    webpage.webpageUrl = HttpMethods.BASE_URL;

                    WXMediaMessage msg = new WXMediaMessage(webpage);
                    msg.title = "应修联盟";
                    msg.description = "应修联盟超好用的哦！快来一起用吧~";
                    Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher);
                    msg.thumbData = bitmap.getNinePatchChunk();
                    SendMessageToWX.Req req = new SendMessageToWX.Req();
                    req.transaction = "lor";
                    req.message = msg;
                    req.scene = SendMessageToWX.Req.WXSceneSession;
                    api.sendReq(req);
                    System.out.println("应修联盟");             }
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

            getCurrentUser = new SubscriberOnNextListener<User>() {
                @Override
                public void onNext(User user) {
                    MyProfileFragment.this.user = user;
                    setUser(user);
                }
            };

            getVersion = new SubscriberOnNextListener() {
                @Override
                public void onNext(Object o) {
                    checkVersion();
                }
            };

        }
        return view;
    }

    /**
     * 检查更新版本
     */
    public void checkVersion() {

        // 发现新版本，提示用户更新
        final AlertDialog.Builder alert = new AlertDialog.Builder(activity);
        alert.setTitle("版本更新")
                .setMessage("发现新版本,建议立即更新使用.")
                .setPositiveButton("更新",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,
                                                int which) {
                                String fileName = "app-debug.apk";
                                String fileStoreDir =activity.getApplicationContext().getExternalFilesDir(null)
                                        + File.separator + "apk";

                                showLoadingDialog();
                                FileRequester.getInstance(HttpMethods.BASE_URL)
                                        .loadFileByName(new FileCallback(fileStoreDir, fileName) {
                                            @Override
                                            public void onSuccess(File file) {
                                                hDialogBuilder.dismiss();
                                                Uri uri = FileProvider.getUriForFile(activity, "com.mydomain.fileprovider", file);;
                                                Intent installIntent = new Intent(Intent.ACTION_VIEW);
                                                installIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                                                installIntent.setDataAndType(uri,
                                                        "application/vnd.android.package-archive");
                                                startActivity(installIntent);
                                            }

                                            @Override
                                            public void progress(long progress, long total) {
                                                updateProgress(progress, total);
                                            }

                                            @Override
                                            public void onFailure(Call<ResponseBody> call, Throwable t) {
                                                System.out.println("t" + t.toString());
                                                hDialogBuilder.dismiss();
                                                call.cancel();
                                            }
                                        });


                            }
                        })
                .setNegativeButton("取消",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,
                                                int which) {

                                FileRequester.cancelLoading();
                                dialog.dismiss();
                            }
                        });
        alert.create().show();

    }

    /**
     * 更新下载进度
     *
     * @param progress
     * @param total
     */
    private void updateProgress(long progress, long total) {
        txtProgress.setText(String.format("正在下载：(%s/%s)",
                DataManager.getFormatSize(progress),
                DataManager.getFormatSize(total)));
    }

    /**
     * 显示下载对话框
     */
    private void showLoadingDialog() {
        txtProgress = (TextView) View.inflate(activity,
                R.layout.layout_hd_dialog_custom_tv, null);
        hDialogBuilder = new HDialogBuilder(activity);
        hDialogBuilder.setCustomView(txtProgress)
                .title("下载")
                .nBtnText("取消")
                .nBtnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        hDialogBuilder.dismiss();
                        FileRequester.cancelLoading();
                    }
                })
                .show();
    }

    public void setNotificationSwitch(boolean notificationSwitch) {
        SharedPreferences preferences = activity.getSharedPreferences("notification", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("check", notificationSwitch);
        editor.commit();
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
        HttpMethods.getInstance().getCurrentUser(new ProgressSubscriber(getCurrentUser, activity, false));
    }

    public void setUser(User user) {
        Picasso.with(activity).load(HttpMethods.BASE_URL + user.getAvatar())
                .placeholder(R.drawable.unknow_avatar)
                .error(R.drawable.unknow_avatar).into(av);
        tvName.setText(user.getName());
        tvMoney.setText(user.getCoin() + "");

    }

    @Override
    public void onReq(BaseReq baseReq) {

    }

    @Override
    public void onResp(BaseResp baseResp) {
        System.out.println("应修联盟"+baseResp.errCode+baseResp.errStr);
    }
}
