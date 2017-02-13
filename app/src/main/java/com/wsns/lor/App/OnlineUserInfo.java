package com.wsns.lor.App;

import com.baidu.location.BDLocation;

import cn.jpush.im.android.api.JMessageClient;
import cn.jpush.im.android.api.model.UserInfo;

/**
 * Created by Administrator on 2016/10/23.
 */

public class OnlineUserInfo {
    public static UserInfo myInfo= JMessageClient.getMyInfo() ;

}
