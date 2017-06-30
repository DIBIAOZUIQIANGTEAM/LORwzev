package com.wsns.lor.view;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.wsns.lor.R;


public class ConversationListView {
	
	private View mConvListFragment;
	private ListView mConvListView = null;
	private TextView mTitle;
	private LinearLayout mHeader;
    private Context mContext;
	private RelativeLayout reParent;
	private ImageView refreshIv;
	RefreshListener refreshListener;

	public ConversationListView(View view, Context context) {
		this.mConvListFragment = view;
        this.mContext = context;
	}

	public void initModule() {
		mTitle = (TextView) mConvListFragment.findViewById(R.id.main_title_bar_title);
		mTitle.setText("消息列表");

		refreshIv= (ImageView) mConvListFragment.findViewById(R.id.iv_refresh);
		mConvListView = (ListView) mConvListFragment.findViewById(R.id.conv_list_view);
		reParent= (RelativeLayout) mConvListFragment.findViewById(R.id.re_parent);
		reParent.setVisibility(View.GONE);
        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mHeader = (LinearLayout) inflater.inflate(R.layout.conv_list_head_view, mConvListView, false);
        mConvListView.addHeaderView(mHeader);
		refreshIv.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				refreshListener.CallRefresh();
			}
		});

	}

	public interface RefreshListener{
		void CallRefresh();
	}

	public void setRefreshListener(RefreshListener refreshListener ){

		this.refreshListener=refreshListener;
	}



	public void setConvListAdapter(ListAdapter adapter) {
		mConvListView.setAdapter(adapter);
	}

	public void setListNull() {
		reParent.setVisibility(View.VISIBLE);
	}



	public void setItemListeners(OnItemClickListener onClickListener) {
		mConvListView.setOnItemClickListener(onClickListener);
	}
	
	public void setLongClickListener(OnItemLongClickListener listener) {
		mConvListView.setOnItemLongClickListener(listener);
	}

    public void showHeaderView() {
        mHeader.findViewById(R.id.network_disconnected_iv).setVisibility(View.VISIBLE);
        mHeader.findViewById(R.id.check_network_hit).setVisibility(View.VISIBLE);
    }

    public void dismissHeaderView() {
		mHeader.findViewById(R.id.network_disconnected_iv).setVisibility(View.GONE);
		mHeader.findViewById(R.id.check_network_hit).setVisibility(View.GONE);
    }



}
