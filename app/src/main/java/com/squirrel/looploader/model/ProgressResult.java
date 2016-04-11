package com.squirrel.looploader.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by squirrel on 4/10/16.
 */
public class ProgressResult implements Serializable {

    @SerializedName("progress")
    @Expose
    private float mProgress;

    @SerializedName("ready")
    @Expose
    private float mReady;

    @SerializedName("error")
    @Expose
    private float mError;

    @Override
    public String toString() {
        return "ProgressResult{" +
                "mProgress=" + mProgress +
                ", mReady=" + mReady +
                ", mError=" + mError +
                '}';
    }

    public float getProgress() {
        return mProgress;
    }

    public void setProgress(float progress) {
        mProgress = progress;
    }

    public float getReady() {
        return mReady;
    }

    public void setReady(float ready) {
        mReady = ready;
    }

    public float getError() {
        return mError;
    }

    public void setError(float error) {
        mError = error;
    }

}
