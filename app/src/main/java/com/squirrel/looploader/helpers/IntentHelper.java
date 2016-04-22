package com.squirrel.looploader.helpers;

import android.content.Intent;
import android.net.Uri;

import java.io.File;

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

    public static Intent getInstagramIntent(String path){
        // Create the new Intent using the 'Send' action.
        Intent share = new Intent(Intent.ACTION_SEND);

        // Set the MIME type
        share.setType("video/*");

        // Create the URI from the media
        File media = new File(path);
        Uri uri = Uri.fromFile(media);

        // Add the URI to the Intent.
        share.putExtra(Intent.EXTRA_STREAM, uri);

        return share;
    }

}
