package com.wsns.lor.http;

import com.wsns.lor.http.entity.DataAndCodeBean;
import com.wsns.lor.http.entity.Orders;
import com.wsns.lor.http.entity.OrdersComment;
import com.wsns.lor.http.entity.OrdersProgress;
import com.wsns.lor.http.entity.PublishAd;
import com.wsns.lor.http.entity.Records;
import com.wsns.lor.http.entity.RepairGoods;
import com.wsns.lor.http.entity.Seller;
import com.wsns.lor.http.entity.User;

import java.util.List;

import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Query;
import retrofit2.http.Streaming;
import rx.Observable;

/**
 * Created by liukun on 16/3/9.
 */
public interface LORService {

    @FormUrlEncoded
    @POST("api/user/login")
    Observable<DataAndCodeBean<User>> getUserDate(@Field("account") String username, @Field("passwordHash") String password);

    @POST("api/user/register")
    Observable<DataAndCodeBean<User>> getRegisterResult(@Body User user);

    @GET("api/user/me")
    Observable<DataAndCodeBean<User>> getCurrentUser();


    @GET("api/user/getSellers")
    Observable<DataAndCodeBean<List<Seller>>> getSellers(@Query("location") String location);

    @GET("api/user/getSellersByType")
    Observable<DataAndCodeBean<List<Seller>>> getSellersByType(@Query("location") String location,@Query("repairsTypes") String repairsTypes);

    @GET("api/user/getSellersOrderByTurnover")
    Observable<DataAndCodeBean<List<Seller>>> getSellersOrderByTunover();

    @GET("api/user/getSellersOrderByCommentCount")
    Observable<DataAndCodeBean<List<Seller>>> getSellersOrderByCommentCount();


    @GET("api/repairGoods/bySellerID")
    Observable<DataAndCodeBean<List<RepairGoods>>> getGoodsResult(@Query("seller_account") String seller_account);

    @POST("api/orders/add")
    Observable<DataAndCodeBean<Orders>> getOrderCreateResult(@Query("seller_account") String seller_account,
                                                             @Body Orders orders);

    @FormUrlEncoded
    @POST("api/orders/getOrders")
    Observable<DataAndCodeBean<Orders>> getOrderByID(@Field("orders_id") int orders_id);

    @FormUrlEncoded
    @POST("api/orders/progress/byOrdersId")
    Observable<DataAndCodeBean<List<OrdersProgress>>> getOrdersProgressPage(@Field("page") int page,@Field("orders_id") int orders_id);

    @FormUrlEncoded
    @POST("api/orders/progress/add")
    Observable<DataAndCodeBean<OrdersProgress>> addOrdersProgress(@Field("content") String content,
                                                                        @Field("title") String title, @Field("orders_id") int orders_id,
                                                                        @Field("state") int state);


    @FormUrlEncoded
    @POST("api/orders/my")
    Observable<DataAndCodeBean<List<Orders>>> getMyOrderPage(@Field("page") int page);

    @FormUrlEncoded
    @POST("api/rec/records")
    Observable<DataAndCodeBean<List<Records>>> getMyRecordsPage(@Field("page") int page);

    @Multipart
    @POST("api/user/update/avatar")
    Observable<DataAndCodeBean<User>> updateAvatar(@Part("avatar\"; filename=\"avatar.png") RequestBody avatar);

    @FormUrlEncoded
    @POST("api/user/update/password")
    Observable<DataAndCodeBean<User>> updatePassword(@Field("newPassword") String newPassword);

    @FormUrlEncoded
    @POST("api/user/update/userName")
    Observable<DataAndCodeBean<User>> updateName(@Field("userName") String userName);

    @FormUrlEncoded
    @POST("api/user/forget/password")
    Observable<DataAndCodeBean<User>> forgetPassword(@Field("account") String account,@Field("newPassword") String newPassword);

    @FormUrlEncoded
    @POST("api/user/finduser")
    Observable<DataAndCodeBean<User>> findUser(@Field("account") String account);

    @GET("api/adv/getadv")
    Observable<DataAndCodeBean<List<PublishAd>>> getAdvertisement();

    @GET("api/update/getversion")
    Observable<DataAndCodeBean> getVersion(@Query("versionCode") int versionCode);

    @Streaming
    @GET("api/update/download")
    Observable<ResponseBody> download();


    @FormUrlEncoded
    @POST("api/comment/user/comment")
    Observable<DataAndCodeBean<OrdersComment>> comment(@Field("comments") String comments, @Field("id") String id);


    @FormUrlEncoded
    @POST("api/comment/user/getSellerComments")
    Observable<DataAndCodeBean<List<OrdersComment>>> findCommentsBySellerForUser(@Field("account") String account);


    @POST("api/comment/user/getComments")
    Observable<DataAndCodeBean<List<OrdersComment>>> findCommentsByUser();

}
