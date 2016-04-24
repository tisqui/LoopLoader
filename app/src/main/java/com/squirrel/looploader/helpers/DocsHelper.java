package com.squirrel.looploader.helpers;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import okhttp3.ResponseBody;

/**
 * Created by squirrel on 4/9/16.
 */
public class DocsHelper {

    public final String FOLDER_PREF = "/Camera/";

    /**
     * Gets the real path for the media files from Uri, using Media provider.
     * Won't work if it is DownloadsProvider,
     * @param uri
     * @param context
     * @return
     */
    public static String getMediaPath(Uri uri, Context context){
        final String docId = DocumentsContract.getDocumentId(uri);
        final String[] split = docId.split(":");
        final String type = split[0];

        Uri contentUri = null;
        if ("image".equals(type)) {
            contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
        } else if ("video".equals(type)) {
            contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
        } else if ("audio".equals(type)) {
            contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        }

        final String selection = "_id=?";
        final String[] selectionArgs = new String[] {split[1]};

        return getDataColumn(context, contentUri, selection, selectionArgs);
    }

    /**
     * Get the value of the data column for this Uri. This is useful for
     * MediaStore Uris, and other file-based ContentProviders.
     *
     * thanks https://github.com/iPaulPro/aFileChooser/blob/master/aFileChooser/src/com/ipaulpro/afilechooser/utils/FileUtils.java
     *
     * @param context The context.
     * @param uri The Uri to query.
     * @param selection (Optional) Filter used in the query.
     * @param selectionArgs (Optional) Selection arguments used in the query.
     * @return The value of the _data column, which is typically a file path.
     */
    public static String getDataColumn(Context context, Uri uri, String selection,
                                       String[] selectionArgs) {

        Cursor cursor = null;
        final String column = "_data";
        final String[] projection = {
                column
        };

        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs,
                    null);
            if (cursor != null && cursor.moveToFirst()) {
                final int column_index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(column_index);
            }
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return null;
    }

    public static boolean createDirIfNotExists(String path) {
        boolean ret = true;

        File file = new File(Environment.getExternalStorageDirectory(), path);
        if (!file.exists()) {
            if (!file.mkdirs()) {
                Log.e("DocsHelper", "Problem creating Loops folder");
                ret = false;
            }
        }
        return ret;
    }

    public static boolean createAlbumStorageDirIfNotExisting(String albumName) {
        // Get the directory for the user's public pictures directory.
        File file = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES), albumName);
        if (!file.exists()) {
            if (!file.mkdirs()) {
                Log.e("DocsHelper", "Problem creating Loops folder");
                return false;
            }
        }
        return true;
    }

    public static boolean writeResponseBodyToDisk(ResponseBody body, final Context context, String fileName) {
        try {

            //check if the directory exists
//            if(!createDirIfNotExists(Environment.DIRECTORY_DCIM + "/Loops/")){
//                return false;
//            }

//            File videoFile = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM + "/Loops/"), fileName);

            if(!createAlbumStorageDirIfNotExisting("/Loops/")){
                return false;
            }

            File videoFile = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES + "/Loops/"), fileName);


            InputStream inputStream = null;
            OutputStream outputStream = null;

            try {
                byte[] fileReader = new byte[4096];

                long fileSize = body.contentLength();
                long fileSizeDownloaded = 0;

                inputStream = body.byteStream();
                outputStream = new FileOutputStream(videoFile);

                while (true) {
                    int read = inputStream.read(fileReader);

                    if (read == -1) {
                        break;
                    }

                    outputStream.write(fileReader, 0, read);

                    fileSizeDownloaded += read;

                    Log.d("Download", "file download: " + fileSizeDownloaded + " of " + fileSize);
                }

                outputStream.flush();

                //tell the gallery to see the new video
                Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                Uri contentUri = Uri.fromFile(videoFile);
                mediaScanIntent.setData(contentUri);
                context.sendBroadcast(mediaScanIntent);

                return true;
            } catch (IOException e) {
                Log.d("Download error", e.getMessage());
                return false;
            } finally {
                if (inputStream != null) {
                    inputStream.close();
                }

                if (outputStream != null) {
                    outputStream.close();
                }
            }
        } catch (IOException e) {
            Log.d("Download", e.getMessage());
            return false;
        }
    }

    public static String[] getFilesList(Context context){
        String path = context.getFilesDir().getPath();
        Log.d("getFilesList: Path=", path);
        return context.fileList();
    }

    public static String getFileExt(String fileName) {
        return fileName.substring(fileName.lastIndexOf(".") + 1, fileName.length());
    }

    public static boolean checkIfVideo(String fileName){
        String fileExt = getFileExt(fileName);
        if(fileExt.equals("3gp") || fileExt.equals("mp4") || fileExt.equals("mkv") || fileExt.equals(" webm")){
            return true;
        } else {
            return false;
        }
    }

    public static boolean isNewGooglePhotosUri(Uri uri) {
        return "com.google.android.apps.photos.contentprovider".equals(uri.getAuthority());
    }

}
