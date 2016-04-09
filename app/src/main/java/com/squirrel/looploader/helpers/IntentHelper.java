package com.squirrel.looploader.helpers;

import android.content.Intent;

/**
 * Created by squirrel on 4/8/16.
 */
public class IntentHelper {
    public final static int GET_GALLERY_VIDEO = 1;

    public static Intent getVideoIntent(){
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_GET_CONTENT);
        intent.setType("video/*");
        return intent;
    }

}
