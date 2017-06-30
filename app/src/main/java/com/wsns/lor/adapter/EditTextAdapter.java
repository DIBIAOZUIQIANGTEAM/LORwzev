package com.wsns.lor.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;


import com.wsns.lor.activity.login.LoginActivity;
import com.wsns.lor.R;

import java.util.List;

/**
 * Created by Administrator on 2016/12/21.
 */

public class EditTextAdapter extends BaseAdapter {

    Context context;
    LoginActivity activity;
    List<String> accountList;

    public EditTextAdapter(Context context, List<String> accountList, LoginActivity activity) {
        this.context = context;
        this.accountList = accountList;
        this.activity = activity;
    }

    @Override
    public int getCount() {
        return accountList == null ? 0 : accountList.size();
    }

    @Override
    public Object getItem(int i) {
        return accountList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View convertView, ViewGroup viewGroup) {
        final String username = accountList.get(i);
        View view=null;

        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
            view = inflater.inflate(R.layout.list_edittext_item, null);

        } else {
            view = convertView;
        }
        final TextView account = (TextView) view.findViewById(R.id.tv_account);
        ImageView cancel = (ImageView) view.findViewById(R.id.iv_cancel);

        account.setText(username);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               activity.deleteAccount(username);
            }
        });
        return view;
    }

}
