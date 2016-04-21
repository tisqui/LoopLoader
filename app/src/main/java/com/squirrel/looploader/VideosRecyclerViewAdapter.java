package com.squirrel.looploader;

import android.content.Context;
import android.os.Environment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.squirrel.looploader.helpers.DocsHelper;
import com.squirrel.looploader.model.VideoFile;

import java.io.File;
import java.util.ArrayList;


public class VideosRecyclerViewAdapter extends RecyclerView.Adapter<VideosRecyclerViewAdapter.ViewHolder> {

    int id = 100;
    private ArrayList<VideoFile> mValues;
    private ProcessedVideoFragment.OnListFragmentInteractionListener mListener;

    public ArrayList<VideoFile> getValues() {
        return mValues;
    }

    public VideosRecyclerViewAdapter(ProcessedVideoFragment.OnListFragmentInteractionListener listener, ArrayList<VideoFile> videous) {
        mListener = listener;
        mValues = videous;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_processed_video, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mItem = mValues.get(position);
        holder.mIdView.setText(String.valueOf(position+1));
        holder.mContentView.setText(mValues.get(position).getFileName());

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mListener) {
                    // Notify the active callbacks interface (the activity, if the
                    // fragment is attached to one) that an item has been selected.
//                    mListener.onListFragmentInteraction(holder.mItem);
                }
            }
        });
    }

    public void updateFileList(Context context){
//
//        if(!DocsHelper.createDirIfNotExists(Environment.DIRECTORY_DCIM+"/Loops/")){
//            return;
//        }
//        File[] listFiles = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM + "/Loops/").listFiles();
        File[] listFiles = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES + "/Loops/").listFiles();

        mValues.clear();
        if(listFiles != null){
            for(File file : listFiles){
                if(DocsHelper.checkIfVideo(file.getName())){
                    VideoFile item = new VideoFile(file.getPath(), file);
                    mValues.add(item);
                }
            }
        }
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView mIdView;
        public final TextView mContentView;
        public VideoFile mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mIdView = (TextView) view.findViewById(R.id.id);
            mContentView = (TextView) view.findViewById(R.id.content);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mContentView.getText() + "'";
        }
    }
}
