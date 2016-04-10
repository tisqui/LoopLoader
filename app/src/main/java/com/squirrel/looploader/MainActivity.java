package com.squirrel.looploader;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.squirrel.looploader.dummy.DummyContent;
import com.squirrel.looploader.helpers.DocsHelper;
import com.squirrel.looploader.helpers.IntentHelper;
import com.squirrel.looploader.services.VideoUploadService;

import java.io.File;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity implements
        ProcessedVideoFragment.OnListFragmentInteractionListener {

    @Bind(R.id.toolbar) Toolbar mToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        setSupportActionBar(mToolbar);

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

                String real_path = DocsHelper.getMediaPath(uri, this);
                Log.d("TAG", "real path " + real_path);

                File file = new File(real_path);
                boolean exists = file.exists();

                Log.d("TAG", "file exists? " + exists);

                VideoUploadService videoUploadService = new VideoUploadService();
                videoUploadService.uploadFile(real_path);
            }
        }
    }

    @Override
    public void onListFragmentInteraction(DummyContent.DummyItem item) {

    }

}
