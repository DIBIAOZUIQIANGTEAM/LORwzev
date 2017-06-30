package com.wsns.lor.activity.login;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.wsns.lor.R;


public class RegisterSuccessActivity extends AppCompatActivity {

    Button btnGOLogin;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_success);
        btnGOLogin = (Button) findViewById(R.id.btn_go_login);
        btnGOLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
                overridePendingTransition(R.anim.none,R.anim.slide_out_bottom);
            }
        });
    }
}
