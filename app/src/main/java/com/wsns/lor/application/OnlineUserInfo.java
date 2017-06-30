package com.wsns.lor.application;

import com.baidu.location.BDLocation;

import cn.jpush.im.android.api.JMessageClient;
import cn.jpush.im.android.api.model.UserInfo;

/**
 * Created by Administrator on 2016/10/23.
 */

public class OnlineUserInfo {
    public static UserInfo myInfo= JMessageClient.getMyInfo() ;
    public static double latitude ;
    public static double longitude ;
    public static String address;
    public static  BDLocation bdLocation;

}
