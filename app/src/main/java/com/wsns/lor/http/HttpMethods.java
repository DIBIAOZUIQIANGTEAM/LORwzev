package com.wsns.lor.http;


import com.wsns.lor.entity.DataAndCodeBean;
import com.wsns.lor.entity.Orders;
import com.wsns.lor.entity.OrdersProgress;
import com.wsns.lor.entity.Page;
import com.wsns.lor.entity.Records;
import com.wsns.lor.entity.RepairGoods;
import com.wsns.lor.entity.User;

import java.net.CookieManager;
import java.net.CookiePolicy;
import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.JavaNetCookieJar;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Query;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * Created by liukun on 16/3/9.
 */
public class HttpMethods {

    //    public static final String BASE_URL = "http://192.168.191.1:8080/LORServer/";
//    public static final String BASE_URL = "http://172.27.151.152:8080/LORServer/";
//    public static final String BASE_URL = "http://192.168.43.135:8080/LORServer/";
//    public static final String BASE_URL = "http://115.28.58.198/";
//    public static final String BASE_URL = "http://119.29.52.160/LORServer/";
    public static final String BASE_URL = "http://192.168.1.103:8080/Lor/";
    private static final int DEFAULT_TIMEOUT = 5;

    private static Retrofit retrofit;
    public static LORService lorService;
    public static OkHttpClient client;

    static {
        CookieManager cookieManager = new CookieManager();
        cookieManager.setCookiePolicy(CookiePolicy.ACCEPT_ALL);
        //手动创建一个OkHttpClient并设置超时时间
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        builder.connectTimeout(DEFAULT_TIMEOUT, TimeUnit.SECONDS);
        client = new OkHttpClient.Builder()
                .cookieJar(new JavaNetCookieJar(cookieManager))
                .build();
        retrofit = new Retrofit.Builder()
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .baseUrl(BASE_URL)
                .build();

    }

    public static Request.Builder requestBuilderWithApi(String api){
        String url=BASE_URL+"api/"+api;
        System.out.println("访问了："+url);
        return new Request.Builder()
                .url(url);
    }
    //构造方法私有
    private HttpMethods() {
        lorService = retrofit.create(LORService.class);
    }

    //在访问HttpMethods时创建单例
    private static class SingletonHolder {
        private static final HttpMethods INSTANCE = new HttpMethods();
    }

    //获取单例
    public static HttpMethods getInstance() {
        System.out.println("访问了:" + BASE_URL);
        return SingletonHolder.INSTANCE;
    }



    public void getCurrentUser(Subscriber<DataAndCodeBean<List<RepairGoods>>> subscriber) {

        Observable observable = lorService.getCurrentUser()
                .map(new HttpResultFunc());
        toSubscribe(observable, subscriber);
    }
    public void getMyRecordsPage(Subscriber<DataAndCodeBean<List<Records>>> subscriber, int page) {

        Observable observable = lorService.getMyRecordsPage(page)
                .map(new HttpResultFunc());
        toSubscribe(observable, subscriber);
    }

    /**
     * 用于维修商品的数据
     *
     * @param subscriber 由调用者传过来的观察者对象
     * @param seller_id  商家id
     */
    public void getGoodsResult(Subscriber<DataAndCodeBean<List<RepairGoods>>> subscriber, String seller_id) {

        Observable observable = lorService.getGoodsResult(seller_id)
                .map(new HttpResultFunc());

        toSubscribe(observable, subscriber);
    }

    /**
     * 用于订单的数据
     */
    public void getOrderCreateResult(Subscriber<Orders> subscriber,
                                     String seller_account,
                                     String goods, String workTime,
                                     String realName, String address,
                                     String phone, double price,
                                     String note, boolean isPayOnline) {

        Observable observable = lorService.getOrderCreateResult(seller_account, goods,workTime,realName,address,phone,price,note,isPayOnline)
                .map(new HttpResultFunc());

        toSubscribe(observable, subscriber);
    }

    public void getOrderByID(Subscriber<DataAndCodeBean<Orders>> subscriber,
                                     int orders_id
                                     ) {

        Observable observable = lorService.getOrderByID(orders_id)
                .map(new HttpResultFunc());

        toSubscribe(observable, subscriber);
    }

    public void getOrdersProgressPage(Subscriber<DataAndCodeBean<Page<OrdersProgress>>> subscriber,
                                      int page,  int orders_id
    ) {

        Observable observable = lorService.getOrdersProgressPage(page,orders_id)
                .map(new HttpResultFunc());

        toSubscribe(observable, subscriber);
    }
    public void addOrdersProgress(Subscriber<DataAndCodeBean<OrdersProgress>> subscriber,
                                  String content, String title,int orders_id,int state) {

        Observable observable = lorService.addOrdersProgress(content,title,orders_id,state)
                .map(new HttpResultFunc());

        toSubscribe(observable, subscriber);
    }

    public void getMyOrderPage(Subscriber<DataAndCodeBean<Page<Orders>>> subscriber,
                                  int page) {

        Observable observable = lorService.getMyOrderPage(page)
                .map(new HttpResultFunc());

        toSubscribe(observable, subscriber);
    }

    public void updateAvatar(Subscriber<DataAndCodeBean<User>> subscriber,
                            String name, MultipartBody avatar) {

        Observable observable = lorService.updateAvatar(name,avatar)
                .map(new HttpResultFunc());

        toSubscribe(observable, subscriber);
    }
    public void updatePassword(Subscriber<DataAndCodeBean<User>> subscriber,
                              String newpassword) {

        Observable observable = lorService.updatePassword(newpassword)
                .map(new HttpResultFunc());

        toSubscribe(observable, subscriber);
    }

    public void updateName(Subscriber<DataAndCodeBean<User>> subscriber,
                          String username) {

        Observable observable = lorService.updateName(username)
                .map(new HttpResultFunc());

        toSubscribe(observable, subscriber);
    }
    public void forgetPassword(Subscriber<DataAndCodeBean<User>> subscriber,
                                    String account,String newpassword) {

        Observable observable = lorService.forgetPassword(account,newpassword)
                .map(new HttpResultFunc());

        toSubscribe(observable, subscriber);
    }
    public void findUser(Subscriber<DataAndCodeBean<User>> subscriber,
                               String account) {

        Observable observable = lorService.findUser(account)
                .map(new HttpResultFunc());

        toSubscribe(observable, subscriber);
    }
    public static <T> void toSubscribe(Observable<T> o, Subscriber<T> s) {
        o.subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(s);
    }


    private class HttpResultFunc<T> implements Func1<DataAndCodeBean<T>, T> {

        @Override
        public T call(DataAndCodeBean<T> httpResult) {
            if (httpResult.getCode() != 1) {
                throw new ApiException(httpResult.getMessege());
            }
            return httpResult.getData();
        }
    }

}
