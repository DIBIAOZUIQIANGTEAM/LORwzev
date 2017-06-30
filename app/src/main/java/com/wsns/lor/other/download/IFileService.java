package com.wsns.lor.other.download;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Streaming;

/**
 * 作者：Tailyou （祝文飞）
 * 时间：2016/5/26 18:46
 * 邮箱：tailyou@163.com
 * 描述：Retrofit文件下载接口
 */
public interface IFileService {

    /**
     * 下载数据库、资源

     */
    @Streaming
    @GET("api/update/download")
    Call<ResponseBody> loadFile();

}
