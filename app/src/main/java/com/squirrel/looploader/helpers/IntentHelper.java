package com.squirrel.looploader.helpers;

import android.content.Intent;
import android.net.Uri;

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

    public static Intent getVideoFolderIntent(String path){
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        Uri uri = Uri.parse(path);
        intent.setDataAndType(uri, "video/*");
        return intent;
    }

}
