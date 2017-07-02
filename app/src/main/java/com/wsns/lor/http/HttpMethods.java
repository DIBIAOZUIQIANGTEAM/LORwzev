package com.wsns.lor.http;


import com.wsns.lor.http.entity.DataAndCodeBean;
import com.wsns.lor.http.entity.Orders;
import com.wsns.lor.http.entity.OrdersProgress;
import com.wsns.lor.http.entity.PublishAd;
import com.wsns.lor.http.entity.Records;
import com.wsns.lor.http.entity.RepairGoods;
import com.wsns.lor.http.entity.Seller;
import com.wsns.lor.http.entity.User;

import java.net.CookieManager;
import java.net.CookiePolicy;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import okhttp3.JavaNetCookieJar;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * Created by liukun on 16/3/9.
 */
public class HttpMethods {

    public static final String BASE_URL = "http://192.168.1.108:8080/lor/";
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
        ExecutorService executorService = Executors.newFixedThreadPool(1);
        retrofit = new Retrofit.Builder()
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .callbackExecutor(executorService) //默认CallBack回调在主线程进行,当设置下载大文件时需设置注解@Stream 不加这句话会报android.os.NetworkOnMainThreadException
                .baseUrl(BASE_URL)
                .build();

    }

    public static Request.Builder requestBuilderWithApi(String api) {
        String url = BASE_URL + "api/" + api;
        System.out.println("访问主机：" + url);
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
        System.out.println("访问主机:" + BASE_URL);
        return SingletonHolder.INSTANCE;
    }


    public void getRegisterResult(Subscriber<DataAndCodeBean<User>> subscriber, User user) {

        Observable observable = lorService.getRegisterResult(user)
                .map(new HttpResultFunc());

        toSubscribe(observable, subscriber);
    }

    /**
     * 用于获取登录用户的数据
     *
     * @param subscriber 由调用者传过来的观察者对象
     * @param username   用户名
     * @param password   密码
     */
    public void getUserData(Subscriber<DataAndCodeBean<User>> subscriber, String username, String password) {

        Observable observable = lorService.getUserDate(username, password)
                .map(new HttpResultFunc());

        toSubscribe(observable, subscriber);
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
                                     Orders orders) {


        Observable observable = lorService.getOrderCreateResult(seller_account, orders)
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

    public void getOrdersProgressPage(Subscriber<DataAndCodeBean<List<OrdersProgress>>> subscriber,
                                      int page, int orders_id
    ) {

        Observable observable = lorService.getOrdersProgressPage(page, orders_id)
                .map(new HttpResultFunc());

        toSubscribe(observable, subscriber);
    }

    public void addOrdersProgress(Subscriber<DataAndCodeBean<OrdersProgress>> subscriber,
                                  String content, String title, int orders_id, int state) {

        Observable observable = lorService.addOrdersProgress(content, title, orders_id, state)
                .map(new HttpResultFunc());

        toSubscribe(observable, subscriber);
    }

    public void getMyOrderPage(Subscriber<DataAndCodeBean<List<Orders>>> subscriber,
                               int page) {

        Observable observable = lorService.getMyOrderPage(page)
                .map(new HttpResultFunc());

        toSubscribe(observable, subscriber);
    }

    public void updateAvatar(Subscriber subscriber,
                             RequestBody avatar) {

        Observable observable = lorService.updateAvatar(avatar)
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
                               String account, String newpassword) {

        Observable observable = lorService.forgetPassword(account, newpassword)
                .map(new HttpResultFunc());

        toSubscribe(observable, subscriber);
    }

    public void findUser(Subscriber<DataAndCodeBean<User>> subscriber,
                         String account) {

        Observable observable = lorService.findUser(account)
                .map(new HttpResultFunc());

        toSubscribe(observable, subscriber);
    }

    public void getSellers(Subscriber<DataAndCodeBean<List<Seller>>> subscriber,
                           String location) {
        Observable observable = lorService.getSellers(location)
                .map(new HttpResultFunc());

        toSubscribe(observable, subscriber);
    }

    public void getSellersByType(Subscriber<DataAndCodeBean<List<Seller>>> subscriber,
                                 String location, String repairsTypes) {
        Observable observable = lorService.getSellersByType(location, repairsTypes)
                .map(new HttpResultFunc());

        toSubscribe(observable, subscriber);
    }

    public void getSellersOrderByTunover(Subscriber<DataAndCodeBean<List<Seller>>> subscriber) {
        Observable observable = lorService.getSellersOrderByTunover()
                .map(new HttpResultFunc());

        toSubscribe(observable, subscriber);
    }

    public void getSellersOrderByCommentCount(Subscriber<DataAndCodeBean<List<Seller>>> subscriber) {
        Observable observable = lorService.getSellersOrderByCommentCount()
                .map(new HttpResultFunc());

        toSubscribe(observable, subscriber);
    }

    public void getAdvertisement(Subscriber<DataAndCodeBean<List<PublishAd>>> subscriber
    ) {
        Observable observable = lorService.getAdvertisement()
                .map(new HttpResultFunc());

        toSubscribe(observable, subscriber);
    }

    public void getVersion(Subscriber<DataAndCodeBean> subscriber, int versionCode
    ) {
        Observable observable = lorService.getVersion(versionCode)
                .map(new HttpResultFunc());

        toSubscribe(observable, subscriber);
    }

    public void download(Subscriber<ResponseBody> subscriber
    ) {
        Observable observable = lorService.download()
                .map(new DownLoadFunc());

        toSubscribe(observable, subscriber);
    }


    public void comment(Subscriber<ResponseBody> subscriber
    ,String comments,String id) {
        Observable observable = lorService.comment(comments,id)
                .map(new HttpResultFunc());

        toSubscribe(observable, subscriber);
    }

    public void getComments(Subscriber<ResponseBody> subscriber
            ) {
        Observable observable = lorService.findCommentsByUser()
                .map(new HttpResultFunc());

        toSubscribe(observable, subscriber);
    }

    public void getComments(Subscriber<ResponseBody> subscriber
            ,String account) {
        Observable observable = lorService.findCommentsBySellerForUser(account)
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

            if (httpResult.getCode() == 2) {
                throw new ApiException(httpResult.getMessege());
            }
            if (httpResult.getCode() == 3) {
                return null;
            }
            return httpResult.getData();
        }
    }

    private class DownLoadFunc<ResponseBody> implements Func1<ResponseBody, ResponseBody> {

        @Override
        public ResponseBody call(ResponseBody responseBody) {

            return responseBody;
        }
    }

}
