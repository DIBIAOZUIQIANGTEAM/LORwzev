package com.wsns.lor.activity.login;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;


import com.wsns.lor.R;
import com.wsns.lor.fragment.login.ForgetPasswordFirstFragment;
import com.wsns.lor.fragment.login.ForgetPasswordSecondFragment;
import com.wsns.lor.fragment.login.ForgetPasswordThirdFragment;

import cn.smssdk.EventHandler;
import cn.smssdk.SMSSDK;

public class ForgetPasswordActivity extends AppCompatActivity {

    private String APPKEY = "1a8522e867d05";
    private String APPSECURITY = "119df776ef1a4fbf570645a33bf93103";

    ForgetPasswordFirstFragment forgetPasswordFirstFragment = new ForgetPasswordFirstFragment();
    ForgetPasswordSecondFragment forgetPasswordSecondFragment = new ForgetPasswordSecondFragment();
    ForgetPasswordThirdFragment forgetPasswordThirdFragment = new ForgetPasswordThirdFragment();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget_password);

        SMSSDK.initSDK(ForgetPasswordActivity.this, APPKEY, APPSECURITY);
        EventHandler eh = new EventHandler() {
            @Override
            public void afterEvent(int event, int result, Object data) {

                switch (event) {
                    case SMSSDK.EVENT_SUBMIT_VERIFICATION_CODE:
                        if (result == SMSSDK.RESULT_COMPLETE) {
                            ;
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    goStep3();
                                }
                            });
                        } else {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(ForgetPasswordActivity.this, "验证码错误，请重新发送短信验证", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                        break;
                    case SMSSDK.EVENT_GET_VERIFICATION_CODE:
                        if (result == SMSSDK.RESULT_COMPLETE) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    goStep2();
                                }
                            });
                            //默认的智能验证是开启的,我已经在后台关闭
                        } else {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(ForgetPasswordActivity.this, "获取验证码失败", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                        break;
                }
            }
        };
        SMSSDK.registerEventHandler(eh); //注册短信回调

        forgetPasswordFirstFragment.setOnGoNextListener(new ForgetPasswordFirstFragment.OnGoNextListener() {
            @Override
            public void onGoNext() {
                goStep2();
            }
        });
        forgetPasswordFirstFragment.setOnGoBackListener(new ForgetPasswordFirstFragment.OnGoBackListener() {
            @Override
            public void onGoBack() {
                backOut();
            }
        });
        forgetPasswordSecondFragment.setOnGoNextListener(new ForgetPasswordSecondFragment.OnGoNextListener() {
            @Override
            public void onGoNext() {
                goStep3();
            }
        });
        forgetPasswordSecondFragment.setOnGoBackListener(new ForgetPasswordSecondFragment.OnGoBackListener() {
            @Override
            public void onGoBack() {
                goBackStep1();
            }
        });
        forgetPasswordThirdFragment.setOnGoNextListener(new ForgetPasswordThirdFragment.OnGoNextListener() {
            @Override
            public void onGoNext() {
                goStep4();
            }
        });
        forgetPasswordThirdFragment.setOnGoBackListener(new ForgetPasswordThirdFragment.OnGoBackListener() {
            @Override
            public void onGoBack() {
                goBackStep2();
            }
        });
        getFragmentManager().beginTransaction().replace(R.id.container, forgetPasswordFirstFragment).commit();


    }

    void backOut() {
        finish();
        overridePendingTransition(R.anim.none, R.anim.slide_out_bottom);
    }

    void goBackStep1() {

        getFragmentManager()
                .beginTransaction()
                .setCustomAnimations(
                        R.animator.slide_in_left,
                        R.animator.slide_out_right,
                        R.animator.slide_in_right,
                        R.animator.slide_out_left)
                .replace(R.id.container, forgetPasswordFirstFragment)
                .commit();
    }

    void goStep2() {

        getFragmentManager()
                .beginTransaction()
                .setCustomAnimations(
                        R.animator.slide_in_right,
                        R.animator.slide_out_left,
                        R.animator.slide_in_left,
                        R.animator.slide_out_right)
                .replace(R.id.container, forgetPasswordSecondFragment)
                .addToBackStack(null)
                .commit();
    }

    void goBackStep2() {

        getFragmentManager()
                .beginTransaction()
                .setCustomAnimations(
                        R.animator.slide_in_left,
                        R.animator.slide_out_right,
                        R.animator.slide_in_right,
                        R.animator.slide_out_left)
                .replace(R.id.container, forgetPasswordSecondFragment)
                .commit();
    }

    void goStep3() {

        getFragmentManager()
                .beginTransaction()
                .setCustomAnimations(
                        R.animator.slide_in_right,
                        R.animator.slide_out_left,
                        R.animator.slide_in_left,
                        R.animator.slide_out_right)
                .replace(R.id.container, forgetPasswordThirdFragment)
                .addToBackStack(null)
                .commit();
    }

    void goStep4() {
        Intent itnt = new Intent(ForgetPasswordActivity.this, LoginActivity.class);
        startActivity(itnt);
        finish();
        overridePendingTransition(R.anim.slide_in_left, R.anim.none);
    }


}
