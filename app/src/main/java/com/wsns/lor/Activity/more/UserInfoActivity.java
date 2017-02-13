package com.wsns.lor.Activity.more;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.text.format.DateFormat;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;
import com.wsns.lor.Activity.LoginActivity;
import com.wsns.lor.R;
import com.wsns.lor.entity.User;
import com.wsns.lor.fragment.InfoListFragment;
import com.wsns.lor.http.HttpMethods;
import com.wsns.lor.http.subscribers.ProgressSubscriber;
import com.wsns.lor.http.subscribers.SubscriberOnNextListener;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;

import cn.jpush.android.api.JPushInterface;
import cn.jpush.im.android.api.JMessageClient;
import cn.jpush.im.api.BasicCallback;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by Administrator on 2016/12/21.
 */

public class UserInfoActivity extends Activity {

    private static final int UPDATEPASSWORD_SUCCESS = 1001;
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

    Uri uri;
    String url;


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

        InfoListFragment.InfoOnClickListener emailOnClickListener = new InfoListFragment.InfoOnClickListener() {
            @Override
            public void onclick() {
//                Intent itnt = new Intent(UserInfoActivity.this, UpdateEmailActivity.class);
//                startActivity(itnt);
//                overridePendingTransition(R.anim.slide_in_left, R.anim.none);
            }
        };

        rlUpdatePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent itnt = new Intent(UserInfoActivity.this, UpdatePasswordActivity.class);
                itnt.putExtra("user",user);
                startActivityForResult(itnt,UPDATEPASSWORD_SUCCESS);
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
                Intent intent = new Intent(UserInfoActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();

            }
        });

    }

    void reLoad(User user){
       this. user=user;
        Picasso.with(UserInfoActivity.this).load(HttpMethods.BASE_URL+ user.getAvatar())
                .memoryPolicy(MemoryPolicy.NO_CACHE)
                .networkPolicy(NetworkPolicy.NO_CACHE)
                .error(R.drawable.unknow_avatar).into(userAvatar);
        fragmentUserName.setTvUserAttribute("昵称");
        fragmentUserName.setTvUserContent(JMessageClient.getMyInfo().getNickname());
        fragmentUsercoins.setTvUserAttribute("金币");
        fragmentUsercoins.setTvUserContent(user.getCoin() + "");
        fragmentUserCreatedate.setTvUserAttribute("创建时间");
        fragmentUserCreatedate.setTvUserContent(user.getCreateDate());
    };
    @Override
    protected void onResume() {
        super.onResume();
        reLoad( user);
    }

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
                                pickFromAlbum();
                                break;
                            default:
                        }
                    }
                }).setNegativeButton("取消", null).show();

    }

    /**
     * 拍照
     */
    private void takePhoto() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        uri = Uri.fromFile(getNewFile());
        intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
        startActivityForResult(intent, REQUESTCODE_CAMERA);
    }

    /**
     * 获取本地图片
     */
    private void pickFromAlbum() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");

        intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
        startActivityForResult(intent, REQUESTCODE_ALBUM);
    }

    Bitmap bitmap;

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_CANCELED) {
            return;
        }
        if (requestCode == REQUESTCODE_CAMERA) {
            startPhotoZoom(uri);
        } else if (requestCode == REQUESTCODE_ALBUM) {
            uri = Uri.fromFile(getNewFile());
            uri = data.getData();
            url = selectImage(UserInfoActivity.this, uri);
            startPhotoZoom(uri);
        } else if (requestCode == REQUESTCODE_CUTTING) {
            try {
                bitmap = BitmapFactory.decodeFile(url);
            } catch (Exception e) {
                e.printStackTrace();
            }
            updateAvatar();
        }else  if(requestCode==UPDATEPASSWORD_SUCCESS)
        {
            Intent intent = new Intent(UserInfoActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        }
    }

    /**
     * 裁剪图片方法实现
     *
     * @param uris
     */
    public void startPhotoZoom(Uri uris) {
        Intent intent = new Intent();
        intent.setAction("com.android.camera.action.CROP");
        intent.setDataAndType(uris, "image/*");
        // crop=true是设置在开启的Intent中设置显示的VIEW可裁剪
        intent.putExtra("crop", true);
        // aspectX aspectY 是宽高的比例
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        // outputX outputY 是裁剪图片宽高
        intent.putExtra("outputX", 300);
        intent.putExtra("outputY", 300);
        intent.putExtra("return-data", true);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
        startActivityForResult(intent, REQUESTCODE_CUTTING);
    }

    public String selectImage(Context context, Uri selectedImage) {
        if (selectedImage != null) {
            String uriStr = selectedImage.toString();
            String path = uriStr.substring(10, uriStr.length());
            if (path.startsWith("com.sec.android.gallery3d")) {
                return null;
            }
        }
        String[] filePathColumn = {MediaStore.Images.Media.DATA};
        Cursor cursor = context.getContentResolver().query(selectedImage, filePathColumn, null, null, null);
        cursor.moveToFirst();
        int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
        String picturePath = cursor.getString(columnIndex);
        cursor.close();
        return picturePath;
    }

    /**
     * 获取图片数据流
     *
     * @return
     */
    public byte[] getPngData() {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        if (bitmap != null)
            bitmap.compress(Bitmap.CompressFormat.JPEG, 50, baos);
        else {
            return null;
        }
        byte[] datas = baos.toByteArray();

        return datas;
    }

    /**
     * 创建新文件
     *
     * @return
     */
    public File getNewFile() {
        if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            return null;
        }
        String sdcardPath = Environment.getExternalStorageDirectory().getPath();
        File imgPath = new File(sdcardPath + "/img/");
        if (!imgPath.exists()) {
            imgPath.mkdirs();
        }
        url = imgPath.getPath() + "/" + user.getId() + ".png";
        File userImgFile = new File(imgPath.getPath() + "/" + user.getId() + ".png");
        if (!userImgFile.exists()) {
            try {
                userImgFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return userImgFile;
    }

    /**
     * 上传头像
     */
    public void updateAvatar() {
        OkHttpClient client = HttpMethods.client;
        MultipartBody.Builder multipartBuilder = new MultipartBody.Builder()
                .setType(MultipartBody.FORM);
        if (getPngData() != null) {
            multipartBuilder.addFormDataPart("avatar", "avatarName"
                    , RequestBody
                            .create(
                                    MediaType.parse("image/png")
                                    , getPngData()));
        }
        final Request request = HttpMethods.requestBuilderWithApi("user/update/avatar")
                .post(multipartBuilder.build())
                .build();

        final ProgressDialog progressDialog = new ProgressDialog(UserInfoActivity.this);
        progressDialog.setMessage("上传中");
        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, final IOException e) {
                progressDialog.dismiss();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        new AlertDialog.Builder(UserInfoActivity.this)
                                .setTitle("上传失败")
                                .setMessage("原因：" + e.getLocalizedMessage())
                                .setCancelable(true)
                                .setNegativeButton("好", null)
                                .show();
                    }
                });
            }

            @Override
            public void onResponse(Call call, final Response response) throws IOException {

                final String result = response.body().string();
                final User user = new ObjectMapper().readValue(result, User.class);
                final File avatar = new File(url);
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
                                        reLoad(user);
                                    }

                                }
                            });

                        }
                    });
                } else {
                    progressDialog.dismiss();
                }
            }
        });
    }
}
