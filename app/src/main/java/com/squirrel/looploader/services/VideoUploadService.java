package com.squirrel.looploader.services;

import android.util.Log;

import com.squirrel.looploader.model.LoopAPI;

import java.io.File;
import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


/**
 * Created by squirrel on 4/8/16.
 */
public class VideoUploadService {
    public final static String TAG = VideoUploadService.class.getSimpleName();

    public VideoUploadService() {
    }

    public void uploadFile(String path) {
        LoopAPI service = ServiceGenerator.createService(LoopAPI.class);

        Log.d (TAG, "FILE: " + path + " start upload");

        File file= new File(path);
        if(file.exists()){

        // create RequestBody instance from file
        RequestBody requestFile =
                RequestBody.create(MediaType.parse("multipart/form-data"), file);

            MultipartBody.Part body =
                    MultipartBody.Part.createFormData("video", file.getName(), requestFile);


        // finally, execute the request
        Call<ResponseBody> call = service.upload(body);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call,
                                   Response<ResponseBody> response) {
                Log.v("Upload", "success");
                if (response.body() != null){
                    try {
                        Log.d("Upload", response.body().string());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.e("Upload error:", t.getMessage());
            }
        });
        }

    }
}
