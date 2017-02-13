package com.wsns.lor.http;

import com.wsns.lor.entity.DataAndCodeBean;
import com.wsns.lor.entity.Orders;
import com.wsns.lor.entity.OrdersProgress;
import com.wsns.lor.entity.Page;
import com.wsns.lor.entity.Records;
import com.wsns.lor.entity.RepairGoods;
import com.wsns.lor.entity.User;

import java.util.List;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Query;
import rx.Observable;

/**
 * Created by liukun on 16/3/9.
 */
public interface LORService {

    @POST("api/user/login")
    Observable<DataAndCodeBean<User>> getUserDate(@Query("account") String username, @Query("passwordHash") String password);

    @POST("api/user/register")
    Observable<DataAndCodeBean<User>> getRegisterResult(@Query("account") String account, @Query("passwordHash") String passwordHash);

    @POST("api/user/me")
    Observable<DataAndCodeBean<User>> getCurrentUser();



    @GET("api/repairGoods/bySellerID")
    Observable<DataAndCodeBean<List<RepairGoods>>> getGoodsResult(@Query("seller_account") String seller_account);

    @POST("api/orders/add")
    Observable<DataAndCodeBean<Orders>> getOrderCreateResult(@Query("seller_account") String seller_account,
                                                                   @Query("goods") String goods, @Query("workTime") String workTime,
                                                                   @Query("realName") String realName, @Query("address") String address,
                                                                   @Query("phone") String phone, @Query("price") double price,
                                                                   @Query("note") String note,
                                                                   @Query("isPayOnline") boolean isPayOnline);
    @POST("api/orders/getOrders")
    Observable<DataAndCodeBean<Page<Orders>>> getOrderByID(@Query("orders_id") int orders_id);

    @POST("api/orders/progress/byOrdersId")
    Observable<DataAndCodeBean<Page<OrdersProgress>>> getOrdersProgressPage(@Query("page") int page,@Query("orders_id") int orders_id);

    @POST("api/orders/progress/add")
    Observable<DataAndCodeBean<OrdersProgress>> addOrdersProgress(@Query("content") String content,
                                                                        @Query("title") String title, @Query("orders_id") int orders_id,
                                                                        @Query("state") int state);
    @POST("api/orders/my")
    Observable<DataAndCodeBean<Page<Orders>>> getMyOrderPage(@Query("page") int page);

    @POST("api/rec/records")
    Observable<DataAndCodeBean<Page<Records>>> getMyRecordsPage(@Query("page") int page);

    @Multipart
    @POST("api/user/update/avatar")
    Observable<DataAndCodeBean<User>> updateAvatar(@Part("avatar") String description,@Part("file\"; filename=\"image.png\"")  RequestBody avatar);
    @POST("api/user/update/password")
    Observable<DataAndCodeBean<User>> updatePassword(@Query("newPassword") String newPassword);
    @POST("api/user/update/userName")
    Observable<DataAndCodeBean<User>> updateName(@Query("userName") String userName);
    @POST("api/user/forget/password")
    Observable<DataAndCodeBean<User>> forgetPassword(@Query("account") String account,@Query("newPassword") String newPassword);
    @POST("api/user/finduser")
    Observable<DataAndCodeBean<User>> findUser(@Query("account") String account);





}
