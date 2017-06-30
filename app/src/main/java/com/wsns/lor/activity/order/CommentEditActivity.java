package com.wsns.lor.activity.order;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import com.wsns.lor.R;
import com.wsns.lor.http.HttpMethods;
import com.wsns.lor.http.subscribers.ProgressSubscriber;
import com.wsns.lor.http.subscribers.SubscriberOnNextListener;


public class CommentEditActivity extends Activity {
    ImageView  finishBtn;
    EditText contentEdit;
    ImageView backBtn;
    SubscriberOnNextListener commentOnNext;
    String id;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note_edit);
        id=getIntent().getStringExtra("orders_id");
        finishBtn= (ImageView) findViewById(R.id.btn_checkmark);
        contentEdit= (EditText) findViewById(R.id.et_content);
        backBtn= (ImageView) findViewById(R.id.iv_add_note_back);

        commentOnNext=new SubscriberOnNextListener() {
            @Override
            public void onNext(Object o) {
                finish();
                overridePendingTransition(R.anim.none,R.anim.slide_out_left);
            }
        };

        finishBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if ( contentEdit.getText().toString().equals(""))
                    return;
                HttpMethods.getInstance().comment(new ProgressSubscriber(commentOnNext, CommentEditActivity.this, true),
                       contentEdit.getText().toString(), id);

            }
        });

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
                overridePendingTransition(R.anim.none,R.anim.slide_out_left);
            }
        });

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
        overridePendingTransition(R.anim.none,R.anim.slide_out_left);
    }
}
