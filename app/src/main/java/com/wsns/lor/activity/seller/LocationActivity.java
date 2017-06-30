package com.wsns.lor.activity.seller;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

import com.wsns.lor.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class LocationActivity extends Activity {


    @BindView(R.id.return_btn)
    ImageButton returnBtn;
    @BindView(R.id.jmui_commit_btn)
    Button jmuiCommitBtn;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location);
        ButterKnife.bind(this);

    }

    @OnClick({R.id.return_btn, R.id.jmui_commit_btn})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.return_btn:
                finish();
                break;
            case R.id.jmui_commit_btn:

                finish();
                setResult(RESULT_OK);
                break;
        }
    }
}

