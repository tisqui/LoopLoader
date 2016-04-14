package com.squirrel.looploader.services;

import android.util.Log;

import com.squirrel.looploader.model.LoopAPI;
import com.squirrel.looploader.model.ProgressResult;

import java.io.File;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


/**
 * Created by squirrel on 4/8/16.
 */
public class VideoService {
    private LoopAPI mLoopAPI;
    private String mId;

    public final static String TAG = VideoService.class.getSimpleName();

    public VideoService() {
        mLoopAPI = ServiceGenerator.createService(LoopAPI.class);
    }

    public String getId() {
        return mId;
    }

    public void setId(String id) {
        mId = id;
    }

    public void uploadFile(String path, final FileUploadCallback fileUploadCallback) {
        Log.d(TAG, "FILE: " + path + " start upload");

        File file = new File(path);
        if (file.exists()) {
            RequestBody requestFile = new MultipartBody.Builder()
                    .setType(MultipartBody.FORM).addFormDataPart("video", file.getName(),
                            RequestBody.create(MediaType.parse("video/*"), file))
                    .build();

            Call<String> call = mLoopAPI.upload(requestFile);
            call.enqueue(new Callback<String>() {
                @Override
                public void onResponse(Call<String> call, Response<String> response) {
                    Log.v("Upload", "success");
                    mId = response.body().toString();
                    Log.d("Upload", mId);
                    fileUploadCallback.onSuccess(response.body().toString());
                }

                @Override
                public void onFailure(Call<String> call, Throwable t) {
                    Log.e("Upload error:", t.getMessage());
                    fileUploadCallback.onError(t);
                }
            });
        }
    }


    public void getProcessingProgress(String videoId, final ServiceCallback serviceCallback) {
        Call<ProgressResult> call = mLoopAPI.getProgress(videoId);
        Log.d("Progress: ", "Requesting the progress for: " + videoId);
        call.enqueue(new Callback<ProgressResult>() {
            @Override
            public void onResponse(Call<ProgressResult> call, Response<ProgressResult> response) {
                int statusCode = response.code();
                Log.v("Progress", "success");
                Log.d("Progress: ", response.body().toString());

                if (response.body().getProgress() != null) {
                    serviceCallback.onProgress(response.body().getProgress());
                } else if (response.body().getReady() != null) {
                    serviceCallback.onReady(response.body().getReady());
                } else if (response.body().getError() != null) {
                    serviceCallback.onError(response.body().getError());
                }
            }

            @Override
            public void onFailure(Call<ProgressResult> call, Throwable t) {
                Log.e("Progress error:", t.getMessage());
                serviceCallback.onError("Progress request error:" + t.getMessage());
            }
        });
    }

    public interface ServiceCallback {
        void onProgress(float progress);

        void onReady(String filePath);

        void onError(String error);
    }

    public interface FileUploadCallback {
        void onSuccess(String fileId);

        void onError(Throwable throwable);
    }

}
