package com.wsns.lor.activity.personal;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;
import com.wsns.lor.R;
import com.wsns.lor.other.chatting.utils.FileHelper;
import com.wsns.lor.http.entity.DataAndCodeBean;
import com.wsns.lor.http.entity.User;
import com.wsns.lor.fragment.personal.InfoListFragment;
import com.wsns.lor.http.HttpMethods;
import com.wsns.lor.http.subscribers.ProgressSubscriber;
import com.wsns.lor.http.subscribers.SubscriberOnNextListener;
import com.wsns.lor.utils.DateToString;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import cn.jpush.android.api.JPushInterface;
import cn.jpush.im.android.api.JMessageClient;
import cn.jpush.im.api.BasicCallback;
import okhttp3.MediaType;
import okhttp3.RequestBody;

/**
 * Created by Administrator on 2016/12/21.
 */

public class UserInfoActivity extends Activity {

    User user;
    ImageView userAvatar, backImg;
    RelativeLayout rlAvatar, rlUpdatePassword;

    InfoListFragment fragmentUserName = new InfoListFragment();
    InfoListFragment fragmentUsercoins = new InfoListFragment();
    InfoListFragment fragmentUserCreatedate = new InfoListFragment();

    Button btnLoginOut;

    final int REQUESTCODE_CAMERA = 1;
    final int REQUESTCODE_ALBUM = 2;
    final int REQUESTCODE_CUTTING = 3;
    private String mPath;
    private Uri mUri;
    SubscriberOnNextListener updateAvatarOnNext;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        user = (User) getIntent().getSerializableExtra("user");
        setContentView(R.layout.activity_userinfo);
        backImg = (ImageView) findViewById(R.id.iv_back);
        rlUpdatePassword = (RelativeLayout) findViewById(R.id.rl_change_password);
        userAvatar = (ImageView) findViewById(R.id.av_user_avatar);
        rlAvatar = (RelativeLayout) findViewById(R.id.fragment_avatar);
        fragmentUserName = (InfoListFragment) getFragmentManager().findFragmentById(R.id.fragment_user_name);
        fragmentUsercoins = (InfoListFragment) getFragmentManager().findFragmentById(R.id.fragment_user_coins);
        fragmentUserCreatedate = (InfoListFragment) getFragmentManager().findFragmentById(R.id.fragment_user_createdate);
        btnLoginOut = (Button) findViewById(R.id.btn_login_out);

        backImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        rlAvatar.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                onImageViewClicked();
            }
        });
        InfoListFragment.InfoOnClickListener infoOnClickListener = new InfoListFragment.InfoOnClickListener() {
            @Override
            public void onclick() {
                Intent itnt = new Intent(UserInfoActivity.this, UpdateUserNameActivity.class);
                startActivity(itnt);
                overridePendingTransition(R.anim.slide_in_left, R.anim.none);
            }
        };
        fragmentUserName.setInfoOnClickListener(infoOnClickListener);

        rlUpdatePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent itnt = new Intent(UserInfoActivity.this, UpdatePasswordActivity.class);
                itnt.putExtra("user",user);
                startActivityForResult(itnt,222);
            }
        });

        btnLoginOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(UserInfoActivity.this,"注销成功",Toast.LENGTH_LONG).show();
                SharedPreferences preferences = getSharedPreferences("user", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = preferences.edit();
                editor.putBoolean(user.getAccount() + "auto", false);
                editor.commit();
                JMessageClient.logout();
                JPushInterface.setAlias(UserInfoActivity.this,"",null);
                finish();

            }
        });

        updateAvatarOnNext=new SubscriberOnNextListener() {
            @Override
            public void onNext(Object o) {
                updateAvatarJM();
            }
        };

        reLoad(user);
    }

    private void updateAvatarJM() {
        final ProgressDialog progressDialog = new ProgressDialog(UserInfoActivity.this);
        progressDialog.setMessage("更新中");
        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();
        final File avatar = new File(mUri.getPath());
        if (avatar.exists()) {
            JMessageClient.updateUserAvatar(avatar, new BasicCallback() {
                @Override
                public void gotResult(final int i, String s) {
                    System.out.println(i + "  " + s);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            progressDialog.dismiss();
                            if (i == 0) {
                                Picasso.with(UserInfoActivity.this).load(HttpMethods.BASE_URL+ user.getAvatar())
                                        .memoryPolicy(MemoryPolicy.NO_CACHE)
                                        .networkPolicy(NetworkPolicy.NO_CACHE)
                                        .placeholder(R.drawable.unknow_avatar) .error(R.drawable.unknow_avatar).into(userAvatar);
                            }
                        }
                    });
                }
            });
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        fragmentUserName.setTvUserContent(JMessageClient.getMyInfo().getNickname());
    }

    void reLoad(User user){
       this. user=user;
        Picasso.with(UserInfoActivity.this).load(HttpMethods.BASE_URL+ user.getAvatar())
                .memoryPolicy(MemoryPolicy.NO_CACHE)
                .networkPolicy(NetworkPolicy.NO_CACHE)
               .placeholder(R.drawable.unknow_avatar) .error(R.drawable.unknow_avatar).into(userAvatar);
        fragmentUserName.setTvUserAttribute("昵称");
        fragmentUserName.setTvUserContent(JMessageClient.getMyInfo().getNickname());
        fragmentUsercoins.setTvUserAttribute("余额");
        fragmentUsercoins.setTvUserContent(user.getCoin() + "");
        fragmentUserCreatedate.setTvUserAttribute("注册时间");

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = null;
        try {
            date = sdf.parse(user.getCreateDate());
            fragmentUserCreatedate.setTvUserContent(DateToString.getStringDateYYMMDD(date));
        } catch (ParseException e) {
            e.printStackTrace();
        }
    };


    void onImageViewClicked() {
        String[] items = {
                "拍照",
                "相册"
        };
        new AlertDialog.Builder(UserInfoActivity.this).setTitle("选择照片")
                .setItems(items, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        switch (i) {
                            case 0:
                                takePhoto();
                                break;
                            case 1:
                                selectImageFromLocal();
                                break;
                            default:
                        }
                    }
                }).setNegativeButton("取消", null).show();
    }

    private void takePhoto() {
        if (FileHelper.isSdCardExist()) {
            mPath = FileHelper.createAvatarPath(JMessageClient.getMyInfo().getUserName());
            File file = new File(mPath);
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(file));
            startActivityForResult(intent, REQUESTCODE_CAMERA);
        } else {
            Toast.makeText(this, this.getString(R.string.jmui_sdcard_not_exist_toast), Toast.LENGTH_SHORT).show();
        }
    }

    public void selectImageFromLocal() {
        Intent intent;
        if (Build.VERSION.SDK_INT < 19) {
            intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("image/*");
        } else {
            intent = new Intent(Intent.ACTION_PICK,
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        }
        startActivityForResult(intent,REQUESTCODE_ALBUM);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_CANCELED) {
            return;
        }
        if (requestCode == 222) {
            finish();
        }
        if (requestCode == REQUESTCODE_CAMERA) {
            if (mPath != null) {
                mUri = Uri.fromFile(new File(mPath));
                cropRawPhoto(mUri);
            }
        } else if (requestCode == REQUESTCODE_ALBUM) {
            if (data != null) {
                Uri selectedImg = data.getData();
                if (selectedImg != null) {
                    String[] filePathColumn = {MediaStore.Images.Media.DATA};
                    Cursor cursor = this.getContentResolver()
                            .query(selectedImg, filePathColumn, null, null, null);
                    try {
                        if (null == cursor) {
                            String path = selectedImg.getPath();
                            File file = new File(path);
                            if (file.isFile()) {
                                copyAndCrop(file);
                                return;
                            } else {
                                Toast.makeText(this, this.getString(R.string.picture_not_found),
                                        Toast.LENGTH_SHORT).show();
                                return;
                            }
                        } else if (!cursor.moveToFirst()) {
                            Toast.makeText(this, this.getString(R.string.picture_not_found),
                                    Toast.LENGTH_SHORT).show();
                            return;
                        }
                        int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                        String path = cursor.getString(columnIndex);
                        if (path != null) {
                            File file = new File(path);
                            if (!file.isFile()) {
                                Toast.makeText(this, this.getString(R.string.picture_not_found),
                                        Toast.LENGTH_SHORT).show();
                                cursor.close();
                            } else {
                                //如果是选择本地图片进行头像设置，复制到临时文件，并进行裁剪
                                copyAndCrop(file);
                                cursor.close();
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }else if (requestCode == REQUESTCODE_CUTTING) {
            updateAvatar(mUri.getPath());
        }
    }

    /**
     * 裁剪图片
     */
    public void cropRawPhoto(Uri uri) {
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri, "image/*");
        // 设置裁剪
        intent.putExtra("crop", "true");
        // aspectX , aspectY :宽高的比例
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        // outputX , outputY : 裁剪图片宽高
        intent.putExtra("outputX", 300);
        intent.putExtra("outputY", 300);
        intent.putExtra("return-data", false);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
        this.startActivityForResult(intent, REQUESTCODE_CUTTING);
    }

    /**
     * 复制后裁剪文件
     * @param file 要复制的文件
     */
    private void copyAndCrop(final File file) {
        FileHelper.getInstance().copyFile(file, this, new FileHelper.CopyFileCallback() {
            @Override
            public void copyCallback(Uri uri) {
                mUri = uri;
                cropRawPhoto(mUri);
            }
        });
    }

    /**
     * 上传头像
     */
    public void updateAvatar(String path) {
        if (path != null) {
            RequestBody imgFile = RequestBody.create(MediaType.parse("image/*"), new File(path));
            HttpMethods.getInstance().updateAvatar(new ProgressSubscriber<DataAndCodeBean<User>>(updateAvatarOnNext, this, true), imgFile);
        }
    }
}
