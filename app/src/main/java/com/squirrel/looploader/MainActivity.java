package com.squirrel.looploader;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.NotificationCompat;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.squirrel.looploader.helpers.DocsHelper;
import com.squirrel.looploader.helpers.IntentHelper;
import com.squirrel.looploader.services.VideoService;

import java.io.File;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.ResponseBody;

public class MainActivity extends AppCompatActivity implements
        ProcessedVideoFragment.OnListFragmentInteractionListener {

    private static int sNotificationSerialId = 101;
    private static int sDownloadNotificationSerialId = 201;
    private ProcessedVideoFragment mProcessedVideoFragment;

    NotificationManager mNotificationManager;
    NotificationCompat.Builder mNotificationBuilder;
    NotificationCompat.Builder mDownloadNotificationBuilder;


    @Bind(R.id.toolbar)
    Toolbar mToolbar;
    //    @Bind(R.id.download_button) Button mDownloadButton;
    private VideoService mVideoService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

//        mDownloadButton.setVisibility(View.INVISIBLE);

        setSupportActionBar(mToolbar);

        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        mProcessedVideoFragment = new ProcessedVideoFragment();
        fragmentTransaction.add(R.id.processed_videos_list_fragment_container, mProcessedVideoFragment);
        fragmentTransaction.commit();

        mVideoService = new VideoService();

        //get the received intent
        Intent receivedIntent = getIntent();
        //get the action
        String receivedAction = receivedIntent.getAction();
        String receivedType = receivedIntent.getType();
        if (receivedAction.equals(Intent.ACTION_SEND)) {
            //content is being shared
            if (receivedType.startsWith("video/")) {
                //we got video!
                //get the uri of the received video
                Uri receivedUri = (Uri) receivedIntent.getParcelableExtra(Intent.EXTRA_STREAM);
                if (receivedUri != null) {
                    if (DocsHelper.isNewGooglePhotosUri(receivedUri)) {
                        Log.d("Shared", " uriPath = " + receivedUri.getPath());
                        String pathUri = receivedUri.getPath();
                        String newUri = pathUri.substring(pathUri.indexOf("content"), pathUri.lastIndexOf("/ORIGINAL"));
                        String realPath = DocsHelper.getDataColumn(this, Uri.parse(newUri), null, null);
                        Log.d("Shared", " path = " + realPath);
                        startUploadingFile(realPath);
                    } else {
                        //old style uri
                        startUploadingFile(DocsHelper.getMediaPath(receivedUri, this));
                    }
                }
            } else {
                Toast.makeText(this, "This application can process only videos :(", Toast.LENGTH_LONG).show();
            }
        } else if (receivedAction.equals(Intent.ACTION_MAIN)) {
            //it was not sharing, direct launch
        }

        mNotificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationBuilder = new NotificationCompat.Builder(this);
        mNotificationBuilder.setContentTitle("Video Processing")
                .setContentText("Processing in progress")
                .setSmallIcon(android.R.drawable.stat_sys_upload);

        //set thee notification builder for download notifications
        mDownloadNotificationBuilder = new NotificationCompat.Builder(this);
        mDownloadNotificationBuilder.setContentTitle("Download of file complete")
                .setSmallIcon(android.R.drawable.stat_sys_download_done);

    }

    private void checkTheProgress() {
        mNotificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationBuilder = new NotificationCompat.Builder(this);
        mNotificationBuilder.setContentTitle("Picture Upload")
                .setContentText("Upload in progress")
                .setSmallIcon(R.drawable.ic_arrow_left_bold_circle_outline);

    }

    //Open the video gallery by click on FAB
    @OnClick(R.id.fab)
    public void onClick(View view) {
        startActivityForResult(Intent.createChooser(IntentHelper.getVideoIntent(), "Select Video"),
                IntentHelper.GET_GALLERY_VIDEO);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
//        if (id == R.id.action_settings) {
//            return true;
//        }

        return super.onOptionsItemSelected(item);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            if (data == null) return;
            if (requestCode == IntentHelper.GET_GALLERY_VIDEO) {

                Uri uri = data.getData();

                String realPath = DocsHelper.getMediaPath(uri, this);
                Log.d("TAG", "real path " + realPath);

                File file = new File(realPath);
                boolean exists = file.exists();

                Log.d("TAG", "file exists? " + exists);

                startUploadingFile(realPath);

//                mVideoService.uploadFile(realPath, new VideoService.FileUploadCallback() {
//                    @Override
//                    public void onSuccess(String fileId) {
//                        new ConvertionProcessing(fileId);
//                    }
//
//                    @Override
//                    public void onError(Throwable throwable) {
//                        Log.d("TAG", "Uploading error: " + throwable.getMessage());
//                        Toast.makeText(getApplicationContext(),
//                                "Uploading error: " + throwable.getMessage(),
//                                Toast.LENGTH_SHORT).show();
//                    }
//                });
            }
        }
    }

    private void startUploadingFile(String path) {
        mVideoService.uploadFile(path, new VideoService.FileUploadCallback() {
            @Override
            public void onSuccess(String fileId) {
                new ConvertionProcessing(fileId);
            }

            @Override
            public void onError(Throwable throwable) {
                Log.d("TAG", "Uploading error: " + throwable.getMessage());
                Toast.makeText(getApplicationContext(),
                        "Uploading error: " + throwable.getMessage(),
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

//    @Override
//    public void onListFragmentInteraction(VideoItem item) {
//
//    }

    private class ConvertionProcessing {

        private String mVideoId;
        private int mNotificationId = sNotificationSerialId++;
        private int mDownloadNotificationId = mNotificationId;
        private VideoService.ServiceCallback mServiceCallback;
        private Handler mHandler;
        private final int INTERVAL = 10000;

        public ConvertionProcessing(String videoId) {
            mVideoId = videoId;
            mNotificationBuilder.setContentText("Processing in progress");
            mNotificationBuilder.setSmallIcon(android.R.drawable.stat_sys_upload);
            updateProgressNotification(100, 0);
            mHandler = new Handler();

            mServiceCallback = new VideoService.ServiceCallback() {
                @Override
                public void onProgress(float progress) {

                    updateProgressNotification(100, Math.round(progress * 100));
                    //schedule the getProcessingProgress for every 10sec

                    mHandler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            mVideoService.getProcessingProgress(mVideoId, mServiceCallback);
                        }
                    }, INTERVAL);
                }

                @Override
                public void onReady(final String filePath) {
                    //change notification to ready
                    mNotificationBuilder.setContentText("Video processing complete");
                    mNotificationBuilder.setSmallIcon(android.R.drawable.stat_sys_upload_done);
                    updateProgressNotification(0, 0);
                    //start the download
                    updateDownloadStartNofification(filePath.split("/")[3]);
                    mVideoService.downloadVideo(getApplicationContext(), filePath, new VideoService.FileDownloadCallback() {
                        @Override
                        public void onSuccess(ResponseBody responseBody) {
                            String fileName = filePath.split("/")[3] + "processed";
                            boolean writtenToDisk = DocsHelper.writeResponseBodyToDisk(responseBody,
                                    getApplicationContext(), filePath.split("/")[3]);

                            Log.d("Download", "file download was a success? " + writtenToDisk);
                            Toast.makeText(getApplicationContext(), "File downloaded", Toast.LENGTH_SHORT).show();

                            //Update the fragment list
                            mProcessedVideoFragment.updateFilesList();
                            updateDownloadCompleteNotification(fileName);
                        }

                        @Override
                        public void onError(Throwable throwable) {
                            Log.e("Download", "Download error");
                            Toast.makeText(getApplicationContext(), "File upload error, try once more", Toast.LENGTH_SHORT).show();
                            mDownloadNotificationBuilder
                                    .setSmallIcon(android.R.drawable.stat_notify_error);
                            mNotificationManager.notify(mDownloadNotificationId, mDownloadNotificationBuilder.build());

                        }
                    });
                }

                @Override
                public void onError(String error) {
                    //change notification state to error
                    mNotificationBuilder.setContentText("Video processing error");
                    mNotificationBuilder.setSmallIcon(android.R.drawable.stat_notify_error);
                    updateProgressNotification(0, 0);

                }
            };
            mVideoService.getProcessingProgress(mVideoId, mServiceCallback);
        }

        private void updateProgressNotification(int max, int progress) {
            mNotificationBuilder.setProgress(max, progress, false);
            mNotificationManager.notify(mNotificationId, mNotificationBuilder.build());
        }

        private void updateDownloadCompleteNotification(String filename) {
            mDownloadNotificationBuilder.setContentText("File: " + filename)
                    .setSmallIcon(android.R.drawable.stat_sys_download_done);
            mNotificationManager.notify(mDownloadNotificationId, mDownloadNotificationBuilder.build());
        }

        private void updateDownloadStartNofification(String filename) {
            mDownloadNotificationBuilder.setContentText("File download in progress: " + filename)
                    .setSmallIcon(android.R.drawable.stat_sys_download);
            mNotificationManager.notify(mDownloadNotificationId, mDownloadNotificationBuilder.build());
        }

    }

}
