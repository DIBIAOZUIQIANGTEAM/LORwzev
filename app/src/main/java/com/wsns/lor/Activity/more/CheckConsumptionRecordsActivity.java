package com.wsns.lor.Activity.more;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.wsns.lor.R;


public class CheckConsumptionRecordsActivity extends Activity {


    ImageView imageview_turnback;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_consumption_records);

        imageview_turnback = (ImageView)findViewById(R.id.imageview_turnback);
        imageview_turnback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

    }
}