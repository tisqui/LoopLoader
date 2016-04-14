package com.squirrel.looploader.model;

import android.support.annotation.Nullable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by squirrel on 4/10/16.
 */
public class ProgressResult implements Serializable {

    @Nullable
    @SerializedName("progress")
    @Expose
    private Float mProgress;

    @Nullable
    @SerializedName("ready")
    @Expose
    private String mReady;

    @Nullable
    @SerializedName("error")
    @Expose
    private String mError;

    @Override
    public String toString() {
        return "ProgressResult{" +
                "mProgress=" + mProgress +
                ", mReady=" + mReady +
                ", mError=" + mError +
                '}';
    }

    public Float getProgress() {
        return mProgress;
    }

    public void setProgress(Float progress) {
        mProgress = progress;
    }

    @Nullable
    public String getReady() {
        return mReady;
    }

    public void setReady(@Nullable String ready) {
        mReady = ready;
    }

    @Nullable
    public String getError() {
        return mError;
    }

    public void setError(@Nullable String error) {
        mError = error;
    }
}
