package com.squirrel.looploader.model;

import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Created by squirrel on 4/8/16.
 */
public interface LoopAPI {
//    @Multipart
//    @POST("/upload/")
//    Call<ResponseBody> upload(@Part("video") MultipartBody.Part file);

    @POST("/upload/")
    Call<String> upload(@Body RequestBody file);

    @GET("/progress/")
    Call<ProgressResult> getProgress(@Query("id") String videoId);

    @GET("/file/{id}/{name}")
    Call<ResponseBody> downloadVideo(@Path("id") String id, @Path("name") String name);

}
