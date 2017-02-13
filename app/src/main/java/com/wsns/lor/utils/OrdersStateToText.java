package com.wsns.lor.utils;

/**
 * Created by Administrator on 2016/12/29.
 */

public class OrdersStateToText {
    public static String getTextForPublisers(int state) {

        switch (state)
        {
            case 1:
                return"新订单";
            case 2:
                return"待发货";
            case 3:
                return"待确认";
            case 4:
                return"已完成";
            case 5:
                return"收到评价";
            case 6:
                return"申请取消";
            case 7:
                return"已退款";
            case 8:
                return"未退款";
        }
        return "";
    }
    public static String getTextForBuyer(int state) {

        switch (state)
        {
            case 1:
                return"新订单";
            case 2:
                return"待发货";
            case 3:
                return"待确认";
            case 4:
                return"已完成";
            case 5:
                return"已评价";
            case 6:
                return"申请取消";
            case 7:
                return"已退款";
            case 8:
                return"未退款";


        }
        return "";
    }
}
