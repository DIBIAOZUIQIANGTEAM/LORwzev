package com.wsns.lor.Activity.order;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import com.wsns.lor.R;


public class NoteEditActivity extends Activity {
    ImageView  finishBtn;
    EditText contentEdit;
    ImageView backBtn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note_edit);
        finishBtn= (ImageView) findViewById(R.id.btn_checkmark);
        contentEdit= (EditText) findViewById(R.id.et_content);
        backBtn= (ImageView) findViewById(R.id.iv_add_note_back);

        finishBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent();
                intent.putExtra("content",contentEdit.getText().toString());
                setResult(RESULT_OK,intent);
                finish();
                overridePendingTransition(R.anim.none,R.anim.slide_out_left);
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
