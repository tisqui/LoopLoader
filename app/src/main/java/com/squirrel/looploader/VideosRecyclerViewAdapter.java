package com.squirrel.looploader;

import android.content.Context;
import android.graphics.Bitmap;
import android.media.ThumbnailUtils;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squirrel.looploader.helpers.DocsHelper;
import com.squirrel.looploader.model.VideoFile;

import java.io.File;
import java.util.ArrayList;


public class VideosRecyclerViewAdapter extends RecyclerView.Adapter<VideosRecyclerViewAdapter.ViewHolder> {

    int id = 100;
    private ArrayList<VideoFile> mValues;
    private ProcessedVideoFragment.OnListFragmentInteractionListener mListener;
    private final OnItemClickListener mOnItemClickListener;

    public interface OnItemClickListener {
        void onItemClick(VideoFile item);
        void onThumbnailClick(VideoFile item);
    }

    public ArrayList<VideoFile> getValues() {
        return mValues;
    }

    public VideosRecyclerViewAdapter(ProcessedVideoFragment.OnListFragmentInteractionListener listener,
                                     ArrayList<VideoFile> videous,
                                     OnItemClickListener onItemClickListener) {
        mListener = listener;
        mValues = videous;
        mOnItemClickListener = onItemClickListener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_processed_video, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
//        holder.mItem = mValues.get(position);
//        holder.mIdView.setText(String.valueOf(position+1));
//        holder.mContentView.setText(mValues.get(position).getFileName());
        holder.bind(mValues.get(position), position, mOnItemClickListener);

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
        public final ImageView mImageView;
        public VideoFile mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mIdView = (TextView) view.findViewById(R.id.id);
            mContentView = (TextView) view.findViewById(R.id.content);
            mImageView = (ImageView) view.findViewById(R.id.video_thumbnail);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mContentView.getText() + "'";
        }

        public void bind(final VideoFile item, final int position, final OnItemClickListener listener) {
            mItem = item;
            mIdView.setText(String.valueOf(position + 1));
            mContentView.setText(item.getFileName());

            Bitmap thumb = ThumbnailUtils.createVideoThumbnail(item.getFilePath(), MediaStore.Video.Thumbnails.MICRO_KIND);

            if(thumb != null){
                mImageView.setImageBitmap(thumb);
            }

            mContentView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onItemClick(item);
                }
            });

            mImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onThumbnailClick(item);
                }
            });
        }
    }

}
