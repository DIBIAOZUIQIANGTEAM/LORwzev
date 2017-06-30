package com.wsns.lor.other.chatting.activity;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.wsns.lor.adapter.ConversationAdapter;
import com.wsns.lor.R;

import java.util.ArrayList;
import java.util.List;

import cn.jpush.im.android.api.JMessageClient;
import cn.jpush.im.android.api.model.Conversation;

/**
 *
 */
public class MessageFragment extends Fragment {
    private boolean hidden;
    private ListView listView;
    private ConversationAdapter adapter;
    private List<Conversation> normal_list = new ArrayList<>();
    public MessageFragment() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_message, container, false);
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (savedInstanceState != null
                && savedInstanceState.getBoolean("isConflict", false))
            return;


        normal_list.addAll(loadConversationsWithRecentChat());
        listView = (ListView) getView().findViewById(R.id.list);
        adapter = new ConversationAdapter(this,getActivity(), normal_list);

        // 设置adapter
        listView.setAdapter(adapter);


    }
    /**
     * 刷新页面
     */
    public void refresh() {
        normal_list.clear();
        normal_list.addAll(loadConversationsWithRecentChat());

        adapter.notifyDataSetChanged();
    }

    /**
     * 获取所有会话
     *
     * @param
     * @return +
     */
    private List<Conversation> loadConversationsWithRecentChat() {
        // 获取所有会话，包括陌生人
        List<Conversation>  conversations = JMessageClient.getConversationList();

        List<Conversation>  list=new ArrayList<>();
        // 置顶列表再刷新一次

        // 过滤掉messages seize为0的conversation
        for (Conversation conversation : conversations) {
            if (conversation.getAllMessage().size() != 0) {
                list.add(conversation);
            }
        }
        return list;
    }
    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        this.hidden = hidden;
        if (!hidden) {
            refresh();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (!hidden ) {
            refresh();
        }
    }
}
