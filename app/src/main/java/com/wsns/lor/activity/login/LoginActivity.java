package com.wsns.lor.activity.login;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;
import com.wsns.lor.adapter.EditTextAdapter;
import com.wsns.lor.R;
import com.wsns.lor.http.entity.User;
import com.wsns.lor.http.HttpMethods;
import com.wsns.lor.http.subscribers.ProgressSubscriber;
import com.wsns.lor.http.subscribers.SubscriberOnNextListener;
import com.wsns.lor.utils.MD5;
import com.wsns.lor.utils.SetAliasAndTagUtil;
import com.wsns.lor.view.widgets.DropEditText;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import cn.jpush.im.android.api.JMessageClient;
import cn.jpush.im.api.BasicCallback;

import static com.wsns.lor.application.LorApplication.RESULT_LOGIN_FAIL;
import static com.wsns.lor.application.LorApplication.RESULT_LOGIN_SUCCESS;


/**
 * 登录界面 实现账号密码的POST请求 得到返回的用户信息
 */
public class LoginActivity extends Activity {
    ProgressSubscriber progressSubscriber;
    Button login;
    TextView recover, register;
    CheckBox cbRememberPassword;
    CheckBox cbAutoLogin;
    DropEditText account;
    EditText password;
    EditTextAdapter adapter;
    ImageView avatar;
    private SubscriberOnNextListener getUserDataOnNext;
    List<String> accountList = new ArrayList<String>();
    User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ;
        System.out.println("LoginActivity at:"+getTaskId()+this.hashCode());

        account = (DropEditText) findViewById(R.id.drop_edit_text);
        password = (EditText) findViewById(R.id.et_pasword);

        login = (Button) findViewById(R.id.username_sign_in_button);
        recover = (TextView) findViewById(R.id.password_recover);
        avatar = (ImageView) findViewById(R.id.tv_face);
        register = (TextView) findViewById(R.id.register);
        cbAutoLogin = (CheckBox) findViewById(R.id.cb_auto_login);
        cbRememberPassword = (CheckBox) findViewById(R.id.cb_remember_password);

        login.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!isInputCorrect()) {
                    return;
                }

                loginHttpRequest();
            }
        });
        register.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setClass(getApplicationContext(), RegistersActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_bottom, R.anim.none);
            }
        });
        recover.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(LoginActivity.this, ForgetPasswordActivity.class));
                overridePendingTransition(R.anim.slide_in_left, R.anim.none);
            }
        });
        cbRememberPassword.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (!b) {
                    cbAutoLogin.setChecked(false);
                }
            }
        });
        cbAutoLogin.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    cbRememberPassword.setChecked(true);
                }
            }
        });


        account.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                password.setText("");
                cbRememberPassword.setChecked(false);
                cbAutoLogin.setChecked(false);
                avatar.setImageResource(R.drawable.unknow_avatar);
                SharedPreferences preferences = getSharedPreferences("user", Context.MODE_PRIVATE);
                for (String account : accountList) {
                    if (account.equals(charSequence.toString())) {
                        password.setText(preferences.getString(account + "password", ""));
                        cbRememberPassword.setChecked(preferences.getBoolean(account + "remember", false));
                        cbAutoLogin.setChecked(preferences.getBoolean(account + "auto", false));
                        Picasso.with(LoginActivity.this).load(HttpMethods.BASE_URL + preferences.getString(account + "avatar", "")).placeholder(R.drawable.unknow_avatar).error(R.drawable.unknow_avatar).into(avatar);
                        break;
                    }
                }
            }


            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        getUserDataOnNext = new SubscriberOnNextListener<User>() {
            @Override
            public void onNext(User user) {
                LoginActivity.this.user = user;
                loginSuccess();

            }
        };
        initUser();

    }


    /**
     * 初始化输入
     */
    public void initUser() {
        SharedPreferences preferences = getSharedPreferences("user", Context.MODE_PRIVATE);
        Set<String> accountSet = preferences.getStringSet("accountset", null);

        if (accountSet == null) {
            return;
        }
        Iterator<String> iterator = accountSet.iterator();
        while (iterator.hasNext()) {
            accountList.add(iterator.next());
        }
        adapter = new EditTextAdapter(this, accountList, LoginActivity.this);
        account.setAdapter(adapter);

        if (accountList != null && accountList.size() > 0) {
            String passwords = preferences.getString(accountList.get(0) + "password", "");
            boolean auto = preferences.getBoolean(accountList.get(0) + "auto", false);
            boolean remember = preferences.getBoolean(accountList.get(0) + "remember", false);
            Picasso.with(LoginActivity.this).load(HttpMethods.BASE_URL + preferences.getString(account + "avatar", "")).placeholder(R.drawable.unknow_avatar).error(R.drawable.unknow_avatar).into(avatar);

            account.setText(accountList.get(0));
            account.setSelection(accountList.get(0).length());
            password.setText(passwords);

            cbAutoLogin.setChecked(auto);
            cbRememberPassword.setChecked(remember);
            if (auto) {
                loginHttpRequest();
            }
        }

    }

    /**
     * 设置User参数
     *
     * @param account
     * @param password
     */
    public void setUser(String account, String password, String avatar, boolean remember, boolean auto) {
        SharedPreferences preferences = getSharedPreferences("user", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();

        Set<String> accountSet = preferences.getStringSet("accountset", null);
        if (accountSet == null) {
            accountSet = new HashSet<String>();
        }
        accountSet.add(account);
        editor.putStringSet("accountset", accountSet);
        editor.putString(account + "avatar", avatar);
        editor.putString(account + "password", password);
        editor.putBoolean(account + "auto", auto);
        editor.putBoolean(account + "remember", remember);
        editor.commit();
    }

    /**
     * 登录
     */
    private void loginHttpRequest() {
        progressSubscriber = new ProgressSubscriber(getUserDataOnNext, LoginActivity.this, true);
        progressSubscriber.setProgressText("登录中~");
        HttpMethods.getInstance().getUserData(progressSubscriber, account.getText().toString(), MD5.getMD5(password.getText().toString()));
    }

    ProgressDialog progressDialog = null;

    public void loginSuccess() {
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("登录IM中");
        progressDialog.setCancelable(false);
        progressDialog.show();
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                /**=================     调用SDk登陆接口    =================*/
                JMessageClient.login(account.getText().toString(), MD5.getMD5(password.getText().toString()), new BasicCallback() {
                    @Override
                    public void gotResult(int responseCode, String LoginDesc) {
                        progressDialog.dismiss();
                        if (responseCode == 0) {

                            if (cbRememberPassword.isChecked()) {
                                if (cbAutoLogin.isChecked()) {
                                    setUser(account.getText().toString(), password.getText().toString(), user.getAvatar(), true, true);
                                } else {
                                    setUser(account.getText().toString(), password.getText().toString(), user.getAvatar(), true, false);
                                }
                            }
                            SetAliasAndTagUtil at = new SetAliasAndTagUtil(LoginActivity.this);
                            at.setAlias(user.getAccount());
                            setResult(RESULT_LOGIN_SUCCESS);
finish();
                            Log.i("LoginActivity", "JMessageClient.login" + ", responseCode = " + responseCode + " ; LoginDesc = " + LoginDesc);
                        } else {
                            Toast.makeText(getApplicationContext(), "登录失败", Toast.LENGTH_SHORT).show();
                            Log.i("LoginActivity", "JMessageClient.login" + ", responseCode = " + responseCode + " ; LoginDesc = " + LoginDesc);
                        }
                    }
                });
            }
        });
    }

    /**
     * 判断输入
     *
     * @return
     */
    private boolean isInputCorrect() {
        if (account.getText().equals("")) {
            Toast.makeText(LoginActivity.this, "用户名不能为空", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (password.getText().equals("")) {
            Toast.makeText(LoginActivity.this, "密码不能为空", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        account.setHint("请输入账号");
        password.setHint("请输入密码");
        password.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
    }

    public void deleteAccount(String accounts) {
        SharedPreferences preferences = getSharedPreferences("user", Context.MODE_PRIVATE);
        Set<String> accountSet = preferences.getStringSet("accountset", null);
        accountSet.remove(accounts);
        SharedPreferences.Editor editor = preferences.edit();
        editor.remove(accounts + "password");
        editor.remove(accounts + "avatar");
        editor.remove(accounts + "remember");
        editor.remove(accounts + "auto");
        editor.commit();
        accountList.remove(accounts);
        if (accountList.size() > 0) {
            account.setText(accountList.get(0));
            adapter.notifyDataSetChanged();
        } else {
            account.setText("");
            account.dismiss();
        }
    }

    @Override
    public void onBackPressed() {
        setResult(RESULT_LOGIN_FAIL);
        finish();
    }


}

