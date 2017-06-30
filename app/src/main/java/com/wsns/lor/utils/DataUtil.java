package com.wsns.lor.utils;

/**
 * Created by Administrator on 2017/4/14.
 */

public class DataUtil {
    public static String doubleTrans1(double num){
        if(num % 1.0 == 0){
            return String.valueOf((long)num);
        }
        return String.valueOf(num);
    }
}
