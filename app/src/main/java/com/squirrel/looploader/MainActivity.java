package com.squirrel.looploader;

import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.NotificationCompat;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.squirrel.looploader.dummy.DummyContent;
import com.squirrel.looploader.helpers.DocsHelper;
import com.squirrel.looploader.helpers.IntentHelper;
import com.squirrel.looploader.services.VideoService;

import java.io.File;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity implements
        ProcessedVideoFragment.OnListFragmentInteractionListener {

    private static int sNotificationSerialId = 101;

    NotificationManager mNotificationManager;
    NotificationCompat.Builder mNotificationBuilder;

    @Bind(R.id.toolbar) Toolbar mToolbar;
    @Bind(R.id.chack_status_button) Button mStatusButt;
    private VideoService mVideoService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        setSupportActionBar(mToolbar);
        mVideoService = new VideoService();

        mNotificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationBuilder = new NotificationCompat.Builder(this);
        mNotificationBuilder.setContentTitle("Picture Upload")
                .setContentText("Upload in progress")
                .setSmallIcon(R.drawable.ic_arrow_left_bold_circle_outline);


        mStatusButt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                if(mVideoService.getId() != null){
//                    mVideoService.getProcessingProgress(mVideoService.getId());
//                }
            }
        });

    }

    private void checkTheProgress(){
        mNotificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationBuilder = new NotificationCompat.Builder(this);
        mNotificationBuilder.setContentTitle("Picture Upload")
                .setContentText("Upload in progress")
                .setSmallIcon(R.drawable.ic_arrow_left_bold_circle_outline);

//        // Start a progress checking operation
//        new Thread(
//                new Runnable() {
//                    @Override
//                    public void run() {
//                        int progress = 0;
//
//                        do{
//                            try {
//                                // Sleep for 5 seconds
//                                Thread.sleep(5*1000);
//                            } catch (InterruptedException e) {
//                            }
//                            mNotificationBuilder.setProgress(100, progress,false);
//                            mNotificationManager.notify(PROGRESS_NOTIFICATION_ID, mNotificationBuilder.build());
//                            progress+=10;
//                        } while (progress <= 100);
//
//                        // When the loop is finished, updates the notification
//                        mNotificationBuilder.setContentText("Upload complete")
//                                // Removes the progress bar
//                                .setProgress(0,0,false);
//                        mNotificationManager.notify(PROGRESS_NOTIFICATION_ID, mNotificationBuilder.build());
//                    }
//                }
//// Starts the thread by calling the run() method in its Runnable
//        ).start();

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

    @Override
    public void onListFragmentInteraction(DummyContent.DummyItem item) {

    }

    private class ConvertionProcessing{

        private String mVideoId;
        private int mNotificationId = sNotificationSerialId++;
        private VideoService.ServiceCallback mServiceCallback;

        public ConvertionProcessing(String videoId) {
            mVideoId = videoId;
            updateNotification(0);
            mServiceCallback = new VideoService.ServiceCallback() {
                @Override
                public void onProgress(float progress) {
                    //schedule the getProcessingProgress for every 10sec
                    //call is with mServiceCallback
                    mVideoService.getProcessingProgress(mVideoId, mServiceCallback);
                }

                @Override
                public void onReady(String filePath) {
                //change notification to ready

                    //TODO show the download button
                }

                @Override
                public void onError(String error) {
                    //change notification state to error
                }
            };
            mVideoService.getProcessingProgress(mVideoId, mServiceCallback);
        }

        private void updateNotification(int progress){
            mNotificationBuilder.setProgress(100, progress,false);
            mNotificationManager.notify(mNotificationId, mNotificationBuilder.build());
        }



    }

}
