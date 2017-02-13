package com.wsns.lor.utils;

import com.baidu.location.BDLocation;

/**
 * Created by Administrator on 2017/2/6.
 */

public class BDUtil {
    public static String location2Str(BDLocation location) {
        return   location.getLongitude() + "," + location.getLatitude();
    }
}
