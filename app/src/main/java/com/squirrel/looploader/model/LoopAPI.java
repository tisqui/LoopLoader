package com.squirrel.looploader.model;

import okhttp3.MultipartBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

/**
 * Created by squirrel on 4/8/16.
 */
public interface LoopAPI {
    @Multipart
    @POST("/upload/")
    Call<ResponseBody> upload(@Part("video") MultipartBody.Part file);


}
