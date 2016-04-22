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
import android.widget.Button;
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
    private ProcessedVideoFragment mProcessedVideoFragment;

    NotificationManager mNotificationManager;
    NotificationCompat.Builder mNotificationBuilder;

    @Bind(R.id.toolbar) Toolbar mToolbar;
    @Bind(R.id.download_button) Button mDownloadButton;
    private VideoService mVideoService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        mDownloadButton.setVisibility(View.INVISIBLE);

        setSupportActionBar(mToolbar);

        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        mProcessedVideoFragment = new ProcessedVideoFragment();
        fragmentTransaction.add(R.id.processed_videos_list_fragment_container, mProcessedVideoFragment);
        fragmentTransaction.commit();


        mVideoService = new VideoService();

        mNotificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationBuilder = new NotificationCompat.Builder(this);
        mNotificationBuilder.setContentTitle("Video Processing")
                .setContentText("Processing in progress")
                .setSmallIcon(android.R.drawable.stat_sys_download);

    }

    private void checkTheProgress(){
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
        if (id == R.id.action_settings) {
            return true;
        }

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

                mVideoService.uploadFile(realPath, new VideoService.FileUploadCallback() {
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
        }
    }

//    @Override
//    public void onListFragmentInteraction(VideoItem item) {
//
//    }

    private class ConvertionProcessing{

        private String mVideoId;
        private int mNotificationId = sNotificationSerialId++;
        private VideoService.ServiceCallback mServiceCallback;
        private Handler mHandler;
        private final int INTERVAL = 10000;

        public ConvertionProcessing(String videoId) {
            mVideoId = videoId;
            mNotificationBuilder.setContentText("Processing in progress");
            mNotificationBuilder.setSmallIcon(android.R.drawable.stat_sys_download);
            updateNotification(100, 0);
            mHandler = new Handler();

            mServiceCallback = new VideoService.ServiceCallback() {
                @Override
                public void onProgress(float progress) {

                    updateNotification(100, Math.round(progress*100));
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
                    updateNotification(0, 0);
                    mDownloadButton.setVisibility(View.VISIBLE);
                    mDownloadButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            mVideoService.downloadVideo(getApplicationContext(), filePath, new VideoService.FileDownloadCallback() {
                                @Override
                                public void onSuccess(ResponseBody responseBody) {
                                    String fileName = filePath.split("/")[3] + "processed";
                                    boolean writtenToDisk = DocsHelper.writeResponseBodyToDisk(responseBody,
                                            getApplicationContext(), filePath.split("/")[3]);
                                    Log.d("Download", "file download was a success? " + writtenToDisk);
                                    mDownloadButton.setVisibility(View.INVISIBLE);
                                    Toast.makeText(getApplicationContext(), "File downloaded", Toast.LENGTH_SHORT).show();

                                    //Update the fragment list
                                    mProcessedVideoFragment.updateFilesList();
                                }

                                @Override
                                public void onError(Throwable throwable) {
                                    Log.e("Download", "Download error");
                                    Toast.makeText(getApplicationContext(), "File upload error, try once more", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    });
                }

                @Override
                public void onError(String error) {
                    //change notification state to error
                    mNotificationBuilder.setContentText("Video processing error");
                    mNotificationBuilder.setSmallIcon(android.R.drawable.stat_notify_error);
                    updateNotification(0,0);

                }
            };
            mVideoService.getProcessingProgress(mVideoId, mServiceCallback);
        }

        private void updateNotification(int max, int progress){
            mNotificationBuilder.setProgress(max, progress,false);
            mNotificationManager.notify(mNotificationId, mNotificationBuilder.build());
        }

    }

}
